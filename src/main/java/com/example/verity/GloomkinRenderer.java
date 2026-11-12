package com.example.verity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GloomkinRenderer extends MobRenderer<GloomkinEntity, GloomkinModel> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VerityMod.MODID, "textures/entity/gloomkin.png");

    public GloomkinRenderer(EntityRendererProvider.Context context) {
        super(context, new GloomkinModel(context.bakeLayer(GloomkinModel.LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(GloomkinEntity entity) {
        return TEXTURE;
    }
}
