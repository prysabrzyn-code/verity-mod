package com.example.verity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CherryMonsterRenderer extends MobRenderer<CherryMonsterEntity, CherryModel<CherryMonsterEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VerityMod.MODID, "textures/entity/cherry_monster.png");

    public CherryMonsterRenderer(EntityRendererProvider.Context context) {
        super(context, new CherryModel<>(context.bakeLayer(CherryModel.LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(CherryMonsterEntity entity) {
        return TEXTURE;
    }
}
