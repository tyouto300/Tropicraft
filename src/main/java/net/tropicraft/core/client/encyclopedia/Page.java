package net.tropicraft.core.client.encyclopedia;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tropicraft.Tropicraft;
import net.tropicraft.core.client.data.TropicraftEncyclopediaLangKeys;

import java.util.List;
import java.util.Locale;

public interface Page {
    String getId();
    void drawIcon(GuiGraphicsExtractor graphics, int i, int j, float partialTicks);
    void drawHeader(GuiGraphicsExtractor graphics, int x, int y, float mouseX, float mouseY, float partialTicks);
    int getHeaderHeight();

    default Component getTitle() {return TropicraftEncyclopediaLangKeys.get(getId().toUpperCase(Locale.ROOT)).getTitle(); };

    default Component getDescription() {return TropicraftEncyclopediaLangKeys.valueOf(getId().toUpperCase(Locale.ROOT)).getDesc();};
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
