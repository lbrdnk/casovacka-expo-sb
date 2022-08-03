import React, { useCallback, useEffect, useState } from "react";
import { FlatList, ScrollView, Text, TouchableOpacity, View } from "react-native";
import Animated, { withTiming, Layout, JumpingTransition, SlideInRight, SlideInLeft, SlideOutLeft, SlideInUp, FadeIn, FadeInUp, FadeOutUp, useDerivedValue, useAnimatedStyle, interpolateColor } from "react-native-reanimated";

import Button from "../Button/Button"

const AccordionItem = (props) => {

    const {
        activeKey,
        contentComponent: Content,
        handleItemClick,
        headerComponent: Header,
        itemContainerStyle,
        itemKeyExtractor,
        renderItemInfo: { item, index, separators },
    } = props;



    // const progress = useDerivedValue(
    //     () => toggle ? withTiming(1) : withTiming(0),
    //     [toggle]
    // )

    // const rStyle = useAnimatedStyle(() => {
    //     return {
    //         backgroundColor: interpolateColor(
    //             progress.value,
    //             [0, 1],
    //             ["#000", "#fff"]
    //         ),
    //     }
    // })

    const itemKey = itemKeyExtractor(item);
    const isActive = itemKey === activeKey;

    return (
        <Animated.View
            // key={itemKey}
            entering={SlideInLeft}
            exiting={SlideOutLeft}
            layout={Layout} // -- this is handled in NAIMATED FLATLIST
            style={[itemContainerStyle, /*rStyle*/]}
        >
            <TouchableOpacity
                onPress={() => {
                    handleItemClick(item);
                }}
            >
                <Header
                    // isActive={isActive}
                    // name={item.name}
                    {...item}
                    isActive={isActive}
                />
                {isActive && (
                    <Animated.View
                        // key={itemKey}
                        entering={FadeInUp}
                        exiting={FadeOutUp}
                        layout={Layout}
                    >
                        <Content {...item} isActive={isActive} />
                    </Animated.View>
                )}
            </TouchableOpacity>
        </Animated.View>
    )
}

const Accordion = (props) => {
    //
    // This should manage active item key!
    //     ? really
    //
    const {
        containerStyle,
        contentComponent,
        headerComponent,
        itemContainerStyle,
        itemKeyExtractor,
        items,
    } = props;
    const [activeKey, setActiveKey] = useState(null);

    // !!! TODO duplicit functionality in TimerSelector, what should be responsible?
    const handleItemClick = useCallback((item) => {
        const itemKey = itemKeyExtractor(item);
        if (itemKey === activeKey) {
            setActiveKey(null);
            console.log("active " + "NONE");
            item.onHeaderPressed();
        } else {
            setActiveKey(itemKey);
            console.log("active " + itemKey);
            item.onHeaderPressed();
        }
    }, [activeKey, setActiveKey])
    const [lastIndex, setLastIndex] = useState(100);
    const [items2, setItems2] = useState(items);
    const addMiddle = () => {
        const midIndex = Math.floor(items2.length / 2);
        setItems2([
            ...items2.slice(0, midIndex),
            { ...items2[0], id: "kokot" + lastIndex, name: "kokot" + lastIndex },
            ...items2.slice(midIndex, items2.length)
        ]);
        setLastIndex(lastIndex + 1);
    }
    const remMiddle = () => {
        if (items2.length > 0) {
            const midIndex = Math.floor(items2.length / 2);
            setItems2([
                ...items2.slice(0, midIndex),
                ...items2.slice(midIndex + 1, items2.length)
            ]);
        }
    }
    //
    // following code will cause rerender when anything change
    //     what is fine
    //
    const itemProps = {
        activeKey,
        handleItemClick,
        headerComponent,
        contentComponent,
        itemContainerStyle,
        itemKeyExtractor,
    }
    const renderItem = useCallback(renderItemInfo => (
        <AccordionItem renderItemInfo={renderItemInfo} {...itemProps} />
    ), [itemProps]);
    return (
        <>
            {/* <View style={{ flexDirection: "row" }}>
                <Button
                    text="add mid"
                    onPress={addMiddle}
                />
                <Button
                    text="rem mid"
                    onPress={remMiddle}
                />
            </View> */}
            <Animated.FlatList
                style={containerStyle}
                // keyExtractor={itemKeyExtractor}
                data={items2}
                renderItem={renderItem}
                itemLayoutAnimation={Layout.delay(0)}
            />
        </>
    )
}

export default Accordion;