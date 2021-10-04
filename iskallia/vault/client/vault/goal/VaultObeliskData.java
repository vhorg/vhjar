package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;

public class VaultObeliskData extends VaultGoalData {
   private ITextComponent message = null;
   private int currentObelisks = 0;
   private int maxObelisks = 0;

   public ITextComponent getMessage() {
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
      CompoundNBT data = pkt.payload;
      this.message = Serializer.func_240643_a_(data.func_74779_i("Message"));
      if (data.func_150297_b("MaxObelisks", 3)) {
         this.maxObelisks = data.func_74762_e("MaxObelisks");
      }

      if (data.func_150297_b("TouchedObelisks", 3)) {
         this.currentObelisks = data.func_74762_e("TouchedObelisks");
      }
   }
}
