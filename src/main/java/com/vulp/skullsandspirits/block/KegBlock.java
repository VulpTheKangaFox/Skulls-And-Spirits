package com.vulp.skullsandspirits.block;

import com.vulp.skullsandspirits.block.blockentity.BlockEntityRegistry;
import com.vulp.skullsandspirits.block.blockentity.KegBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class KegBlock extends Block implements EntityBlock {

    public static final BooleanProperty STACKED = BooleanProperty.create("stacked");
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape KEG_SHAPE = Stream.of(
            Block.box(0, 0, 2, 4, 4, 4),
            Block.box(0, 0, 12, 4, 4, 14),
            Block.box(12, 0, 2, 16, 4, 4),
            Block.box(12, 0, 12, 16, 4, 14),
            Block.box(4, 1, 2, 12, 2, 4),
            Block.box(4, 1, 12, 12, 2, 14),
            Block.box(1, 2, 0, 15, 16, 16))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape KEG_SHAPE_90 = Stream.of(
                    Block.box(2, 0, 0, 4, 4, 4),
                    Block.box(12, 0, 0, 14, 4, 4),
                    Block.box(2, 0, 12, 4, 4, 16),
                    Block.box(12, 0, 12, 14, 4, 16),
                    Block.box(2, 1, 4, 4, 2, 12),
                    Block.box(12, 1, 4, 14, 2, 12),
                    Block.box(0, 2, 1, 16, 16, 15))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape KEG_SHAPE_STACKED = Stream.of(
            Block.box(0, 0, 2, 16, 5, 4),
            Block.box(0, 0, 12, 16, 5, 14),
            Block.box(1, 2, 0, 15, 16, 16))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape KEG_SHAPE_STACKED_90 = Stream.of(
                    Block.box(2, 0, 0, 4, 5, 16),
                    Block.box(12, 0, 0, 14, 5, 16),
                    Block.box(0, 2, 1, 16, 16, 15))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape KEG_SHAPE_EXTRA = Shapes.join(Block.box(0, 13, 12, 16, 16, 14), Block.box(0, 13, 2, 16, 16, 4), BooleanOp.OR);
    private static final VoxelShape KEG_SHAPE_EXTRA_90 = Shapes.join(Block.box(12, 13, 0, 14, 16, 16), Block.box(2, 13, 0, 4, 16, 16), BooleanOp.OR);
    private static final VoxelShape KEG_SHAPE_WITH_EXTRA = Shapes.join(KEG_SHAPE, KEG_SHAPE_EXTRA, BooleanOp.OR);
    private static final VoxelShape KEG_SHAPE_WITH_EXTRA_90 = Shapes.join(KEG_SHAPE_90, KEG_SHAPE_EXTRA_90, BooleanOp.OR);
    private static final VoxelShape KEG_SHAPE_STACKED_WITH_EXTRA = Shapes.join(KEG_SHAPE_STACKED, KEG_SHAPE_EXTRA, BooleanOp.OR);
    private static final VoxelShape KEG_SHAPE_STACKED_WITH_EXTRA_90 = Shapes.join(KEG_SHAPE_STACKED_90, KEG_SHAPE_EXTRA_90, BooleanOp.OR);


    public KegBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(STACKED, false).setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean stacked = state.getValue(STACKED);
        BlockState aboveState = level.getBlockState(pos.above());
        boolean hasExtra = aboveState.getBlock() == this && aboveState.getValue(HORIZONTAL_FACING).getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
        if (state.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.X) {
            return stacked
                    ? (hasExtra ? KEG_SHAPE_STACKED_WITH_EXTRA_90 : KEG_SHAPE_STACKED_90)
                    : (hasExtra ? KEG_SHAPE_WITH_EXTRA_90 : KEG_SHAPE_90);
        } else {
            return stacked
                    ? (hasExtra ? KEG_SHAPE_STACKED_WITH_EXTRA : KEG_SHAPE_STACKED)
                    : (hasExtra ? KEG_SHAPE_WITH_EXTRA : KEG_SHAPE);
        }
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new KegBlockEntity(blockPos, blockState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == BlockEntityRegistry.SHODDY_KEG.get() ? KegBlockEntity::tick : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof KegBlockEntity kegBlockEntity) {
                player.openMenu(kegBlockEntity);
                // player.awardStat(Stats.INTERACT_WITH_KEG);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof KegBlockEntity kegBlockEntity) {
                if (level instanceof ServerLevel) {
                    Containers.dropContents(level, pos, kegBlockEntity);
                }
                super.onRemove(state, level, pos, newState, isMoving);
                level.updateNeighbourForOutputSignal(pos, this);
            } else {
                super.onRemove(state, level, pos, newState, isMoving);
            }
        }
    }


    private boolean shouldStack(LevelAccessor level, BlockPos pos, BlockState currentState) {
        BlockState belowState = level.getBlockState(pos.below());
        return belowState.getBlock() == this && currentState.getValue(HORIZONTAL_FACING).getAxis() == belowState.getValue(HORIZONTAL_FACING).getAxis();
    }

    private boolean shouldStack(LevelAccessor level, BlockPos pos, Direction currentDir) {
        BlockState belowState = level.getBlockState(pos.below());
        return belowState.getBlock() == this && currentDir.getAxis() == belowState.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(state.setValue(STACKED, shouldStack(level, pos, state)), direction, neighborState, level, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        if (stateForPlacement != null) {
            return stateForPlacement.setValue(STACKED, shouldStack(context.getLevel(), context.getClickedPos(), context.getHorizontalDirection())).setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
        } else {
            return null;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(STACKED, HORIZONTAL_FACING));
    }
}
