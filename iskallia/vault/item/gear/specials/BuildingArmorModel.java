package iskallia.vault.item.gear.specials;

import iskallia.vault.item.gear.model.VaultGearModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class BuildingArmorModel<T extends LivingEntity> extends VaultGearModel<T> {
   public BuildingArmorModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.field_78090_t = this.isLayer2() ? 64 : 64;
      this.field_78089_u = this.isLayer2() ? 64 : 64;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(0, 13).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
      this.Head.func_78784_a(0, 0).func_228303_a_(-6.0F, -6.0F, -6.0F, 12.0F, 1.0F, 12.0F, 0.0F, false);
      this.Head.func_78784_a(21, 18).func_228303_a_(-2.0F, -10.0F, -5.5F, 4.0F, 4.0F, 11.0F, 0.0F, false);
      this.Body = new ModelRenderer(this);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Body.func_78784_a(0, 29).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
      this.RightArm = new ModelRenderer(this);
      this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.RightArm.func_78784_a(0, 45).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftArm = new ModelRenderer(this);
      this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
      this.LeftArm.func_78784_a(40, 33).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.RightBoot = new ModelRenderer(this);
      this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightBoot.func_78784_a(40, 13).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftBoot = new ModelRenderer(this);
      this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftBoot.func_78784_a(24, 33).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.Belt = new ModelRenderer(this);
      this.Belt.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Belt.func_78784_a(0, 0).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);
      this.Belt.func_78784_a(27, 27).func_228303_a_(-6.0F, 12.0F, -3.0F, 4.0F, 5.0F, 5.0F, 0.0F, false);
      this.Belt.func_78784_a(24, 0).func_228303_a_(2.0F, 12.0F, -3.0F, 4.0F, 5.0F, 5.0F, 0.0F, false);
      this.Belt.func_78784_a(28, 15).func_228303_a_(-2.0F, 11.0F, -3.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r1_l2 = new ModelRenderer(this);
      cube_r1_l2.func_78793_a(5.0F, 11.6F, 0.25F);
      this.Belt.func_78792_a(cube_r1_l2);
      this.setRotationAngle(cube_r1_l2, -0.2618F, 0.0F, 0.0F);
      cube_r1_l2.func_78784_a(24, 0).func_228303_a_(-0.5F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r2_l2 = new ModelRenderer(this);
      cube_r2_l2.func_78793_a(5.0F, 11.6F, -1.0F);
      this.Belt.func_78792_a(cube_r2_l2);
      this.setRotationAngle(cube_r2_l2, 0.3054F, 0.0F, 0.0F);
      cube_r2_l2.func_78784_a(0, 32).func_228303_a_(-0.5F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r3_l2 = new ModelRenderer(this);
      cube_r3_l2.func_78793_a(-5.0F, 10.25F, 0.5F);
      this.Belt.func_78792_a(cube_r3_l2);
      this.setRotationAngle(cube_r3_l2, 0.5672F, 0.0F, 0.0F);
      cube_r3_l2.func_78784_a(24, 10).func_228303_a_(-1.0F, -3.25F, -4.5F, 2.0F, 2.0F, 3.0F, 0.0F, false);
      cube_r3_l2.func_78784_a(4, 32).func_228303_a_(-0.5F, -1.25F, -3.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
      this.RightLeg = new ModelRenderer(this);
      this.RightLeg.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightLeg.func_78784_a(16, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
      this.LeftLeg = new ModelRenderer(this);
      this.LeftLeg.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
   }
}
