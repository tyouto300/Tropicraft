package net.tropicraft.core.client.encyclopedia;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class EncyclopediaIndexButton extends EncyclopediaTextButton{
    private final Page page;
    //use flyweight pattern
    private final Identifier buttonOutlineLoc;
    private static final int backgroundColor = -2006555033;
    public EncyclopediaIndexButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, int type, int textColor, String title, Page page, Identifier loc) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION, type, textColor, Component.literal(title));
        if (page.isBookmark()) {
            this.title = this.title.copy().withStyle(ChatFormatting.UNDERLINE);
        }
        buttonOutlineLoc = loc;
        this.page = page;
    }
    public Page getPage() { return page;}
    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int i, int j, float partialTicks) {

        if (page.isBookmark()) {
            super.extractContents(graphics, -1000, -1000, partialTicks);
        } else {
            super.extractContents(graphics, i, j, partialTicks);
        }
        if (visible && page.hasIcon()) {
            graphics.pose().pushMatrix();
            graphics.blit(RenderPipelines.GUI_TEXTURED, buttonOutlineLoc, getX() - 20, getY() - 3, 3, 190,
                    18, 18, 256, 256);
            graphics.fill(getX() - 19 , getY() - 2, getX() - 3 , getY() + 14, backgroundColor);
            page.drawIcon(graphics, getX() - 19, getY() - 2, partialTicks);
            graphics.pose().popMatrix();
        }
    }
}
