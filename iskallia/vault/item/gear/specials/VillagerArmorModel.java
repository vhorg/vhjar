package iskallia.vault.item.gear.specials;

import iskallia.vault.item.gear.model.VaultGearModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class VillagerArmorModel<T extends LivingEntity> extends VaultGearModel<T> {
   public VillagerArmorModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.field_78090_t = this.isLayer2() ? 64 : 128;
      this.field_78089_u = this.isLayer2() ? 32 : 128;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(0, 0).func_228303_a_(-4.0F, -11.0F, -4.0F, 8.0F, 11.0F, 8.0F, 1.0F, false);
      this.Head.func_78784_a(22, 30).func_228303_a_(-4.0F, -14.0F, -4.0F, 8.0F, 2.0F, 8.0F, 0.0F, false);
      this.Head.func_78784_a(32, 0).func_228303_a_(-3.0F, -15.0F, -3.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
      this.Head.func_78784_a(0, 0).func_228303_a_(-0.5F, -16.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
      this.Head.func_78784_a(44, 40).func_228303_a_(-1.5F, -4.0F, -7.0F, 3.0F, 6.0F, 2.0F, 0.0F, false);
      this.Body = new ModelRenderer(this);
      this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Body.func_78784_a(48, 50).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
      this.Body.func_78784_a(30, 11).func_228303_a_(-5.0F, 1.0F, -4.0F, 10.0F, 4.0F, 8.0F, 0.0F, false);
      ModelRenderer cube_r1 = new ModelRenderer(this);
      cube_r1.func_78793_a(0.5F, 11.5F, 3.0F);
      this.Body.func_78792_a(cube_r1);
      this.setRotationAngle(cube_r1, 0.3927F, 0.0F, 0.0F);
      cube_r1.func_78784_a(0, 19).func_228303_a_(-7.5F, -9.5F, 3.0F, 15.0F, 19.0F, 0.0F, 0.0F, false);
      this.RightArm = new ModelRenderer(this);
      this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.RightArm.func_78784_a(32, 54).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.RightArm.func_78784_a(26, 40).func_228303_a_(-5.0F, -4.0F, -4.0F, 5.0F, 6.0F, 8.0F, 0.0F, false);
      this.LeftArm = new ModelRenderer(this);
      this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
      this.LeftArm.func_78784_a(54, 23).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftArm.func_78784_a(0, 38).func_228303_a_(0.0F, -4.0F, -4.0F, 5.0F, 6.0F, 8.0F, 0.0F, false);
      this.RightBoot = new ModelRenderer(this);
      this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.RightBoot.func_78784_a(16, 54).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
      this.LeftBoot = new ModelRenderer(this);
      this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
      this.LeftBoot.func_78784_a(0, 52).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
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
