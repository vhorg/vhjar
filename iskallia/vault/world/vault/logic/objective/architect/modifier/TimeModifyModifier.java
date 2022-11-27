package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.time.extension.RoomGenerationExtension;
import net.minecraft.server.level.ServerLevel;

public class TimeModifyModifier extends VoteModifier {
   @Expose
   private final int timeChange;

   public TimeModifyModifier(String name, String description, int voteLockDurationChangeSeconds, int timeChange) {
      super(name, description, voteLockDurationChangeSeconds);
      this.timeChange = timeChange;
   }

   @Override
   public void onApply(ArchitectObjective objective, VaultRaid vault, ServerLevel world) {
      super.onApply(objective, vault, world);
      vault.getPlayers().forEach(vPlayer -> vPlayer.getTimer().addTime(new RoomGenerationExtension(this.timeChange), 0));
   }
}
