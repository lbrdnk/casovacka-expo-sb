module.exports = {
  stories: ["../src/ts/ui/components/**/*.stories.?(ts|tsx|js|jsx)"],
  addons: [
    "@storybook/addon-ondevice-notes",
    "@storybook/addon-ondevice-controls",
    "@storybook/addon-ondevice-backgrounds",
    "@storybook/addon-ondevice-actions",
  ],
};
