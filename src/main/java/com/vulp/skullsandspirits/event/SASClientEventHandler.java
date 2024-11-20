package com.vulp.skullsandspirits.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.vulp.skullsandspirits.effect.MulesMightEffect;
import com.vulp.skullsandspirits.fluid.FluidTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class SASClientEventHandler {

	@SubscribeEvent
	public void onKeyInputEvent(InputEvent.Key event) {
		if (event.getKey() == Minecraft.getInstance().options.keyJump.getKey().getValue() && event.getAction() == InputConstants.PRESS) {
			Player player = Minecraft.getInstance().player;
			if (player != null && MulesMightEffect.canDoubleJump(player)) {
				MulesMightEffect.doDoubleJump(player);
			}
		}
	}

	@SubscribeEvent
	public void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
		if (eyesInBlood()) {
			event.setBlue(0F);
			event.setGreen(0.02F);
			event.setRed(0.3F);
		}
	}

	@SubscribeEvent
	public void onRenderFog(ViewportEvent.RenderFog event) {
		if (eyesInBlood()) {
			RenderSystem.setShaderFogStart(-5F);
			RenderSystem.setShaderFogEnd(3F);
		}
	}

	private static boolean eyesInBlood() {
		Entity entity = Minecraft.getInstance().cameraEntity;
		return entity != null && entity.isEyeInFluidType(FluidTypeRegistry.BLOOD_FLUID_TYPE.get());
	}

}
