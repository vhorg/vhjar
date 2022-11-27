package iskallia.vault.entity.model;

import iskallia.vault.entity.entity.VaultGummySoldier;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.geom.ModelPart;

public class VaultGummySoldierModel<T extends VaultGummySoldier> extends AbstractZombieModel<T> {
   public VaultGummySoldierModel(ModelPart p_171090_) {
      super(p_171090_);
   }

   public boolean isAggressive(T pEntity) {
      return pEntity.isAggressive();
   }
}
