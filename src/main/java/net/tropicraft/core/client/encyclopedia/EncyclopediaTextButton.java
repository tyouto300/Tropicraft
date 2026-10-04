package net.tropicraft.core.client.encyclopedia;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class EncyclopediaTextButton extends EncyclopediaButton {
    private int textColor;
    protected Component title;
    public EncyclopediaTextButton(int x, int y, int width, int height, Component message, OnPress onPress, int type, Component title) {
        this(x, y, width, height, message, onPress, DEFAULT_NARRATION, type, GuiTropicalBook.COLOR_READ, title);
    }
    public EncyclopediaTextButton(int x, int y, int width, int height, Component message, OnPress onPress, int type, int textColor, Component title) {
        this(x, y, width, height, message, onPress, DEFAULT_NARRATION, type, textColor, title);
    }
    public EncyclopediaTextButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration, int type, int textColor, Component title) {
        super(x, y, width, height, message, onPress, createNarration, type);
        this.textColor = textColor;
        this.title = title;
    }


    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int i, int j, float partialTicks) {
        //if (!visible) { return; }
        //if (buttonImage != null) { super.extractContents(graphics, i, j, partialTicks); }
        isHovered = i >= getX() && j >= getY() && i < getX() + width && j < getY() + height;
        if (isHoveredOrFocused()) {
            graphics.text(Minecraft.getInstance().font, title, getX(), getY() + (height - 8) / 2, GuiTropicalBook.COLOR_HIGHLIGHT, false);
        } else {
            graphics.text(Minecraft.getInstance().font, title, getX(), getY() + (height - 8) / 2, textColor, false);
        }
    }

}
