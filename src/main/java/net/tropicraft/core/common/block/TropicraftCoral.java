package net.tropicraft.core.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Supplier;

public enum TropicraftCoral implements Supplier<Block> {
    PINK_CORAL(10, BlockTags.CORAL_PLANTS), TEALY_CORAL(12, BlockTags.CORAL_PLANTS),
    BRAIN_CORAL(15, BlockTags.CORAL_PLANTS), FIRE_CORAL(13, 7, BlockTags.CORAL_PLANTS),
    GREEN_CORAL(13, BlockTags.CORAL_PLANTS), SPIRAL_CORAL(15, BlockTags.CORAL_PLANTS),
    HOT_PINK_CORAL(13, BlockTags.CORAL_PLANTS);
    private final VoxelShape shape;
    private final TagKey<Block>[] tags;
    @SafeVarargs
    TropicraftCoral(int w, int h, TagKey<Block>... tags) {
        this(null, w, h, tags);
    }
    @SafeVarargs
    TropicraftCoral(int w, TagKey<Block>... tags) {
        this(null, w, 15, tags);
    }
    @SafeVarargs
    TropicraftCoral(@Nullable String name, int w, int h, TagKey<Block>... tags) {
        float halfW = w / 2.0F;
        this.tags = tags;
        shape = Block.box(8 - halfW, 0 , 8 - halfW, 8 + halfW, h, 8 + halfW);
    }
    public VoxelShape getShape() { return shape; }
    @Override
    public Block get() {
        return TropicraftBlocks.CORALS.get(this).get();
    }

    public String getId() {
        return name().toLowerCase(Locale.ROOT); }

    public TagKey<Block>[] getTags() { return tags; }
}
