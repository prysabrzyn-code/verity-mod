package com.example.verity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ChreyBeastRenderer extends MobRenderer<ChreyBeastEntity, ChreyModel<ChreyBeastEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VerityMod.MODID, "textures/entity/chrey_beast.png");

    public ChreyBeastRenderer(EntityRendererProvider.Context context) {
        super(context, new ChreyModel<>(context.bakeLayer(ChreyModel.BEAST_LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(ChreyBeastEntity entity) {
        return TEXTURE;
    }
}
