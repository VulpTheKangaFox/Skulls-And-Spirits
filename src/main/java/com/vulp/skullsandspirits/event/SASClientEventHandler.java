package com.vulp.skullsandspirits.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.vulp.skullsandspirits.effect.MulesMightEffect;
import com.vulp.skullsandspirits.fluid.FluidRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
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
		if (eyesInFluid(FluidRegistry.BLOOD.get(), FluidRegistry.FLOWING_BLOOD.get(), event.getPartialTick())) {
			event.setBlue(0F);
			event.setGreen(0.02F);
			event.setRed(0.3F);
		}
	}

	@SubscribeEvent
	public void onRenderFog(ViewportEvent.RenderFog event) {
		if (eyesInFluid(FluidRegistry.BLOOD.get(), FluidRegistry.FLOWING_BLOOD.get(), event.getPartialTick())) {
			RenderSystem.setShaderFogStart(-5F);
			RenderSystem.setShaderFogEnd(3F);
		}
	}

	private static boolean eyesInFluid(FlowingFluid fluid, FlowingFluid flowingFluid, double partialTick) {
		Entity entity = Minecraft.getInstance().cameraEntity;
		if (entity == null) {
			return false;
		}

		Level level = entity.level();
		Vec3 cameraPosition = entity.getEyePosition((float) partialTick).add(0.0F, -0.0625F, 0.0F);;
		BlockPos cameraBlockPos = BlockPos.containing(cameraPosition);

		FluidState fluidState = level.getFluidState(cameraBlockPos);
		if (fluidState.is(fluid) &&
				cameraPosition.y <= cameraBlockPos.getY() + fluidState.getHeight(level, cameraBlockPos)) {
			return true;
		}

		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		Camera.NearPlane nearPlane = camera.getNearPlane();
		Vec3[] nearPlanePoints = new Vec3[] {
				nearPlane.getTopLeft(),
				nearPlane.getTopRight(),
				nearPlane.getBottomLeft(),
				nearPlane.getBottomRight()
		};

		for (Vec3 point : nearPlanePoints) {
			Vec3 planePosition = cameraPosition.add(point);
			BlockPos blockPos = BlockPos.containing(planePosition);
			FluidState planeFluidState = level.getFluidState(blockPos);

			if (planeFluidState.is(flowingFluid) &&
					planePosition.y <= blockPos.getY() + planeFluidState.getHeight(level, blockPos)) {
				return true;
			}
		}

		return false;
	}

}
