module.exports = {
  stories: [
    "../src/ts/ui/components/**/*.stories.mdx",
    "../src/ts/ui/components/**/*.stories.@(js|jsx|ts|tsx)",
  ],
  addons: [
    "@storybook/addon-links",
    "@storybook/addon-essentials",
    "@storybook/addon-react-native-web",
  ],
  framework: "@storybook/react",
};
