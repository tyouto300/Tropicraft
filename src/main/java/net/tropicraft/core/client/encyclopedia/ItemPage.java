package net.tropicraft.core.client.encyclopedia;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.tropicraft.Tropicraft;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemPage extends SimplePage {
    private final ItemStack stack;
    public ItemPage(String id, ItemStack stack) {
        super(id);
        this.stack = stack;
    }
    protected ItemStack getStack() { return stack; }
    //rendering
    public void extractIcon(GuiGraphicsExtractor graphics, int xo, int yo) {
        graphics.fakeItem(stack, xo, yo);
    }

    //CLIENT SIDE ONLY
    @Override
    public void drawIcon(GuiGraphicsExtractor graphics, int x, int y, float cycle) {

        graphics.item(stack, x, y);

    }

    @Override
    public List<RecipeEntry> getRelevantRecipes(/*ServerLevel serverLevel*/) {
        List<RecipeEntry> recipeList = new ArrayList<>();
        RecipeManager recipes = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
        /*Optional<RecipeHolder<?>> recipeKey = recipes.byKey(
                ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(TestingMod.MODID, getId()))
        );
         */
        //I'm not sure if this works
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Tropicraft.ID, getId()));
        List<ResourceKey<Recipe<?>>> recipeIDs = recipes.recipeMap().byType(RecipeType.CRAFTING).stream().map(RecipeHolder::id).toList();
        for (ResourceKey<Recipe<?>> recipe : recipeIDs) {
            if (recipeKey.equals(recipe) ) {
                recipeList.add(getFormattedRecipe((Recipe) recipe));
            }
        }
        return recipeList;
    }
    @Nullable
    public RecipeEntry getFormattedRecipe(Recipe recipe) {
        //Support other kinds of recipes
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shaped = (ShapedRecipe) recipe;
            int width = shaped.getWidth();
            int height = shaped.getHeight();
            //use optional api to turn into normal list
            NonNullList<Ingredient> items =   NonNullList.copyOf(
                    shaped.getIngredients().stream().flatMap(Optional::stream).toList() );
            //check if this actually works?
            ItemStack output = recipe.assemble(CraftingInput.of(1,1,List.of(new ItemStack(Items.AIR))));
            return new RecipeEntry(width, height, items, output);
        } else if( recipe instanceof ShapelessRecipe) {
            //return new RecipeEntry(3, 3, recipe.getIngredients(), recipe.getRecipeOutput());
        }
        return null;
    }

    @Override
    public boolean discover(Level level, Player player) {
        for( ItemStack is : player.getInventory()) {
            if(!is.isEmpty()) {
                ItemStack stack = getStack();
                if (ItemStack.isSameItem(is, stack) && //Verify DataComponents.CUSTOM_DATA is actually working
                        ( !stack.has(DataComponents.CUSTOM_DATA) || is.tags().equals(stack.tags())) ) {
                    return true;
                }
            }
        }
        return false;
    }
}
