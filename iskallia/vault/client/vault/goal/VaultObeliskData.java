package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;

public class VaultObeliskData extends VaultGoalData {
   private Component message = null;
   private int currentObelisks = 0;
   private int maxObelisks = 0;

   public Component getMessage() {
      return this.message;
   }

   public int getCurrentObelisks() {
      return this.currentObelisks;
   }

   public int getMaxObelisks() {
      return this.maxObelisks;
   }

   @Nullable
   @Override
   public BossBarOverlay getBossBarOverlay() {
      return null;
   }

   @Override
   public void receive(VaultGoalMessage pkt) {
      CompoundTag data = pkt.payload;
      this.message = Serializer.fromJson(data.getString("Message"));
      if (data.contains("MaxObelisks", 3)) {
         this.maxObelisks = data.getInt("MaxObelisks");
      }

      if (data.contains("TouchedObelisks", 3)) {
         this.currentObelisks = data.getInt("TouchedObelisks");
      }
   }
}
