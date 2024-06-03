package iskallia.vault.block.entity.hologram.model;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MeshPart implements ISerializable<CompoundTag, JsonObject> {
   private String name;
   private Vector3f pivot;
   private Vector3f rotation;
   private List<MeshBuilderAction> builder;
   private List<MeshPart> children;

   public MeshPart() {
   }

   public MeshPart(String name) {
      this.name = name;
      this.pivot = Vector3f.ZERO;
      this.rotation = Vector3f.ZERO;
      this.builder = new ArrayList<>();
      this.children = new ArrayList<>();
   }

   public MeshPart uv(float u, float v) {
      this.builder.add(new TextureMeshBuilderAction(u, v));
      return this;
   }

   public MeshPart mirror(boolean mirrored) {
      this.builder.add(new MirrorMeshBuilderAction(mirrored));
      return this;
   }

   public MeshPart cuboid(String name, Vector3f position, Vector3f size, Vector3f dilation, Vec2 texture) {
      this.builder.add(new CuboidMeshBuilderAction(name, null, texture, Vec2.ONE, position, size, dilation));
      return this;
   }

   public MeshPart cuboid(String name, Vector3f position, Vector3f size, Vec2 texture) {
      this.builder.add(new CuboidMeshBuilderAction(name, null, texture, Vec2.ONE, position, size, Vector3f.ZERO));
      return this;
   }

   public MeshPart cuboid(Vector3f position, Vector3f size) {
      this.builder.add(new CuboidMeshBuilderAction(null, null, null, Vec2.ONE, position, size, Vector3f.ZERO));
      return this;
   }

   public MeshPart cuboid(String name, Vector3f position, Vector3f size) {
      this.builder.add(new CuboidMeshBuilderAction(name, null, null, Vec2.ONE, position, size, Vector3f.ZERO));
      return this;
   }

   public MeshPart cuboid(String name, Vector3f position, Vector3f size, Vector3f dilation) {
      this.builder.add(new CuboidMeshBuilderAction(name, null, null, Vec2.ONE, position, size, dilation));
      return this;
   }

   public MeshPart cuboid(Vector3f position, Vector3f size, boolean mirrored) {
      this.builder.add(new CuboidMeshBuilderAction(null, mirrored, null, Vec2.ONE, position, size, Vector3f.ZERO));
      return this;
   }

   public MeshPart cuboid(Vector3f position, Vector3f size, Vector3f dilation, Vec2 scale) {
      this.builder.add(new CuboidMeshBuilderAction(null, null, null, scale, position, size, dilation));
      return this;
   }

   public MeshPart cuboid(Vector3f position, Vector3f size, Vector3f dilation) {
      this.builder.add(new CuboidMeshBuilderAction(null, null, null, Vec2.ONE, position, size, dilation));
      return this;
   }

   public MeshPart setPivot(Vector3f pivot) {
      this.pivot = pivot;
      return this;
   }

   public MeshPart setRotation(Vector3f rotation) {
      this.rotation = rotation;
      return this;
   }

   public MeshPart add(MeshPart child) {
      this.children.add(child);
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public void attach(PartDefinition root) {
      MeshBuilder builder = new MeshBuilder().build(this.builder);
      PartDefinition self = new PartDefinition(
         builder.getCuboids(),
         PartPose.offsetAndRotation(this.pivot.x(), this.pivot.y(), this.pivot.z(), this.rotation.x(), this.rotation.y(), this.rotation.z())
      );
      PartDefinition oldSelf = root.children.put(this.name, self);
      if (oldSelf != null) {
         self.children.putAll(oldSelf.children);
      }

      for (MeshPart child : this.children) {
         child.attach(self);
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.UTF_8.writeNbt(this.name).ifPresent(tag -> nbt.put("name", tag));
      CompoundTag pivot = new CompoundTag();
      Adapters.FLOAT.writeNbt(Float.valueOf(this.pivot.x())).ifPresent(tag -> pivot.put("x", tag));
      Adapters.FLOAT.writeNbt(Float.valueOf(this.pivot.y())).ifPresent(tag -> pivot.put("y", tag));
      Adapters.FLOAT.writeNbt(Float.valueOf(this.pivot.z())).ifPresent(tag -> pivot.put("z", tag));
      nbt.put("pivot", pivot);
      CompoundTag rotation = new CompoundTag();
      Adapters.FLOAT.writeNbt(Float.valueOf(this.rotation.x())).ifPresent(tag -> rotation.put("pitch", tag));
      Adapters.FLOAT.writeNbt(Float.valueOf(this.rotation.y())).ifPresent(tag -> rotation.put("yaw", tag));
      Adapters.FLOAT.writeNbt(Float.valueOf(this.rotation.z())).ifPresent(tag -> rotation.put("roll", tag));
      nbt.put("rotation", rotation);
      ListTag builder = new ListTag();

      for (MeshBuilderAction action : this.builder) {
         MeshBuilderAction.ADAPTER.writeNbt(action).ifPresent(builder::add);
      }

      nbt.put("builder", builder);
      ListTag children = new ListTag();

      for (MeshPart child : this.children) {
         child.writeNbt().ifPresent(children::add);
      }

      nbt.put("children", children);
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.name = Adapters.UTF_8.readNbt(nbt.get("name")).orElse(null);
      this.pivot = nbt.get("pivot") instanceof CompoundTag pivot
         ? new Vector3f(
            Adapters.FLOAT.readNbt(pivot.get("x")).orElse(0.0F),
            Adapters.FLOAT.readNbt(pivot.get("y")).orElse(0.0F),
            Adapters.FLOAT.readNbt(pivot.get("z")).orElse(0.0F)
         )
         : Vector3f.ZERO;
      this.rotation = nbt.get("rotation") instanceof CompoundTag rotation
         ? new Vector3f(
            Adapters.FLOAT.readNbt(rotation.get("x")).orElse(0.0F),
            Adapters.FLOAT.readNbt(rotation.get("y")).orElse(0.0F),
            Adapters.FLOAT.readNbt(rotation.get("z")).orElse(0.0F)
         )
         : Vector3f.ZERO;
      this.builder = new ArrayList<>();

      for (Tag tag : nbt.getList("builder", 10)) {
         if (tag instanceof CompoundTag action) {
            MeshBuilderAction.ADAPTER.readNbt(action).ifPresent(this.builder::add);
         }
      }

      this.children = new ArrayList<>();

      for (Tag tagx : nbt.getList("children", 10)) {
         if (tagx instanceof CompoundTag compound) {
            MeshPart child = new MeshPart();
            child.readNbt(compound);
            this.children.add(child);
         }
      }
   }
}
