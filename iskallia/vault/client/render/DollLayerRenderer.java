package iskallia.vault.client.render;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.IntegrationCurios;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class DollLayerRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   private final PlayerModel<Player> alexMiniModel;
   private final PlayerModel<Player> steveMiniModel;

   public DollLayerRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
      super(renderer);
      EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
      this.alexMiniModel = new PlayerModel<Player>(entityModels.bakeLayer(ModelLayers.PLAYER_SLIM), true) {
         protected Iterable<ModelPart> headParts() {
            return List.of(this.head, this.hat);
         }
      };
      this.setModelProperties(this.alexMiniModel);
      this.steveMiniModel = new PlayerModel<Player>(entityModels.bakeLayer(ModelLayers.PLAYER), false) {
         protected Iterable<ModelPart> headParts() {
            return List.of(this.head, this.hat);
         }
      };
      this.setModelProperties(this.steveMiniModel);
   }

   private void setModelProperties(PlayerModel<Player> model) {
      model.young = true;
      float rotation = (float) (-Math.PI / 2);
      model.leftLeg.xRot = rotation;
      model.rightLeg.xRot = rotation;
      model.leftPants.xRot = rotation;
      model.rightPants.xRot = rotation;
   }

   @SubscribeEvent
   public static void on(RegisterClientReloadListenersEvent event) {
      event.registerReloadListener((ResourceManagerReloadListener)resourceManager -> registerDollLayer());
   }

   private static void registerDollLayer() {
      EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
      Map<String, EntityRenderer<? extends Player>> skinMap = renderManager.getSkinMap();

      for (EntityRenderer<? extends Player> renderer : skinMap.values()) {
         if (renderer instanceof PlayerRenderer playerRenderer) {
            playerRenderer.addLayer(new DollLayerRenderer(playerRenderer));
         }
      }
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      AbstractClientPlayer player,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      ItemStack doll = IntegrationCurios.getItemFromCuriosHeadSlot(player, stack -> stack.getItem() == ModItems.VAULT_DOLL);
      if (!doll.isEmpty()) {
         Minecraft minecraft = Minecraft.getInstance();
         DollISTER.getPlayerProfileTexture(minecraft, doll)
            .ifPresent(
               profileTexture -> {
                  ResourceLocation playerSkin = minecraft.getSkinManager().registerTexture(profileTexture, Type.SKIN);
                  String metadata = profileTexture.getMetadata("model");
                  boolean slimSkin = metadata != null && !metadata.equals("default");
                  poseStack.pushPose();
                  ((PlayerModel)this.getParentModel()).head.translateAndRotate(poseStack);
                  ItemStack helmetItem = player.getItemBySlot(EquipmentSlot.HEAD);
                  double offset = -1.1 - (helmetItem.isEmpty() ? 0.0F : ModConfigs.VAULT_ITEMS.VAULT_DOLL.getHelmetOffset(helmetItem.getItem()));
                  poseStack.translate(0.0, offset, 0.0);
                  poseStack.scale(0.5F, 0.5F, 0.5F);
                  if (slimSkin) {
                     this.alexMiniModel
                        .renderToBuffer(
                           poseStack,
                           buffer.getBuffer(RenderType.entityTranslucent(playerSkin)),
                           packedLight,
                           OverlayTexture.NO_OVERLAY,
                           1.0F,
                           1.0F,
                           1.0F,
                           1.0F
                        );
                  } else {
                     this.steveMiniModel
                        .renderToBuffer(
                           poseStack,
                           buffer.getBuffer(RenderType.entityTranslucent(playerSkin)),
                           packedLight,
                           OverlayTexture.NO_OVERLAY,
                           1.0F,
                           1.0F,
                           1.0F,
                           1.0F
                        );
                  }

                  poseStack.popPose();
               }
            );
      }
   }
}
