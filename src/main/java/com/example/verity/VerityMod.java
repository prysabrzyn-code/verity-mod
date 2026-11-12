package com.example.verity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(VerityMod.MODID)
public class VerityMod {
    public static final String MODID = "verity";

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<EntityType<VerityEntity>> VERITY = ENTITY_TYPES.register("verity",
            () -> EntityType.Builder.of(VerityEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 0.9F)
                    .build("verity"));

    // Yellow body with dark eyes/teeth colours for the spawn egg
    public static final RegistryObject<Item> VERITY_SPAWN_EGG = ITEMS.register("verity_spawn_egg",
            () -> new ForgeSpawnEggItem(VERITY, 0xFFD400, 0x3B2A00, new Item.Properties()));

    // Cardboard box block: right-click to open it and Verity pops out
    public static final RegistryObject<EntityType<GloomkinEntity>> GLOOMKIN = ENTITY_TYPES.register("gloomkin",
            () -> EntityType.Builder.of(GloomkinEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 2.0F)
                    .build("gloomkin"));

    public static final RegistryObject<Item> GLOOMKIN_SPAWN_EGG = ITEMS.register("gloomkin_spawn_egg",
            () -> new ForgeSpawnEggItem(GLOOMKIN, 0x3A2A4A, 0xFF1A1A, new Item.Properties()));

    public static final RegistryObject<Block> CARDBOARD_BOX = BLOCKS.register("cardboard_box",
            () -> new CardboardBoxBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    public static final RegistryObject<Item> CARDBOARD_BOX_ITEM = ITEMS.register("cardboard_box",
            () -> new BlockItem(CARDBOARD_BOX.get(), new Item.Properties()));

    public VerityMod() {
        AsItems.init();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITY_TYPES.register(modBus);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::onEntityAttributes);
        modBus.addListener(this::onBuildCreativeTabs);
    }

    private void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(VERITY.get(), VerityEntity.createAttributes().build());
        event.put(GLOOMKIN.get(), GloomkinEntity.createAttributes().build());
    }

    private void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(VERITY_SPAWN_EGG);
            event.accept(GLOOMKIN_SPAWN_EGG);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(CARDBOARD_BOX_ITEM);
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            AsItems.ALL.forEach(event::accept);
        }
    }
}
