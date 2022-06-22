package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.time.extension.RoomGenerationExtension;
import net.minecraft.world.server.ServerWorld;

public class FinalVaultTimeModifier extends VoteModifier {
   @Expose
   private final int timeChange;

   public FinalVaultTimeModifier(String name, String description, int timeChange) {
      super(name, description, 0);
      this.timeChange = timeChange;
   }

   @Override
   public void onApply(ArchitectObjective objective, VaultRaid vault, ServerWorld world) {
      super.onApply(objective, vault, world);
      vault.getPlayers().forEach(vPlayer -> vPlayer.getTimer().addTime(new RoomGenerationExtension(this.timeChange * 20), 0));
   }
}
