package com.example.verity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ChreyRenderer extends MobRenderer<ChreyEntity, ChreyModel<ChreyEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VerityMod.MODID, "textures/entity/chrey.png");

    public ChreyRenderer(EntityRendererProvider.Context context) {
        super(context, new ChreyModel<>(context.bakeLayer(ChreyModel.LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(ChreyEntity entity) {
        return TEXTURE;
    }
}
