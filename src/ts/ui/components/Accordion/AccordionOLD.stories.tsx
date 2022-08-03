import React, { useCallback, useEffect, useState } from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react-native";
import Accordion from "./Accordion";
import { Text, View } from "react-native";

const MockHeader = (props) => {
  return (
    <View>
      <Text>HEADER</Text>
    </View>
  )
}

const MockContent = (props) => {
  return (
    <View>
      <Text style={{color: "red"}}>{props.someContent}</Text>
      <Text style={{color: "green"}}>{props.someContent}</Text>
      <Text style={{color: "blue"}}>{props.someContent}</Text>
    </View>
  )
}

const MyAccordionMeta: ComponentMeta<typeof Accordion> = {
  title: "Accordion",
  component: Accordion,
  argTypes: {
    onPress: { action: "pressed the Accordion" },
  },
  args: {
    headerComponent: MockHeader,
    contentComponent: MockContent,
    itemKeyExtractor: item => item.id,
    items: [
      {
        id: 0,
        name: "kokot",
        headerProps: {
          name: "Ahoj"
        },
        contentProps: {
          someContent: "SomeContent"
        }
      }
    ],
    text: "Hello world",
  },
};

export default MyAccordionMeta;

type MyAccordionStory = ComponentStory<typeof Accordion>;

export const Basic: MyAccordionStory = (args) => <Accordion {...args} />
