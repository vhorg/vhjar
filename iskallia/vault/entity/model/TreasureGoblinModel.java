package iskallia.vault.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.entity.TreasureGoblinEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class TreasureGoblinModel extends EntityModel<TreasureGoblinEntity> {
   private final ModelRenderer Head;
   private final ModelRenderer ear6_r1;
   private final ModelRenderer ear5_r1;
   private final ModelRenderer Body;
   private final ModelRenderer RightArm;
   private final ModelRenderer LeftArm;
   private final ModelRenderer RightLeg;
   private final ModelRenderer LeftLeg;

   public TreasureGoblinModel() {
      this.field_78090_t = 64;
      this.field_78089_u = 64;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(0, 0).func_228303_a_(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
      this.Head.func_78784_a(0, 26).func_228303_a_(-1.0F, -2.0F, -7.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
      this.Head.func_78784_a(0, 21).func_228303_a_(-4.0F, -4.0F, -6.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);
      this.ear6_r1 = new ModelRenderer(this);
      this.ear6_r1.func_78793_a(6.375F, -4.875F, 2.125F);
      this.Head.func_78792_a(this.ear6_r1);
      this.setRotationAngle(this.ear6_r1, 0.0F, 0.3927F, 0.0F);
      this.ear6_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -2.125F, 0.875F, 0.0F, 1.0F, 2.0F, 0.0F, false);
      this.ear6_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -1.125F, -1.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
      this.ear6_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -0.125F, -2.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
      this.ear6_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, 0.875F, -3.125F, 0.0F, 2.0F, 3.0F, 0.0F, false);
      this.ear5_r1 = new ModelRenderer(this);
      this.ear5_r1.func_78793_a(-6.625F, -4.875F, 2.125F);
      this.Head.func_78792_a(this.ear5_r1);
      this.setRotationAngle(this.ear5_r1, 0.0F, -0.7854F, 0.0F);
      this.ear5_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -2.125F, 0.875F, 0.0F, 1.0F, 2.0F, 0.0F, false);
      this.ear5_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -1.125F, -1.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
      this.ear5_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -0.125F, -2.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
      this.ear5_r1.func_78784_a(0, 1).func_228303_a_(-0.125F, 0.875F, -3.125F, 0.0F, 2.0F, 3.0F, 0.0F, false);
      this.Body = new ModelRenderer(this, 16, 16);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Body.func_228300_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.RightArm = new ModelRenderer(this, 40, 16);
      this.RightArm.func_228300_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.LeftArm = new ModelRenderer(this, 40, 16);
      this.LeftArm.field_78809_i = true;
      this.LeftArm.func_228300_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
      this.RightLeg = new ModelRenderer(this, 0, 16);
      this.RightLeg.func_228300_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.RightLeg.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.LeftLeg = new ModelRenderer(this, 0, 16);
      this.LeftLeg.field_78809_i = true;
      this.LeftLeg.func_228300_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.LeftLeg.func_78793_a(1.9F, 12.0F, 0.0F);
   }

   public void setRotationAngles(TreasureGoblinEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void func_225598_a_(
      MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.0, 0.75, 0.0);
      matrixStack.func_227862_a_(0.5F, 0.5F, 0.5F);
      this.Head.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.Body.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.RightArm.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.LeftArm.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.RightLeg.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.LeftLeg.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      matrixStack.func_227865_b_();
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
