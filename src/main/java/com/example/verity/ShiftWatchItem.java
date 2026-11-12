package com.example.verity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/** Right-click cycles the mode; Shift + right-click activates its effects for 60 seconds. */
public class ShiftWatchItem extends Item {

    private static final String[] MODES = {"Ember", "Frost", "Stone"};
    private static final int EFFECT_TICKS = 1200;
    private static final int COOLDOWN_TICKS = 1200;

    public ShiftWatchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        int mode = getMode(stack);
        if (player.isShiftKeyDown()) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.fail(stack);
            }
            applyMode(player, mode);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        } else {
            int next = (mode + 1) % MODES.length;
            setMode(stack, next);
            player.displayClientMessage(Component.literal("Mode: " + MODES[next]), true);
        }
        return InteractionResultHolder.success(stack);
    }

    private static int getMode(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt("Mode") % MODES.length : 0;
    }

    private static void setMode(ItemStack stack, int mode) {
        stack.getOrCreateTag().putInt("Mode", mode);
    }

    private static void applyMode(Player player, int mode) {
        switch (mode) {
            case 0 -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, EFFECT_TICKS, 0));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, EFFECT_TICKS, 0));
            }
            case 1 -> {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, EFFECT_TICKS, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_TICKS, 0));
            }
            default -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_TICKS, 1));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, EFFECT_TICKS, 0));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Mode: " + MODES[getMode(stack)]));
        tooltip.add(Component.literal("Right-click: change mode"));
        tooltip.add(Component.literal("Shift + right-click: activate (60s, 60s cooldown)"));
    }
}
