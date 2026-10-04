package net.tropicraft.core.client.encyclopedia;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nullable;

/*
ENCYCLOPEDIABUTTTON class is the basic top of hierarchy, represents an encyclopedia button with NO TEXT and renders an image
ENCYCLOPEDIATEXTBUTTON class represents an encyclopedia button that does not necessarily render an image but instead(or in addition) renders text
*/
//TODO:Use flyweight pattern so that every button doesn't store a copy of the sprite, instead just a reference
public class EncyclopediaButton extends Button {
    private boolean playTurnSound;
    private int type;
    protected @Nullable Identifier buttonImage ;
    public EncyclopediaButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration, int type, Identifier loc) {
        this(x, y, width, height, message, onPress, createNarration, true, type, loc);
    }
    public EncyclopediaButton(int x, int y, int width, int height, Component message, OnPress onPress, int type, Identifier loc) {
        this(x, y, width, height, message, onPress, DEFAULT_NARRATION, true, type, loc);
    }

    public EncyclopediaButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration, boolean playTurnSound, int type, Identifier loc) {
        super(x, y, width, height, message, onPress, createNarration);
        this.playTurnSound = playTurnSound;
        this.type = type;
        buttonImage = loc;
    }
    public EncyclopediaButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration, int type) {
        this(x, y, width, height, message, onPress, createNarration, true, type);
    }
    public EncyclopediaButton(int x, int y, int width, int height, Component message, OnPress onPress, int type) {
        this(x, y, width, height, message, onPress, DEFAULT_NARRATION, true, type);
    }
    public EncyclopediaButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration, boolean playTurnSound, int type) {
        super(x, y, width, height, message, onPress, createNarration);
        this.playTurnSound = playTurnSound;
        this.type = type;

    }
    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int i, int j, float partialTicks) {
        int k = isHovered ? 1 : 2;
        //graphics.pose().pushMatrix();
        graphics.blit(RenderPipelines.GUI_TEXTURED, buttonImage, getX(), getY(), type * 22 + 125 + (k - 1) * 11, 234,
                width, height, 256, 256);
        //graphics.pose().popMatrix();
    }


    public void playDownSound(SoundManager soundManager) {
        if (playTurnSound) {
            soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }
    public boolean shouldTakeFocusAfterInteraction() { return false;}
}
