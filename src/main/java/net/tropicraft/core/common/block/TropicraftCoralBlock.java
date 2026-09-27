package net.tropicraft.core.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class TropicraftCoralBlock extends VegetationBlock {
    private final VoxelShape shape;
    public TropicraftCoralBlock( VoxelShape shape, BlockBehaviour.Properties properties) {
        super(properties);
        this.shape = shape;
    }
    @Override
    protected MapCodec<? extends VegetationBlock> codec() {
        return null;
    }
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.is(BlockTags.SAND) || state.is(BlockTags.GRASS_BLOCKS) || state.is(BlockTags.CORAL_BLOCKS);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        BlockPos abovePos = pos.above();
        return level.getFluidState(pos).is(FluidTags.WATER) && level.getFluidState(abovePos).is(FluidTags.WATER) &&
                mayPlaceOn(belowState, level, pos);
    }
    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
                                     RandomSource random) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }
    @Override
    protected FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getSource(false);
    }
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(pos);
        return shape.move(offset.x, offset.y, offset.z);
    }
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }
}
