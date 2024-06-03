package iskallia.vault.block.entity.hologram.model;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class MeshBuilderAction implements ISerializable<CompoundTag, JsonObject> {
   public static final MeshBuilderAction.Adapter ADAPTER = new MeshBuilderAction.Adapter();

   @OnlyIn(Dist.CLIENT)
   public abstract void apply(MeshBuilder var1);

   public static class Adapter extends TypeSupplierAdapter<MeshBuilderAction> {
      public Adapter() {
         super("type", true);
         this.register("texture", TextureMeshBuilderAction.class, TextureMeshBuilderAction::new);
         this.register("mirror", MirrorMeshBuilderAction.class, MirrorMeshBuilderAction::new);
         this.register("cuboid", CuboidMeshBuilderAction.class, CuboidMeshBuilderAction::new);
      }
   }
}
