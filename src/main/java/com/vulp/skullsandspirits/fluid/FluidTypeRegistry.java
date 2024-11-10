package com.vulp.skullsandspirits.fluid;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

// Here we register the fluid types and also register the fluid textures.
public class FluidTypeRegistry {

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, SkullsAndSpirits.MODID);

    public static final Supplier<FluidType> BLOOD_FLUID_TYPE = FLUID_TYPES.register("blood", () ->
            new FluidType(FluidType.Properties.create()
                    .density(15)
                    .viscosity(5)
                    .sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK)
            )
    );

    private static final ResourceLocation BLOOD_STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "block/blood_still");
    private static final ResourceLocation BLOOD_FLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "block/blood_flow");
    private static final ResourceLocation BLOOD_OVERLAY_TEXTURE = ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "textures/misc/blood_overlay.png");

    @SubscribeEvent
    public void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {

            @Override
            public ResourceLocation getStillTexture() {
                return BLOOD_STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return BLOOD_FLOW_TEXTURE;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return BLOOD_OVERLAY_TEXTURE;
            }
        }, BLOOD_FLUID_TYPE.get());
    }

}
