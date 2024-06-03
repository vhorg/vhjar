package iskallia.vault.block.entity.hologram.model;

import iskallia.vault.core.data.adapter.Adapters;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MirrorMeshBuilderAction extends MeshBuilderAction {
   private Boolean mirrored;

   public MirrorMeshBuilderAction() {
   }

   public MirrorMeshBuilderAction(boolean mirrored) {
      this.mirrored = mirrored;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void apply(MeshBuilder builder) {
      if (this.mirrored != null) {
         builder.setMirrored(this.mirrored);
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.BOOLEAN.writeNbt(this.mirrored).ifPresent(tag -> nbt.put("mirrored", tag));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.mirrored = Adapters.BOOLEAN.readNbt(nbt.get("mirrored")).orElse(null);
   }
}
