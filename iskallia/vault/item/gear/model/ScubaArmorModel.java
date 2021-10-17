package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class ScubaArmorModel {
   public static class Variant1<T extends LivingEntity> extends VaultGearModel<T> {
      public Variant1(float modelSize, EquipmentSlotType slotType) {
         super(modelSize, slotType);
         this.field_78090_t = this.isLayer2() ? 64 : 128;
         this.field_78089_u = this.isLayer2() ? 32 : 128;
         this.Head = new ModelRenderer(this);
         this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
         this.Head.func_78784_a(26, 6).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
         ModelRenderer cube_r1 = new ModelRenderer(this);
         cube_r1.func_78793_a(5.0F, -3.25F, -5.0F);
         this.Head.func_78792_a(cube_r1);
         this.setRotationAngle(cube_r1, -0.3491F, 0.0F, 0.0F);
         cube_r1.func_78784_a(0, 0).func_228303_a_(1.0F, -5.75F, -0.25F, 2.0F, 7.0F, 2.0F, 0.0F, false);
         cube_r1.func_78784_a(24, 0).func_228303_a_(-7.0F, 1.25F, -0.25F, 10.0F, 2.0F, 2.0F, 0.0F, false);
         this.Body = new ModelRenderer(this);
         this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
         this.Body.func_78784_a(32, 44).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
         this.Body.func_78784_a(0, 35).func_228303_a_(-8.0F, 8.0F, -6.0F, 16.0F, 3.0F, 4.0F, 0.0F, false);
         this.Body.func_78784_a(0, 28).func_228303_a_(-8.0F, 8.0F, 2.0F, 16.0F, 3.0F, 4.0F, 0.0F, false);
         this.Body.func_78784_a(32, 60).func_228303_a_(4.0F, 8.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);
         this.Body.func_78784_a(58, 12).func_228303_a_(-8.0F, 8.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);
         ModelRenderer cube_r2 = new ModelRenderer(this);
         cube_r2.func_78793_a(0.0F, 6.5F, 5.5F);
         this.Body.func_78792_a(cube_r2);
         this.setRotationAngle(cube_r2, -0.6109F, 0.0F, 0.0F);
         cube_r2.func_78784_a(0, 14).func_228303_a_(-2.0F, -1.5F, 0.5F, 4.0F, 3.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r3 = new ModelRenderer(this);
         cube_r3.func_78793_a(0.0F, 5.0F, -4.0F);
         this.Body.func_78792_a(cube_r3);
         this.setRotationAngle(cube_r3, 0.48F, -0.0436F, 0.0F);
         cube_r3.func_78784_a(0, 18).func_228303_a_(-1.0F, -3.0F, -8.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
         cube_r3.func_78784_a(34, 22).func_228303_a_(-1.0F, -5.0F, -8.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
         cube_r3.func_78784_a(50, 0).func_228303_a_(-2.0F, -5.0F, -4.0F, 4.0F, 9.0F, 3.0F, 0.0F, false);
         this.RightArm = new ModelRenderer(this);
         this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
         this.RightArm.func_78784_a(56, 44).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.RightArm.func_78784_a(0, 14).func_228303_a_(-6.0F, 2.0F, -5.0F, 7.0F, 4.0F, 10.0F, 0.0F, false);
         this.LeftArm = new ModelRenderer(this);
         this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
         this.LeftArm.func_78784_a(0, 0).func_228303_a_(-1.0F, 2.0F, -5.0F, 7.0F, 4.0F, 10.0F, 0.0F, false);
         this.LeftArm.func_78784_a(16, 55).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.RightBoot = new ModelRenderer(this);
         this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
         this.RightBoot.func_78784_a(0, 55).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         ModelRenderer cube_r4 = new ModelRenderer(this);
         cube_r4.func_78793_a(0.0F, 11.5F, -4.5F);
         this.RightBoot.func_78792_a(cube_r4);
         this.setRotationAngle(cube_r4, 0.0F, 0.48F, 0.0F);
         cube_r4.func_78784_a(0, 42).func_228303_a_(-4.5F, -0.5F, -5.5F, 5.0F, 2.0F, 11.0F, 0.0F, false);
         this.LeftBoot = new ModelRenderer(this);
         this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
         this.LeftBoot.func_78784_a(50, 22).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         ModelRenderer cube_r5 = new ModelRenderer(this);
         cube_r5.func_78793_a(0.2F, 11.5F, -4.5F);
         this.LeftBoot.func_78792_a(cube_r5);
         this.setRotationAngle(cube_r5, 0.0F, -0.5236F, 0.0F);
         cube_r5.func_78784_a(29, 31).func_228303_a_(-0.25F, -0.5F, -5.75F, 5.0F, 2.0F, 11.0F, 0.0F, false);
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
}
