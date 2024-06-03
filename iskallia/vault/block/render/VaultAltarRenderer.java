package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItems;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class VaultAltarRenderer implements BlockEntityRenderer<VaultAltarTileEntity> {
   private final Minecraft mc = Minecraft.getInstance();

   public VaultAltarRenderer(Context context) {
   }

   public void render(VaultAltarTileEntity altar, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
      if (altar.getAltarState() != null && altar.getAltarState() != VaultAltarTileEntity.AltarState.IDLE) {
         Level level = altar.getLevel();
         if (level != null) {
            LocalPlayer player = this.mc.player;
            if (player != null) {
               int lightLevel = this.getLightAtPos(altar.getLevel(), altar.getBlockPos().above());
               this.renderItem(
                  altar.getInput(),
                  new double[]{0.5, 1.35, 0.5},
                  1.0F,
                  Vector3f.YP.rotationDegrees(180.0F - player.getYRot()),
                  matrixStack,
                  buffer,
                  partialTicks,
                  combinedOverlay,
                  lightLevel
               );
               if (altar.getRecipe() != null && !altar.getRecipe().getRequiredItems().isEmpty()) {
                  AltarInfusionRecipe recipe = altar.getRecipe();
                  List<RequiredItems> items = recipe.getRequiredItems();
                  Map<String, RequiredItems> itemMap = new HashMap<>();
                  items.forEach(requiredItemsx -> itemMap.put(requiredItemsx.getPoolId(), requiredItemsx));

                  for (int idIndex = 0; idIndex < itemMap.keySet().size(); idIndex++) {
                     String id = (String)itemMap.keySet().toArray()[idIndex];
                     RequiredItems requiredItems = itemMap.get(id);
                     double[] translation = this.getTranslation(idIndex);
                     List<ItemStack> stacks = requiredItems.getItems();
                     TextComponent text = new TextComponent(String.valueOf(requiredItems.getAmountRequired() - requiredItems.getCurrentAmount()));
                     int textColor = 16777215;
                     if (requiredItems.isComplete()) {
                        text = new TextComponent("Complete");
                        textColor = 65280;
                     }

                     if (!stacks.isEmpty() && !altar.getDisplayedIndex().isEmpty() && altar.getDisplayedIndex().containsKey(id)) {
                        int index = altar.getDisplayedIndex().get(id);
                        ItemStack toRender = ItemStack.EMPTY;
                        if (index >= 0 && index < stacks.size()) {
                           toRender = stacks.get(index);
                        }

                        this.renderItem(
                           toRender,
                           translation,
                           0.5F,
                           Vector3f.YP.rotationDegrees(this.getAngle(player, partialTicks) * 5.0F),
                           matrixStack,
                           buffer,
                           partialTicks,
                           combinedOverlay,
                           lightLevel
                        );
                        this.renderLabel(toRender, matrixStack, buffer, lightLevel, translation, text, textColor);
                     }
                  }
               }
            }
         }
      }
   }

   private void renderItem(
      ItemStack stack,
      double[] translation,
      float scale,
      Quaternion rotation,
      PoseStack matrixStack,
      MultiBufferSource buffer,
      float partialTicks,
      int combinedOverlay,
      int lightLevel
   ) {
      matrixStack.pushPose();
      matrixStack.translate(translation[0], translation[1], translation[2]);
      matrixStack.mulPose(rotation);
      matrixStack.scale(scale, scale, scale);
      BakedModel ibakedmodel = this.mc.getItemRenderer().getModel(stack, null, null, 0);
      this.mc.getItemRenderer().render(stack, TransformType.GROUND, true, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
      matrixStack.popPose();
   }

   private void renderLabel(ItemStack stack, PoseStack matrixStack, MultiBufferSource buffer, int lightLevel, double[] corner, TextComponent text, int color) {
      Font fontRenderer = this.mc.font;
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         matrixStack.pushPose();
         float scale = 0.01F;
         int opacity = 1711276032;
         float offset = -fontRenderer.width(text) / 2;
         Matrix4f matrix4f = matrixStack.last().pose();
         matrixStack.translate(corner[0], corner[1] + 0.25, corner[2]);
         matrixStack.scale(scale, scale, scale);
         matrixStack.mulPose(this.mc.getEntityRenderDispatcher().cameraOrientation());
         matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         fontRenderer.drawInBatch(text, offset, 0.0F, color, false, matrix4f, buffer, false, opacity, lightLevel);
         if (player.isShiftKeyDown()) {
            Component itemName = stack.getHoverName();
            offset = -fontRenderer.width(itemName) / 2;
            matrixStack.translate(0.0, 1.4F, 0.0);
            matrix4f.translate(new Vector3f(0.0F, 0.15F, 0.0F));
            fontRenderer.drawInBatch(stack.getHoverName(), offset, 0.0F, color, false, matrix4f, buffer, false, opacity, lightLevel);
         }

         matrixStack.popPose();
      }
   }

   private float getAngle(LocalPlayer player, float partialTicks) {
      float currentTick = player.tickCount;
      return (currentTick + partialTicks) % 360.0F;
   }

   private int getLightAtPos(Level world, BlockPos pos) {
      int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
      int skyLight = world.getBrightness(LightLayer.SKY, pos);
      return LightTexture.pack(blockLight, skyLight);
   }

   private double[] getTranslation(int index) {
      return switch (index) {
         case 0 -> new double[]{0.95, 1.35, 0.05};
         case 1 -> new double[]{0.95, 1.35, 0.95};
         case 2 -> new double[]{0.05, 1.35, 0.95};
         default -> new double[]{0.05, 1.35, 0.05};
      };
   }
}
