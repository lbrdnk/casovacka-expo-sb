import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react-native";
import { TimerSelector } from "./TimerSelector";

const TimerSelectorMeta: ComponentMeta<typeof TimerSelector> = {
  title: "TimerSelector",
  component: TimerSelector,
  argTypes: {
    // onPress: { action: "pressed the button" },
  },
  args: {
    timers:
      Array(30).fill(0).map((_, i) => {
        return {
          id: "cicmbrus " + i,
          name: "cicimbrus " + i,
        }
      })
  },
};

export default TimerSelectorMeta;

type TimerSelectorStory = ComponentStory<typeof TimerSelector>;

export const Basic: TimerSelectorStory = (args) => <TimerSelector {...args} />;
