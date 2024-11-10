package com.vulp.skullsandspirits.block;

import com.vulp.skullsandspirits.block.blockentity.BlockEntityRegistry;
import com.vulp.skullsandspirits.block.blockentity.DrainingBasinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class DrainingBasinBlock extends Block implements EntityBlock {

    private static final VoxelShape SHAPE_VISUAL = Stream.of(
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 2, 0, 2, 12, 16),
            Block.box(14, 2, 0, 16, 12, 16),
            Block.box(2, 2, 0, 14, 12, 2),
            Block.box(2, 2, 14, 14, 12, 16))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SHAPE_PHYSICAL = Stream.of(
            Block.box(0, 0, 0, 16, 9, 16),
            Block.box(0, 9, 0, 2, 12, 16),
            Block.box(14, 9, 0, 16, 12, 16),
            Block.box(2, 9, 0, 14, 12, 2),
            Block.box(2, 9, 14, 14, 12, 16))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public DrainingBasinBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DrainingBasinBlockEntity(blockPos, blockState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == BlockEntityRegistry.DRAINING_BASIN.get() ? DrainingBasinBlockEntity::tick : null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_VISUAL;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_PHYSICAL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof DrainingBasinBlockEntity drainingBasinBlockEntity) {
                player.openMenu(drainingBasinBlockEntity);
                // player.awardStat(Stats.INTERACT_WITH_DRAINING_BASIN);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof DrainingBasinBlockEntity drainingBasinBlockEntity) {
                if (level instanceof ServerLevel) {
                    Containers.dropContents(level, pos, drainingBasinBlockEntity);
                }
                super.onRemove(state, level, pos, newState, isMoving);
                level.updateNeighbourForOutputSignal(pos, this);
            } else {
                super.onRemove(state, level, pos, newState, isMoving);
            }
        }
    }

}
