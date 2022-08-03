import { View, Text, StyleSheet } from "react-native";
import { Stopwatch } from "../Stopwatch/Stopwatch";
import TimerSelector from "../TimerSelector/TimerSelector";

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
        // isTimerCleared,
     } = props;
    // const currentTimer = find
    // console.log(props)
    return (
        <View style={styles.container}>
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
            />
        </View>
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