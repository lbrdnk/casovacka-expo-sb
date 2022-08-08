import { useEffect } from "react";
import { View, Text, StyleSheet } from "react-native";
import useReanimatedBg from "../../hooks/useReanimatedBg";
import { Stopwatch } from "../Stopwatch/Stopwatch";
import TimerSelector from "../TimerSelector/TimerSelector";

import Animated from "react-native-reanimated";

interface StopwatchScreenProps {

}
export const StopwatchScreen = (props) => {
    const { 
        isTimerRunning,
        timers,
        stopwatchTimeStr: timeStr,
        selectTimerFn,
        startTimerFn,
        stopTimerFn,
        resetTimerFn,
        activeColor,
        // isTimerCleared,
     } = props;
    // const currentTimer = find
    // console.log(props)

    const {style, setBgColor} = useReanimatedBg();

    useEffect(() => {
        setBgColor(activeColor);
    }, [activeColor])

    return (
        <Animated.View style={[styles.container, style]}>
            <Stopwatch
                // timer={currentTimer}
                // timer={}
                timeStr={timeStr}
                resetTimerFn={resetTimerFn}
                startTimerFn={startTimerFn}
                stopTimerFn={stopTimerFn}
                isTimerRunning={isTimerRunning}
            />
            <TimerSelector
                timers={timers}
                selectTimerFn={selectTimerFn}
                activeColor={activeColor}
            />
        </Animated.View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 8,
        // backgroundColor: "aquamarine"
    }
})

export default StopwatchScreen;