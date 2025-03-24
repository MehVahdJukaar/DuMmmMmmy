package testdummy.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import testdummy.entity.EntityDummy;

public class ModelDummy extends ModelBiped {
    public ModelRenderer standPlate;

    public ModelDummy(float size, float v) {
        this(size, v, 64, 64);
    }

    public ModelDummy(float size, float v, int xw, int yw) {
        super(size, v, xw, yw);

        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.addBox(-3.01F, 1.0F, -2.01F, 4, 8, 4, size);
        this.bipedRightArm.setRotationPoint(-2.5F, 2.0F + v, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.01F, 1.0F, -2.01F, 4, 8, 4, size);
        this.bipedLeftArm.setRotationPoint(2.5F, 2.0F + v, 0.0F);

        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.addBox(-2.0F, -12.0F, -2.0F, 4, 12, 4, size);
        this.bipedLeftLeg.setRotationPoint(0.0F, 24.0F + v, 0.0F);

        this.bipedRightLeg = new ModelRenderer(this, 0, 0);

        this.standPlate = new ModelRenderer(this, 0, 32);
        this.standPlate.addBox(-8.0F, 11.5F, -8.0F, 16, 1, 16, size);
        this.standPlate.setRotationPoint(0.0F, 12.0F + v, 0.0F);

        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0F, -24.0F, -2.0F, 8, 12, 4, size);
        this.bipedBody.setRotationPoint(0.0F, 24.0F + v, 0.0F);

        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, size + 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + v, 0.0F);
    }

    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

        if (this.isChild) {
            float f6 = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
            GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            this.bipedHead.render(scale);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);

            this.standPlate.render(scale);
            this.bipedLeftLeg.render(scale);
            GL11.glPopMatrix();
        } else {

            this.bipedHead.render(scale);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.standPlate.render(scale);
            this.bipedLeftLeg.render(scale);
            this.bipedHeadwear.render(scale);
        }
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        this.bipedHead.rotateAngleY = netHeadYaw / 57.295776F;
        this.bipedHead.rotateAngleX = headPitch / 57.295776F;
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;

        //this.bipedRightArm.rotateAngleZ = 0.0F;
        //this.bipedLeftArm.rotateAngleZ = 0.0F;

        //this.bipedLeftArm.rotateAngleX = 0.0F;
        //this.bipedRightArm.rotateAngleX = 0.0F;

        this.bipedRightArm.rotateAngleY = 0.0F;
        this.bipedLeftArm.rotateAngleY = 0.0F;

        //this.bipedBody.rotateAngleX = 0.0F;

        this.bipedHead.rotationPointY = 0.0F;
        this.bipedHeadwear.rotationPointY = 0.0F;

        this.bipedRightArm.rotateAngleZ = 1.5707964F;
        this.bipedLeftArm.rotateAngleZ = -1.5707964F;

        float phase = ((EntityDummy) entity).shakeAnimation;
        float shake = ((EntityDummy) entity).shake;
        float r = 0.0F, r2 = 0.0F;
        if (shake > 0.0F) {
            r = (float) -(MathHelper.sin(phase) * Math.PI / 100.0D * shake);
            r2 = (float) (MathHelper.cos(phase) * Math.PI / 20.0D);
        }

        this.bipedLeftLeg.rotateAngleX = r / 2.0F;
        this.bipedBody.rotateAngleX = r / 2.0F;
        this.bipedLeftArm.rotateAngleX = r * 1.1F;
        this.bipedRightArm.rotateAngleX = r * 1.1F;
        this.bipedHead.rotateAngleX = -r;
        this.bipedHead.rotateAngleZ = r2;
    }
}
