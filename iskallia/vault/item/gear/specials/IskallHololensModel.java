package iskallia.vault.item.gear.specials;

import iskallia.vault.item.gear.model.VaultGearModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class IskallHololensModel<T extends LivingEntity> extends VaultGearModel<T> {
   public IskallHololensModel(float modelSize, EquipmentSlotType slotType) {
      super(modelSize, slotType);
      this.field_78090_t = 32;
      this.field_78089_u = 32;
      this.Head = new ModelRenderer(this);
      this.Head.func_78793_a(0.0F, 0.0F, 0.0F);
      this.Head.func_78784_a(0, 0).func_228303_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
   }
}
