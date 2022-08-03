import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react-native";
import { Stopwatch } from "./Stopwatch";

const StopwatchMeta: ComponentMeta<typeof Stopwatch> = {
  title: "Stopwatch",
  component: Stopwatch,
  argTypes: {
    onPress: { action: "pressed the button" },
  },
  args: {
    // data: [{id: 1, name: "Name 1"}, {id: 2, name: "Name 2"}]
  },
};

export default StopwatchMeta;

type StopwatchStory = ComponentStory<typeof Stopwatch>;

export const Basic: StopwatchStory = (args) => <Stopwatch {...args} />;
