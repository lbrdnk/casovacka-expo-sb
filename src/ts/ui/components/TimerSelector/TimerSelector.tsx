import { useCallback, useEffect } from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native"
import Ionicons from '@expo/vector-icons/Ionicons';

import Accordion from "../Accordion/Accordion";

import { getHeaderTitle } from '@react-navigation/elements';
import { Header } from '@react-navigation/elements';

import Animated, {
    interpolateColor,
    useAnimatedStyle,
    useDerivedValue,
    useSharedValue,
    withTiming
} from "react-native-reanimated";


const TimerSelectorItemHeader2 = (props) => {
    const { name, isActive, cleared, timeLeftStr } = props;
    const rotation = useSharedValue(0);
    const iconContainerStyle = useAnimatedStyle(() => ({
        transform: [{ rotateZ: rotation.value + "deg" }]
    }), [])
    useEffect(() => {
        if (isActive) {
            rotation.value = withTiming(90, { duration: 300 })
        } else {
            rotation.value = withTiming(0, { duration: 300 })
        }
    }, [isActive])
    return (
        <View
            style={
                timerSelectorItemHeaderStyles.container
            }
        >

            <Animated.View
                style={iconContainerStyle}
            >
                <Ionicons name="caret-forward" size={20} color="black" />
            </Animated.View>
            <View
                style={{
                    flex: 1,
                    flexDirection: "row",
                    justifyContent: "space-between",
                    alignItems: "center",
                }}
            >
                <Text style={timerSelectorItemHeaderStyles.text}>{name}</Text>
                {!cleared && <Text style={timerSelectorItemHeaderStyles.numText}>{timeLeftStr}</Text>}
            </View>
        </View>
    )
};

const colorActive = "#1ecbe1";
const colorInactive = "#ecfbfc";
const TimerSelectorItemHeader = (props) => {
    console.log(props)
    const { name, isActive, cleared, timeLeftStr, color = "#afafaf" } = props;

    const progress = useDerivedValue(
        () => isActive ? withTiming(1) : withTiming(0),
        [isActive]
    );
    const rStyle = useAnimatedStyle(() => ({
        backgroundColor: interpolateColor(
            progress.value,
            [0, 1],
            [colorInactive, color]
        ),
    }))


    return (
        <Animated.View
            style={[{
                margin: 8,
            }, rStyle]}
        >

            <View
                style={{
                    flex: 1,
                    flexDirection: "row",
                    justifyContent: "space-between",
                    alignItems: "center",
                }}
            >
                <Text style={timerSelectorItemHeaderStyles.text}>{name}</Text>
                {!cleared && <Text style={timerSelectorItemHeaderStyles.numText}>{timeLeftStr}</Text>}
            </View>
        </Animated.View>
    )
};


const TimerSelectorItemContent = (props) => {
    const { 
        name,
        totalTimeStr,
        timeLeftStr,
        nextIntervalName,

        isActive,
        
        currentIntervalName,
        currentIntervalTimeLeftStr,
        
        currentActivityName,
        currentActivityTimeLeftStr,
     } = props;

     const progress = useDerivedValue(
        () => isActive ? withTiming(1) : withTiming(0),
        [isActive]
    );
    const rStyle = useAnimatedStyle(() => ({
        backgroundColor: interpolateColor(
            progress.value,
            [0, 1],
            [colorInactive, colorActive]
        ),
    }))


    // if not started, print something
    //  console.log(props)
    return (
        <Animated.View style={[timerSelectorItemContentStyles.container, rStyle]}>

            {currentActivityName && <Text>{"Current activity name " + currentActivityName}</Text>}
            {currentActivityTimeLeftStr && <Text>{"Current activity time left " + currentActivityTimeLeftStr}</Text>}
    
            {currentIntervalName && <Text>{"Current interval name " + currentIntervalName}</Text>}
            {currentIntervalTimeLeftStr && <Text>{"Current interval time left " + currentIntervalTimeLeftStr}</Text>}
        </Animated.View>
    )
};

export const TimerSelector = (props) => {

    const {
        selectTimerFn,
        timers
    } = props;

    const timersWithProps = timers
        .map(timer => ({
            ...timer,
            onHeaderPressed: () => selectTimerFn(timer.id)
        }));

    const itemKeyExtractor = useCallback(({ id }) => id, [])

    // here animated color bg for header / content
    // -> container style bg color

    return (
        <>
        <Accordion
            // containerStyle={timerSelectorStyles.container}
            // itemContainerStyle={timerSelectorStyles.itemContainer}
            itemKeyExtractor={itemKeyExtractor}
            items={timersWithProps}
            headerComponent={TimerSelectorItemHeader}
            contentComponent={TimerSelectorItemContent}
        />
        </>
    )
}

const timerSelectorStyles = StyleSheet.create({
    container: {
        // backgroundColor: "aquamarine"
        borderWidth: 4,
        
    },
    itemContainer: {
        margin: 8,
        // borderWidth: 1,
        backgroundColor: "white",
        // shadowOpacity: 0.08,
        // shadowOffset: {
        //     width: 5,
        //     height: 0
        // },
        // shadowRadius: 5
    }
})

const timerSelectorItemHeaderStyles = StyleSheet.create({
    container: {
        flexDirection: "row",
        alignItems: "center",
        height: 64,
        width: "100%",
        padding: 8,
    },
    text: {
        fontSize: 18,
        // fontFamily: "sans"
    },
    numText: {
        fontSize: 14,
        fontVariant: ["tabular-nums"]
    }
})

const timerSelectorItemContentStyles = StyleSheet.create({
    container: {
        padding: 8,
    }
})

export default TimerSelector




        // .map(timer => ({
        //     ...timer,
        //     headerProps: extractHeaderProps(timer),
        // }))
        // .map(timer => ({
        //     ...timer,
        //     contentProps: extractContentProps(timer)
        // }))
        // add selection function to header props



// const extractContentProps = (timer) => ({
//     name: timer.name,
    
//     currentIntervalName: timer.currentIntervalName,
//     currentIntervalTimeLeft: timer.currentIntervalTimeLeft,
    
//     currentActivityName: timer.currentActivityName,
//     currentActivityTimeLeftStr: timer.currentActivityTimeLeftStr
    
// });

// const extractHeaderProps = (timer) => ({
//     name: timer.name,
//     cleared: timer.cleared,
//     timeLeftStr: timer.timeLeftStr
// });