package iskallia.vault.block.entity.hologram.model;

import iskallia.vault.core.data.adapter.Adapters;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TextureMeshBuilderAction extends MeshBuilderAction {
   private Vec2 texture;

   public TextureMeshBuilderAction() {
   }

   public TextureMeshBuilderAction(float textureX, float textureY) {
      this.texture = new Vec2(textureX, textureY);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void apply(MeshBuilder builder) {
      if (this.texture != null) {
         builder.setTexture(this.texture);
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      CompoundTag texture = new CompoundTag();
      Adapters.FLOAT.writeNbt(Float.valueOf(this.texture.x)).ifPresent(tag -> texture.put("u", tag));
      Adapters.FLOAT.writeNbt(Float.valueOf(this.texture.y)).ifPresent(tag -> texture.put("v", tag));
      nbt.put("texture", texture);
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.texture = nbt.get("texture") instanceof CompoundTag texture
         ? new Vec2(Adapters.FLOAT.readNbt(texture.get("u")).orElse(0.0F), Adapters.FLOAT.readNbt(texture.get("v")).orElse(0.0F))
         : Vec2.ZERO;
   }
}
