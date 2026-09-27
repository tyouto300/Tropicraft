package net.tropicraft.core.client.encyclopedia;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tropicraft.Tropicraft;

import java.util.List;

public interface Page {
    String getId();
    void drawIcon(GuiGraphicsExtractor graphics, int i, int j, float partialTicks);
    void drawHeader(GuiGraphicsExtractor graphics, int x, int y, float mouseX, float mouseY, float partialTicks);
    int getHeaderHeight();
    default String getTitle() { return Tropicraft.ID + ".encyclopedia." + getId() + ".title";}
    default String getLocalizedTitle() { return Component.translatable(getTitle()).toString(); }
    default String getDescription() { return Tropicraft.ID + ".encyclopedia." + getId() + ".desc"; }
    default String getLocalizedDescription() { return Component.translatable(getDescription()).toString(); }
    default boolean isBookmark() {
        return false;
    }
    default boolean hasContent() {
        return !isBookmark();
    }
    default boolean hasIcon() {
        return !isBookmark();
    }
    List<RecipeEntry> getRelevantRecipes();
    boolean discover(Level world, Player player);
}
