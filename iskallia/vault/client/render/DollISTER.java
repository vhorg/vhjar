package iskallia.vault.client.render;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.item.VaultDollItem;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.client.ForgeHooksClient;

public class DollISTER extends BlockEntityWithoutLevelRenderer {
   public static final DollISTER INSTANCE = new DollISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
   private static final String UPDATING_SKIN_TAG = "updatingSkin";
   private static final String DEFAULT_SKIN_TAG = "defaultSkin";
   private final PlayerModel<Player> alexMiniModel;
   private final PlayerModel<Player> steveMiniModel;

   private DollISTER(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
      super(blockEntityRenderDispatcher, entityModelSet);
      this.alexMiniModel = new PlayerModel<Player>(entityModelSet.bakeLayer(ModelLayers.PLAYER_SLIM), true) {
         protected Iterable<ModelPart> headParts() {
            return List.of(this.head, this.hat);
         }
      };
      this.alexMiniModel.young = true;
      this.steveMiniModel = new PlayerModel<Player>(entityModelSet.bakeLayer(ModelLayers.PLAYER), false) {
         protected Iterable<ModelPart> headParts() {
            return List.of(this.head, this.hat);
         }
      };
      this.steveMiniModel.young = true;
   }

   public void renderByItem(ItemStack stack, TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      poseStack.popPose();
      poseStack.pushPose();
      poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      poseStack.translate(0.0, -0.5, 0.0);
      Minecraft minecraft = Minecraft.getInstance();
      boolean slimSkin = false;
      ResourceLocation playerSkin = DefaultPlayerSkin.getDefaultSkin();
      Optional<MinecraftProfileTexture> pt = getPlayerProfileTexture(minecraft, stack);
      if (pt.isPresent()) {
         MinecraftProfileTexture profileTexture = pt.get();
         playerSkin = minecraft.getSkinManager().registerTexture(profileTexture, Type.SKIN);
         String metadata = profileTexture.getMetadata("model");
         slimSkin = metadata != null && !metadata.equals("default");
      }

      ItemRenderer itemRenderer = minecraft.getItemRenderer();
      BakedModel model = itemRenderer.getModel(stack, null, minecraft.player, 0);
      boolean leftHand = minecraft.player != null && minecraft.player.getOffhandItem() == stack;
      ForgeHooksClient.handleCameraTransforms(poseStack, model, transformType, leftHand);
      if (slimSkin) {
         this.alexMiniModel
            .renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(playerSkin)), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.steveMiniModel
            .renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(playerSkin)), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public static Optional<MinecraftProfileTexture> getPlayerProfileTexture(Minecraft minecraft, ItemStack stack) {
      return VaultDollItem.getPlayerGameProfile(stack).flatMap(gameProfile -> {
         CompoundTag tag = stack.getOrCreateTag();
         if (!tag.contains("updatingSkin") && !tag.contains("defaultSkin")) {
            if (!gameProfile.getProperties().containsKey("textures")) {
               tag.putBoolean("updatingSkin", true);
               SkullBlockEntity.updateGameprofile(gameProfile, gp -> {
                  VaultDollItem.setGameProfile(stack.getOrCreateTag(), gp);
                  CompoundTag t = stack.getOrCreateTag();
                  if (!gp.getProperties().containsKey("textures")) {
                     t.putBoolean("defaultSkin", true);
                  }

                  t.remove("updatingSkin");
               });
               return Optional.empty();
            } else {
               SkinManager skinManager = minecraft.getSkinManager();
               Map<Type, MinecraftProfileTexture> skinInfo = skinManager.getInsecureSkinInformation(gameProfile);
               if (skinInfo.containsKey(Type.SKIN)) {
                  MinecraftProfileTexture profileTexture = skinInfo.get(Type.SKIN);
                  return Optional.of(profileTexture);
               } else {
                  return Optional.empty();
               }
            }
         } else {
            return Optional.empty();
         }
      });
   }
}
