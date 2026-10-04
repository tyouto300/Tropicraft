package net.tropicraft.core.client.encyclopedia;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class EncyclopediaCoverButton extends EncyclopediaButton {


    public EncyclopediaCoverButton(int x, int y, int width, int height, Component message, OnPress onPress, int type) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION, type);//, playTurnSound);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v){;}

}
