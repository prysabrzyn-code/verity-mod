package com.example.verity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ChreyModel<T extends Entity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(VerityMod.MODID, "chrey"), "main");
    public static final ModelLayerLocation BEAST_LAYER =
            new ModelLayerLocation(new ResourceLocation(VerityMod.MODID, "chrey_beast"), "main");

    private final ModelPart head;
    private final ModelPart body;

    public ChreyModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offset(0.0F, 9.0F, -8.0F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, -4.0F, -7.0F, 8.0F, 8.0F, 14.0F),
                PartPose.offset(0.0F, 12.0F, 0.0F));
        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 44)
                .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F);
        root.addOrReplaceChild("leg0", leg, PartPose.offset(-2.5F, 16.0F, -4.5F));
        root.addOrReplaceChild("leg1", leg, PartPose.offset(2.5F, 16.0F, -4.5F));
        root.addOrReplaceChild("leg2", leg, PartPose.offset(-2.5F, 16.0F, 4.5F));
        root.addOrReplaceChild("leg3", leg, PartPose.offset(2.5F, 16.0F, 4.5F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
