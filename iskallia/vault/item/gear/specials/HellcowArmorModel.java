package iskallia.vault.item.gear.specials;

import iskallia.vault.item.gear.model.VaultGearModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class HellcowArmorModel<T extends LivingEntity> extends VaultGearModel<T> {
   public HellcowArmorModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.field_78090_t = this.isLayer2() ? 64 : 64;
      this.field_78089_u = this.isLayer2() ? 64 : 64;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(0, 0).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
      this.Head.func_78784_a(44, 0).func_228303_a_(-2.0F, -1.0F, -6.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r1 = new ModelRenderer(this);
      cube_r1.func_78793_a(0.0F, 2.5F, -6.0F);
      this.Head.func_78792_a(cube_r1);
      this.setRotationAngle(cube_r1, -0.6109F, 0.0F, 0.0F);
      cube_r1.func_78784_a(16, 36).func_228303_a_(-2.0F, -1.75F, -0.75F, 4.0F, 3.0F, 0.0F, 0.0F, false);
      ModelRenderer cube_r2 = new ModelRenderer(this);
      cube_r2.func_78793_a(0.0F, -8.0F, 0.0F);
      this.Head.func_78792_a(cube_r2);
      this.setRotationAngle(cube_r2, 1.0472F, 0.0F, 0.0F);
      cube_r2.func_78784_a(12, 32).func_228303_a_(5.0F, 0.0F, -3.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);
      cube_r2.func_78784_a(0, 0).func_228303_a_(9.0F, -4.0F, -3.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
      cube_r2.func_78784_a(24, 0).func_228303_a_(-11.0F, -4.0F, -3.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
      cube_r2.func_78784_a(16, 41).func_228303_a_(-9.0F, 0.0F, -3.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);
      this.Body = new ModelRenderer(this);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Body.func_78784_a(0, 16).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
      this.Body.func_78784_a(40, 25).func_228303_a_(-3.0F, 6.0F, -4.0F, 6.0F, 6.0F, 1.0F, 0.0F, false);
      this.Body.func_78784_a(24, 16).func_228303_a_(-5.0F, 1.0F, 3.0F, 10.0F, 7.0F, 2.0F, 0.0F, false);
      this.Body.func_78784_a(40, 32).func_228303_a_(-3.0F, 8.0F, 3.0F, 6.0F, 4.0F, 1.0F, 0.0F, false);
      this.RightArm = new ModelRenderer(this);
      this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.RightArm.func_78784_a(36, 37).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftArm = new ModelRenderer(this);
      this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
      this.LeftArm.func_78784_a(32, 0).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.RightBoot = new ModelRenderer(this);
      this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightBoot.func_78784_a(0, 32).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftBoot = new ModelRenderer(this);
      this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftBoot.func_78784_a(24, 25).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.Belt = new ModelRenderer(this);
      this.Belt.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Belt.func_78784_a(0, 0).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);
      ModelRenderer cube_r1_l2 = new ModelRenderer(this);
      cube_r1_l2.func_78793_a(0.0F, 16.8221F, 6.7705F);
      this.Belt.func_78792_a(cube_r1_l2);
      this.setRotationAngle(cube_r1_l2, -0.3491F, 0.0F, 0.0F);
      cube_r1_l2.func_78784_a(12, 16).func_228303_a_(-0.5F, -1.5F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
      ModelRenderer cube_r2_l2 = new ModelRenderer(this);
      cube_r2_l2.func_78793_a(-0.25F, 11.5F, 4.0F);
      this.Belt.func_78792_a(cube_r2_l2);
      this.setRotationAngle(cube_r2_l2, -1.0908F, 0.0F, 0.0F);
      cube_r2_l2.func_78784_a(24, 0).func_228303_a_(-1.25F, -0.5F, 2.0F, 3.0F, 2.0F, 4.0F, 0.0F, false);
      cube_r2_l2.func_78784_a(24, 6).func_228303_a_(-0.25F, 0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
      this.RightLeg = new ModelRenderer(this);
      this.RightLeg.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightLeg.func_78784_a(16, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
      this.LeftLeg = new ModelRenderer(this);
      this.LeftLeg.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
   }
}
