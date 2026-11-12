package com.example.verity;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public record AsTier(int uses, float damage, int enchantValue) implements Tier {
    @Override public int getUses() { return uses; }
    @Override public float getSpeed() { return 6.0F; }
    @Override public float getAttackDamageBonus() { return damage; }
    @Override public int getLevel() { return 2; }
    @Override public int getEnchantmentValue() { return enchantValue; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
}
