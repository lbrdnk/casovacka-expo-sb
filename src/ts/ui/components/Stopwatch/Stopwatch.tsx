import { FlatList, ListRenderItem, StyleSheet, Text, TouchableOpacity, View } from "react-native";

import Button from "../Button/Button"

// interface StopwatchProps {
//     time: string;

// }

export const Stopwatch = (props) => {
    // console.log(props);
    const {
        isTimerRunning,
        startTimerFn,
        stopTimerFn,
        resetTimerFn,
        timeStr,
    } = props;
    return (
        <View style={[{
            // borderWidth: 16,
            // borderBottomColor: "transparent",
            // borderRightColor: "transparent",
            // borderTopColor: "white",
            // borderLeftColor: "white",
        }, styles.container, { backgroundColor: "transparent" }]}>
            <Text style={styles.timerText}>{timeStr}</Text>
            <View style={styles.buttonContainer}>
                <Button
                    disabled={isTimerRunning}
                    style={styles.button}
                    textStyle={styles.buttonText}
                    text="Reset"
                    onPress={resetTimerFn}
                />
                {/* <View style={{width: 16}} /> */}
                {isTimerRunning ? (
                    <Button
                        style={styles.button}
                        textStyle={styles.buttonText}
                        text="Stop"
                        onPress={stopTimerFn}
                    />
                ) : (
                    <Button
                        style={styles.button}
                        textStyle={styles.buttonText}
                        text="Start"
                        onPress={startTimerFn}
                    />
                )}
            </View>
        </View>
    )
}

const styles = StyleSheet.create({
    buttonContainer: {
        padding: 8,
        flexDirection: "row",
        // backgroundColor: "gainsboro",
        width: "100%",
        justifyContent: "space-around",
    },
    container: {
        // backgroundColor: "violet",
        // borderWidth: 4,
        alignItems: "center",

    },
    timerText: {
        fontSize: 64,
        fontVariant: ["tabular-nums"]
    },
    button: {
        // backgroundColor: "red",
        // backgroundColor: "rgba(224,255,255,0.8)",
        // opacity: 0.5,
        borderWidth: 4,
        // borderColor: "pink",
        borderColor: "black",
    },
    buttonText: {
        fontSize: 20
    }
})