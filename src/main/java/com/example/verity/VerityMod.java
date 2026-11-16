package com.example.verity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.food.FoodProperties;
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
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    // ---- Cherry the dog ----
    public static final RegistryObject<SoundEvent> CHERRY_BARK = SOUNDS.register("cherry_bark",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "cherry_bark")));

    public static final RegistryObject<EntityType<CherryEntity>> CHERRY = ENTITY_TYPES.register("cherry_dog",
            () -> EntityType.Builder.of(CherryEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.9F)
                    .build("cherry_dog"));

    public static final RegistryObject<EntityType<CherryMonsterEntity>> CHERRY_MONSTER =
            ENTITY_TYPES.register("cherry_monster",
                    () -> EntityType.Builder.of(CherryMonsterEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 0.9F)
                            .build("cherry_monster"));

    public static final RegistryObject<Item> CHERRY_SPAWN_EGG = ITEMS.register("cherry_spawn_egg",
            () -> new ForgeSpawnEggItem(CHERRY, 0xEFEDE6, 0xC81E1E, new Item.Properties()));

    public static final RegistryObject<Item> CHERRY_TREAT = ITEMS.register("cherry_treat",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build())));

    public static final RegistryObject<Item> CHERRY_COLLAR = ITEMS.register("cherry_collar",
            () -> new Item(new Item.Properties().stacksTo(1)));

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

    public static final RegistryObject<Item> SHIFT_WATCH = ITEMS.register("shift_watch",
            () -> new ShiftWatchItem(new Item.Properties()));

    public static final RegistryObject<EntityType<AshwalkerEntity>> ASHWALKER = ENTITY_TYPES.register("ashwalker",
            () -> EntityType.Builder.of(AshwalkerEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build("ashwalker"));

    public static final RegistryObject<Item> ASHWALKER_SPAWN_EGG = ITEMS.register("ashwalker_spawn_egg",
            () -> new ForgeSpawnEggItem(ASHWALKER, 0x4A5A4A, 0xE8731A, new Item.Properties()));

    public static final RegistryObject<Item> GLOW_SHARD = ITEMS.register("glow_shard",
            () -> new ArtifactItem(new Item.Properties(), MobEffects.MOVEMENT_SPEED, MobEffects.NIGHT_VISION));

    public static final RegistryObject<Item> EMBER_CORE = ITEMS.register("ember_core",
            () -> new ArtifactItem(new Item.Properties(), MobEffects.DAMAGE_BOOST, MobEffects.FIRE_RESISTANCE));

    public static final RegistryObject<Item> FROZEN_EYE = ITEMS.register("frozen_eye",
            () -> new ArtifactItem(new Item.Properties(), MobEffects.REGENERATION, MobEffects.DAMAGE_RESISTANCE));

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
        SOUNDS.register(modBus);
        modBus.addListener(this::onEntityAttributes);
        modBus.addListener(this::onBuildCreativeTabs);
    }

    private void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(VERITY.get(), VerityEntity.createAttributes().build());
        event.put(GLOOMKIN.get(), GloomkinEntity.createAttributes().build());
        event.put(ASHWALKER.get(), Zombie.createAttributes().build());
        event.put(CHERRY.get(), CherryEntity.createAttributes().build());
        event.put(CHERRY_MONSTER.get(), CherryMonsterEntity.createAttributes().build());
    }

    private void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(VERITY_SPAWN_EGG);
            event.accept(GLOOMKIN_SPAWN_EGG);
            event.accept(ASHWALKER_SPAWN_EGG);
            event.accept(CHERRY_SPAWN_EGG);
        }
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(CHERRY_TREAT);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(CARDBOARD_BOX_ITEM);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(GLOW_SHARD);
            event.accept(EMBER_CORE);
            event.accept(FROZEN_EYE);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(SHIFT_WATCH);
            event.accept(CHERRY_COLLAR);
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            AsItems.ALL.forEach(event::accept);
        }
    }
}
