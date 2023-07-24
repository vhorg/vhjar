package iskallia.vault.bounty.client;

import iskallia.vault.bounty.BountyList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ClientBountyData {
   public static final ClientBountyData INSTANCE = new ClientBountyData();
   private final BountyList bounties = new BountyList();
   private final BountyList available = new BountyList();

   public void updateBounties(BountyList bounties) {
      this.bounties.clear();
      this.bounties.addAll(bounties);
   }

   public void updateAvailableBounties(BountyList bounties) {
      this.available.clear();
      this.available.addAll(bounties);
   }

   public BountyList getBounties() {
      return this.bounties;
   }

   public BountyList getAvailable() {
      return this.available;
   }

   @SubscribeEvent
   public static void onLogOut(PlayerLoggedOutEvent event) {
      INSTANCE.bounties.clear();
      INSTANCE.available.clear();
   }
}
