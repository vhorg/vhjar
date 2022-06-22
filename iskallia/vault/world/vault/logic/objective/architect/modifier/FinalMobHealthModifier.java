package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectSummonAndKillBossesObjective;
import net.minecraft.world.server.ServerWorld;

public class FinalMobHealthModifier extends VoteModifier {
   @Expose
   private final float healthIncrease;

   public FinalMobHealthModifier(String name, String description, float healthIncrease) {
      super(name, description, 0);
      this.healthIncrease = healthIncrease;
   }

   @Override
   public void onApply(ArchitectObjective objective, VaultRaid vault, ServerWorld world) {
      super.onApply(objective, vault, world);
      if (objective instanceof ArchitectSummonAndKillBossesObjective) {
         ((ArchitectSummonAndKillBossesObjective)objective).addMobHealthMultiplier(this.healthIncrease);
      }
   }
}
