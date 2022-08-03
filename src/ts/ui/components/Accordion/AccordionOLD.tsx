import { FlatList } from "native-base";
import React, { useCallback, useEffect, useState } from "react";
import { Button, ScrollView, Text, TouchableOpacity, View } from "react-native";
import Animated, { withTiming, Layout, JumpingTransition, SlideInRight, SlideInLeft, SlideOutLeft, SlideInUp, FadeIn, FadeInUp, FadeOutUp } from "react-native-reanimated";

export const Accordion = (props) => {

    const {
        containerStyle,
        itemContainerStyle,
        headerComponent: Header,
        contentComponent: Content,
        itemKeyExtractor,
        items,
    } = props;

    const [activeKey, setActiveKey] = useState(-1);

    //
    const itemClickHandler = useCallback((item) => {
        // console.log("click handler called")
        const itemKey = itemKeyExtractor(item);
        if (itemKey === activeKey) {
            setActiveKey(-1);
        } else {
            setActiveKey(itemKey);
        }
    }, [activeKey, setActiveKey]);

    return (
        <ScrollView style={containerStyle} showsVerticalScrollIndicator>
            {items.map(item => {
                const { id, headerProps, contentProps, onHeaderPressed } = item;
                const isActive = itemKeyExtractor(item) === activeKey;
                return (
                    <Animated.View
                        key={itemKeyExtractor(item)}
                        style={itemContainerStyle}
                        entering={SlideInLeft}
                        exiting={SlideOutLeft}
                        layout={Layout}
                    >
                        {/* header */}
                        {/* <View
                            onStartShouldSetResponder={() => true}


                        >
                            <TouchableOpacity
                                onPress={() => console.log("cici")}
                            > */}
                                <TouchableOpacity
                                    onPress={() => {
                                        itemClickHandler(item);
                                        if (item.onHeaderPressed) {
                                            item.onHeaderPressed();
                                        } else {
                                            console.log("no onHeaderPressed");
                                        }
                                    }}
                                >
                                    <Header {...item} isActive={isActive} />
                                </TouchableOpacity>
                            {/* </TouchableOpacity>
                            </View> */}
                            {/* content */}
                            {isActive && (
                                <Animated.View
                                    entering={FadeInUp}
                                    exiting={FadeOutUp}
                                    layout={Layout}
                                >
                                    <Content {...item} isActive={isActive} />
                                </Animated.View>
                            )}
                    </Animated.View>
                )
            })}
        </ScrollView>
    )
}

export default Accordion;