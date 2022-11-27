package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.renderer.VaultArmorRenderProperties;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.calc.BlockChanceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerRenderer.class})
public abstract class MixinPlayerRenderer {
   private static boolean resolvingPose = false;

   @Shadow
   private static ArmPose getArmPose(AbstractClientPlayer player, InteractionHand hand) {
      return null;
   }

   @Inject(
      method = {"getArmPose"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void overrideArmAnimation(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<ArmPose> cir) {
      if (BlockChanceHelper.isPlayerBlocking(player)) {
         if (!resolvingPose) {
            ArmPose normalPose;
            try {
               resolvingPose = true;
               normalPose = getArmPose(player, hand);
            } finally {
               resolvingPose = false;
            }

            if (normalPose != null) {
               if (normalPose == ArmPose.ITEM) {
                  ItemStack stack = player.getItemInHand(hand);
                  if (!stack.isEmpty() && stack.getItem() instanceof ShieldItem) {
                     cir.setReturnValue(ArmPose.BLOCK);
                  }
               }
            }
         }
      }
   }

   @Inject(
      method = {"renderHand"},
      at = {@At("TAIL")}
   )
   private void renderVaultArmorSleeve(
      PoseStack pMatrixStack,
      MultiBufferSource pBuffer,
      int pCombinedLight,
      AbstractClientPlayer pPlayer,
      ModelPart pRendererArm,
      ModelPart pRendererArmwear,
      CallbackInfo ci
   ) {
      ItemStack chestplateStack = (ItemStack)pPlayer.getInventory().armor.get(2);
      if (chestplateStack.getItem() instanceof VaultGearItem vaultArmorItem) {
         VaultGearData gearData = VaultGearData.read(chestplateStack);
         gearData.getFirstValue(ModGearAttributes.GEAR_MODEL)
            .map(VaultArmorRenderProperties.BAKED_LAYERS::get)
            .filter(layer -> layer instanceof ArmorLayers.MainLayer)
            .map(layer -> (ArmorLayers.MainLayer)layer)
            .ifPresent(mainLayer -> {
               String baseTexture = vaultArmorItem.getArmorTexture(chestplateStack, null, chestplateStack.getEquipmentSlot(), null);
               String overlayTexture = vaultArmorItem.getArmorTexture(chestplateStack, null, chestplateStack.getEquipmentSlot(), "overlay");
               ModelPart armPart = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT ? mainLayer.getRightArm() : mainLayer.getLeftArm();
               pMatrixStack.pushPose();
               mainLayer.adjustForFirstPersonRender(pMatrixStack);
               armPart.xRot = 0.0F;
               if (baseTexture != null) {
                  VertexConsumer baseVertexConsumer = pBuffer.getBuffer(mainLayer.renderType(new ResourceLocation(baseTexture)));
                  armPart.render(pMatrixStack, baseVertexConsumer, pCombinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
               }

               if (overlayTexture != null) {
                  VertexConsumer overlayVertexConsumer = pBuffer.getBuffer(mainLayer.renderType(new ResourceLocation(overlayTexture)));
                  armPart.render(pMatrixStack, overlayVertexConsumer, pCombinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
               }

               pMatrixStack.popPose();
            });
      }
   }
}
