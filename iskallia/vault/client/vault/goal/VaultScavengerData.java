package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.client.gui.overlay.goal.ScavengerBarOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.world.vault.logic.objective.LegacyScavengerHuntObjective;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;

public class VaultScavengerData extends VaultGoalData {
   private final List<LegacyScavengerHuntObjective.ItemSubmission> itemSubmissions = new ArrayList<>();

   public List<LegacyScavengerHuntObjective.ItemSubmission> getRequiredItemSubmissions() {
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
      ListTag itemList = pkt.payload.getList("scavengerItems", 10);

      for (int i = 0; i < itemList.size(); i++) {
         this.itemSubmissions.add(LegacyScavengerHuntObjective.ItemSubmission.deserialize(itemList.getCompound(i)));
      }
   }
}
