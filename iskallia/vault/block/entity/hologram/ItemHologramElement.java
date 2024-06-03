package iskallia.vault.block.entity.hologram;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.data.adapter.Adapters;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ItemHologramElement extends HologramElement {
   private ItemStack stack;
   private boolean xCentered;
   private boolean yCentered;

   public ItemHologramElement() {
   }

   public ItemHologramElement(ItemStack stack, boolean xCentered, boolean yCentered) {
      this.stack = stack;
      this.xCentered = xCentered;
      this.yCentered = yCentered;
   }

   @Override
   protected void renderInternal(PoseStack matrices, MultiBufferSource bufferSource, float partialTick, int light, int overlay) {
      super.renderInternal(matrices, bufferSource, partialTick, light, overlay);
      ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
      matrices.pushPose();
      matrices.mulPose(this.getQuaternionRotation(new Vec3(0.0, 0.0, 180.0)));
      renderer.renderStatic(this.stack, TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrices, bufferSource, light);
      matrices.popPose();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.ITEM_STACK.writeNbt(this.stack).ifPresent(tag -> nbt.put("stack", tag));
         Adapters.BOOLEAN.writeNbt(this.xCentered).ifPresent(tag -> nbt.put("x_centered", tag));
         Adapters.BOOLEAN.writeNbt(this.yCentered).ifPresent(tag -> nbt.put("y_centered", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.stack = Adapters.ITEM_STACK.readNbt(nbt.get("stack")).orElse(ItemStack.EMPTY);
      this.xCentered = Adapters.BOOLEAN.readNbt(nbt.get("x_centered")).orElse(false);
      this.yCentered = Adapters.BOOLEAN.readNbt(nbt.get("y_centered")).orElse(false);
   }
}
