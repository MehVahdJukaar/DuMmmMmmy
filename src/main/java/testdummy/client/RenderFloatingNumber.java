package testdummy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import testdummy.TestDummy;
import testdummy.entity.EntityDpsFloatingNumber;
import testdummy.entity.EntityFloatingNumber;
import testdummy.handlers.ConfigHandler;

public class RenderFloatingNumber extends Render<EntityFloatingNumber> {
    private static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    public RenderFloatingNumber(RenderManager renderManager) {
        super(renderManager);
    }

    public void doRender(EntityFloatingNumber entity, double x, double y, double z, float entityYaw, float partialTicks) {
        boolean dps = entity instanceof EntityDpsFloatingNumber;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        EntityPlayerSP entityPlayerSP = (Minecraft.getMinecraft()).player;
        double xd = entityPlayerSP.posX - entity.posX;
        double yd = entityPlayerSP.posY - entity.posY;
        double zd = entityPlayerSP.posZ - entity.posZ;
        double l = Math.sqrt(xd * xd + yd * yd + zd * zd);
        double scale = 0.01D * l;
        if (dps) {
            scale += 0.03D;
        }
        GL11.glScaled(-scale, -scale, scale);
        GL11.glTranslated(0.0D, -l / 10.0D, 0.0D);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        String s = TestDummy.df.format((entity.damage));
        if (dps) {
            s = ConfigHandler.server.outputMessage+": " + s;
        }
        GL11.glTranslated((-fontRenderer.getStringWidth(s) / 2D), 0.0D, 0.0D);
        fontRenderer.drawString(s, 0.0F, 0.0F, -1, true);
        GL11.glTranslated((fontRenderer.getStringWidth(s) / 2D), 0.0D, 0.0D);
        GL11.glDepthMask(true);
        GL11.glEnable(2896);
        GL11.glPopMatrix();
    }

    protected ResourceLocation getEntityTexture(EntityFloatingNumber entity) {
        return null;
    }
}