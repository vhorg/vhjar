package iskallia.vault.skill.talent;

import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import java.util.List;

public interface GearAttributeTalent extends Talent {
   List<VaultGearAttributeInstance<?>> getAttributes();
}
