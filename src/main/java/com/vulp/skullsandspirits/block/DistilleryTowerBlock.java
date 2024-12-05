package com.vulp.skullsandspirits.block;

import com.mojang.serialization.MapCodec;
import com.vulp.skullsandspirits.block.blockentity.DistilleryBaseBlockEntity;
import com.vulp.skullsandspirits.util.DrinkTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;


// TODO: Best option is to use an enum property as a blockstate option. It will only really ever account for looks and logic anyway. Then we code the main block separately.
public class DistilleryTowerBlock extends HorizontalDirectionalBlock implements EntityBlock {

	public static final MapCodec<DistilleryTowerBlock> CODEC = simpleCodec(DistilleryTowerBlock::new
			/*RecordCodecBuilder.mapCodec(builder -> builder.group(DrinkTier.CODEC.fieldOf("tier").forGetter(DistilleryTowerBlock::getTier), propertiesCodec()).apply(builder, DistilleryTowerBlock::new)*/
	);

	public static final EnumProperty<DrinkTier> TIER = EnumProperty.create("tier", DrinkTier.class, tier -> tier != DrinkTier.S);

	public DistilleryTowerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(TIER, DrinkTier.D));
	}

	@Override
	protected MapCodec<DistilleryTowerBlock> codec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DistilleryBaseBlockEntity(pos, state); // TODO Change to tower.
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(TIER));
	}

}
