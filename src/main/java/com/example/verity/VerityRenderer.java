package com.example.verity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class VerityRenderer extends MobRenderer<VerityEntity, VerityModel> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VerityMod.MODID, "textures/entity/verity.png");

    public VerityRenderer(EntityRendererProvider.Context context) {
        super(context, new VerityModel(context.bakeLayer(VerityModel.LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(VerityEntity entity) {
        return TEXTURE;
    }
}
