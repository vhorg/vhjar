package iskallia.vault.item.gear.specials;

import iskallia.vault.item.gear.model.VaultGearModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class PowahArmorModel<T extends LivingEntity> extends VaultGearModel<T> {
   public PowahArmorModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.field_78090_t = this.isLayer2() ? 64 : 128;
      this.field_78089_u = this.isLayer2() ? 32 : 128;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(0, 0).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
      ModelRenderer cube_r1 = new ModelRenderer(this);
      cube_r1.func_78793_a(4.0F, 0.5F, -7.5F);
      this.Head.func_78792_a(cube_r1);
      this.setRotationAngle(cube_r1, 0.2856F, 0.5973F, 0.4812F);
      cube_r1.func_78784_a(56, 40).func_228303_a_(-0.25F, -0.25F, -2.75F, 2.0F, 3.0F, 3.0F, 0.0F, false);
      cube_r1.func_78784_a(32, 21).func_228303_a_(-2.25F, 0.75F, -1.75F, 2.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r2 = new ModelRenderer(this);
      cube_r2.func_78793_a(-4.0F, 0.5F, -7.5F);
      this.Head.func_78792_a(cube_r2);
      this.setRotationAngle(cube_r2, 0.4047F, -0.5437F, -0.6912F);
      cube_r2.func_78784_a(56, 46).func_228303_a_(-1.6F, -0.25F, -2.8F, 2.0F, 3.0F, 3.0F, 0.0F, false);
      cube_r2.func_78784_a(35, 0).func_228303_a_(0.4F, 0.75F, -1.8F, 2.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r3 = new ModelRenderer(this);
      cube_r3.func_78793_a(0.0F, -1.0134F, -4.8941F);
      this.Head.func_78792_a(cube_r3);
      this.setRotationAngle(cube_r3, 0.2618F, 0.0F, 0.0F);
      cube_r3.func_78784_a(52, 33).func_228303_a_(-2.0F, -2.0F, -3.5F, 4.0F, 4.0F, 3.0F, 0.0F, false);
      ModelRenderer cube_r4 = new ModelRenderer(this);
      cube_r4.func_78793_a(0.0F, -6.0F, -3.5F);
      this.Head.func_78792_a(cube_r4);
      this.setRotationAngle(cube_r4, -0.5672F, 0.0F, 0.0F);
      cube_r4.func_78784_a(0, 16).func_228303_a_(-5.5F, -4.0F, -3.0F, 11.0F, 10.0F, 5.0F, 0.0F, false);
      this.Body = new ModelRenderer(this);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Body.func_78784_a(0, 31).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
      this.Body.func_78784_a(46, 21).func_228303_a_(1.0F, 1.0F, 3.0F, 3.0F, 3.0F, 5.0F, 0.0F, false);
      this.Body.func_78784_a(48, 52).func_228303_a_(1.5F, 5.0F, 3.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
      this.Body.func_78784_a(36, 52).func_228303_a_(-3.5F, 5.0F, 3.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
      this.Body.func_78784_a(0, 4).func_228303_a_(-3.0F, 8.25F, 3.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
      this.Body.func_78784_a(0, 0).func_228303_a_(2.0F, 8.25F, 3.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
      this.Body.func_78784_a(24, 0).func_228303_a_(-4.0F, 1.0F, 3.0F, 3.0F, 3.0F, 5.0F, 0.0F, false);
      this.RightArm = new ModelRenderer(this);
      this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.RightArm.func_78784_a(49, 0).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.RightArm.func_78784_a(27, 8).func_228303_a_(-5.0F, -5.0F, -4.0F, 7.0F, 5.0F, 8.0F, 0.0F, false);
      this.RightArm.func_78784_a(26, 52).func_228303_a_(-5.0F, 0.0F, -2.0F, 1.0F, 11.0F, 4.0F, 0.0F, false);
      this.LeftArm = new ModelRenderer(this);
      this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
      this.LeftArm.func_78784_a(16, 48).func_228303_a_(4.0F, 0.0F, -2.0F, 1.0F, 11.0F, 4.0F, 0.0F, false);
      this.LeftArm.func_78784_a(24, 23).func_228303_a_(-2.0F, -5.0F, -4.0F, 7.0F, 5.0F, 8.0F, 0.0F, false);
      this.LeftArm.func_78784_a(0, 47).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.RightBoot = new ModelRenderer(this);
      this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightBoot.func_78784_a(40, 36).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftBoot = new ModelRenderer(this);
      this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftBoot.func_78784_a(24, 36).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
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
