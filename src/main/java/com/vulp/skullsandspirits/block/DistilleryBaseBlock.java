package com.vulp.skullsandspirits.block;

import com.vulp.skullsandspirits.block.blockentity.BlockEntityRegistry;
import com.vulp.skullsandspirits.block.blockentity.DistilleryBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class DistilleryBaseBlock extends DistilleryTowerBlock {

	// TODO: Block entity is done but we now need to worry about blockstate and logic. Should be easy enough. Think about JEI support.
	public DistilleryBaseBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DistilleryBaseBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return type == BlockEntityRegistry.DISTILLERY_BASE.get() ? DistilleryBaseBlockEntity::tick : null;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockentity = level.getBlockEntity(pos);
			if (blockentity instanceof DistilleryBaseBlockEntity distilleryBaseBlockEntity) {
				player.openMenu(distilleryBaseBlockEntity);
				// player.awardStat(Stats.INTERACT_WITH_DISTILLERY);
			}

			return InteractionResult.CONSUME;
		}
	}

}
