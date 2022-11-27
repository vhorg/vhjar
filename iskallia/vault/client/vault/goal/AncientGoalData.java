package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class AncientGoalData extends VaultGoalData {
   private int totalAncients = 0;
   private int foundAncients = 0;

   public int getTotalAncients() {
      return this.totalAncients;
   }

   public int getFoundAncients() {
      return this.foundAncients;
   }

   @Nullable
   @Override
   public BossBarOverlay getBossBarOverlay() {
      return null;
   }

   @Override
   public void receive(VaultGoalMessage pkt) {
      CompoundTag data = pkt.payload;
      this.totalAncients = data.getInt("total");
      this.foundAncients = data.getInt("found");
   }
}
