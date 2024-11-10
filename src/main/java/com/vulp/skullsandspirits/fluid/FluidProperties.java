package com.vulp.skullsandspirits.fluid;

import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.item.ItemRegistry;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class FluidProperties {

	public static final BaseFlowingFluid.Properties BLOOD_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BLOOD_FLUID_TYPE, FluidRegistry.BLOOD, FluidRegistry.FLOWING_BLOOD)
			.slopeFindDistance(3)
			.levelDecreasePerBlock(2)
			.block(() -> (LiquidBlock) BlockRegistry.BLOOD.get())
			.bucket(ItemRegistry.BLOOD_BUCKET);
}
