package net.tropicraft.core.client.encyclopedia;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;


public class EncyclopediaBookmarkButton extends EncyclopediaTextButton{
    private final int targetPage;
    private final Page section;
    public EncyclopediaBookmarkButton(Page section, int target, int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, -1, GuiTropicalBook.COLOR_READ, section.getTitle());
        targetPage = target;
        this.section = section;
    }
    public int getTarget() { return targetPage; }
    public Page getPage() { return section; }
    /*
    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int i, int j, float partialTicks) {
        LogUtils.getLogger().error("Extracting Contents from Bookmark button");
        graphics.text(Minecraft.getInstance().font, "AHAHAHAHHAHAHAHHA", getX(), getY(), GuiTropicalBook.COLOR_READ);
        //super.extractContents(graphics, i, j, partialTicks);
    }

     */
}
