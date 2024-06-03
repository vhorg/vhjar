package iskallia.vault.block.entity.hologram;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.data.adapter.Adapters;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;

public class TextHologramElement extends HologramElement {
   private Component text;
   private boolean xCentered;
   private boolean yCentered;
   private int color;

   public TextHologramElement() {
   }

   public TextHologramElement(Component text, boolean xCentered, boolean yCentered, int color) {
      this.text = text;
      this.xCentered = xCentered;
      this.yCentered = yCentered;
      this.color = color;
   }

   @Override
   protected void renderInternal(PoseStack matrices, MultiBufferSource bufferSource, float partialTick, int light, int overlay) {
      super.renderInternal(matrices, bufferSource, partialTick, light, overlay);
      Font font = Minecraft.getInstance().font;
      float x = this.xCentered ? -font.width(this.text) / 2.0F : 0.0F;
      float y = this.yCentered ? -9 / 2.0F : 0.0F;
      font.draw(matrices, this.text, x, y, this.color);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         nbt.putString("text", Serializer.toJson(this.text));
         Adapters.BOOLEAN.writeNbt(this.xCentered).ifPresent(tag -> nbt.put("x_centered", tag));
         Adapters.BOOLEAN.writeNbt(this.yCentered).ifPresent(tag -> nbt.put("y_centered", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.color)).ifPresent(tag -> nbt.put("color", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.text = Serializer.fromJson(nbt.getString("text"));
      this.xCentered = Adapters.BOOLEAN.readNbt(nbt.get("x_centered")).orElse(false);
      this.yCentered = Adapters.BOOLEAN.readNbt(nbt.get("y_centered")).orElse(false);
      this.color = Adapters.INT.readNbt(nbt.get("color")).orElse(16777215);
   }
}
