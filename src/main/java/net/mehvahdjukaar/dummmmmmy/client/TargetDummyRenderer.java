package net.mehvahdjukaar.dummmmmmy.client;

import net.mehvahdjukaar.dummmmmmy.common.Configs;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.setup.ClientHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TargetDummyRenderer extends HumanoidMobRenderer<TargetDummyEntity, TargetDummyModel<TargetDummyEntity>> {

    public TargetDummyRenderer(EntityRendererProvider.Context context) {
        super(context, new TargetDummyModel<>(context.bakeLayer(ClientHandler.DUMMY_BODY)), 0);
        this.addLayer(new LayerDummyArmor<>(this,
                new TargetDummyModel<>(context.bakeLayer(ClientHandler.DUMMY_ARMOR_INNER)),
                new TargetDummyModel<>(context.bakeLayer(ClientHandler.DUMMY_ARMOR_OUTER))));
    }


    @Override
    public ResourceLocation getTextureLocation(TargetDummyEntity entity) {
        return Configs.cached.SKIN.getSkin(entity.isSheared());
    }


/*
//te
    public void render2(TargetDummyEntity entity, float p_225623_2_, float p_225623_3_, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
        super.render(entity, p_225623_2_, p_225623_3_, matrixStackIn, bufferIn, combinedLightIn);
        ItemStack stack= new ItemStack(Items.SAND);
        RenderTarget fb = new RenderTarget(16, 16, true, true);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        //itemRenderer.render(stack, ItemCameraTransforms.TransformType.GUI, false, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);

        BakedModel ibakedmodel = itemRenderer.getModel(stack, entity.level, null);

        RenderTarget mcFb = Minecraft.getInstance().getMainRenderTarget();
        fb.bindWrite(false);

        fb.blitToScreen(500,500,false);
        RenderSystem.pushMatrix();
        itemRenderer.renderAndDecorateItem(stack, 0, 0);
        RenderSystem.popMatrix();
        //fb.destroyBuffers();
        fb.destroyBuffers();

        mcFb.bindWrite(false);


        fb.bindRead();



        //itemRenderer.render(stack, ItemCameraTransforms.TransformType.GUI, false, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);

        //itemRenderer.renderAndDecorateItem(stack, 0, 0);

        //fb.destroyBuffers();
        fb.unbindWrite();
        fb.unbindRead();








        //matrixStackIn.translate(1,1,1);
        //matrixStackIn.scale(1F, 1F, 0.01F);


        //RenderHelper.turnBackOn();

        //Following 9 lines lifted from Storage Drawers. Spent ages trying to figure out lighting...
        //int ambLight = getWorld().getCombinedLight(te.getPos().offset(barrelFacing), 0);
        //int lu = ambLight % 65536;
        //int lv = ambLight / 65536;
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lu / 1f, lv / 1f);


        RenderSystem.enableRescaleNormal();  //This hack Storage Drawers uses is crazy!!!

        RenderSystem.disableRescaleNormal(); //I guess the purpose is to make the lighting

        matrixStackIn.pushPose();
        //still work when the item is flattened
        RenderSystem.enableRescaleNormal();
        matrixStackIn.popPose();






        //RenderHelper.turnOff();

        //drawItem(matrixStackIn,bufferIn,stack,combinedLightIn);

    }

    //private static Framebuffer fb = new Framebuffer(16, 16, true, true);

    public static void drawItem(PoseStack matrices, MultiBufferSource buffer, ItemStack stack, int light) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        matrices.pushPose();




        Minecraft mc = Minecraft.getInstance();
        GraphicsStatus cache = mc.options.graphicsMode;
        mc.options.graphicsMode = GraphicsStatus.FANCY;







        //renderFastItem(renderStacks[i], tile, state, i, matrix, buffer, combinedLight, combinedOverlay, side, partialTickTime);


        //BlockDrawers block = (BlockDrawers)state.getBlock();
        //AxisAlignedBB labelGeometry = block.labelGeometry[slot];

        float scaleX = (float)8 / 16;
        float scaleY = (float)8 / 16;
        float moveX = (float)0 + (8 * scaleX);
        float moveY = 16f - (float)5 + (8 * scaleY);
        float moveZ = (float)0 * .0625f;


        matrices.pushPose();

        //alignRendering(matrix, side);
        moveRendering(matrices, scaleX, scaleY, moveX, moveY, moveZ);

        //List<IRenderLabel> renderHandlers = StorageDrawers.renderRegistry.getRenderHandlers();
        //for (IRenderLabel renderHandler : renderHandlers) {
        //    renderHandler.render(tile, tile.getGroup(), slot, 0, partialTickTime);
        //}

        Consumer<MultiBufferSource> finish = (MultiBufferSource buf) -> {
            if (buf instanceof MultiBufferSource.BufferSource)
                ((MultiBufferSource.BufferSource) buf).endBatch();
        };

        try {
            matrices.translate(0, 0, 100f);
            matrices.scale(1, -1, 1);
            matrices.scale(16, 16, 16);

            //IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            BakedModel itemModel = itemRenderer.getModel(stack, null, null);
            boolean render3D = itemModel.isGui3d(); // itemModel.func_230044_c_();
            finish.accept(buffer);

            if (render3D)
                Lighting.setupFor3DItems();
            else
                Lighting.setupForFlatItems();

            //matrices.last().normal().set(Matrix3f.createScaleMatrix(1, -1, 1));
            itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, matrices, buffer, light, OverlayTexture.NO_OVERLAY, itemModel);
            finish.accept(buffer);
        }
        catch (Exception e) {
            // Shrug
        }


        mc.options.graphicsMode = cache;
        matrices.popPose();
        Lighting.setupLevel(matrices.last().pose());
        matrices.popPose();
    }


    private static void moveRendering (PoseStack matrix, float scaleX, float scaleY, float offsetX, float offsetY, float offsetZ) {
        // NOTE: RenderItem expects to be called in a context where Y increases toward the bottom of the screen
        // However, for in-world rendering the opposite is true. So we translate up by 1 along Y, and then flip
        // along Y. Since the item is drawn at the back of the drawer, we also translate by `1-offsetZ` to move
        // it to the front.

        // The 0.00001 for the Z-scale both flattens the item and negates the 32.0 Z-scale done by RenderItem.

        matrix.translate(0, 1, 1-offsetZ);
        matrix.scale(1 / 16f, -1 / 16f, 0.00005f);

        matrix.translate(offsetX, offsetY, 0);
        matrix.scale(scaleX, scaleY, 1);
    }
*/
}
