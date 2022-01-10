package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import javax.annotation.Nullable;

public abstract class VaultGoalData {
   public static VaultGoalData CURRENT_DATA = null;
   public static VaultGoalData ADDITIONAL_DATA = null;

   public abstract void receive(VaultGoalMessage var1);

   @Nullable
   public abstract BossBarOverlay getBossBarOverlay();

   public static void create(VaultGoalMessage pkt) {
      switch ((VaultGoalMessage.VaultGoal)pkt.opcode) {
         case OBELISK_GOAL:
         case OBELISK_MESSAGE:
            CURRENT_DATA = new VaultObeliskData();
            CURRENT_DATA.receive(pkt);
            break;
         case SCAVENGER_GOAL:
            CURRENT_DATA = new VaultScavengerData();
            CURRENT_DATA.receive(pkt);
            break;
         case ARCHITECT_GOAL:
            CURRENT_DATA = new ArchitectGoalData();
            CURRENT_DATA.receive(pkt);
            break;
         case ANCIENTS_GOAL:
            CURRENT_DATA = new AncientGoalData();
            CURRENT_DATA.receive(pkt);
            break;
         case RAID_GOAL:
            CURRENT_DATA = new ActiveRaidGoalData();
            CURRENT_DATA.receive(pkt);
            break;
         case CAKE_HUNT_GOAL:
            CURRENT_DATA = new CakeHuntData();
            CURRENT_DATA.receive(pkt);
            break;
         case CLEAR:
            CURRENT_DATA = null;
            ADDITIONAL_DATA = null;
      }
   }
}
