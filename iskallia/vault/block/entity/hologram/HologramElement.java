package iskallia.vault.block.entity.hologram;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class HologramElement implements ISerializable<CompoundTag, JsonObject> {
   public static final HologramElement.Adapter ADAPTER = new HologramElement.Adapter();
   protected Vec3 translation = Vec3.ZERO;
   protected Vec3 rotation = Vec3.ZERO;
   protected Vec3 scale = new Vec3(1.0, 1.0, 1.0);
   protected List<HologramElement> children = new ArrayList<>();

   public Vec3 getTranslation() {
      return this.translation;
   }

   public <T extends HologramElement> T setTranslation(Vec3 translation) {
      this.translation = translation;
      return (T)this;
   }

   public Vec3 getEulerRotation() {
      return this.rotation;
   }

   public Quaternion getQuaternionRotation() {
      return this.getQuaternionRotation(this.getEulerRotation());
   }

   public Quaternion getQuaternionRotation(Vec3 eulerRotation) {
      return Quaternion.fromYXZ(
         (float)(eulerRotation.y * Math.PI / 180.0), (float)(eulerRotation.x * Math.PI / 180.0), (float)(eulerRotation.z * Math.PI / 180.0)
      );
   }

   public <T extends HologramElement> T setEulerRotation(Vec3 rotation) {
      this.rotation = new Vec3(rotation.x, Mth.wrapDegrees(rotation.y), Mth.wrapDegrees(rotation.z));
      return (T)this;
   }

   public Vec3 getScale() {
      return this.scale;
   }

   public <T extends HologramElement> T setScale(Vec3 scale) {
      this.scale = scale;
      return (T)this;
   }

   public <T extends HologramElement> T setScale(double scale) {
      this.scale = new Vec3(scale, scale, scale);
      return (T)this;
   }

   public List<HologramElement> getChildren() {
      return this.children;
   }

   public <T extends HologramElement> T add(HologramElement... elements) {
      this.children.addAll(Arrays.asList(elements));
      return (T)this;
   }

   public <T> void iterate(Class<T> type, Consumer<T> runnable) {
      if (type.isAssignableFrom(this.getClass())) {
         runnable.accept((T)this);
      }

      for (HologramElement child : this.children) {
         child.iterate(type, runnable);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void render(PoseStack matrices, MultiBufferSource bufferSource, float partialTick, int light, int overlay) {
      matrices.pushPose();
      matrices.translate(this.translation.x, this.translation.y, this.translation.z);
      matrices.mulPose(this.getQuaternionRotation());
      matrices.scale((float)this.scale.x, (float)this.scale.y, (float)this.scale.z);
      this.renderInternal(matrices, bufferSource, partialTick, light, overlay);

      for (HologramElement child : this.children) {
         child.render(matrices, bufferSource, partialTick, light, overlay);
      }

      matrices.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   protected void renderInternal(PoseStack matrices, MultiBufferSource bufferSource, float partialTick, int light, int overlay) {
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      CompoundTag translation = new CompoundTag();
      Adapters.DOUBLE.writeNbt(Double.valueOf(this.translation.x)).ifPresent(tag -> translation.put("x", tag));
      Adapters.DOUBLE.writeNbt(Double.valueOf(this.translation.y)).ifPresent(tag -> translation.put("y", tag));
      Adapters.DOUBLE.writeNbt(Double.valueOf(this.translation.z)).ifPresent(tag -> translation.put("z", tag));
      nbt.put("translation", translation);
      CompoundTag rotation = new CompoundTag();
      Adapters.DOUBLE.writeNbt(Double.valueOf(this.rotation.x)).ifPresent(tag -> rotation.put("pitch", tag));
      Adapters.DOUBLE.writeNbt(Double.valueOf(this.rotation.y)).ifPresent(tag -> rotation.put("yaw", tag));
      Adapters.DOUBLE.writeNbt(Double.valueOf(this.rotation.z)).ifPresent(tag -> rotation.put("roll", tag));
      nbt.put("rotation", rotation);
      CompoundTag scale = new CompoundTag();
      Adapters.DOUBLE.writeNbt(Double.valueOf(this.scale.x)).ifPresent(tag -> scale.put("x", tag));
      Adapters.DOUBLE.writeNbt(Double.valueOf(this.scale.y)).ifPresent(tag -> scale.put("y", tag));
      Adapters.DOUBLE.writeNbt(Double.valueOf(this.scale.z)).ifPresent(tag -> scale.put("z", tag));
      nbt.put("scale", scale);
      ListTag elements = new ListTag();

      for (HologramElement child : this.children) {
         ADAPTER.writeNbt(child).ifPresent(elements::add);
      }

      nbt.put("children", elements);
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.setTranslation(
         new Vec3(
            Adapters.DOUBLE.readNbt(nbt.getCompound("translation").get("x")).orElse(0.0),
            Adapters.DOUBLE.readNbt(nbt.getCompound("translation").get("y")).orElse(0.0),
            Adapters.DOUBLE.readNbt(nbt.getCompound("translation").get("z")).orElse(0.0)
         )
      );
      this.setEulerRotation(
         new Vec3(
            Adapters.DOUBLE.readNbt(nbt.getCompound("rotation").get("pitch")).orElse(0.0),
            Adapters.DOUBLE.readNbt(nbt.getCompound("rotation").get("yaw")).orElse(0.0),
            Adapters.DOUBLE.readNbt(nbt.getCompound("rotation").get("roll")).orElse(0.0)
         )
      );
      this.setScale(
         new Vec3(
            Adapters.DOUBLE.readNbt(nbt.getCompound("scale").get("x")).orElse(1.0),
            Adapters.DOUBLE.readNbt(nbt.getCompound("scale").get("y")).orElse(1.0),
            Adapters.DOUBLE.readNbt(nbt.getCompound("scale").get("z")).orElse(1.0)
         )
      );
      this.children = new ArrayList<>();

      for (Tag tag : nbt.getList("children", 10)) {
         if (tag instanceof CompoundTag child) {
            ADAPTER.readNbt(child).ifPresent(this.children::add);
         }
      }
   }

   public static class Adapter extends TypeSupplierAdapter<HologramElement> {
      public Adapter() {
         super("type", true);
         this.register("root", RootHologramElement.class, RootHologramElement::new);
         this.register("model", ModelHologramElement.class, ModelHologramElement::new);
         this.register("text", TextHologramElement.class, TextHologramElement::new);
         this.register("item", ItemHologramElement.class, ItemHologramElement::new);
      }
   }
}
