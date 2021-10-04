package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.client.gui.overlay.goal.ScavengerBarOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.world.vault.logic.objective.ScavengerHuntObjective;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListNBT;

public class VaultScavengerData extends VaultGoalData {
   private final List<ScavengerHuntObjective.ItemSubmission> itemSubmissions = new ArrayList<>();

   public List<ScavengerHuntObjective.ItemSubmission> getRequiredItemSubmissions() {
      return Collections.unmodifiableList(this.itemSubmissions);
   }

   @Nullable
   @Override
   public BossBarOverlay getBossBarOverlay() {
      return new ScavengerBarOverlay(this);
   }

   @Override
   public void receive(VaultGoalMessage pkt) {
      this.itemSubmissions.clear();
      ListNBT itemList = pkt.payload.func_150295_c("scavengerItems", 10);

      for (int i = 0; i < itemList.size(); i++) {
         this.itemSubmissions.add(ScavengerHuntObjective.ItemSubmission.deserialize(itemList.func_150305_b(i)));
      }
   }
}
