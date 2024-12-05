package com.vulp.skullsandspirits.component;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.util.DrinkTier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class DataComponentRegistry {

	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SkullsAndSpirits.MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<DrinkTier>> DRINK_TIER = register("drink_tier", builder -> builder.persistent(DrinkTier.CODEC).networkSynchronized(DrinkTier.STREAM_CODEC));

	private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
		return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
	}

}
