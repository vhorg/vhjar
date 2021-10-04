package iskallia.vault.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.entity.TreasureGoblinEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class TreasureGoblinModel extends PlayerModel<TreasureGoblinEntity> {
   public TreasureGoblinModel() {
      super(1.0F, false);
      this.field_78090_t = 64;
      this.field_78089_u = 64;
      this.field_78116_c = new ModelRenderer(this);
      this.field_78116_c.func_78793_a(0.0F, 0.0F, 0.0F);
      this.field_78116_c.func_78784_a(0, 0).func_228303_a_(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
      this.field_78116_c.func_78784_a(0, 26).func_228303_a_(-1.0F, -2.0F, -7.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
      this.field_78116_c.func_78784_a(0, 21).func_228303_a_(-4.0F, -4.0F, -6.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer ear6_r1 = new ModelRenderer(this);
      ear6_r1.func_78793_a(6.375F, -4.875F, 2.125F);
      this.field_78116_c.func_78792_a(ear6_r1);
      this.setRotationAngle(ear6_r1, 0.0F, 0.3927F, 0.0F);
      ear6_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -2.125F, 0.875F, 0.0F, 1.0F, 2.0F, 0.0F, false);
      ear6_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -1.125F, -1.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
      ear6_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -0.125F, -2.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
      ear6_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, 0.875F, -3.125F, 0.0F, 2.0F, 3.0F, 0.0F, false);
      ModelRenderer ear5_r1 = new ModelRenderer(this);
      ear5_r1.func_78793_a(-6.625F, -4.875F, 2.125F);
      this.field_78116_c.func_78792_a(ear5_r1);
      this.setRotationAngle(ear5_r1, 0.0F, -0.7854F, 0.0F);
      ear5_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -2.125F, 0.875F, 0.0F, 1.0F, 2.0F, 0.0F, false);
      ear5_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -1.125F, -1.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
      ear5_r1.func_78784_a(0, 0).func_228303_a_(-0.125F, -0.125F, -2.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
      ear5_r1.func_78784_a(0, 1).func_228303_a_(-0.125F, 0.875F, -3.125F, 0.0F, 2.0F, 3.0F, 0.0F, false);
      this.field_78115_e = new ModelRenderer(this, 16, 16);
      this.field_78115_e.func_78793_a(0.0F, 0.0F, 0.0F);
      this.field_78115_e.func_228300_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F);
      this.field_78115_e.func_78793_a(0.0F, 0.0F, 0.0F);
      this.field_178723_h = new ModelRenderer(this, 40, 16);
      this.field_178723_h.func_228300_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.field_178723_h.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.field_178724_i = new ModelRenderer(this, 40, 16);
      this.field_178724_i.field_78809_i = true;
      this.field_178724_i.func_228300_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.field_178724_i.func_78793_a(5.0F, 2.0F, 0.0F);
      this.field_178721_j = new ModelRenderer(this, 0, 16);
      this.field_178721_j.func_228300_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.field_178721_j.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.field_178722_k = new ModelRenderer(this, 0, 16);
      this.field_178722_k.field_78809_i = true;
      this.field_178722_k.func_228300_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.field_178722_k.func_78793_a(1.9F, 12.0F, 0.0F);
   }

   public void setRotationAngles(
      TreasureGoblinEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netbipedHeadYaw, float bipedHeadPitch
   ) {
      super.func_225597_a_(entity, limbSwing, limbSwingAmount, ageInTicks, netbipedHeadYaw, bipedHeadPitch);
   }

   public void func_225598_a_(
      MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      matrixStack.func_227860_a_();
      this.field_78116_c.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.field_78115_e.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.field_178723_h.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.field_178724_i.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.field_178721_j.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.field_178722_k.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      matrixStack.func_227865_b_();
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
