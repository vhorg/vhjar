package iskallia.vault.skill.talent.type.archetype;

import iskallia.vault.Vault;
import iskallia.vault.skill.talent.type.PlayerTalent;
import net.minecraft.world.World;

public abstract class ArchetypeTalent extends PlayerTalent {
   public ArchetypeTalent(int cost) {
      super(cost);
   }

   public ArchetypeTalent(int cost, int levelRequirement) {
      super(cost, levelRequirement);
   }

   public static boolean isEnabled(World world) {
      return world.func_234923_W_() == Vault.VAULT_KEY;
   }
}
