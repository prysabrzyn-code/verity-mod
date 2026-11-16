package com.example.verity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CherryRenderer extends MobRenderer<CherryEntity, CherryModel<CherryEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VerityMod.MODID, "textures/entity/cherry.png");

    public CherryRenderer(EntityRendererProvider.Context context) {
        super(context, new CherryModel<>(context.bakeLayer(CherryModel.LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(CherryEntity entity) {
        return TEXTURE;
    }
}
