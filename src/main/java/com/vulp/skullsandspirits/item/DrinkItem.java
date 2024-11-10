package com.vulp.skullsandspirits.item;

import com.mojang.blaze3d.platform.InputConstants;
import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.effect.EffectRegistry;
import com.vulp.skullsandspirits.util.SASUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class DrinkItem extends Item {

    private final int nutrition;
    private final int saturation;
    private final ItemLike vesselItem;
    private final MobEffectInstance[] effects;
    private final FoodProperties foodProperties;
    private final int loreColor;
    private final int infoColor;

    public DrinkItem(int nutrition, int saturation, Supplier<ItemLike> vesselItem, MobEffectInstance[] effects, int[] descriptionColors, Properties properties) {
        super(properties.stacksTo(8));
        this.nutrition = nutrition;
        this.saturation = saturation;
        this.vesselItem = vesselItem.get();
        this.effects = effects;
        this.foodProperties = buildFoodProperties();
        this.loreColor = descriptionColors[0];
        this.infoColor = descriptionColors[1];
    }

    private FoodProperties buildFoodProperties() {
        FoodProperties.Builder foodBuilder = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).alwaysEdible().usingConvertsTo(vesselItem).effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 140), 1.0F);
        for (MobEffectInstance effect : effects) {
            foodBuilder.effect(() -> effect, 1.0F);
        }
        return foodBuilder.build();
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        return this.foodProperties;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 40;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }
        if (!level.isClientSide) {
            int amp = 0;
            MobEffectInstance sickness = entity.getEffect(EffectRegistry.BREW_SICKNESS);
            if (sickness != null) {
                amp = sickness.getAmplifier() + 1;
            }
            entity.addEffect(new MobEffectInstance(EffectRegistry.BREW_SICKNESS, 6000, amp, true, true));
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LSHIFT)) {
            String time = SASUtils.ticksToFormattedTimeCompact(this.effects[0].getDuration(), ",");
            tooltipComponents.add(Component.translatable(stack.getItem().getDescriptionId() + ".info", time)
                    .withStyle(Style.EMPTY.withBold(true).withColor(this.infoColor)));
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(Component.translatable(stack.getItem().getDescriptionId() + ".lore")
                    .withStyle(Style.EMPTY.withItalic(true).withColor(this.loreColor)));
        } else {
            ClientLevel level = Minecraft.getInstance().level;
            int animatedColor = 0;
            if (level != null) {
                long ticks = level.getGameTime() % 20;
                float progress = (ticks / 20.0F) * 2;
                if (progress > 1) {
                    progress = 2 - progress;
                }

                // Used sine easing to make the transition less sharp near the ends. I think it only applied to one end but I'm unsure and it looks fine.
                progress = (float) Math.sin(progress * Math.PI / 2);
                animatedColor = getGradientColor(this.loreColor, this.infoColor, progress);
            }

            tooltipComponents.add(Component.translatable("item." + SkullsAndSpirits.MODID + ".hold_shift.pre")
                    .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.BOLD)
                    .append(Component.translatable("item." + SkullsAndSpirits.MODID + ".hold_shift.key")
                            .withStyle(Style.EMPTY.withColor(animatedColor).withBold(true)))
                    .append(Component.translatable("item." + SkullsAndSpirits.MODID + ".hold_shift.post")
                            .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.BOLD)));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public int getGradientColor(int color1, int color2, float progress) {
        int red = (int) ((1 - progress) * ((color1 >> 16) & 0xFF) + progress * ((color2 >> 16) & 0xFF));
        int green = (int) ((1 - progress) * ((color1 >> 8) & 0xFF) + progress * ((color2 >> 8) & 0xFF));
        int blue = (int) ((1 - progress) * (color1 & 0xFF) + progress * (color2 & 0xFF));
        return (red << 16) | (green << 8) | blue;
    }

}
