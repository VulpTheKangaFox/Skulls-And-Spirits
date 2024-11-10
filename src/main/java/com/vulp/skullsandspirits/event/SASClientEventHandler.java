package com.vulp.skullsandspirits.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vulp.skullsandspirits.fluid.FluidTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class SASClientEventHandler {

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
