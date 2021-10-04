package iskallia.vault.item.gear.specials;

import iskallia.vault.item.gear.model.VaultGearModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class CheeseHatModel<T extends LivingEntity> extends VaultGearModel<T> {
   public CheeseHatModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.field_78090_t = 96;
      this.field_78089_u = 96;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(38, 49).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
      ModelRenderer cube_r1 = new ModelRenderer(this);
      cube_r1.func_78793_a(-0.1845F, -9.2768F, -1.6093F);
      this.Head.func_78792_a(cube_r1);
      this.setRotationAngle(cube_r1, 2.7501F, 0.693F, 2.8387F);
      cube_r1.func_78784_a(38, 26).func_228303_a_(-0.2966F, -5.9666F, -14.6349F, 9.0F, 9.0F, 14.0F, 0.0F, false);
      ModelRenderer cube_r2 = new ModelRenderer(this);
      cube_r2.func_78793_a(-0.1845F, -9.2768F, -1.6093F);
      this.Head.func_78792_a(cube_r2);
      this.setRotationAngle(cube_r2, 0.3488F, 0.0149F, -0.041F);
      cube_r2.func_78784_a(0, 0).func_228303_a_(-10.067F, -5.9651F, -0.7989F, 23.0F, 9.0F, 12.0F, 0.0F, false);
      ModelRenderer cube_r3 = new ModelRenderer(this);
      cube_r3.func_78793_a(-0.1845F, -9.2768F, -1.6093F);
      this.Head.func_78792_a(cube_r3);
      this.setRotationAngle(cube_r3, 2.7682F, 0.6344F, 2.8604F);
      cube_r3.func_78784_a(0, 21).func_228303_a_(-16.1878F, -5.9667F, -0.6391F, 16.0F, 9.0F, 10.0F, 0.0F, false);
      ModelRenderer cube_r4 = new ModelRenderer(this);
      cube_r4.func_78793_a(-0.1845F, -9.2768F, -1.6093F);
      this.Head.func_78792_a(cube_r4);
      this.setRotationAngle(cube_r4, 0.4002F, -0.7045F, -0.308F);
      cube_r4.func_78784_a(0, 40).func_228303_a_(-8.6758F, -5.9665F, -9.3418F, 9.0F, 9.0F, 10.0F, 0.0F, false);
   }
}
