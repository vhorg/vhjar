package iskallia.vault.world.vault.logic;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.function.Supplier;

public enum VaultLogic {
   CLASSIC(VaultRaid::classic, () -> ModConfigs.VAULT_GENERAL::generateObjective),
   RAFFLE(VaultRaid::classic, () -> VaultRaid.SUMMON_AND_KILL_BOSS::get),
   COOP(VaultRaid::coop, () -> ModConfigs.VAULT_GENERAL::generateCoopObjective);

   private final VaultRaid.Factory factory;
   private final Supplier<Supplier<VaultObjective>> objectiveGenerator;

   private VaultLogic(VaultRaid.Factory factory, Supplier<Supplier<VaultObjective>> objectiveGenerator) {
      this.factory = factory;
      this.objectiveGenerator = objectiveGenerator;
   }

   public VaultRaid.Factory getFactory() {
      return this.factory;
   }

   public VaultObjective getRandomObjective() {
      return this.objectiveGenerator.get().get();
   }
}
