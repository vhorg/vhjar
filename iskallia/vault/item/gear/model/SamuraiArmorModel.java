package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class SamuraiArmorModel<T extends LivingEntity> extends VaultGearModel<T> {
   public SamuraiArmorModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(0, 0).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
      this.Head.func_78784_a(36, 3).func_228303_a_(-1.5F, -10.0F, -5.0F, 3.0F, 1.0F, 0.0F, 0.0F, false);
      this.Head.func_78784_a(36, 3).func_228303_a_(-2.5F, -11.0F, -5.0F, 2.0F, 1.0F, 0.0F, 0.0F, false);
      this.Head.func_78784_a(37, 4).func_228303_a_(0.5F, -11.0F, -5.0F, 2.0F, 1.0F, 0.0F, 0.0F, false);
      this.Head.func_78784_a(38, 6).func_228303_a_(2.5F, -12.0F, -5.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);
      this.Head.func_78784_a(40, 6).func_228303_a_(-3.5F, -12.0F, -5.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);
      this.Body = new ModelRenderer(this);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Body.func_78784_a(16, 16).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(-1.5F, 1.0F, -3.5F, 3.0F, 4.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(-1.5F, 1.0F, 2.5F, 3.0F, 4.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(-2.5F, 0.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(-2.5F, 0.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(1.5F, 0.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(1.5F, 0.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(2.5F, -1.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(2.5F, -1.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(-3.5F, -1.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(-3.5F, -1.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(-4.5F, -1.0F, -3.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(-4.5F, -1.0F, 2.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(3.5F, -1.0F, -3.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(56, 0).func_228303_a_(3.5F, -1.0F, 2.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
      this.RightArm = new ModelRenderer(this);
      this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.RightArm.func_78784_a(40, 16).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      ModelRenderer shoulderpad_r1 = new ModelRenderer(this);
      shoulderpad_r1.func_78793_a(-1.5F, -2.75F, -0.5F);
      this.RightArm.func_78792_a(shoulderpad_r1);
      this.setRotationAngle(shoulderpad_r1, 0.0F, 0.0F, 0.3927F);
      shoulderpad_r1.func_78784_a(36, 7).func_228303_a_(-3.0F, -1.0F, -3.0F, 5.0F, 2.0F, 7.0F, 0.0F, false);
      this.LeftArm = new ModelRenderer(this);
      this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
      this.LeftArm.func_78784_a(40, 16).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);
      ModelRenderer shoulderpad_r2 = new ModelRenderer(this);
      shoulderpad_r2.func_78793_a(2.0381F, -2.9413F, 0.0F);
      this.LeftArm.func_78792_a(shoulderpad_r2);
      this.setRotationAngle(shoulderpad_r2, 0.0F, 0.0F, -0.3491F);
      shoulderpad_r2.func_78784_a(36, 7).func_228303_a_(-2.5F, -1.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);
      this.RightLeg = new ModelRenderer(this);
      this.RightLeg.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftLeg = new ModelRenderer(this);
      this.LeftLeg.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);
      this.Belt = new ModelRenderer(this);
      this.Belt.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Belt.func_78784_a(16, 16).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);
      this.RightBoot = new ModelRenderer(this);
      this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightBoot.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
      this.LeftBoot = new ModelRenderer(this);
      this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftBoot.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
   }
}
