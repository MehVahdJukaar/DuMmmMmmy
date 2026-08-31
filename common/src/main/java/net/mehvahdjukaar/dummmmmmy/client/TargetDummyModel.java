package net.mehvahdjukaar.dummmmmmy.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class TargetDummyModel<T extends TargetDummyEntity> extends HumanoidModel<T> {
    public final ModelPart standPlate;

    private float bodyWobble = 0;
    private float headSideWobble = 0;
    private float rechargingAnim = 0;

    public TargetDummyModel(ModelPart modelPart) {
        super(modelPart);
        standPlate = modelPart.getChild("stand");
    }

    public static LayerDefinition createMesh(float size, int textHeight) {
        CubeDeformation deformation = new CubeDeformation(size);
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("stand", CubeListBuilder.create()
                        .texOffs(0, 32)
                        .addBox(-6.0F, 12F, -6.0F, 12F, 1F, 12F, deformation),
                PartPose.offset(0.0F, 11.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(40, 16)
                        .addBox(-3.0F, 1.0F, -2.0F, 4.0F, 8F, 4.0F, deformation.extend(0.01f)),
                PartPose.offset(-2.5F, 2.0F, -0.005F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(40, 16).mirror()
                        .addBox(-1.0F, 1.0F, -2.0F, 4.0F, 8F, 4.0F, deformation.extend(0.01f)),
                PartPose.offset(2.5F, 2.0F, -0.005F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, deformation.extend(size != 0 ? -0.01f : 0)),
                PartPose.offset(0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, textHeight);
    }

    //don't touch. it just works
    public void rotateModelX(ModelPart model, float pivotX, float pivotY, float pivotZ, float angle) {
        Vec3 oldRot = new Vec3(model.x, model.y, model.z);
        Vec3 actualRot = new Vec3(pivotX, pivotY, pivotZ);

        Vec3 newRot = actualRot.add(oldRot.subtract(actualRot).xRot(-angle));

        model.setPos((float) newRot.x(), (float) newRot.y(), (float) newRot.z());
        model.xRot = angle;
    }

    public void rotateModelY(ModelPart model, float pivotX, float pivotY, float pivotZ, float angle, int mult) {
        Vec3 oldRot = new Vec3(model.x, model.y, model.z);
        Vec3 actualRot = new Vec3(pivotX, pivotY, pivotZ);

        Vec3 newRot = actualRot.add(oldRot.subtract(actualRot).xRot(-angle));

        model.setPos((float) newRot.x(), (float) newRot.y(), (float) newRot.z());
        model.yRot = angle * mult;
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int overlayIn, int color) {
        int overlay = OverlayTexture.NO_OVERLAY;
        matrixStackIn.pushPose();
        this.standPlate.render(matrixStackIn, bufferIn, packedLightIn, overlay, color);

        this.head.render(matrixStackIn, bufferIn, packedLightIn, overlay, color);
        this.rightArm.render(matrixStackIn, bufferIn, packedLightIn, overlay, color);
        this.leftArm.render(matrixStackIn, bufferIn, packedLightIn, overlay, color);
        this.body.render(matrixStackIn, bufferIn, packedLightIn, overlay, color);
        this.leftLeg.render(matrixStackIn, bufferIn, packedLightIn, overlay, color);

        this.hat.render(matrixStackIn, bufferIn, packedLightIn, overlay, color);
        matrixStackIn.popPose();
    }

    public ModelPart getBody() {
        return this.leftLeg;
    }

    //TODO: this is horrible
    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        float unscaledSwingAmount = entity.getAnimationPosition(partialTick);
        setHitAnimation(entity.getShake(partialTick), unscaledSwingAmount);

        // un-rotate the stand plate, so it's aligned to the block grid
        this.standPlate.yRot = Mth.DEG_TO_RAD * -Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);

        float recharge = entity.getRechargingAnimation(partialTick);
        this.rechargingAnim = smoothRamp(recharge, 0.1);
    }

    public void setHitAnimation(float phase, float unscaledSwingAmount) {
        float swingAmount = Math.min((float) (unscaledSwingAmount * ClientConfigs.ANIMATION_INTENSITY.get()), 40f);
        if (swingAmount > 0) {
            this.bodyWobble = (float) -(Mth.sin(phase) * Math.PI / 100f * swingAmount);
            this.headSideWobble = (float) (Mth.sin(phase) * Math.PI / 20 * Math.min(swingAmount, 1));
        } else {
            this.bodyWobble = 0;
            this.headSideWobble = 0;
        }
        this.standPlate.xRot = 0.0F;
        this.standPlate.yRot = 0.0F;
        this.standPlate.zRot = 0.0F;
        this.rechargingAnim = 0;
    }

    private float smoothRamp(float number, double cutoff) {
        return (float) (number < cutoff ? number / cutoff : 1);
    }

    @Override
    public void setupAnim(TargetDummyEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                          float headPitch) {

        float n = 1.5f;

        //------new---------

        float yOffsetIn = -1;

        float xangle = bodyWobble / 2;

        this.leftLeg.setPos(0, 12.0F + yOffsetIn, 0.0F);
        this.rotateModelX(this.leftLeg, 0, 24 + yOffsetIn, 0, xangle);
        //for mod support
        this.rightLeg.setPos(0, 12.0F + yOffsetIn, 0.0F);
        this.rotateModelX(this.rightLeg, 0.01f, 24 + yOffsetIn + 0.01f, 0.01f, xangle);

        this.body.setPos(0.0F, 0.0F + yOffsetIn, 0.0F);
        this.rotateModelX(this.body, 0, 24 + yOffsetIn, 0, xangle);

        this.rightArm.setPos(-2.5F, 2.0F + yOffsetIn, -0.005F);
        this.rotateModelY(this.rightArm, 0, 24 + yOffsetIn, 0, xangle, -1);

        this.leftArm.setPos(2.5F, 2.0F + yOffsetIn, -0.005F);
        this.rotateModelY(this.leftArm, 0, 24 + yOffsetIn, 0, xangle, 1);


        this.head.setPos(0.0F, 0.0F + yOffsetIn, 0.0F);


        this.rotateModelX(this.head, 0, 24 + yOffsetIn, 0, xangle);
        this.head.xRot = -bodyWobble + rechargingAnim * 0.8f; //-r
        this.head.zRot = headSideWobble; //r2

        //mod support
        this.hat.copyFrom(this.head);

        //rotate arms up
        this.rightArm.zRot = (float) Math.PI / 2f;
        this.leftArm.zRot = -(float) Math.PI / 2f;
        //swing arm
        this.rightArm.xRot = bodyWobble * n;
        this.leftArm.xRot = bodyWobble * n;

        this.leftArm.zRot += rechargingAnim * 0.25f;
        this.rightArm.zRot += rechargingAnim * -0.25f;


    }

}