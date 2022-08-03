import { TouchableOpacity, Text, StyleSheet } from "react-native";


export const Button = ({ disabled, style, onPress, textStyle, text }) => {
  return (
    <TouchableOpacity 
      disabled={disabled}
      style={{...styles.container, ...(typeof style !== "undefined" ? style : {})}}
      onPress={onPress}
    >
      <Text style={{...styles.text, ...(typeof textStyle !== "undefined" ? textStyle : {})}}>{text}</Text>
    </TouchableOpacity>
  );
};


const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 9999,
    width: 100,
    height: 100,
    alignItems: "center",
    justifyContent: "center",
    // backgroundColor: "white",

  },
  text: { color: "black" },
});

export default Button;