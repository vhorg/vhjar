package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class Plated3ArmorModel<T extends LivingEntity> extends VaultGearModel<T> {
   public Plated3ArmorModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.field_78090_t = this.isLayer2() ? 64 : 128;
      this.field_78089_u = this.isLayer2() ? 32 : 128;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(0, 25).func_228303_a_(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
      this.Head.func_78784_a(24, 50).func_228303_a_(-6.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, 0.0F, false);
      this.Head.func_78784_a(24, 50).func_228303_a_(5.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, 0.0F, false);
      this.Head.func_78784_a(0, 19).func_228303_a_(-6.0F, -1.0F, -6.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);
      this.Head.func_78784_a(0, 19).func_228303_a_(3.0F, -1.0F, -6.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);
      this.Head.func_78784_a(0, 0).func_228303_a_(-1.0F, -9.0F, -6.0F, 2.0F, 5.0F, 1.0F, 0.0F, false);
      this.Head.func_78784_a(0, 41).func_228303_a_(-1.0F, -9.0F, -5.0F, 2.0F, 1.0F, 10.0F, 0.0F, false);
      this.Head.func_78784_a(0, 14).func_228303_a_(-1.0F, -9.0F, 5.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r1 = new ModelRenderer(this);
      cube_r1.func_78793_a(6.0F, -7.4944F, -4.5165F);
      this.Head.func_78792_a(cube_r1);
      this.setRotationAngle(cube_r1, 0.8727F, 0.0F, 0.0F);
      cube_r1.func_78784_a(30, 18).func_228303_a_(-4.0F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
      cube_r1.func_78784_a(30, 18).func_228303_a_(-9.0F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r2 = new ModelRenderer(this);
      cube_r2.func_78793_a(-2.5F, 0.5F, -5.5F);
      this.Head.func_78792_a(cube_r2);
      this.setRotationAngle(cube_r2, 0.0F, -0.3927F, 0.0F);
      cube_r2.func_78784_a(0, 6).func_228303_a_(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r3 = new ModelRenderer(this);
      cube_r3.func_78793_a(2.5F, 0.5F, -5.5F);
      this.Head.func_78792_a(cube_r3);
      this.setRotationAngle(cube_r3, 0.0F, 0.3927F, 0.0F);
      cube_r3.func_78784_a(0, 6).func_228303_a_(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r4 = new ModelRenderer(this);
      cube_r4.func_78793_a(2.1F, -7.75F, 5.0F);
      this.Head.func_78792_a(cube_r4);
      this.setRotationAngle(cube_r4, 0.0F, 0.0F, 0.6545F);
      cube_r4.func_78784_a(4, 25).func_228303_a_(-0.5F, -2.75F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r5 = new ModelRenderer(this);
      cube_r5.func_78793_a(5.6F, -9.5F, 3.5F);
      this.Head.func_78792_a(cube_r5);
      this.setRotationAngle(cube_r5, 0.0F, 0.0F, 0.6545F);
      cube_r5.func_78784_a(4, 25).func_228303_a_(-0.5F, -2.75F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r6 = new ModelRenderer(this);
      cube_r6.func_78793_a(3.6F, -9.0F, -0.5F);
      this.Head.func_78792_a(cube_r6);
      this.setRotationAngle(cube_r6, 0.0F, 0.0F, 0.5236F);
      cube_r6.func_78784_a(32, 32).func_228303_a_(-0.5F, -2.25F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r7 = new ModelRenderer(this);
      cube_r7.func_78793_a(-1.9F, -7.75F, 4.5F);
      this.Head.func_78792_a(cube_r7);
      this.setRotationAngle(cube_r7, 0.0F, 0.0F, -0.6109F);
      cube_r7.func_78784_a(4, 25).func_228303_a_(-0.5F, -2.75F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r8 = new ModelRenderer(this);
      cube_r8.func_78793_a(-5.4F, -9.25F, 3.0F);
      this.Head.func_78792_a(cube_r8);
      this.setRotationAngle(cube_r8, 0.0F, 0.0F, -0.6109F);
      cube_r8.func_78784_a(4, 25).func_228303_a_(-0.5F, -2.75F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r9 = new ModelRenderer(this);
      cube_r9.func_78793_a(-3.4F, -8.75F, -1.0F);
      this.Head.func_78792_a(cube_r9);
      this.setRotationAngle(cube_r9, 0.0F, 0.0F, -0.5236F);
      cube_r9.func_78784_a(32, 32).func_228303_a_(-0.5F, -2.25F, 0.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
      this.Body = new ModelRenderer(this);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Body.func_78784_a(40, 0).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
      this.Body.func_78784_a(0, 0).func_228303_a_(-6.0F, -2.0F, -4.0F, 12.0F, 6.0F, 8.0F, 0.0F, false);
      this.Body.func_78784_a(0, 14).func_228303_a_(-5.5F, 4.0F, -4.0F, 11.0F, 3.0F, 8.0F, 0.0F, false);
      this.Body.func_78784_a(31, 18).func_228303_a_(-5.25F, 7.0F, -3.5F, 10.0F, 3.0F, 7.0F, 0.0F, false);
      this.Body.func_78784_a(0, 52).func_228303_a_(-5.0F, -1.0F, -5.0F, 10.0F, 3.0F, 2.0F, 0.0F, false);
      this.RightArm = new ModelRenderer(this);
      this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.RightArm.func_78784_a(38, 44).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.RightArm.func_78784_a(32, 32).func_228303_a_(-6.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, false);
      this.RightArm.func_78784_a(14, 41).func_228303_a_(-5.0F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);
      ModelRenderer cube_r10 = new ModelRenderer(this);
      cube_r10.func_78793_a(-3.0F, -5.0F, -4.0F);
      this.RightArm.func_78792_a(cube_r10);
      this.setRotationAngle(cube_r10, 0.0F, 0.0F, 0.829F);
      cube_r10.func_78784_a(30, 16).func_228303_a_(-1.0F, 0.0F, 2.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r11 = new ModelRenderer(this);
      cube_r11.func_78793_a(-4.0F, -5.0F, -2.0F);
      this.RightArm.func_78792_a(cube_r11);
      this.setRotationAngle(cube_r11, 0.0F, 0.0F, 0.829F);
      cube_r11.func_78784_a(24, 30).func_228303_a_(-3.0F, 0.0F, 2.0F, 7.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r12 = new ModelRenderer(this);
      cube_r12.func_78793_a(13.0F, -4.0F, -4.0F);
      this.RightArm.func_78792_a(cube_r12);
      this.setRotationAngle(cube_r12, 0.0F, 0.0F, 2.3562F);
      cube_r12.func_78784_a(30, 16).func_228303_a_(-1.5F, 0.0F, 2.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r13 = new ModelRenderer(this);
      cube_r13.func_78793_a(14.0F, -4.0F, -2.0F);
      this.RightArm.func_78792_a(cube_r13);
      this.setRotationAngle(cube_r13, 0.0F, 0.0F, 2.3562F);
      cube_r13.func_78784_a(24, 30).func_228303_a_(-3.0F, 0.0F, 2.0F, 7.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r14 = new ModelRenderer(this);
      cube_r14.func_78793_a(15.0F, -1.0F, 0.0F);
      this.RightArm.func_78792_a(cube_r14);
      this.setRotationAngle(cube_r14, 0.0F, 0.0F, 2.618F);
      cube_r14.func_78784_a(24, 28).func_228303_a_(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r15 = new ModelRenderer(this);
      cube_r15.func_78793_a(15.0F, -4.0F, 0.0F);
      this.RightArm.func_78792_a(cube_r15);
      this.setRotationAngle(cube_r15, 0.0F, 0.0F, 2.618F);
      cube_r15.func_78784_a(24, 28).func_228303_a_(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r16 = new ModelRenderer(this);
      cube_r16.func_78793_a(14.125F, -4.5F, 3.0F);
      this.RightArm.func_78792_a(cube_r16);
      this.setRotationAngle(cube_r16, -0.6545F, 0.0F, 0.0F);
      cube_r16.func_78784_a(0, 25).func_228303_a_(-0.125F, -4.5F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      cube_r16.func_78784_a(0, 25).func_228303_a_(-19.125F, -4.5F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r17 = new ModelRenderer(this);
      cube_r17.func_78793_a(-5.0F, -2.0F, 0.0F);
      this.RightArm.func_78792_a(cube_r17);
      this.setRotationAngle(cube_r17, 0.0F, 0.0F, 0.4363F);
      cube_r17.func_78784_a(24, 28).func_228303_a_(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r18 = new ModelRenderer(this);
      cube_r18.func_78793_a(-5.0F, -5.0F, 0.0F);
      this.RightArm.func_78792_a(cube_r18);
      this.setRotationAngle(cube_r18, 0.0F, 0.0F, 0.4363F);
      cube_r18.func_78784_a(24, 28).func_228303_a_(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);
      this.LeftArm = new ModelRenderer(this);
      this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
      this.LeftArm.func_78784_a(38, 44).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);
      this.LeftArm.func_78784_a(14, 41).func_228303_a_(-0.25F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, false);
      this.LeftArm.func_78784_a(32, 32).func_228303_a_(-1.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, false);
      this.RightBoot = new ModelRenderer(this);
      this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightBoot.func_78784_a(53, 28).func_228303_a_(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, false);
      this.RightBoot.func_78784_a(44, 5).func_228303_a_(-3.25F, 8.0F, -3.75F, 6.0F, 5.0F, 1.0F, 0.0F, false);
      this.RightBoot.func_78784_a(44, 5).func_228303_a_(0.75F, 8.0F, -3.75F, 6.0F, 5.0F, 1.0F, 0.0F, false);
      this.RightBoot.func_78784_a(43, 5).func_228303_a_(-2.25F, 7.0F, -3.75F, 4.0F, 1.0F, 1.0F, 0.0F, false);
      this.RightBoot.func_78784_a(43, 5).func_228303_a_(1.75F, 7.0F, -3.75F, 4.0F, 1.0F, 1.0F, 0.0F, false);
      this.LeftBoot = new ModelRenderer(this);
      this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftBoot.func_78784_a(53, 28).func_228303_a_(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, true);
      this.Belt = new ModelRenderer(this);
      this.Belt.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Belt.func_78784_a(16, 16).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);
      this.RightLeg = new ModelRenderer(this);
      this.RightLeg.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
      this.RightLeg.func_78784_a(0, 0).func_228303_a_(-3.0F, 3.0F, -3.0F, 5.0F, 5.0F, 6.0F, 0.0F, false);
      this.RightLeg.func_78784_a(0, 0).func_228303_a_(2.0F, 3.0F, -3.0F, 5.0F, 5.0F, 6.0F, 0.0F, false);
      this.LeftLeg = new ModelRenderer(this);
      this.LeftLeg.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
   }
}
