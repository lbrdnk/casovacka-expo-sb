import { useEffect } from "react";
import { StyleSheet, Text, TouchableOpacity } from "react-native"
import Animated, {
    FadeIn,
    FadeInUp,
    FadeOut,
    FadeOutUp,
    Layout,
    SlideInLeft,
    SlideInRight,
    SlideOutLeft,
} from "react-native-reanimated";

import Ionicons from "@expo/vector-icons/Ionicons";

import useReanimatedBg from "../../hooks/useReanimatedBg"

import Button from "../Button/Button";


const TimerSelectorItem = (props) => {

    const {
        timer: { name, isSelected, selectTimerFn, color,
            currentActivityName,
            currentIntervalName,
            nextActivityName,
            nextIntervalName,
            isTimerRunning,
            cleared,
            running
        },
    } = props;

    // next interval name aj current interval name ked neni -- znamena ze nejsu
    // setnute intervaly
    // -> naju sa zobrazovat len mena aktivit

    return (
        <TouchableOpacity
            onPress={selectTimerFn}
        >
            <Animated.View
                style={[
                    {
                        // borderWidth: 16,
                        // borderBottomColor: "transparent",
                        // borderRightColor: "transparent",
                        // borderTopColor: "white",
                        // borderLeftColor: "white",
                    },
                    { minHeight: 64, backgroundColor: color, padding: 16 }
                ]}
                entering={SlideInLeft}
                exiting={SlideOutLeft}
                layout={Layout}
            >

                {/* header */}
                <Animated.View style={{ flexDirection: "row", alignItems: "center" }}>

                    {!cleared ? (


                        running ? (
                            <Animated.View
                                key="kokot1"
                                entering={FadeIn}
                                exiting={FadeOut}
                            // layout={Layout}
                            >
                                <Ionicons name="play" size={20} />
                            </Animated.View>
                        ) : (
                            <Animated.View
                                key="kokot2"
                                entering={FadeIn}
                                exiting={FadeOut}
                            // layout={Layout}
                            >
                                <Ionicons name="stop" size={20} />
                            </Animated.View>
                        )

                    ) : null}

                    <Animated.Text
                        // entering={FadeInUp}
                        // exiting={FadeOutUp}
                        layout={Layout}
                        style={[{ marginLeft: 8 },
                        timerSelectorItemStyles.headerText]}
                    >
                        {name}
                    </Animated.Text>
                </Animated.View>
                {isSelected ? (
                    <Animated.View
                        // style={{ backgroundColor: color }}
                        entering={FadeInUp}
                        exiting={FadeOutUp}
                        layout={Layout}
                    >
                        <Animated.View
                            style={{
                                backgroundColor: "lightcyan",
                                flexDirection: "row",
                                justifyContent: "space-evenly",
                                marginBottom: 16
                            }}
                        >
                            <Text>{currentActivityName}</Text>
                            <Text>{nextActivityName}</Text>
                        </Animated.View>
                        <Animated.View
                            style={{
                                backgroundColor: "yellow",
                                flexDirection: "row",
                                justifyContent: "space-evenly",
                            }}
                        >
                            <Animated.Text
                                key={currentIntervalName}
                                entering={SlideInRight}
                                exiting={SlideOutLeft}
                                layout={Layout}
                            >
                                {currentIntervalName}
                            </Animated.Text>
                            <Animated.Text
                                key={nextIntervalName}
                                entering={SlideInRight}
                                exiting={SlideOutLeft}
                                layout={Layout}
                            >
                                {nextIntervalName}
                            </Animated.Text>
                        </Animated.View>
                    </Animated.View>
                ) : null}
                {/* content */}

            </Animated.View>
        </TouchableOpacity >

    )
}

const renderTimerSelectorItem = ({ item, index, separators }) => {
    return (
        <TimerSelectorItem timer={item} />
    )
}

export const TimerSelector = (props) => {

    const {
        timers,
    } = props;

    const { style, setBgColor } = useReanimatedBg();

    return (
        <>
            <Animated.FlatList
                data={timers}
                renderItem={renderTimerSelectorItem}
                keyExtractor={item => item.id}
                // style={[style]}
                itemLayoutAnimation={Layout.delay(0)}
            />
        </>
    )
}

const timerSelectorItemStyles = StyleSheet.create({
    headerContainer: {
        flexDirection: "row",
        alignItems: "center",
        height: 64,
        width: "100%",
        padding: 8,
    },
    headerText: {
        fontSize: 20,
        lineHeight: 32
    },

})

export default TimerSelector;