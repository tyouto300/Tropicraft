package net.tropicraft.core.client.encyclopedia;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class SimplePage implements Page{
    private final String id;
    public SimplePage(String id) { this.id = id;}
    @Override
    public String getId() { return id; }
    @Override
    public void drawHeader(GuiGraphicsExtractor graphics, int x, int y, float mouseX, float mouseY, float cycle) {}
    @Override
    public void drawIcon(GuiGraphicsExtractor graphics, int x, int y, float cycle) {}

    @Override
    public int getHeaderHeight() { return 0; }
    @Override
    public List<RecipeEntry> getRelevantRecipes() { return Collections.emptyList(); }
    @Override
    public boolean discover(Level world, Player player) { return false; }
    @Override
    public String toString() { return id; }
}
