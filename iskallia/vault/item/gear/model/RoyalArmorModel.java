package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class RoyalArmorModel {
   public static class Variant1<T extends LivingEntity> extends VaultGearModel<T> {
      public Variant1(float modelSize, EquipmentSlotType slotType) {
         super(modelSize, slotType);
         this.field_78090_t = this.isLayer2() ? 64 : 128;
         this.field_78089_u = this.isLayer2() ? 32 : 128;
         this.Head = new ModelRenderer(this);
         this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
         this.Head.func_78784_a(0, 17).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
         this.Head.func_78784_a(40, 50).func_228303_a_(-3.0F, -6.25F, -5.0F, 6.0F, 2.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(28, 52).func_228303_a_(-2.0F, -7.25F, -5.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(28, 50).func_228303_a_(-2.0F, -7.25F, 4.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(0, 17).func_228303_a_(-1.0F, -8.25F, -5.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(0, 6).func_228303_a_(-1.0F, -8.25F, 4.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(36, 0).func_228303_a_(-3.0F, -6.25F, 4.0F, 6.0F, 2.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(32, 50).func_228303_a_(-5.0F, -6.25F, -3.0F, 1.0F, 2.0F, 6.0F, 0.0F, false);
         this.Head.func_78784_a(28, 0).func_228303_a_(4.0F, -6.25F, -3.0F, 1.0F, 2.0F, 6.0F, 0.0F, false);
         this.Head.func_78784_a(28, 3).func_228303_a_(-4.0F, -6.25F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(28, 0).func_228303_a_(-4.0F, -6.25F, 3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(24, 20).func_228303_a_(3.0F, -6.25F, 3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(24, 17).func_228303_a_(3.0F, -6.25F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(60, 32).func_228303_a_(-5.0F, -7.25F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
         this.Head.func_78784_a(0, 3).func_228303_a_(-5.0F, -8.25F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
         this.Head.func_78784_a(38, 4).func_228303_a_(4.0F, -7.25F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
         this.Head.func_78784_a(0, 0).func_228303_a_(4.0F, -8.25F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
         this.Head.func_78784_a(27, 45).func_228303_a_(-2.2F, -5.75F, -5.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(27, 41).func_228303_a_(-2.2F, -5.75F, 4.15F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(24, 44).func_228303_a_(1.2F, -5.75F, -5.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(24, 40).func_228303_a_(1.2F, -5.75F, 4.15F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(4, 22).func_228303_a_(-0.4F, -6.75F, -5.25F, 1.0F, 2.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(0, 19).func_228303_a_(-0.4F, -6.75F, 4.15F, 1.0F, 2.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(0, 22).func_228303_a_(4.4F, -6.75F, -0.55F, 1.0F, 2.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(44, 5).func_228303_a_(4.4F, -5.75F, -2.15F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(44, 3).func_228303_a_(4.4F, -5.75F, 1.15F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(27, 43).func_228303_a_(-5.2F, -5.75F, 1.15F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(24, 42).func_228303_a_(-5.2F, -5.75F, -2.35F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(4, 19).func_228303_a_(-5.3F, -6.75F, -0.55F, 1.0F, 2.0F, 1.0F, 0.0F, false);
         this.Body = new ModelRenderer(this);
         this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
         this.Body.func_78784_a(0, 33).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
         this.Body.func_78784_a(0, 0).func_228303_a_(-5.0F, 1.0F, -4.0F, 10.0F, 9.0F, 8.0F, 0.0F, false);
         this.Body.func_78784_a(24, 40).func_228303_a_(-4.0F, 9.25F, -3.5F, 8.0F, 3.0F, 7.0F, 0.0F, false);
         ModelRenderer cube_r1 = new ModelRenderer(this);
         cube_r1.func_78793_a(0.048F, 5.4F, -4.3293F);
         this.Body.func_78792_a(cube_r1);
         this.setRotationAngle(cube_r1, 0.2752F, 0.473F, 0.1279F);
         cube_r1.func_78784_a(32, 58).func_228303_a_(-4.2417F, -4.0F, -1.4261F, 5.0F, 8.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r2 = new ModelRenderer(this);
         cube_r2.func_78793_a(-2.0F, 5.3F, 4.55F);
         this.Body.func_78792_a(cube_r2);
         this.setRotationAngle(cube_r2, -0.3001F, -0.504F, 0.1483F);
         cube_r2.func_78784_a(44, 61).func_228303_a_(-2.5F, -4.0F, -0.5F, 5.0F, 8.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r3 = new ModelRenderer(this);
         cube_r3.func_78793_a(0.048F, 5.4F, -4.3293F);
         this.Body.func_78792_a(cube_r3);
         this.setRotationAngle(cube_r3, 0.2752F, -0.473F, -0.1279F);
         cube_r3.func_78784_a(44, 24).func_228303_a_(-0.7583F, -4.0F, -1.4261F, 5.0F, 8.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r4 = new ModelRenderer(this);
         cube_r4.func_78793_a(1.8301F, 5.3F, 4.55F);
         this.Body.func_78792_a(cube_r4);
         this.setRotationAngle(cube_r4, -0.3001F, 0.504F, -0.1483F);
         cube_r4.func_78784_a(56, 61).func_228303_a_(-2.5F, -4.0F, -0.5F, 5.0F, 8.0F, 1.0F, 0.0F, false);
         this.RightArm = new ModelRenderer(this);
         this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
         this.RightArm.func_78784_a(56, 16).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.RightArm.func_78784_a(36, 3).func_228303_a_(-6.25F, 2.75F, 2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.RightArm.func_78784_a(0, 35).func_228303_a_(-6.25F, 2.75F, 0.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.RightArm.func_78784_a(20, 35).func_228303_a_(-6.25F, 2.75F, -3.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.RightArm.func_78784_a(20, 33).func_228303_a_(-6.25F, 2.75F, -1.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r5 = new ModelRenderer(this);
         cube_r5.func_78793_a(-3.0F, -0.5F, 0.0F);
         this.RightArm.func_78792_a(cube_r5);
         this.setRotationAngle(cube_r5, 0.0F, 0.0F, 0.6981F);
         cube_r5.func_78784_a(47, 33).func_228303_a_(-2.65F, -1.5F, -3.5F, 3.0F, 7.0F, 7.0F, 0.0F, false);
         cube_r5.func_78784_a(24, 25).func_228303_a_(-3.0F, -3.5F, -4.1F, 6.0F, 7.0F, 8.0F, 0.0F, false);
         this.LeftArm = new ModelRenderer(this);
         this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
         this.LeftArm.func_78784_a(16, 50).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.LeftArm.func_78784_a(4, 0).func_228303_a_(5.25F, 2.75F, 0.35F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.LeftArm.func_78784_a(24, 23).func_228303_a_(5.25F, 2.75F, 2.15F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.LeftArm.func_78784_a(4, 3).func_228303_a_(5.25F, 2.75F, -1.35F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.LeftArm.func_78784_a(0, 33).func_228303_a_(5.25F, 2.75F, -3.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r6 = new ModelRenderer(this);
         cube_r6.func_78793_a(3.0F, -0.5F, 0.0F);
         this.LeftArm.func_78792_a(cube_r6);
         this.setRotationAngle(cube_r6, 0.0F, 0.0F, -0.6981F);
         cube_r6.func_78784_a(47, 47).func_228303_a_(-0.45F, -1.5F, -3.5F, 3.0F, 7.0F, 7.0F, 0.0F, false);
         cube_r6.func_78784_a(28, 9).func_228303_a_(-3.0F, -3.5F, -4.2F, 6.0F, 7.0F, 8.0F, 0.0F, false);
         this.RightBoot = new ModelRenderer(this);
         this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
         this.RightBoot.func_78784_a(0, 49).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.LeftBoot = new ModelRenderer(this);
         this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
         this.LeftBoot.func_78784_a(48, 0).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
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

   public static class Variant2<T extends LivingEntity> extends VaultGearModel<T> {
      public Variant2(float modelSize, EquipmentSlotType slotType) {
         super(modelSize, slotType);
         this.field_78090_t = this.isLayer2() ? 64 : 128;
         this.field_78089_u = this.isLayer2() ? 32 : 128;
         this.Head = new ModelRenderer(this);
         this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
         this.Head.func_78784_a(2, 95).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
         this.Head.func_78784_a(26, 95).func_228303_a_(-5.0F, -9.0F, -6.0F, 10.0F, 4.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(36, 74).func_228303_a_(-3.0F, -8.0F, -7.0F, 6.0F, 3.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(26, 100).func_228303_a_(-5.0F, -1.0F, -6.0F, 10.0F, 2.0F, 1.0F, 0.0F, false);
         this.Head.func_78784_a(36, 78).func_228303_a_(-3.0F, -1.0F, -6.5F, 6.0F, 2.0F, 0.1F, 0.0F, false);
         this.Head.func_78784_a(2, 74).func_228303_a_(-6.0F, -10.0F, -4.0F, 12.0F, 11.0F, 10.0F, 0.0F, false);
         this.Head.func_78784_a(26, 103).func_228303_a_(-5.0F, -11.0F, -3.0F, 10.0F, 1.0F, 8.0F, 0.0F, false);
         ModelRenderer cube_r8 = new ModelRenderer(this);
         cube_r8.func_78793_a(1.6F, -0.7527F, -5.7789F);
         this.Head.func_78792_a(cube_r8);
         this.setRotationAngle(cube_r8, 0.7418F, -1.5708F, 0.0F);
         cube_r8.func_78784_a(2, 80).func_228303_a_(-0.5F, -0.75F, -0.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r7 = new ModelRenderer(this);
         cube_r7.func_78793_a(-2.4F, -0.7527F, -5.7789F);
         this.Head.func_78792_a(cube_r7);
         this.setRotationAngle(cube_r7, 0.7418F, -1.5708F, 0.0F);
         cube_r7.func_78784_a(2, 80).func_228303_a_(-0.5F, -0.75F, -0.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r9 = new ModelRenderer(this);
         cube_r9.func_78793_a(-1.15F, -0.5027F, -5.7789F);
         this.Head.func_78792_a(cube_r9);
         this.setRotationAngle(cube_r9, 0.7418F, -1.5708F, 0.0F);
         cube_r9.func_78784_a(2, 80).func_228303_a_(-0.5F, -0.7654F, -0.3968F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r10 = new ModelRenderer(this);
         cube_r10.func_78793_a(0.35F, -0.7527F, -5.7789F);
         this.Head.func_78792_a(cube_r10);
         this.setRotationAngle(cube_r10, 0.7418F, -1.5708F, 0.0F);
         cube_r10.func_78784_a(2, 80).func_228303_a_(-0.5F, -0.9189F, -0.9343F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r11 = new ModelRenderer(this);
         cube_r11.func_78793_a(1.6F, -5.2527F, -5.7789F);
         this.Head.func_78792_a(cube_r11);
         this.setRotationAngle(cube_r11, 0.7418F, -1.5708F, 0.0F);
         cube_r11.func_78784_a(2, 80).func_228303_a_(-0.5F, -0.5657F, -0.9189F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r12 = new ModelRenderer(this);
         cube_r12.func_78793_a(1.6F, -5.2527F, -5.7789F);
         this.Head.func_78792_a(cube_r12);
         this.setRotationAngle(cube_r12, 0.7418F, -1.5708F, 0.0F);
         cube_r12.func_78784_a(2, 80).func_228303_a_(-0.5F, 2.1368F, 2.0301F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r13 = new ModelRenderer(this);
         cube_r13.func_78793_a(1.6F, -5.2527F, -5.7789F);
         this.Head.func_78792_a(cube_r13);
         this.setRotationAngle(cube_r13, 0.7418F, -1.5708F, 0.0F);
         cube_r13.func_78784_a(2, 80).func_228303_a_(-0.5F, 0.0945F, 0.1716F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r14 = new ModelRenderer(this);
         cube_r14.func_78793_a(1.6F, -5.2527F, -5.7789F);
         this.Head.func_78792_a(cube_r14);
         this.setRotationAngle(cube_r14, 0.7418F, -1.5708F, 0.0F);
         cube_r14.func_78784_a(2, 80).func_228303_a_(-0.5F, 1.1079F, 1.2774F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.Body = new ModelRenderer(this);
         this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
         this.Body.func_78784_a(0, 33).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
         this.Body.func_78784_a(0, 0).func_228303_a_(-5.0F, 1.0F, -4.0F, 10.0F, 9.0F, 8.0F, 0.0F, false);
         this.Body.func_78784_a(24, 40).func_228303_a_(-4.0F, 9.25F, -3.5F, 8.0F, 3.0F, 7.0F, 0.0F, false);
         ModelRenderer cube_r1 = new ModelRenderer(this);
         cube_r1.func_78793_a(0.048F, 5.4F, -4.3293F);
         this.Body.func_78792_a(cube_r1);
         this.setRotationAngle(cube_r1, 0.2752F, 0.473F, 0.1279F);
         cube_r1.func_78784_a(32, 58).func_228303_a_(-4.2417F, -4.0F, -1.4261F, 5.0F, 8.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r2 = new ModelRenderer(this);
         cube_r2.func_78793_a(-2.0F, 5.3F, 4.55F);
         this.Body.func_78792_a(cube_r2);
         this.setRotationAngle(cube_r2, -0.3001F, -0.504F, 0.1483F);
         cube_r2.func_78784_a(44, 61).func_228303_a_(-2.5F, -4.0F, -0.5F, 5.0F, 8.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r3 = new ModelRenderer(this);
         cube_r3.func_78793_a(0.048F, 5.4F, -4.3293F);
         this.Body.func_78792_a(cube_r3);
         this.setRotationAngle(cube_r3, 0.2752F, -0.473F, -0.1279F);
         cube_r3.func_78784_a(44, 24).func_228303_a_(-0.7583F, -4.0F, -1.4261F, 5.0F, 8.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r4 = new ModelRenderer(this);
         cube_r4.func_78793_a(1.8301F, 5.3F, 4.55F);
         this.Body.func_78792_a(cube_r4);
         this.setRotationAngle(cube_r4, -0.3001F, 0.504F, -0.1483F);
         cube_r4.func_78784_a(56, 61).func_228303_a_(-2.5F, -4.0F, -0.5F, 5.0F, 8.0F, 1.0F, 0.0F, false);
         this.RightArm = new ModelRenderer(this);
         this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
         this.RightArm.func_78784_a(56, 16).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.RightArm.func_78784_a(36, 3).func_228303_a_(-6.25F, 2.75F, 2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.RightArm.func_78784_a(0, 35).func_228303_a_(-6.25F, 2.75F, 0.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.RightArm.func_78784_a(20, 35).func_228303_a_(-6.25F, 2.75F, -3.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.RightArm.func_78784_a(20, 33).func_228303_a_(-6.25F, 2.75F, -1.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r5 = new ModelRenderer(this);
         cube_r5.func_78793_a(-3.0F, -0.5F, 0.0F);
         this.RightArm.func_78792_a(cube_r5);
         this.setRotationAngle(cube_r5, 0.0F, 0.0F, 0.6981F);
         cube_r5.func_78784_a(47, 33).func_228303_a_(-2.65F, -1.5F, -3.5F, 3.0F, 7.0F, 7.0F, 0.0F, false);
         cube_r5.func_78784_a(24, 25).func_228303_a_(-3.0F, -3.5F, -4.1F, 6.0F, 7.0F, 8.0F, 0.0F, false);
         this.LeftArm = new ModelRenderer(this);
         this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
         this.LeftArm.func_78784_a(16, 50).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.LeftArm.func_78784_a(4, 0).func_228303_a_(5.25F, 2.75F, 0.35F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.LeftArm.func_78784_a(24, 23).func_228303_a_(5.25F, 2.75F, 2.15F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.LeftArm.func_78784_a(4, 3).func_228303_a_(5.25F, 2.75F, -1.35F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         this.LeftArm.func_78784_a(0, 33).func_228303_a_(5.25F, 2.75F, -3.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
         ModelRenderer cube_r6 = new ModelRenderer(this);
         cube_r6.func_78793_a(3.0F, -0.5F, 0.0F);
         this.LeftArm.func_78792_a(cube_r6);
         this.setRotationAngle(cube_r6, 0.0F, 0.0F, -0.6981F);
         cube_r6.func_78784_a(47, 47).func_228303_a_(-0.45F, -1.5F, -3.5F, 3.0F, 7.0F, 7.0F, 0.0F, false);
         cube_r6.func_78784_a(28, 9).func_228303_a_(-3.0F, -3.5F, -4.2F, 6.0F, 7.0F, 8.0F, 0.0F, false);
         this.RightBoot = new ModelRenderer(this);
         this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
         this.RightBoot.func_78784_a(0, 49).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.LeftBoot = new ModelRenderer(this);
         this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
         this.LeftBoot.func_78784_a(48, 0).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
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
