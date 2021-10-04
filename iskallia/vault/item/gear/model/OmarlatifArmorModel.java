package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class OmarlatifArmorModel<T extends LivingEntity> extends VaultGearModel<T> {
   public OmarlatifArmorModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.field_78090_t = this.isLayer2() ? 32 : 128;
      this.field_78089_u = this.isLayer2() ? 32 : 128;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(0, 15).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
      this.Body = new ModelRenderer(this);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Body.func_78784_a(0, 31).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
      ModelRenderer cube_r1 = new ModelRenderer(this);
      cube_r1.func_78793_a(0.0F, 3.5F, -3.5F);
      this.Body.func_78792_a(cube_r1);
      this.setRotationAngle(cube_r1, 0.48F, 0.0F, 0.0F);
      cube_r1.func_78784_a(38, 3).func_228303_a_(-5.0F, -2.5F, -0.5F, 10.0F, 5.0F, 1.0F, 0.0F, false);
      this.RightArm = new ModelRenderer(this);
      this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.RightArm.func_78784_a(40, 41).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      ModelRenderer cube_r2 = new ModelRenderer(this);
      cube_r2.func_78793_a(-3.0F, -1.0F, 0.0F);
      this.RightArm.func_78792_a(cube_r2);
      this.setRotationAngle(cube_r2, 0.0F, 0.0F, 0.9599F);
      cube_r2.func_78784_a(34, 15).func_228303_a_(-3.0F, -3.0F, -4.0F, 4.0F, 6.0F, 8.0F, 0.0F, false);
      this.LeftArm = new ModelRenderer(this);
      this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
      this.LeftArm.func_78784_a(40, 41).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);
      ModelRenderer cube_r3 = new ModelRenderer(this);
      cube_r3.func_78793_a(3.0F, -1.0F, 0.0F);
      this.LeftArm.func_78792_a(cube_r3);
      this.setRotationAngle(cube_r3, 0.0F, 0.0F, -0.9599F);
      cube_r3.func_78784_a(34, 15).func_228303_a_(-1.0F, -3.0F, -4.0F, 4.0F, 6.0F, 8.0F, 0.0F, false);
      this.RightBoot = new ModelRenderer(this);
      this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightBoot.func_78784_a(24, 41).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftBoot = new ModelRenderer(this);
      this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftBoot.func_78784_a(24, 41).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);
      this.Belt = new ModelRenderer(this);
      this.Belt.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Belt.func_78784_a(0, 0).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);
      this.RightLeg = new ModelRenderer(this);
      this.RightLeg.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightLeg.func_78784_a(16, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
      this.LeftLeg = new ModelRenderer(this);
      this.LeftLeg.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
   }
}
