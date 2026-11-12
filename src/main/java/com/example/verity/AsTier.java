package com.example.verity;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public record AsTier(int uses, float damage, int enchantValue) implements Tier {
    @Override public int getUses() { return uses; }
    @Override public float getSpeed() { return 6.0F; }
    @Override public float getAttackDamageBonus() { return damage; }
    @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_IRON_TOOL; }
    @Override public int getEnchantmentValue() { return enchantValue; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
}
