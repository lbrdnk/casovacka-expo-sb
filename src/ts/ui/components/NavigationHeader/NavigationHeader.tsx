import { useState, useEffect } from "react";
import { Text, View } from "react-native";
import Animated, { interpolateColor, Layout, SlideInLeft, SlideOutLeft, SlideOutRight, useAnimatedStyle, useDerivedValue, useSharedValue, withTiming } from "react-native-reanimated";
import { getHeaderTitle, Header as OriginalHeader } from "@react-navigation/elements";
import useReanimatedBg from "../../hooks/useReanimatedBg";

import { HeaderTitle } from "@react-navigation/elements"

const HeaderTitleAnimated = (props) => {
    // console.log(props)
    return (
        <Animated.View
            key={props.children}
            entering={SlideInLeft}
            exiting={SlideOutRight}
            layout={Layout}
        >
            <HeaderTitle><Text>{props.children}</Text></HeaderTitle>
        </Animated.View>
    )
}

// using
// https://github.com/software-mansion/react-native-reanimated/issues/2739#issuecomment-998995153
//
const NavigationHeader = (props) => {
    const {
        activeColor,
        navigation,
        route,
        options,
        back,
        title
    } = props;
    console.log(title)
    // console.log(Object.keys(props))
    // const title = getHeaderTitle(options, route.name);

    // const [prevCol, setPrevCol] = useState("#000000");
    // const [curCol, setCurCol] = useState(activeColor);
    // const [dir, setDir] = useState("UP");

    // const [colors, setColors] = useState(["#000000", activeColor]);
    // const [inputRange, setInputRange] = useState([0,1]);

    // useEffect(() => {

    //     // shift + add
    //     setColors([colors[1], activeColor])
    //     // swap
    //     setInputRange([inputRange[1], inputRange[0]])

    // }, [activeColor])

    // const progress = useSharedValue(0);

    // // console.log(colors)
    // // console.log(inputRange)

    // useEffect(() => {
    //     progress.value = inputRange[1] === 1 ? withTiming(1) : withTiming(0)
    // }, [inputRange])

    // const rStyle = useAnimatedStyle(() => ({
    //     backgroundColor: //activeColor
    //     interpolateColor(
    //         progress.value, 
    //         inputRange,
    //         // [0, 1],
    //         colors.slice())
    // }), [colors, inputRange])

    const { style: rStyle, setBgColor } = useReanimatedBg(activeColor);

    useEffect(() => {
        setBgColor(activeColor);
    }, [activeColor])

    return (
        <Animated.View style={[rStyle]
            // {backgroundColor: activeColor}
        }>
            <OriginalHeader
                headerTitle={HeaderTitleAnimated}
                title={title}
                headerTransparent
            />
        </Animated.View>
    );
}

export default NavigationHeader;