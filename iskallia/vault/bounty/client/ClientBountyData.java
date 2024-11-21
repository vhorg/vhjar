package iskallia.vault.bounty.client;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.bounty.BountyList;
import iskallia.vault.client.ClientExpertiseData;
import iskallia.vault.init.ModItems;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.expertise.type.BountyHunterExpertise;
import iskallia.vault.util.InventoryUtil;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ClientBountyData {
   public static final ClientBountyData INSTANCE = new ClientBountyData();
   private final BountyList bounties = new BountyList();
   private final BountyList available = new BountyList();
   private static int maxActiveBounties = 1;
   private static boolean hasLostBountyInInventory = false;
   private static boolean hasCompletedBounty = false;
   private static boolean hasLegendaryBounty = false;

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

   public static int getMaxActiveBounties() {
      return maxActiveBounties;
   }

   public static boolean hasLostBountyInInventory() {
      return hasLostBountyInInventory;
   }

   public static boolean hasCompletedBounty() {
      return hasCompletedBounty;
   }

   public static boolean hasLegendaryBounty() {
      return hasLegendaryBounty;
   }

   @SubscribeEvent
   public static void onLogOut(PlayerLoggedOutEvent event) {
      INSTANCE.bounties.clear();
      INSTANCE.available.clear();
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase != Phase.END) {
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            maxActiveBounties = 1;

            for (TieredSkill learnedTalentNode : ClientExpertiseData.getLearnedTalentNodes()) {
               if (learnedTalentNode.getChild() instanceof BountyHunterExpertise bountyHunterExpertise) {
                  maxActiveBounties = bountyHunterExpertise.getMaxActive();
               }
            }

            hasLostBountyInInventory = InventoryUtil.findAllItems(player).stream().anyMatch(itemAccess -> itemAccess.getStack().is(ModItems.LOST_BOUNTY));
            List<Bounty> bounties = INSTANCE.getBounties();
            hasCompletedBounty = false;
            hasLegendaryBounty = false;

            for (Bounty bounty : bounties) {
               if (bounty.getTask().isComplete()) {
                  hasCompletedBounty = true;
               }

               if (bounty.getTask().getProperties().getRewardPool().equals("legendary")) {
                  hasLegendaryBounty = true;
               }
            }
         }
      }
   }
}
