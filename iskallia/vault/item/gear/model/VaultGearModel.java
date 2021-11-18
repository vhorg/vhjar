package iskallia.vault.item.gear.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public abstract class VaultGearModel<T extends LivingEntity> extends BipedModel<T> {
   protected static final float VOXEL_SIZE = 0.0625F;
   protected final EquipmentSlotType slotType;
   protected ModelRenderer Head;
   protected ModelRenderer Body;
   protected ModelRenderer RightArm;
   protected ModelRenderer LeftArm;
   protected ModelRenderer RightLeg;
   protected ModelRenderer LeftLeg;
   protected ModelRenderer Belt;
   protected ModelRenderer RightBoot;
   protected ModelRenderer LeftBoot;

   public VaultGearModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, 0.0F, 64, 32);
      this.slotType = slotType;
   }

   public boolean isLayer2() {
      return this.slotType == EquipmentSlotType.LEGS;
   }

   protected void prepareForRender(
      MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
   }

   public void func_225598_a_(
      MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      matrixStack.func_227860_a_();
      this.prepareForRender(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      if (this.slotType == EquipmentSlotType.HEAD) {
         this.renderWithModelAngles(this.Head, this.field_78116_c, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      } else if (this.slotType == EquipmentSlotType.CHEST) {
         this.renderWithModelAngles(this.Body, this.field_78115_e, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
         this.renderWithModelAngles(this.RightArm, this.field_178723_h, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
         this.renderWithModelAngles(this.LeftArm, this.field_178724_i, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      } else if (this.slotType == EquipmentSlotType.LEGS) {
         this.renderWithModelAngles(this.Belt, this.field_78115_e, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
         this.renderWithModelAngles(this.RightLeg, this.field_178721_j, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
         this.renderWithModelAngles(this.LeftLeg, this.field_178722_k, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      } else if (this.slotType == EquipmentSlotType.FEET) {
         this.renderWithModelAngles(this.RightBoot, this.field_178721_j, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
         this.renderWithModelAngles(this.LeftBoot, this.field_178722_k, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      }

      matrixStack.func_227865_b_();
   }

   private void renderWithModelAngles(
      ModelRenderer renderer,
      ModelRenderer target,
      MatrixStack matrixStack,
      IVertexBuilder buffer,
      int packedLight,
      int packedOverlay,
      float red,
      float green,
      float blue,
      float alpha
   ) {
      if (renderer != null && target != null) {
         renderer.func_217177_a(target);
         renderer.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      }
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
