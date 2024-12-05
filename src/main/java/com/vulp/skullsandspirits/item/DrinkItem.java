package com.vulp.skullsandspirits.item;

import com.mojang.blaze3d.platform.InputConstants;
import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.component.DataComponentRegistry;
import com.vulp.skullsandspirits.effect.EffectRegistry;
import com.vulp.skullsandspirits.util.DrinkTier;
import com.vulp.skullsandspirits.util.SASUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
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

	public static DrinkTier getTier(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.DRINK_TIER.get(), DrinkTier.D);
	}

    public static void setTier(ItemStack stack, DrinkTier tier) {
        stack.set(DataComponentRegistry.DRINK_TIER.get(), tier);
    }

    public static void upgrade(ItemStack stack) {
        setTier(stack, DrinkTier.values()[Math.min(getTier(stack).ordinal() + 1, DrinkTier.values().length - 1)]);
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

        DrinkTier currentTier = getTier(stack);

        // Get the next tier, cycling back to the first tier if at the end
        DrinkTier[] tiers = DrinkTier.values();
        int nextIndex = (currentTier.ordinal() + 1) % tiers.length;
        DrinkTier nextTier = tiers[nextIndex];

        // Set the new tier back onto the stack
        setTier(stack, nextTier);

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
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        DrinkTier currentTier = getTier(stack);

        // Get the next tier, cycling back to the first tier if at the end
        DrinkTier[] tiers = DrinkTier.values();
        int nextIndex = (currentTier.ordinal() + 1) % tiers.length;
        DrinkTier nextTier = tiers[nextIndex];

        // Set the new tier back onto the stack
        setTier(stack, nextTier);

        // Return success
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getName(ItemStack stack) {
        DrinkTier tier = getTier(stack);
        Component tierPrefix = Component.translatable("item.skullsandspirits.drink_tier." + tier.getGrade()).withStyle(ChatFormatting.BOLD, getTierColor(tier, true));
        return Component.literal("")
                .append(tierPrefix)
                .append(" ")
                .append(super.getName(stack));
    }

    public static ChatFormatting getTierColor(ItemStack stack, boolean bright) {
        return getTierColor(getTier(stack), bright);
    }

    public static ChatFormatting getTierColor(DrinkTier tier, boolean bright) {
        return switch (tier) {
            case C -> bright ? ChatFormatting.GREEN : ChatFormatting.DARK_GREEN;
            case B -> ChatFormatting.BLUE;
            case A -> bright ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.DARK_PURPLE;
            case S -> ChatFormatting.GOLD;
            default -> bright ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY;
        };
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

                // Used sine easing to make the transition less sharp near the ends. I think it only applied to one end but it looks fine.
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
