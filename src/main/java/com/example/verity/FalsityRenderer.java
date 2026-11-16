package com.example.verity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FalsityRenderer extends MobRenderer<FalsityEntity, FalsityModel> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VerityMod.MODID, "textures/entity/falsity.png");

    public FalsityRenderer(EntityRendererProvider.Context context) {
        super(context, new FalsityModel(context.bakeLayer(FalsityModel.LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(FalsityEntity entity) {
        return TEXTURE;
    }
}
