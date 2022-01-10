package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;

public class CakeHuntData extends VaultGoalData {
   private int totalCakes;
   private int foundCakes;

   @Override
   public void receive(VaultGoalMessage pkt) {
      CompoundNBT tag = pkt.payload;
      this.totalCakes = tag.func_74762_e("total");
      this.foundCakes = tag.func_74762_e("found");
   }

   @Nullable
   @Override
   public BossBarOverlay getBossBarOverlay() {
      return null;
   }

   public float getCompletePercent() {
      return (float)this.foundCakes / this.totalCakes;
   }

   public int getTotalCakes() {
      return this.totalCakes;
   }

   public int getFoundCakes() {
      return this.foundCakes;
   }
}
