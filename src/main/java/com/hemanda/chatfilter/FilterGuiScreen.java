package com.hemanda.chatfilter;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.client.Minecraft;

import java.util.List;

public class FilterGuiScreen extends Screen {

    private int selectedIndex = -1;
    private TextFieldWidget inputField;

    protected FilterGuiScreen() {
        super(new StringTextComponent("Chat Filters"));
    }

    @Override
    protected void init() {
        super.init();

        List<String> filters = ChatFilterStorage.getFilters();

        int y = 20;

        // Add buttons for each existing filter
        for (int i = 0; i < filters.size(); i++) {
            String filter = filters.get(i);
            int index = i;
            this.addButton(new Button(this.width / 2 - 100, y, 200, 20, new StringTextComponent(filter),
                    button -> {
                        selectedIndex = index;
                        ChatFilterStorage.setCurrentFilter(filter); // activate filter
                    }));
            y += 25;
        }

        // Add TextField for new filter
        inputField = new TextFieldWidget(this.font, this.width / 2 - 100, y, 200, 20, new StringTextComponent("New Filter"));
        inputField.setValue("");
        this.children.add(inputField); // must add to children to update
        y += 25;

        // Add "Add Filter" button
        this.addButton(new Button(this.width / 2 - 100, y, 95, 20, new StringTextComponent("Add Filter"),
                button -> {
                    String text = inputField.getValue().trim(); // use .getValue() in 1.16.5
                    if (!text.isEmpty()) {
                        ChatFilterStorage.addFilter(text);
                        Minecraft.getInstance().setScreen(new FilterGuiScreen()); // reload GUI
                    }
                }));

        // Add "Remove Filter" button
        this.addButton(new Button(this.width / 2 + 5, y, 95, 20, new StringTextComponent("Remove Filter"),
                button -> {
                    if (selectedIndex >= 0 && selectedIndex < ChatFilterStorage.getFilters().size()) {
                        ChatFilterStorage.removeFilter(ChatFilterStorage.getFilters().get(selectedIndex));
                        Minecraft.getInstance().setScreen(new FilterGuiScreen()); // reload GUI
                    }
                }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, "Chat Filters", this.width / 2, 10, 0xFFFFFF);

        // Render text field
        if (inputField != null) {
            inputField.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (inputField != null && inputField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (inputField != null && inputField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
