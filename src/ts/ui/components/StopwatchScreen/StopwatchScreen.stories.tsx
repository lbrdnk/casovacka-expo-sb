import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react-native";
import { StopwatchScreen } from "./StopwatchScreen";

const StopwatchScreenMeta: ComponentMeta<typeof StopwatchScreen> = {
  title: "StopwatchScreen",
  component: StopwatchScreen,
  argTypes: {
    onPress: { action: "pressed the button" },
  },
  args: {
    // data: [{id: 1, name: "Name 1"}, {id: 2, name: "Name 2"}]
    timers: [
      {id: 1, name: "Name 1"},
      {id: 2, name: "Name 2"}
    ]
  },
};

export default StopwatchScreenMeta;

type StopwatchScreenStory = ComponentStory<typeof StopwatchScreen>;

export const Basic: StopwatchScreenStory = (args) => <StopwatchScreen {...args} />;
