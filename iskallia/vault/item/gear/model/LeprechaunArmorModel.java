package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class LeprechaunArmorModel {
   public static class Variant1<T extends LivingEntity> extends VaultGearModel<T> {
      public Variant1(float modelSize, EquipmentSlotType slotType) {
         super(modelSize, slotType);
         this.field_78090_t = this.isLayer2() ? 32 : 64;
         this.field_78089_u = this.isLayer2() ? 32 : 64;
         this.Head = new ModelRenderer(this);
         this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
         this.Head.func_78784_a(0, 0).func_228303_a_(-5.0F, -9.0F, -5.0F, 10.0F, 2.0F, 10.0F, 0.0F, false);
         this.Head.func_78784_a(0, 26).func_228303_a_(-4.0F, -16.0F, -4.0F, 8.0F, 7.0F, 8.0F, 0.0F, false);
         this.Body = new ModelRenderer(this);
         this.Body.func_78793_a(0.0F, 0.0F, 0.0F);
         this.Body.func_78784_a(32, 22).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
         this.Body.func_78784_a(0, 12).func_228303_a_(-5.0F, -2.0F, -4.0F, 10.0F, 6.0F, 8.0F, 0.0F, false);
         this.Body.func_78784_a(16, 41).func_228303_a_(-5.25F, -1.5F, -4.5F, 2.0F, 13.0F, 1.0F, 0.0F, false);
         this.RightArm = new ModelRenderer(this);
         this.RightArm.func_78793_a(-5.0F, 2.0F, 0.0F);
         this.RightArm.func_78784_a(44, 38).func_228303_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.LeftArm = new ModelRenderer(this);
         this.LeftArm.func_78793_a(5.0F, 2.0F, 0.0F);
         this.LeftArm.func_78784_a(0, 41).func_228303_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.RightBoot = new ModelRenderer(this);
         this.RightBoot.func_78793_a(-1.9F, 12.0F, 0.0F);
         this.RightBoot.func_78784_a(40, 0).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.LeftBoot = new ModelRenderer(this);
         this.LeftBoot.func_78793_a(1.9F, 12.0F, 0.0F);
         this.LeftBoot.func_78784_a(28, 38).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
         this.Belt = new ModelRenderer(this);
         this.Belt.func_78793_a(0.0F, 0.0F, 0.0F);
         this.Belt.func_78784_a(0, 0).func_228303_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);
         this.Belt.func_78784_a(12, 16).func_228303_a_(-2.0F, 9.0F, -3.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);
         this.RightLeg = new ModelRenderer(this);
         this.RightLeg.func_78793_a(-1.9F, 12.0F, 0.0F);
         this.RightLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
         this.LeftLeg = new ModelRenderer(this);
         this.LeftLeg.func_78793_a(1.9F, 12.0F, 0.0F);
         this.LeftLeg.func_78784_a(0, 16).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
      }
   }
}
