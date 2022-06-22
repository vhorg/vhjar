package iskallia.vault.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.entity.EyestalkEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class EyestalkModel extends SegmentedModel<EyestalkEntity> {
   private final ImmutableList<ModelRenderer> segments;
   private final ModelRenderer body;
   private final ModelRenderer tail;

   public EyestalkModel() {
      this.field_78090_t = 32;
      this.field_78089_u = 32;
      Builder<ModelRenderer> segmentBuilder = ImmutableList.builder();
      this.body = new ModelRenderer(this);
      this.body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.body.func_78784_a(0, 0).func_228303_a_(-4.0F, 4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
      this.tail = new ModelRenderer(this);
      this.tail.func_78793_a(0.0F, 11.5F, 1.0F);
      this.body.func_78792_a(this.tail);
      this.setRotationAngle(this.tail, 0.2618F, 0.0F, 0.0F);
      this.tail.func_78784_a(0, 16).func_228303_a_(-2.0F, -1.3775F, -2.0694F, 4.0F, 8.0F, 4.0F, 0.0F, false);
      segmentBuilder.add(this.tail);
      this.segments = segmentBuilder.build();
   }

   @Nonnull
   public Iterable<ModelRenderer> func_225601_a_() {
      return this.segments;
   }

   public void setRotationAngles(@Nonnull EyestalkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.tail.field_78795_f = 0.5F + 0.1F * MathHelper.func_76126_a(ageInTicks * 0.3F);
      this.body.field_78796_g = netHeadYaw * (float) (Math.PI / 180.0);
      this.body.field_78795_f = headPitch * (float) (Math.PI / 180.0);
   }

   public void func_225598_a_(
      @Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      this.body.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
