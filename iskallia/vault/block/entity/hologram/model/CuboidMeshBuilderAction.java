package iskallia.vault.block.entity.hologram.model;

import com.mojang.math.Vector3f;
import iskallia.vault.core.data.adapter.Adapters;
import java.util.Optional;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CuboidMeshBuilderAction extends MeshBuilderAction {
   private String name;
   private Boolean mirrored;
   private Vec2 texture;
   private Vec2 scale;
   private Vector3f position;
   private Vector3f size;
   private Vector3f dilation;

   public CuboidMeshBuilderAction() {
   }

   public CuboidMeshBuilderAction(String name, Boolean mirrored, Vec2 texture, Vec2 scale, Vector3f position, Vector3f size, Vector3f dilation) {
      this.name = name;
      this.mirrored = mirrored;
      this.texture = texture;
      this.scale = scale;
      this.position = position;
      this.size = size;
      this.dilation = dilation;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void apply(MeshBuilder builder) {
      builder.getCuboids()
         .add(
            new CubeDefinition(
               this.name,
               this.texture != null ? this.texture.x : builder.getTexture().x,
               this.texture != null ? this.texture.y : builder.getTexture().y,
               this.position.x(),
               this.position.y(),
               this.position.z(),
               this.size.x(),
               this.size.y(),
               this.size.z(),
               new CubeDeformation(this.dilation.x(), this.dilation.y(), this.dilation.z()),
               this.mirrored != null ? this.mirrored : builder.isMirrored(),
               this.scale.x,
               this.scale.y
            )
         );
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.UTF_8.writeNbt(this.name).ifPresent(tag -> nbt.put("name", tag));
      Adapters.BOOLEAN.writeNbt(this.mirrored).ifPresent(tag -> nbt.put("mirrored", tag));
      if (this.texture != null) {
         CompoundTag texture = new CompoundTag();
         Adapters.FLOAT.writeNbt(Float.valueOf(this.texture.x)).ifPresent(tag -> texture.put("u", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.texture.y)).ifPresent(tag -> texture.put("v", tag));
         nbt.put("texture", texture);
      }

      if (!this.scale.equals(Vec2.ONE)) {
         CompoundTag scale = new CompoundTag();
         Adapters.FLOAT.writeNbt(Float.valueOf(this.scale.x)).ifPresent(tag -> scale.put("u", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.scale.y)).ifPresent(tag -> scale.put("v", tag));
         nbt.put("scale", scale);
      }

      if (!this.position.equals(Vector3f.ZERO)) {
         CompoundTag position = new CompoundTag();
         Adapters.FLOAT.writeNbt(Float.valueOf(this.position.x())).ifPresent(tag -> position.put("x", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.position.y())).ifPresent(tag -> position.put("y", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.position.z())).ifPresent(tag -> position.put("z", tag));
         nbt.put("position", position);
      }

      if (!this.position.equals(new Vector3f(1.0F, 1.0F, 1.0F))) {
         CompoundTag size = new CompoundTag();
         Adapters.FLOAT.writeNbt(Float.valueOf(this.size.x())).ifPresent(tag -> size.put("x", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.size.y())).ifPresent(tag -> size.put("y", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.size.z())).ifPresent(tag -> size.put("z", tag));
         nbt.put("size", size);
      }

      if (!this.position.equals(Vector3f.ZERO)) {
         CompoundTag dilation = new CompoundTag();
         Adapters.FLOAT.writeNbt(Float.valueOf(this.dilation.x())).ifPresent(tag -> dilation.put("x", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.dilation.y())).ifPresent(tag -> dilation.put("y", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.dilation.z())).ifPresent(tag -> dilation.put("z", tag));
         nbt.put("dilation", dilation);
      }

      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.name = Adapters.UTF_8.readNbt(nbt.get("name")).orElse(null);
      this.mirrored = Adapters.BOOLEAN.readNbt(nbt.get("mirrored")).orElse(null);
      this.texture = nbt.get("texture") instanceof CompoundTag texture
         ? new Vec2(Adapters.FLOAT.readNbt(texture.get("u")).orElse(0.0F), Adapters.FLOAT.readNbt(texture.get("v")).orElse(0.0F))
         : null;
      this.scale = new Vec2(
         Adapters.FLOAT.readNbt(nbt.getCompound("scale").get("u")).orElse(1.0F), Adapters.FLOAT.readNbt(nbt.getCompound("scale").get("v")).orElse(1.0F)
      );
      this.position = new Vector3f(
         Adapters.FLOAT.readNbt(nbt.getCompound("position").get("x")).orElse(0.0F),
         Adapters.FLOAT.readNbt(nbt.getCompound("position").get("y")).orElse(0.0F),
         Adapters.FLOAT.readNbt(nbt.getCompound("position").get("z")).orElse(0.0F)
      );
      this.size = new Vector3f(
         Adapters.FLOAT.readNbt(nbt.getCompound("size").get("x")).orElse(1.0F),
         Adapters.FLOAT.readNbt(nbt.getCompound("size").get("y")).orElse(1.0F),
         Adapters.FLOAT.readNbt(nbt.getCompound("size").get("z")).orElse(1.0F)
      );
      this.dilation = new Vector3f(
         Adapters.FLOAT.readNbt(nbt.getCompound("dilation").get("x")).orElse(0.0F),
         Adapters.FLOAT.readNbt(nbt.getCompound("dilation").get("y")).orElse(0.0F),
         Adapters.FLOAT.readNbt(nbt.getCompound("dilation").get("z")).orElse(0.0F)
      );
   }
}
