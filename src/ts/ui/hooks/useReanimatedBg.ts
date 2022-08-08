import { useEffect, useState } from "react";
import { interpolateColor, useAnimatedStyle, useDerivedValue, useSharedValue, withTiming } from "react-native-reanimated";

const useReanimatedBg = (initialColor) => {

    const [color, setColor] = useState(initialColor ? initialColor : "transparent")

    // initial fill
    const [colors, setColors] = useState([color, color]);
    const [inputRange, setInputRange] = useState([0,1]);
    // const progress = useSharedValue(0);

    useEffect(() => {
        // swap inputRange values
        const newInputRange = [inputRange[1], inputRange[0]];
        // shift colors
        const newColors = [colors[1], color];
        setInputRange(newInputRange);
        setColors(newColors);

    }, [color])

    const progress = useDerivedValue(() => inputRange[1] === 1 ? withTiming(1/*, {duration: 5000}*/) : withTiming(0/*, {duration: 5000}*/), [colors])

    const rStyle = useAnimatedStyle(() => ({
        backgroundColor: interpolateColor(
            progress.value, 
            // withTiming(0),
            inputRange,
            // [0, 1],
            colors, "RGB"
        )
    }), [colors, progress.value, inputRange])

    return {style: rStyle, setBgColor: setColor}
}

export default useReanimatedBg