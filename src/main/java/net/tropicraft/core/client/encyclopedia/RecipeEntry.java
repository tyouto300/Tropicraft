package net.tropicraft.core.client.encyclopedia;

import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NonNull;

public class RecipeEntry {
    private final int width;
    private final int height;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;

    public RecipeEntry(int width, int height, NonNullList<Ingredient> items, ItemStack output) {
        this.width = width;
        this.height = height;
        this.ingredients = items;
        while (this.ingredients.size() < width * height) {
            this.ingredients.add(Ingredient.of(Items.AIR));
        }

        this.output = output;
    }

    @NonNull
    public ItemStack getCycledStack(int index, float cycle) {
        if (index >= ingredients.size() || index < 0) {
            return ItemStack.EMPTY;
        }
        Ingredient ing = ingredients.get(index);
        ing.items();
        int i = Mth.floor(cycle / 30);

        HolderSet<Item> stacks = ing.getValues() ;
        if (i < 0 || stacks.size() == 0) {
            return ItemStack.EMPTY;
        }
        ItemStack ret = new ItemStack(stacks.get(i % stacks.size()));
        if (ret == null) {//shouldnt happen supposedly
            ret = ItemStack.EMPTY;
        }
        return ret;
    }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public NonNullList<Ingredient> getIngredients() { return ingredients; }
    public ItemStack getOutput() { return output; }

}
