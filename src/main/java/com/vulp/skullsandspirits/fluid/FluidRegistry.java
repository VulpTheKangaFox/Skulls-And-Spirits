package com.vulp.skullsandspirits.fluid;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FluidRegistry {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, SkullsAndSpirits.MODID);

    public static final Supplier<FlowingFluid> FLOWING_BLOOD = FLUIDS.register("flowing_blood", () -> new BaseFlowingFluid.Flowing(FluidProperties.BLOOD_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> BLOOD = FLUIDS.register("blood", () -> new BaseFlowingFluid.Source(FluidProperties.BLOOD_FLUID_PROPERTIES));

}