package com.example.verity;

import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Items ported from the A&S Bedrock add-on (stats only; special behaviours simplified). */
public final class AsItems {
    public static final List<RegistryObject<Item>> ALL = new ArrayList<>();

    private AsItems() {}

    public static void init() {}

    private static void reg(String name, Supplier<? extends Item> supplier) {
        ALL.add(VerityMod.ITEMS.register(name, supplier));
    }

    static {
        reg("cata_diamond_greatsword", () -> new SwordItem(new AsTier(3124, 8.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_exodius_greatsword", () -> new SwordItem(new AsTier(0, 15.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_iron_greatsword", () -> new SwordItem(new AsTier(502, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_nebulous_greatsword", () -> new SwordItem(new AsTier(5000, 15.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_netherite_greatsword", () -> new SwordItem(new AsTier(4062, 9.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_phantasma_greatsword", () -> new SwordItem(new AsTier(1200, 2.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_platinum_greatsword", () -> new SwordItem(new AsTier(890, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_stellar_greatsword", () -> new SwordItem(new AsTier(890, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_stone_greatsword", () -> new SwordItem(new AsTier(264, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_verdant_greatsword", () -> new SwordItem(new AsTier(1400, 12.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_wooden_greatsword", () -> new SwordItem(new AsTier(120, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_blazing_excalibur", () -> new SwordItem(new AsTier(5000, 12.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_bonebreaker", () -> new SwordItem(new AsTier(50, 9.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_divine_scythe_of_heaven", () -> new SwordItem(new AsTier(0, 25.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_everbloom", () -> new SwordItem(new AsTier(4500, 10.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_floret_hatchet", () -> new SwordItem(new AsTier(450, 8.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_moonlight", () -> new SwordItem(new AsTier(0, 15.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_phoenix_blade", () -> new SwordItem(new AsTier(8000, 14.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_platinum_halberd", () -> new SwordItem(new AsTier(4300, 8.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_stellar_mace", () -> new SwordItem(new AsTier(1400, 18.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_sunlight", () -> new SwordItem(new AsTier(0, 12.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_verdant_blade", () -> new SwordItem(new AsTier(3400, 9.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_verdant_hatchet", () -> new SwordItem(new AsTier(251, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("cata_verdant_sword", () -> new SwordItem(new AsTier(850, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dragonforge_mace", () -> new SwordItem(new AsTier(1500, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("nicothekid_dripstone_club", () -> new SwordItem(new AsTier(189, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("nicothekid_magma_club", () -> new SwordItem(new AsTier(189, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("nicothekid_piglin_war_hammer", () -> new SwordItem(new AsTier(256, 20.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("nicothekid_wooden_club", () -> new SwordItem(new AsTier(126, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("mythicus_hs_claw_bow", () -> new BowItem(new Item.Properties().stacksTo(1).durability(450)));
        reg("mythicus_hs_seismic_bow", () -> new BowItem(new Item.Properties().stacksTo(1).durability(250)));
        reg("mythicus_hs_straw_bow", () -> new BowItem(new Item.Properties().stacksTo(1).durability(250)));
        reg("mythicus_hs_vampire_hunter_bow", () -> new BowItem(new Item.Properties().stacksTo(1).durability(750)));
        reg("megaverse_carbon_steel_ball_grenade", () -> new Item(new Item.Properties().stacksTo(16)));
        reg("megaverse_dracon_bone_bow", () -> new BowItem(new Item.Properties().stacksTo(1).durability(390)));
        reg("megaverse_iron_ball_grenade", () -> new Item(new Item.Properties().stacksTo(16)));
        reg("megaverse_longbow", () -> new BowItem(new Item.Properties().stacksTo(1).durability(768)));
        reg("megaverse_pig_iron_ball_grenade", () -> new Item(new Item.Properties().stacksTo(16)));
        reg("megaverse_shortbow", () -> new BowItem(new Item.Properties().stacksTo(1).durability(192)));
        reg("megaverse_steel_ball_grenade", () -> new Item(new Item.Properties().stacksTo(16)));
        reg("dungeons_axe", () -> new SwordItem(new AsTier(289, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_claymore", () -> new SwordItem(new AsTier(555, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_double_axe", () -> new SwordItem(new AsTier(400, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_glaive", () -> new SwordItem(new AsTier(322, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_obsidian_claymore", () -> new SwordItem(new AsTier(1500, 9.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_soul_knife", () -> new SwordItem(new AsTier(412, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_soul_scythe", () -> new SwordItem(new AsTier(699, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_tempest_knife", () -> new SwordItem(new AsTier(206, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_whip", () -> new SwordItem(new AsTier(177, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_skull_scythe", () -> new SwordItem(new AsTier(1722, 8.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_firebrand", () -> new SwordItem(new AsTier(1622, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_highlands_axe", () -> new SwordItem(new AsTier(1622, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_great_axeblade", () -> new SwordItem(new AsTier(1861, 8.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_broadsword", () -> new SwordItem(new AsTier(1861, 10.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_cursed_axe", () -> new SwordItem(new AsTier(1739, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_whirlwind", () -> new SwordItem(new AsTier(1739, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_grave_bane", () -> new SwordItem(new AsTier(1599, 8.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_venom_glaive", () -> new SwordItem(new AsTier(1599, 8.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_starless_night", () -> new SwordItem(new AsTier(3500, 17.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_frost_scythe", () -> new SwordItem(new AsTier(1722, 8.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_jailors_scythe", () -> new SwordItem(new AsTier(1722, 8.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_vine_whip", () -> new SwordItem(new AsTier(1239, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dungeons_the_beginning_and_the_end", () -> new SwordItem(new AsTier(2023, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("edx_copper_spear_i", () -> new SwordItem(new AsTier(400, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("edx_crimson_netherite_spear_i", () -> new SwordItem(new AsTier(5000, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("edx_crimson_nickel_spear_i", () -> new SwordItem(new AsTier(3200, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("edx_iron_spear_i", () -> new SwordItem(new AsTier(700, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("edx_platinium_spear_i", () -> new SwordItem(new AsTier(2200, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("edx_tin_spear_i", () -> new SwordItem(new AsTier(500, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("edx_warped_netherite_spear_i", () -> new SwordItem(new AsTier(5000, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("edx_warped_nickel_spear_i", () -> new SwordItem(new AsTier(3200, 5.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dk_elemental_blade", () -> new SwordItem(new AsTier(1024, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dk_halberd", () -> new SwordItem(new AsTier(1000, 20.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dk_holy_sword", () -> new SwordItem(new AsTier(0, 7.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dk_sentinel_bow", () -> new BowItem(new Item.Properties().stacksTo(1)));
        reg("dk_sentinel_halberd", () -> new SwordItem(new AsTier(25000, 17.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dk_sickle_of_fatality", () -> new SwordItem(new AsTier(0, 14.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dk_sword_of_fatality", () -> new SwordItem(new AsTier(0, 14.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dk_vendaga", () -> new SwordItem(new AsTier(1024, 6.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
        reg("dk_volcano_sword", () -> new SwordItem(new AsTier(1750, 15.0F, 10), 0, -2.4F, new Item.Properties().stacksTo(1)));
    }
}
