package iskallia.vault.block.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.GateLockBlock;
import iskallia.vault.block.entity.GateLockTileEntity;
import iskallia.vault.client.ClientStatisticsData;
import iskallia.vault.config.VaultModifierOverlayConfig;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.overlay.ModifiersRenderer;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GateLockRenderer implements BlockEntityRenderer<GateLockTileEntity> {
   private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
   private final Font font;
   private final ItemRenderer itemRenderer;

   public GateLockRenderer(Context context) {
      this.font = context.getFont();
      this.itemRenderer = Minecraft.getInstance().getItemRenderer();
   }

   public void render(GateLockTileEntity entity, float pPartialTick, PoseStack matrices, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
      if (entity.getGod() != null) {
         BlockState blockstate = entity.getBlockState();
         matrices.pushPose();
         float f = 0.6666667F;
         Vec3i vector = ((Direction)blockstate.getValue(GateLockBlock.FACING)).getNormal();
         matrices.translate(0.5 + 0.9 * vector.getX(), 0.5 + 0.9 * vector.getY(), 0.5 + 0.9 * vector.getZ());
         float f4 = -((Direction)blockstate.getValue(GateLockBlock.FACING)).toYRot();
         matrices.mulPose(Vector3f.YP.rotationDegrees(f4));
         matrices.translate(0.0, -0.3125, -0.4375);
         matrices.translate(0.0, 0.33333334F, 0.046666667F);
         matrices.scale(0.010416667F, -0.010416667F, 0.010416667F);
         matrices.pushPose();
         matrices.pushPose();
         matrices.scale(2.0F, 2.0F, 2.0F);
         this.renderLine(new TextComponent(entity.getName()).withStyle(Style.EMPTY.withColor(entity.getColor())), true, matrices, pBufferSource, pPackedLight);
         matrices.popPose();
         Minecraft minecraft = Minecraft.getInstance();
         List<ItemStack> items = minecraft.player.inventoryMenu.getItems().stream().<ItemStack>map(ItemStack::copy).toList();
         int count = entity.getModifiers().size();
         if (count > 0) {
            matrices.translate(0.0, 30.0, 0.0);
            matrices.pushPose();
            int right = minecraft.getWindow().getGuiScaledWidth();
            int bottom = minecraft.getWindow().getGuiScaledHeight();
            matrices.translate(-right, -bottom, 0.0);
            VaultModifierOverlayConfig config = ModConfigs.VAULT_MODIFIER_OVERLAY;
            matrices.translate(config.spacingX + (config.size + config.spacingX) * count / 2.0, 0.0, 0.0);
            ModifiersRenderer.renderVaultModifiers(entity.getModifiers(), matrices);
            matrices.popPose();
         }

         AtomicInteger index = new AtomicInteger(1);

         for (VaultModifierStack stack : entity.getModifiers()) {
            VaultModifierRegistry.getOpt(stack.getModifierId()).ifPresent(modifier -> {
               matrices.pushPose();
               matrices.translate(0.0, 10.0 * index.get(), 0.0);
               this.renderLine(modifier.getChatDisplayNameComponent(stack.getSize()), true, matrices, pBufferSource, pPackedLight);
               matrices.popPose();
               index.getAndIncrement();
            });
         }

         if (entity.getReputationCost() > 0) {
            matrices.translate(0.0, 10.0 * index.get(), 0.0);
            matrices.pushPose();
            int reputation = ClientStatisticsData.getReputation(entity.getGod());
            ChatFormatting form = reputation >= entity.getReputationCost() ? ChatFormatting.WHITE : ChatFormatting.RED;
            this.renderLine(
               new TextComponent("")
                  .append(new TextComponent(entity.getReputationCost() + " ").withStyle(form))
                  .append(new TextComponent(entity.getGod().getName()).withStyle(entity.getGod().getChatColor()))
                  .append(new TextComponent(" Reputation").withStyle(form)),
               true,
               matrices,
               pBufferSource,
               pPackedLight
            );
            matrices.popPose();
         }

         for (ItemStack stack : entity.getCost()) {
            matrices.translate(0.0, 45.0, 0.0);
            ChatFormatting color = this.check(items, stack.copy(), true) && this.check(items, stack.copy(), false) ? ChatFormatting.WHITE : ChatFormatting.RED;
            this.renderItemLine(
               stack,
               stack.getHoverName().copy().withStyle(color),
               new TextComponent((stack.getCount() < 10 ? " " : "") + stack.getCount()).withStyle(color),
               true,
               matrices,
               pBufferSource,
               pPackedLight
            );
         }

         matrices.popPose();
         matrices.popPose();
      }
   }

   private boolean check(List<ItemStack> items, ItemStack stack, boolean simulate) {
      int deductedAmount = 0;

      for (ItemStack plStack : items) {
         if (stack.getCount() <= 0) {
            return true;
         }

         if (isEqualCrafting(stack, plStack)) {
            deductedAmount += Math.min(stack.getCount(), plStack.getCount());
            stack.shrink(deductedAmount);
            if (!simulate) {
               plStack.shrink(deductedAmount);
            }
         }
      }

      return false;
   }

   private static boolean isEqualCrafting(ItemStack thisStack, ItemStack thatStack) {
      return thisStack.getItem() == thatStack.getItem()
         && thisStack.getDamageValue() == thatStack.getDamageValue()
         && (thisStack.getTag() == null || thisStack.areShareTagsEqual(thatStack));
   }

   public void renderLine(Component component, boolean centered, PoseStack matrices, MultiBufferSource source, int light) {
      FormattedCharSequence formatted = (FormattedCharSequence)this.font.split(component, 9000).get(0);
      float offsetX = centered ? (float)(-this.font.width(formatted) / 2.0) : 0.0F;
      float offsetY = centered ? (float)(-9 / 2.0) : 0.0F;
      RenderSystem.enableDepthTest();
      this.font.drawInBatch(formatted, offsetX, offsetY, 16777215, false, matrices.last().pose(), source, true, 0, light);
      RenderSystem.enableDepthTest();
      this.font.drawInBatch(formatted, offsetX, offsetY, -1, false, matrices.last().pose(), source, false, 0, light);
   }

   public void renderItemLine(ItemStack stack, Component text, Component count, boolean centered, PoseStack matrices, MultiBufferSource source, int light) {
      FormattedCharSequence formatted = (FormattedCharSequence)this.font.split(text, 9000).get(0);
      float offsetX = centered ? (float)(-this.font.width(formatted) / 2.0) + 15.0F : 0.0F;
      float offsetY = centered ? (float)(-9 / 2.0) : 0.0F;
      RenderSystem.enableDepthTest();
      this.font.drawInBatch(formatted, offsetX, offsetY, 16777215, false, matrices.last().pose(), source, true, 0, light);
      RenderSystem.enableDepthTest();
      this.font.drawInBatch(formatted, offsetX, offsetY, -1, false, matrices.last().pose(), source, false, 0, light);
      matrices.pushPose();
      matrices.translate(offsetX - 18.0F, -2.0, -0.4F);
      matrices.scale(24.0F, -24.0F, 1.0F);
      this.itemRenderer.renderStatic(stack, TransformType.GUI, light, OverlayTexture.NO_OVERLAY, matrices, source, light);
      matrices.scale(1.0F, -1.0F, 0.4F);
      matrices.translate(0.0, 0.0, 0.2F);
      formatted = (FormattedCharSequence)this.font.split(count, 9000).get(0);
      matrices.scale(0.045454547F, 0.045454547F, 1.0F);
      RenderSystem.enableDepthTest();
      this.font.drawInBatch(formatted, 1.0F, offsetY + 8.0F, 16777215, false, matrices.last().pose(), source, true, 0, light);
      RenderSystem.enableDepthTest();
      this.font.drawInBatch(formatted, 1.0F, offsetY + 8.0F, -1, false, matrices.last().pose(), source, false, 0, light);
      matrices.popPose();
   }

   private static int getDarkColor(GateLockTileEntity pBlockEntity) {
      int i = 16777215;
      boolean glowing = false;
      double d0 = 0.4;
      int j = (int)(NativeImage.getR(i) * 1.0);
      int k = (int)(NativeImage.getG(i) * 1.0);
      int l = (int)(NativeImage.getB(i) * 1.0);
      return i == DyeColor.BLACK.getTextColor() && glowing ? -988212 : NativeImage.combine(0, l, k, j);
   }

   private static boolean isOutlineVisible(GateLockTileEntity pBlockEntity, int pTextColor) {
      if (pTextColor == DyeColor.BLACK.getTextColor()) {
         return true;
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer localplayer = minecraft.player;
         if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping()) {
            return true;
         } else {
            Entity entity = minecraft.getCameraEntity();
            return entity != null && entity.distanceToSqr(Vec3.atCenterOf(pBlockEntity.getBlockPos())) < OUTLINE_RENDER_DISTANCE;
         }
      }
   }
}
