package iskallia.vault.gear.charm;

import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import java.util.List;

public interface GearAttributeCharm {
   List<VaultGearAttributeInstance<?>> getAttributes();

   List<VaultGearAttributeInstance<?>> getAttributes(float var1);
}
