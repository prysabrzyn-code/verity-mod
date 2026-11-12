package com.example.verity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** A single-use artifact that grants two effects for 30 seconds. */
public class ArtifactItem extends Item {

    private static final int EFFECT_TICKS = 600;
    private static final int COOLDOWN_TICKS = 1200;

    private final MobEffect first;
    private final MobEffect second;

    public ArtifactItem(Properties properties, MobEffect first, MobEffect second) {
        super(properties.stacksTo(16));
        this.first = first;
        this.second = second;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        player.addEffect(new MobEffectInstance(first, EFFECT_TICKS, 0));
        player.addEffect(new MobEffectInstance(second, EFFECT_TICKS, 0));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.success(stack);
    }
}
