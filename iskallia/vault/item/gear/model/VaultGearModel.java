package iskallia.vault.item.gear.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public abstract class VaultGearModel<T extends LivingEntity> extends BipedModel<T> {
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
      return this.slotType == EquipmentSlotType.LEGS || this.slotType == EquipmentSlotType.FEET;
   }

   public void func_225598_a_(
      MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      matrixStack.func_227860_a_();
      if (this.slotType == EquipmentSlotType.HEAD) {
         this.Head.func_217177_a(this.field_78116_c);
         this.Head.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      } else if (this.slotType == EquipmentSlotType.CHEST) {
         this.Body.func_217177_a(this.field_78115_e);
         this.Body.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
         this.RightArm.func_217177_a(this.field_178723_h);
         this.RightArm.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
         this.LeftArm.func_217177_a(this.field_178724_i);
         this.LeftArm.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      } else if (this.slotType == EquipmentSlotType.LEGS) {
         this.Belt.func_217177_a(this.field_78115_e);
         this.Belt.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
         this.RightLeg.func_217177_a(this.field_178721_j);
         this.RightLeg.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
         this.LeftLeg.func_217177_a(this.field_178722_k);
         this.LeftLeg.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      } else if (this.slotType == EquipmentSlotType.FEET) {
         this.RightBoot.func_217177_a(this.field_178721_j);
         this.RightBoot.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
         this.LeftBoot.func_217177_a(this.field_178722_k);
         this.LeftBoot.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      }

      matrixStack.func_227865_b_();
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
