package iskallia.vault.item.gear.specials;

import iskallia.vault.item.gear.model.VaultGearModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class ThermalArmorModel<T extends LivingEntity> extends VaultGearModel<T> {
   public ThermalArmorModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.field_78090_t = this.isLayer2() ? 64 : 128;
      this.field_78089_u = this.isLayer2() ? 32 : 128;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(64, 49).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
      this.Head.func_78784_a(76, 19).func_228303_a_(-3.0F, -10.0F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
      this.Head.func_78784_a(88, 47).func_228303_a_(-5.0F, -9.0F, -6.0F, 10.0F, 4.0F, 1.0F, 0.0F, false);
      this.Head.func_78784_a(18, 69).func_228303_a_(-5.0F, -9.0F, 5.0F, 10.0F, 5.0F, 1.0F, 0.0F, false);
      this.Body = new ModelRenderer(this);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Body.func_78784_a(16, 92).func_228303_a_(1.5F, -7.0F, 7.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
      this.Body.func_78784_a(88, 52).func_228303_a_(-5.5F, -7.0F, 7.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
      this.Body.func_78784_a(24, 76).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
      this.Body.func_78784_a(32, 12).func_228303_a_(-5.0F, -4.0F, -6.0F, 10.0F, 9.0F, 12.0F, 0.0F, false);
      this.Body.func_78784_a(64, 19).func_228303_a_(-4.0F, 5.0F, -4.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(32, 92).func_228303_a_(-3.0F, 5.0F, 3.0F, 6.0F, 3.0F, 2.0F, 0.0F, false);
      this.Body.func_78784_a(70, 27).func_228303_a_(-6.5F, -6.0F, 6.0F, 6.0F, 14.0F, 6.0F, 0.0F, false);
      this.Body.func_78784_a(0, 69).func_228303_a_(0.5F, -6.0F, 6.0F, 6.0F, 14.0F, 6.0F, 0.0F, false);
      this.Body.func_78784_a(6, 11).func_228303_a_(2.5F, 7.75F, 8.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);
      this.Body.func_78784_a(30, 33).func_228303_a_(3.0F, 8.75F, 8.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(68, 78).func_228303_a_(3.0F, 9.75F, 2.75F, 1.0F, 1.0F, 7.0F, 0.0F, false);
      this.Body.func_78784_a(60, 40).func_228303_a_(-4.0F, 9.75F, 2.75F, 1.0F, 1.0F, 7.0F, 0.0F, false);
      this.Body.func_78784_a(30, 12).func_228303_a_(-4.0F, 8.75F, 8.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(0, 10).func_228303_a_(-4.5F, 7.75F, 8.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);
      ModelRenderer cube_r1 = new ModelRenderer(this);
      cube_r1.func_78793_a(0.0F, 8.5F, 3.5F);
      this.Body.func_78792_a(cube_r1);
      this.setRotationAngle(cube_r1, -0.3927F, 0.0F, 0.0F);
      cube_r1.func_78784_a(34, 0).func_228303_a_(-1.0F, -1.5F, -0.5F, 2.0F, 5.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r2 = new ModelRenderer(this);
      cube_r2.func_78793_a(0.0F, 7.6455F, -3.1695F);
      this.Body.func_78792_a(cube_r2);
      this.setRotationAngle(cube_r2, 0.3491F, 0.0F, 0.0F);
      cube_r2.func_78784_a(0, 0).func_228303_a_(-2.0F, -3.5F, -1.5F, 4.0F, 7.0F, 3.0F, 0.0F, false);
      this.RightArm = new ModelRenderer(this);
      this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.RightArm.func_78784_a(0, 89).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.RightArm.func_78784_a(0, 24).func_228303_a_(-8.0F, -8.0F, -7.0F, 8.0F, 10.0F, 14.0F, 0.0F, false);
      this.RightArm.func_78784_a(0, 48).func_228303_a_(-8.0F, 4.0F, -6.0F, 8.0F, 9.0F, 12.0F, 0.0F, false);
      this.RightArm.func_78784_a(76, 78).func_228303_a_(-6.0F, -9.0F, -4.0F, 6.0F, 1.0F, 8.0F, 0.0F, false);
      this.RightArm.func_78784_a(88, 0).func_228303_a_(-5.0F, -10.0F, -3.0F, 5.0F, 1.0F, 6.0F, 0.0F, false);
      this.RightArm.func_78784_a(62, 66).func_228303_a_(-7.0F, 2.0F, -5.0F, 6.0F, 2.0F, 10.0F, 0.0F, false);
      this.RightArm.func_78784_a(30, 35).func_228303_a_(-9.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.RightArm.func_78784_a(30, 0).func_228303_a_(-10.0F, -1.0F, 2.0F, 1.0F, 9.0F, 1.0F, 0.0F, false);
      this.RightArm.func_78784_a(33, 34).func_228303_a_(-9.0F, 7.0F, 2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.RightArm.func_78784_a(34, 8).func_228303_a_(-9.0F, 7.0F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.RightArm.func_78784_a(8, 24).func_228303_a_(-10.0F, -1.0F, -3.0F, 1.0F, 9.0F, 1.0F, 0.0F, false);
      this.RightArm.func_78784_a(34, 12).func_228303_a_(-9.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.LeftArm = new ModelRenderer(this);
      this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
      this.LeftArm.func_78784_a(30, 0).func_228303_a_(1.0F, 2.0F, -5.0F, 6.0F, 2.0F, 10.0F, 0.0F, false);
      this.LeftArm.func_78784_a(32, 36).func_228303_a_(0.0F, 4.0F, -6.0F, 8.0F, 9.0F, 12.0F, 0.0F, false);
      this.LeftArm.func_78784_a(48, 78).func_228303_a_(0.0F, -9.0F, -4.0F, 6.0F, 1.0F, 8.0F, 0.0F, false);
      this.LeftArm.func_78784_a(0, 0).func_228303_a_(0.0F, -8.0F, -7.0F, 8.0F, 10.0F, 14.0F, 0.0F, false);
      this.LeftArm.func_78784_a(80, 87).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftArm.func_78784_a(84, 65).func_228303_a_(0.0F, -10.0F, -3.0F, 5.0F, 1.0F, 6.0F, 0.0F, false);
      this.LeftArm.func_78784_a(8, 34).func_228303_a_(8.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.LeftArm.func_78784_a(34, 6).func_228303_a_(8.0F, 7.0F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.LeftArm.func_78784_a(4, 24).func_228303_a_(9.0F, -1.0F, -3.0F, 1.0F, 9.0F, 1.0F, 0.0F, false);
      this.LeftArm.func_78784_a(0, 34).func_228303_a_(8.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.LeftArm.func_78784_a(0, 24).func_228303_a_(9.0F, -1.0F, 2.0F, 1.0F, 9.0F, 1.0F, 0.0F, false);
      this.LeftArm.func_78784_a(4, 34).func_228303_a_(8.0F, 7.0F, 2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.RightBoot = new ModelRenderer(this);
      this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightBoot.func_78784_a(64, 87).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.RightBoot.func_78784_a(64, 0).func_228303_a_(-4.0F, 2.0F, -4.0F, 8.0F, 11.0F, 8.0F, 0.0F, false);
      this.RightBoot.func_78784_a(52, 0).func_228303_a_(-3.95F, 8.0F, -6.0F, 8.0F, 5.0F, 2.0F, 0.0F, false);
      this.LeftBoot = new ModelRenderer(this);
      this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftBoot.func_78784_a(48, 87).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftBoot.func_78784_a(40, 57).func_228303_a_(-3.8F, 2.0F, -4.0F, 8.0F, 11.0F, 8.0F, 0.0F, false);
      this.LeftBoot.func_78784_a(88, 26).func_228303_a_(-3.75F, 8.0F, -6.0F, 8.0F, 5.0F, 2.0F, 0.0F, false);
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