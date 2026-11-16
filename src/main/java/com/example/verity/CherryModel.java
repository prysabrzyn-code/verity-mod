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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;

/**
 * Cherry's body, built from the same pixel layout as the Bedrock add-on (128x128 texture).
 * Java model space: the ground is at y = 24, so Bedrock y values are converted with y = 24 - y.
 */
public class CherryModel<T extends PathfinderMob> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(VerityMod.MODID, "cherry"), "main");

    private static final float DEG = (float) Math.PI / 180F;

    // Pivots (absolute, in model space)
    private static final float[] ROOT = {0.0F, 0.0F, 0.0F};
    private static final float[] BODY = {0.0F, 11.0F, 0.0F};
    private static final float[] HEAD = {0.0F, 9.0F, -6.0F};
    private static final float[] EAR_L = {-2.5F, 1.0F, -8.0F};
    private static final float[] EAR_R = {2.5F, 1.0F, -8.0F};
    private static final float[] TAIL = {0.0F, 9.0F, 6.0F};
    private static final float[] LEG_FR = {-2.5F, 15.0F, -4.5F};
    private static final float[] LEG_FL = {2.5F, 15.0F, -4.5F};
    private static final float[] LEG_BR = {-2.5F, 15.0F, 4.5F};
    private static final float[] LEG_BL = {2.5F, 15.0F, 4.5F};

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart legFR;
    private final ModelPart legFL;
    private final ModelPart legBR;
    private final ModelPart legBL;

    public CherryModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.tail = body.getChild("tail");
        this.legFR = root.getChild("leg_fr");
        this.legFL = root.getChild("leg_fl");
        this.legBR = root.getChild("leg_br");
        this.legBL = root.getChild("leg_bl");
    }

    /** Cube whose min corner is (x, y, z) in model space, relative to the given pivot. */
    private static CubeListBuilder box(float[] pivot, float x, float y, float z,
                                       float w, float h, float d, int u, int v) {
        return CubeListBuilder.create().texOffs(u, v)
                .addBox(x - pivot[0], y - pivot[1], z - pivot[2], w, h, d);
    }

    /** Pose of a child part, relative to its parent's pivot. */
    private static PartPose pose(float[] pivot, float[] parentPivot) {
        return PartPose.offset(pivot[0] - parentPivot[0], pivot[1] - parentPivot[1], pivot[2] - parentPivot[2]);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
                box(BODY, -4.0F, 7.0F, -6.0F, 8.0F, 8.0F, 12.0F, 0, 0),
                pose(BODY, ROOT));
        body.addOrReplaceChild("collar",
                box(BODY, -4.5F, 8.5F, -7.5F, 9.0F, 2.0F, 3.0F, 0, 80),
                PartPose.ZERO);
        body.addOrReplaceChild("tag",
                box(BODY, -1.0F, 10.5F, -8.5F, 2.0F, 2.0F, 1.0F, 24, 80),
                PartPose.ZERO);
        body.addOrReplaceChild("tail",
                box(TAIL, -1.0F, 9.0F, 6.0F, 2.0F, 2.0F, 6.0F, 0, 90),
                pose(TAIL, BODY));

        PartDefinition head = root.addOrReplaceChild("head",
                box(HEAD, -4.0F, 1.0F, -12.0F, 8.0F, 8.0F, 8.0F, 0, 30),
                pose(HEAD, ROOT));
        head.addOrReplaceChild("snout",
                box(HEAD, -2.0F, 5.0F, -15.0F, 4.0F, 4.0F, 3.0F, 40, 40),
                PartPose.ZERO);
        head.addOrReplaceChild("nose",
                box(HEAD, -1.0F, 5.0F, -15.5F, 2.0F, 2.0F, 1.0F, 56, 40),
                PartPose.ZERO);
        head.addOrReplaceChild("ear_l",
                box(EAR_L, -4.0F, -2.0F, -9.0F, 3.0F, 3.0F, 2.0F, 40, 30),
                pose(EAR_L, HEAD));
        head.addOrReplaceChild("ear_r",
                box(EAR_R, 1.0F, -2.0F, -9.0F, 3.0F, 3.0F, 2.0F, 40, 30),
                pose(EAR_R, HEAD));

        root.addOrReplaceChild("leg_fr",
                box(LEG_FR, -4.0F, 15.0F, -6.0F, 3.0F, 9.0F, 3.0F, 0, 64),
                pose(LEG_FR, ROOT));
        root.addOrReplaceChild("leg_fl",
                box(LEG_FL, 1.0F, 15.0F, -6.0F, 3.0F, 9.0F, 3.0F, 16, 64),
                pose(LEG_FL, ROOT));
        root.addOrReplaceChild("leg_br",
                box(LEG_BR, -4.0F, 15.0F, 3.0F, 3.0F, 9.0F, 3.0F, 32, 64),
                pose(LEG_BR, ROOT));
        root.addOrReplaceChild("leg_bl",
                box(LEG_BL, 1.0F, 15.0F, 3.0F, 3.0F, 9.0F, 3.0F, 48, 64),
                pose(LEG_BL, ROOT));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        int state = entity instanceof CherryEntity cherry ? cherry.getState() : CherryEntity.STAND;

        resetPose();
        this.head.yRot = netHeadYaw * DEG;
        this.head.xRot = headPitch * DEG;
        this.tail.yRot = Mth.cos(ageInTicks * 0.3F) * 0.4F;

        if (state == CherryEntity.STAND) {
            // Walking / running: diagonal legs swing together.
            float swing = limbSwing * 0.6662F;
            float amp = limbSwingAmount * 1.4F;
            this.legFR.xRot = Mth.cos(swing) * amp;
            this.legBL.xRot = Mth.cos(swing) * amp;
            this.legFL.xRot = Mth.cos(swing + Mth.PI) * amp;
            this.legBR.xRot = Mth.cos(swing + Mth.PI) * amp;
        } else if (state == CherryEntity.SIT) {
            this.body.xRot = -0.6F;
            this.legBR.xRot = -1.3F;
            this.legBL.xRot = -1.3F;
            this.tail.xRot = -0.8F;
        } else {
            // Sleeping: lies on her side.
            this.body.zRot = 1.5F;
            this.head.zRot = 0.3F;
            this.tail.zRot = 0.2F;
        }
    }

    private void resetPose() {
        this.body.xRot = 0.0F;
        this.body.zRot = 0.0F;
        this.head.zRot = 0.0F;
        this.tail.xRot = 0.0F;
        this.tail.zRot = 0.0F;
        this.legFR.xRot = 0.0F;
        this.legFL.xRot = 0.0F;
        this.legBR.xRot = 0.0F;
        this.legBL.xRot = 0.0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
