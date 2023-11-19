package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.AncientCopperConduitTileEntity;
import iskallia.vault.entity.model.ModModelLayers;
import javax.annotation.Nonnull;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.Level;

public class AncientCopperConduitRenderer implements BlockEntityRenderer<AncientCopperConduitTileEntity> {
   public static final Material CONDUIT_OUTTER = new Material(TextureAtlas.LOCATION_BLOCKS, VaultMod.id("entity/ancient_copper_conduit/conduit_outter"));
   public static final Material CONDUIT_OUTTER2 = new Material(TextureAtlas.LOCATION_BLOCKS, VaultMod.id("entity/ancient_copper_conduit/conduit_outter2"));
   public static final Material CONDUIT_WIND = new Material(TextureAtlas.LOCATION_BLOCKS, VaultMod.id("entity/ancient_copper_conduit/conduit_wind"));
   public static final Material CONDUIT_WIND_VERTICAL = new Material(
      TextureAtlas.LOCATION_BLOCKS, VaultMod.id("entity/ancient_copper_conduit/conduit_wind_vertical")
   );
   public static final Material CONDUIT_CENTER = new Material(TextureAtlas.LOCATION_BLOCKS, VaultMod.id("entity/ancient_copper_conduit/conduit_center"));
   private final ModelPart eye;
   private final ModelPart wind;
   private final ModelPart cage;
   private final BlockEntityRenderDispatcher renderer;

   public AncientCopperConduitRenderer(Context pContext) {
      this.renderer = pContext.getBlockEntityRenderDispatcher();
      this.eye = pContext.bakeLayer(ModModelLayers.ANGEL_BLOCK_EYE);
      this.wind = pContext.bakeLayer(ModModelLayers.ANGEL_BLOCK_WIND);
      this.cage = pContext.bakeLayer(ModModelLayers.ANGEL_BLOCK_CAGE);
   }

   public ModelPart getCage() {
      return this.cage;
   }

   public ModelPart getWind() {
      return this.wind;
   }

   public ModelPart getEye() {
      return this.eye;
   }

   public static LayerDefinition createEyeLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild(
         "eye", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.ZERO
      );
      return LayerDefinition.create(meshdefinition, 16, 16);
   }

   public static LayerDefinition createWindLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public static LayerDefinition createCageLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 32, 16);
   }

   public void render(
      AncientCopperConduitTileEntity pBlockEntity,
      float partialTicks,
      @Nonnull PoseStack pPoseStack,
      @Nonnull MultiBufferSource pBufferSource,
      int pPackedLight,
      int pPackedOverlay
   ) {
      Level world = pBlockEntity.getLevel();
      pPackedLight = 15728880;
      if (world != null) {
         float f = pBlockEntity.tickCount + partialTicks;
         float f1 = pBlockEntity.getActiveRotation(partialTicks) * (180.0F / (float)Math.PI);
         pPoseStack.pushPose();
         pPoseStack.translate(0.5, 0.5, 0.5);
         Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
         vector3f.normalize();
         pPoseStack.pushPose();
         pPoseStack.mulPose(vector3f.rotationDegrees(f1));
         this.cage.render(pPoseStack, CONDUIT_OUTTER.buffer(pBufferSource, RenderType::entityCutoutNoCull), pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
         pPoseStack.pushPose();
         float scale = 1.5F;
         pPoseStack.scale(scale, scale, scale);
         pPoseStack.pushPose();
         pPoseStack.mulPose(vector3f.rotationDegrees(-f1));
         this.cage.render(pPoseStack, CONDUIT_OUTTER.buffer(pBufferSource, RenderType::entityCutoutNoCull), pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
         pPoseStack.pushPose();
         pPoseStack.scale(1.1F, 1.1F, 1.1F);
         pPoseStack.mulPose(vector3f.rotationDegrees(-f1));
         this.cage.render(pPoseStack, CONDUIT_OUTTER2.buffer(pBufferSource, RenderType::entityCutoutNoCull), pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
         pPoseStack.popPose();
         pPoseStack.popPose();
         int i = 1;
         pPoseStack.pushPose();
         pPoseStack.translate(0.5, 0.5, 0.5);
         pPoseStack.mulPose(vector3f.rotationDegrees(-f1));
         pPoseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
         VertexConsumer vertexconsumer = CONDUIT_WIND_VERTICAL.buffer(pBufferSource, RenderType::entityCutoutNoCull);
         this.wind.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
         pPoseStack.pushPose();
         pPoseStack.translate(0.5, 0.5, 0.5);
         pPoseStack.scale(0.875F, 0.875F, 0.875F);
         vertexconsumer = CONDUIT_WIND.buffer(pBufferSource, RenderType::entityCutoutNoCull);
         pPoseStack.mulPose(vector3f.rotationDegrees(-f1));
         this.wind.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
         Camera camera = this.renderer.camera;
         pPoseStack.pushPose();
         pPoseStack.translate(0.5, 0.5, 0.5);
         pPoseStack.scale(0.5F, 0.5F, 0.5F);
         float f3 = -camera.getYRot();
         pPoseStack.mulPose(Vector3f.YP.rotationDegrees(f3));
         pPoseStack.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
         pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         float f4 = 1.3333334F;
         pPoseStack.scale(f4, f4, f4);
         this.eye.render(pPoseStack, CONDUIT_CENTER.buffer(pBufferSource, RenderType::entityCutoutNoCull), pPackedLight, pPackedOverlay);
         pPoseStack.popPose();
      }
   }
}
