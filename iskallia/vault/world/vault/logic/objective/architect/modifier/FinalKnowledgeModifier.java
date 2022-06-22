package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectSummonAndKillBossesObjective;
import net.minecraft.world.server.ServerWorld;

public class FinalKnowledgeModifier extends VoteModifier {
   @Expose
   private final int knowledge;

   public FinalKnowledgeModifier(String name, String description, int knowledge) {
      super(name, description, 0);
      this.knowledge = knowledge;
   }

   @Override
   public void onApply(ArchitectObjective objective, VaultRaid vault, ServerWorld world) {
      super.onApply(objective, vault, world);
      if (objective instanceof ArchitectSummonAndKillBossesObjective) {
         ((ArchitectSummonAndKillBossesObjective)objective).addKnowledge(this.knowledge);
      }
   }
}
