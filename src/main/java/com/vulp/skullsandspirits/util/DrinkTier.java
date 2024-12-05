package com.vulp.skullsandspirits.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;

import java.util.function.IntFunction;

public enum DrinkTier implements StringRepresentable {

	D("d", 0, "Crude"),
	C("c", 1, "Common"),
	B("b", 2, "Refined"),
	A("a", 3, "Exceptional"),
	S("s", 4, "Flawless");

	private static final IntFunction<DrinkTier> BY_TIER = ByIdMap.continuous(DrinkTier::getTier, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StringRepresentable.EnumCodec<DrinkTier> CODEC = StringRepresentable.fromEnum(DrinkTier::values);
	public static final StreamCodec<ByteBuf, DrinkTier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_TIER, DrinkTier::getTier);

	private final String name;
	private final int tier;
	private final String prefix;

	DrinkTier(String name, int tier, String prefix) {
		this.name = name;
		this.tier = tier;
		this.prefix = prefix;
	}

	public int getTier() {
		return this.tier;
	}

	public String getPrefix() {
		String localizationKey = "tier.skullsandspirits." + this.name.toLowerCase();
		return I18n.exists(localizationKey) ? I18n.get(localizationKey) : localizationKey;
	}

	public String getGrade() {
		return this.name.toLowerCase();
	}

	@Override
	public String toString() {
		return name() + " (" + this.prefix + ")";
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public static DrinkTier fromInt(int value) {
		return BY_TIER.apply(value);
	}

}