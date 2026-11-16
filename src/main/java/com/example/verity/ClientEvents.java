package com.example.verity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VerityMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(VerityModel.LAYER, VerityModel::createBodyLayer);
        event.registerLayerDefinition(FalsityModel.LAYER, FalsityModel::createBodyLayer);
        event.registerLayerDefinition(ChreyModel.LAYER, ChreyModel::createBodyLayer);
        event.registerLayerDefinition(ChreyModel.BEAST_LAYER, ChreyModel::createBodyLayer);
        event.registerLayerDefinition(GloomkinModel.LAYER, GloomkinModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VerityMod.VERITY.get(), VerityRenderer::new);
        event.registerEntityRenderer(VerityMod.FALSITY.get(), FalsityRenderer::new);
        event.registerEntityRenderer(VerityMod.CHREY.get(), ChreyRenderer::new);
        event.registerEntityRenderer(VerityMod.CHREY_BEAST.get(), ChreyBeastRenderer::new);
        event.registerEntityRenderer(VerityMod.GLOOMKIN.get(), GloomkinRenderer::new);
        event.registerEntityRenderer(VerityMod.ASHWALKER.get(), AshwalkerRenderer::new);
    }
}
