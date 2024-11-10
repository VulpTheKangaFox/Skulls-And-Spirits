package com.vulp.skullsandspirits.fluid;

import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.item.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Optional;

// We don't actually use this class and I don't know if we ever will.
public abstract class BloodFluid extends FlowingFluid {

    @Override
    public FluidType getFluidType() {
        return FluidTypeRegistry.BLOOD_FLUID_TYPE.get();
    }

    @Override
    public Fluid getFlowing() {
        return FluidRegistry.FLOWING_BLOOD.get();
    }

    @Override
    public Fluid getSource() {
        return FluidRegistry.BLOOD.get();
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockentity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockentity);
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return 3;
    }

    @Override
    protected int getDropOff(LevelReader level) {
        return 1;
    }

    @Override
    public Item getBucket() {
        return ItemRegistry.BLOOD_BUCKET.get();
    }

    @Override
    public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
        return fluidState.getHeight(blockReader, pos) >= 0.44444445F && fluid.is(FluidTags.WATER);
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 20;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return BlockRegistry.BLOOD.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (direction == Direction.DOWN) {
            FluidState fluidstate = level.getFluidState(pos);
            if (this.is(FluidTags.LAVA) && fluidstate.is(FluidTags.WATER)) {
                if (blockState.getBlock() instanceof LiquidBlock) {
                    level.setBlock(pos, net.neoforged.neoforge.event.EventHooks.fireFluidPlaceBlockEvent(level, pos, pos, Blocks.STONE.defaultBlockState()), 3);
                }
                return;
            }
        }
        super.spreadTo(level, pos, blockState, direction, fluidState);
    }


    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public static class Flowing extends BloodFluid {

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }

    }

    public static class Source extends BloodFluid {

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

    }

}
