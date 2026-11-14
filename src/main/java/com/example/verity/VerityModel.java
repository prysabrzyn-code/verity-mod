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

public class VerityModel extends EntityModel<VerityEntity> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(VerityMod.MODID, "verity"), "main");

    private final ModelPart body;

    public VerityModel(ModelPart root) {
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        // Round ball standing on the ground: 8 stacked 1-unit layers, radius 4 at the centre.
        CubeListBuilder ball = CubeListBuilder.create().texOffs(0, 0);
        int[] radius = {2, 3, 4, 4, 4, 4, 3, 2};
        for (int i = 0; i < radius.length; i++) {
            float r = radius[i];
            ball.addBox(-r, -8.0F + i, -r, r * 2.0F, 1.0F, r * 2.0F);
        }
        PartDefinition body = root.addOrReplaceChild("body", ball, PartPose.offset(0.0F, 24.0F, 0.0F));
        // Black hat sitting on top of the ball (y from -12 to -8 in body space)
        body.addOrReplaceChild("hat",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-3.0F, -12.0F, -3.0F, 6.0F, 4.0F, 6.0F),
                PartPose.ZERO);
        // Dark sunglasses bar across the front of the eyes
        body.addOrReplaceChild("sunglasses",
                CubeListBuilder.create().texOffs(0, 30)
                        .addBox(-3.5F, -5.0F, -4.8F, 7.0F, 1.5F, 1.0F),
                PartPose.ZERO);
        // Brown moustache under the sunglasses
        body.addOrReplaceChild("moustache",
                CubeListBuilder.create().texOffs(0, 36)
                        .addBox(-2.5F, -2.5F, -4.8F, 5.0F, 1.5F, 1.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(VerityEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.body.yRot = netHeadYaw * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
