package iskallia.vault.block.entity.hologram;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.hologram.model.MeshPart;
import iskallia.vault.core.data.adapter.Adapters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModelHologramElement extends HologramElement {
   private ResourceLocation textureId;
   private int textureWidth;
   private int textureHeight;
   private int color;
   private List<MeshPart> mesh;
   @OnlyIn(Dist.CLIENT)
   private ModelHologramElement.DelegatedModel cache;

   public ModelHologramElement() {
   }

   public ModelHologramElement(ResourceLocation textureId, int textureWidth, int textureHeight, int color, MeshPart... mesh) {
      this.textureId = textureId;
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.color = color;
      this.mesh = new ArrayList<>(Arrays.asList(mesh));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   protected void renderInternal(PoseStack matrices, MultiBufferSource bufferSource, float partialTick, int light, int overlay) {
      if (this.cache == null) {
         MeshDefinition data = new MeshDefinition();
         PartDefinition root = data.getRoot();

         for (MeshPart part : this.mesh) {
            part.attach(root);
         }

         this.cache = new ModelHologramElement.DelegatedModel(LayerDefinition.create(data, this.textureWidth, this.textureHeight).bakeRoot());
      }

      VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(this.textureId));
      this.cache
         .renderToBuffer(
            matrices,
            buffer,
            light,
            overlay,
            (this.color >> 16 & 0xFF) / 255.0F,
            (this.color >>> 8 & 0xFF) / 255.0F,
            (this.color & 0xFF) / 255.0F,
            (this.color >>> 24) / 255.0F
         );
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      CompoundTag texture = new CompoundTag();
      Adapters.IDENTIFIER.writeNbt(this.textureId).ifPresent(tag -> texture.put("id", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.textureWidth)).ifPresent(tag -> texture.put("width", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.textureHeight)).ifPresent(tag -> texture.put("height", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.color)).ifPresent(tag -> texture.put("color", tag));
      nbt.put("texture", texture);
      ListTag mesh = new ListTag();

      for (MeshPart part : this.mesh) {
         part.writeNbt().ifPresent(mesh::add);
      }

      nbt.put("mesh", mesh);
      return Optional.of(nbt);
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.textureId = Adapters.IDENTIFIER.readNbt(nbt.getCompound("texture").get("id")).orElseGet(() -> VaultMod.id("textures/block/error_texture"));
      this.textureWidth = Adapters.INT.readNbt(nbt.getCompound("texture").get("width")).orElse(2);
      this.textureHeight = Adapters.INT.readNbt(nbt.getCompound("texture").get("height")).orElse(2);
      this.color = Adapters.INT.readNbt(nbt.getCompound("texture").get("color")).orElse(-1);
      this.mesh = new ArrayList<>();

      for (Tag tag : nbt.getList("mesh", 10)) {
         if (tag instanceof CompoundTag compound) {
            MeshPart part = new MeshPart();
            part.readNbt(compound);
            this.mesh.add(part);
         }
      }
   }

   public static class DelegatedModel extends Model {
      private final ModelPart root;

      public DelegatedModel(ModelPart root) {
         super(RenderType::entityTranslucent);
         this.root = root;
      }

      public void renderToBuffer(PoseStack matrices, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
         this.root.render(matrices, buffer, light, overlay, red, green, blue, alpha);
      }
   }
}
