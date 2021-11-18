package iskallia.vault.item.gear.specials;

import iskallia.vault.item.gear.model.VaultGearModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class ImmersiveEngineeringArmorModel<T extends LivingEntity> extends VaultGearModel<T> {
   public ImmersiveEngineeringArmorModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.field_78090_t = this.isLayer2() ? 64 : 128;
      this.field_78089_u = this.isLayer2() ? 32 : 128;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(0, 0).func_228303_a_(-8.0F, -15.0F, -8.0F, 16.0F, 17.0F, 16.0F, 0.0F, false);
      this.Head.func_78784_a(0, 0).func_228303_a_(-11.0F, -10.0F, -2.0F, 3.0F, 10.0F, 4.0F, 0.0F, false);
      this.Head.func_78784_a(0, 0).func_228303_a_(8.0F, -10.0F, -2.0F, 3.0F, 10.0F, 4.0F, 0.0F, false);
      this.Head.func_78784_a(0, 33).func_228303_a_(-2.0F, -9.0F, -10.0F, 4.0F, 6.0F, 4.0F, 0.0F, false);
      this.Head.func_78784_a(18, 71).func_228303_a_(-7.0F, -7.0F, -9.0F, 4.0F, 4.0F, 3.0F, 0.0F, false);
      this.Head.func_78784_a(18, 71).func_228303_a_(3.0F, -7.0F, -9.0F, 4.0F, 4.0F, 3.0F, 0.0F, false);
      this.Head.func_78784_a(24, 56).func_228303_a_(-7.0F, -13.0F, -10.0F, 14.0F, 4.0F, 4.0F, 0.0F, false);
      this.Head.func_78784_a(64, 48).func_228303_a_(-7.0F, -2.0F, -9.0F, 14.0F, 4.0F, 3.0F, 0.0F, false);
      this.Body = new ModelRenderer(this);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Body.func_78784_a(32, 66).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
      this.Body.func_78784_a(0, 33).func_228303_a_(-8.0F, 2.0F, -8.0F, 16.0F, 7.0F, 16.0F, 0.0F, false);
      this.Body.func_78784_a(48, 0).func_228303_a_(-6.0F, 9.0F, -5.0F, 12.0F, 3.0F, 10.0F, 0.0F, false);
      this.RightArm = new ModelRenderer(this);
      this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.RightArm.func_78784_a(64, 13).func_228303_a_(-7.0F, 1.0F, -4.0F, 6.0F, 6.0F, 8.0F, 0.0F, false);
      this.RightArm.func_78784_a(6, 43).func_228303_a_(-1.0F, 13.0F, -1.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
      this.RightArm.func_78784_a(48, 6).func_228303_a_(-2.0F, 16.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
      this.RightArm.func_78784_a(10, 0).func_228303_a_(-6.0F, 16.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
      this.RightArm.func_78784_a(0, 43).func_228303_a_(-7.0F, 13.0F, -1.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
      this.RightArm.func_78784_a(0, 71).func_228303_a_(-6.0F, 9.0F, -4.0F, 5.0F, 5.0F, 8.0F, 0.0F, false);
      this.LeftArm = new ModelRenderer(this);
      this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
      this.LeftArm.func_78784_a(52, 56).func_228303_a_(1.0F, 1.0F, -4.0F, 6.0F, 6.0F, 8.0F, 0.0F, false);
      this.LeftArm.func_78784_a(56, 70).func_228303_a_(1.0F, 9.0F, -4.0F, 5.0F, 5.0F, 8.0F, 0.0F, false);
      this.LeftArm.func_78784_a(48, 33).func_228303_a_(6.0F, 13.0F, -1.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
      this.LeftArm.func_78784_a(48, 0).func_228303_a_(0.0F, 13.0F, -1.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
      this.LeftArm.func_78784_a(52, 4).func_228303_a_(1.0F, 16.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
      this.LeftArm.func_78784_a(48, 13).func_228303_a_(5.0F, 16.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
      this.RightBoot = new ModelRenderer(this);
      this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightBoot.func_78784_a(0, 56).func_228303_a_(-4.0F, 7.0F, -4.0F, 8.0F, 7.0F, 8.0F, 0.0F, false);
      this.RightBoot.func_78784_a(72, 32).func_228303_a_(-4.0F, 11.0F, -6.0F, 8.0F, 3.0F, 2.0F, 0.0F, false);
      this.LeftBoot = new ModelRenderer(this);
      this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftBoot.func_78784_a(64, 27).func_228303_a_(-3.8F, 11.0F, -6.0F, 8.0F, 3.0F, 2.0F, 0.0F, false);
      this.LeftBoot.func_78784_a(48, 33).func_228303_a_(-3.8F, 7.0F, -4.0F, 8.0F, 7.0F, 8.0F, 0.0F, false);
      this.Belt = new ModelRenderer(this);
      this.Belt.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Belt.func_78784_a(16, 16).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);
      this.RightLeg = new ModelRenderer(this);
      this.RightLeg.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
      this.LeftLeg = new ModelRenderer(this);
      this.LeftLeg.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
   }
}
