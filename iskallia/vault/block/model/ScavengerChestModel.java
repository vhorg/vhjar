package iskallia.vault.block.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ScavengerChestModel extends Model {
   private final ModelRenderer chest;
   private final ModelRenderer horn_R3_r1;
   private final ModelRenderer horn_R2_r1;
   private final ModelRenderer horn_R1_r1;
   private final ModelRenderer horn_L3_r1;
   private final ModelRenderer horn_L2_r1;
   private final ModelRenderer horn_L1_r1;
   private final ModelRenderer angle_R_r1;
   private final ModelRenderer angle_L_r1;
   private final ModelRenderer nose_r1;
   private final ModelRenderer eyelarge_r1;
   private final ModelRenderer eyesmall_r1;
   private final ModelRenderer lid;
   private final ModelRenderer bottom;

   public ScavengerChestModel() {
      super(RenderType::func_228638_b_);
      this.field_78090_t = 128;
      this.field_78089_u = 128;
      this.chest = new ModelRenderer(this);
      this.chest.func_78793_a(0.0F, 16.0F, 0.0F);
      this.setRotationAngle(this.chest, 0.0F, 0.0F, -3.1416F);
      this.lid = new ModelRenderer(this);
      this.lid.func_78793_a(0.0F, 1.0F, -7.0F);
      this.chest.func_78792_a(this.lid);
      this.lid.func_78784_a(0, 43).func_228303_a_(-4.0F, 0.25F, -1.0F, 8.0F, 5.0F, 16.0F, 0.0F, false);
      this.lid.func_78784_a(0, 24).func_228303_a_(-7.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F, false);
      this.horn_R3_r1 = new ModelRenderer(this);
      this.horn_R3_r1.func_78793_a(3.8366F, 3.3584F, 15.0F);
      this.lid.func_78792_a(this.horn_R3_r1);
      this.setRotationAngle(this.horn_R3_r1, 0.0F, 0.0F, -0.3491F);
      this.horn_R3_r1.func_78784_a(42, 0).func_228303_a_(-0.4013F, -0.3725F, -1.6F, 1.0F, 2.0F, 2.0F, 0.0F, false);
      this.horn_R2_r1 = new ModelRenderer(this);
      this.horn_R2_r1.func_78793_a(3.8366F, 3.3584F, 15.0F);
      this.lid.func_78792_a(this.horn_R2_r1);
      this.setRotationAngle(this.horn_R2_r1, 0.0F, 0.0F, -0.7418F);
      this.horn_R2_r1.func_78784_a(42, 0).func_228303_a_(-0.8131F, -1.6893F, -1.3F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      this.horn_R1_r1 = new ModelRenderer(this);
      this.horn_R1_r1.func_78793_a(3.8366F, 3.3584F, 15.0F);
      this.lid.func_78792_a(this.horn_R1_r1);
      this.setRotationAngle(this.horn_R1_r1, 0.0F, 0.0F, -1.4399F);
      this.horn_R1_r1.func_78784_a(42, 0).func_228303_a_(0.0469F, -2.5778F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      this.horn_L3_r1 = new ModelRenderer(this);
      this.horn_L3_r1.func_78793_a(-3.6253F, 3.3311F, 15.0F);
      this.lid.func_78792_a(this.horn_L3_r1);
      this.setRotationAngle(this.horn_L3_r1, 0.0F, 0.0F, 0.3491F);
      this.horn_L3_r1.func_78784_a(42, 0).func_228303_a_(-0.5983F, -0.4446F, -1.35F, 1.0F, 2.0F, 2.0F, 0.0F, false);
      this.horn_L2_r1 = new ModelRenderer(this);
      this.horn_L2_r1.func_78793_a(-3.6253F, 3.3311F, 15.0F);
      this.lid.func_78792_a(this.horn_L2_r1);
      this.setRotationAngle(this.horn_L2_r1, 0.0F, 0.0F, 0.7418F);
      this.horn_L2_r1.func_78784_a(42, 0).func_228303_a_(-1.0105F, -1.9365F, -1.3F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      this.horn_L1_r1 = new ModelRenderer(this);
      this.horn_L1_r1.func_78793_a(-3.6253F, 3.3311F, 15.0F);
      this.lid.func_78792_a(this.horn_L1_r1);
      this.setRotationAngle(this.horn_L1_r1, 0.0F, 0.0F, 1.4399F);
      this.horn_L1_r1.func_78784_a(42, 0).func_228303_a_(-2.0469F, -2.5483F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      this.angle_R_r1 = new ModelRenderer(this);
      this.angle_R_r1.func_78793_a(-0.0127F, -0.2087F, 15.4708F);
      this.lid.func_78792_a(this.angle_R_r1);
      this.setRotationAngle(this.angle_R_r1, -0.0295F, -0.0322F, -0.8286F);
      this.angle_R_r1.func_78784_a(2, 1).func_228303_a_(0.813F, -1.0545F, -0.7208F, 1.0F, 2.0F, 1.0F, 0.0F, false);
      this.angle_L_r1 = new ModelRenderer(this);
      this.angle_L_r1.func_78793_a(-0.0127F, -0.2087F, 15.4708F);
      this.lid.func_78792_a(this.angle_L_r1);
      this.setRotationAngle(this.angle_L_r1, -0.0295F, 0.0322F, 0.8286F);
      this.angle_L_r1.func_78784_a(2, 0).func_228303_a_(-1.8568F, -1.0258F, -0.7208F, 1.0F, 2.0F, 1.0F, 0.0F, false);
      this.nose_r1 = new ModelRenderer(this);
      this.nose_r1.func_78793_a(-0.0127F, -0.2087F, 15.4708F);
      this.lid.func_78792_a(this.nose_r1);
      this.setRotationAngle(this.nose_r1, -0.0436F, 0.0F, 0.0F);
      this.nose_r1.func_78784_a(0, 10).func_228303_a_(-0.9873F, -2.7913F, -0.4708F, 2.0F, 2.0F, 1.0F, 0.0F, false);
      this.eyelarge_r1 = new ModelRenderer(this);
      this.eyelarge_r1.func_78793_a(-0.0127F, -0.2087F, 15.7208F);
      this.lid.func_78792_a(this.eyelarge_r1);
      this.setRotationAngle(this.eyelarge_r1, -0.0436F, 0.0F, 0.0F);
      this.eyelarge_r1.func_78784_a(0, 29).func_228303_a_(0.5127F, 0.4587F, -0.4708F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.eyelarge_r1.func_78784_a(0, 0).func_228303_a_(-1.9873F, -0.7913F, -1.7208F, 4.0F, 4.0F, 2.0F, 0.0F, false);
      this.eyesmall_r1 = new ModelRenderer(this);
      this.eyesmall_r1.func_78793_a(-1.0F, 0.8221F, 16.2055F);
      this.lid.func_78792_a(this.eyesmall_r1);
      this.setRotationAngle(this.eyesmall_r1, -0.0436F, 0.0F, 0.0F);
      this.eyesmall_r1.func_78784_a(0, 29).func_228303_a_(-0.5F, -0.5721F, -0.9555F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.bottom = new ModelRenderer(this);
      this.bottom.func_78793_a(0.0F, 8.0F, 0.0F);
      this.chest.func_78792_a(this.bottom);
      this.bottom.func_78784_a(40, 27).func_228303_a_(-4.0F, -16.0F, -8.0F, 8.0F, 9.0F, 16.0F, 0.0F, false);
      this.bottom.func_78784_a(0, 0).func_228303_a_(-7.0F, -16.0F, -7.0F, 14.0F, 10.0F, 14.0F, 0.0F, false);
   }

   private void setRotationAngle(ModelRenderer model, float x, float y, float z) {
      model.field_78795_f = x;
      model.field_78796_g = y;
      model.field_78808_h = z;
   }

   public void func_225598_a_(
      MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      this.lid.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.bottom.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
   }

   public void setLidAngle(float lidAngle) {
      this.lid.field_78795_f = -(lidAngle * (float) (Math.PI / 2));
   }
}
