package net.mehvahdjukaar.dummmmmmy.client;


import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Set;

public class TargetDummyModel extends HumanoidModel<TargetDummyRenderState> {

    private static final CubeDeformation ARMOR_INNER = new CubeDeformation(0.5f);
    private static final CubeDeformation ARMOR_OUTER = new CubeDeformation(1.0f);

    private static final Map<EquipmentSlot, Set<String>> ARMOR_PARTS_PER_SLOT = Map.of(
            EquipmentSlot.HEAD, Set.of("head"),
            EquipmentSlot.CHEST, Set.of("body", "right_arm", "left_arm"),
            EquipmentSlot.LEGS, Set.of("body", "left_leg"),
            EquipmentSlot.FEET, Set.of("left_leg"));

    public final ModelPart standPlate;

    private float bodyWobble = 0;
    private float headSideWobble = 0;
    private float rechargingAnim = 0;

    public TargetDummyModel(ModelPart modelPart) {
        super(modelPart);
        standPlate = modelPart.getChild("stand");
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createMesh(CubeDeformation.NONE), 64, 64);
    }

    public static ArmorModelSet<LayerDefinition> createArmorLayers() {
        return createArmorMeshSet(TargetDummyModel::createMesh, ARMOR_PARTS_PER_SLOT, ARMOR_INNER, ARMOR_OUTER)
                .map(mesh -> LayerDefinition.create(mesh, 64, 32));
    }

    public static MeshDefinition createMesh(CubeDeformation deformation) {
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
                        .addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, deformation.extend(deformation == CubeDeformation.NONE ? 0 : -0.01f)),
                PartPose.offset(0F, 12.0F, 0.0F));

        return meshdefinition;
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

    public ModelPart getBody() {
        return this.leftLeg;
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
    public void setupAnim(TargetDummyRenderState state) {
        this.resetPose();

        setHitAnimation(state.shake, state.swing);

        // un-rotate the stand plate, so it's aligned to the block grid
        this.standPlate.yRot = Mth.DEG_TO_RAD * -state.bodyRot;
        this.rechargingAnim = smoothRamp(state.recharging, 0.1);

        float n = 1.5f;

        //------new---------

        float yOffsetIn = -1;

        float xangle = bodyWobble / 2;

        this.leftLeg.setPos(0, 12.0F + yOffsetIn, 0.0F);
        this.rotateModelX(this.leftLeg, 0, 24 + yOffsetIn, 0, xangle);
        //for mod support
        this.rightLeg.setPos(0, 12.0F + yOffsetIn, 0.0F);
        this.rotateModelX(this.rightLeg, 0.01f, 24 + yOffsetIn + 0.01f, 0.01f, xangle);
        this.rightLeg.visible = false;

        this.body.setPos(0.0F, 0.0F + yOffsetIn, 0.0F);
        this.rotateModelX(this.body, 0, 24 + yOffsetIn, 0, xangle);

        this.rightArm.setPos(-2.5F, 2.0F + yOffsetIn, -0.005F);
        this.rotateModelY(this.rightArm, 0, 24 + yOffsetIn, 0, xangle, -1);

        this.leftArm.setPos(2.5F, 2.0F + yOffsetIn, -0.005F);
        this.rotateModelY(this.leftArm, 0, 24 + yOffsetIn, 0, xangle, 1);


        this.head.setPos(0.0F, 0.0F + yOffsetIn, 0.0F);


        this.rotateModelX(this.head, 0, 24 + yOffsetIn, 0, xangle);
        this.head.xRot = -bodyWobble + rechargingAnim * 0.8f + state.headPitch; //-r
        this.head.yRot = state.headYaw;
        this.head.zRot = headSideWobble; //r2

        //mod support
        this.hat.loadPose(this.head.storePose());

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
