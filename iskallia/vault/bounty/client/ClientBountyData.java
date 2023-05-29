package iskallia.vault.bounty.client;

import iskallia.vault.bounty.BountyList;

public class ClientBountyData {
   public static final ClientBountyData INSTANCE = new ClientBountyData();
   private final BountyList bounties = new BountyList();

   public void updateBounties(BountyList bounties) {
      this.bounties.clear();
      this.bounties.addAll(bounties);
   }

   public BountyList getBounties() {
      return this.bounties;
   }
}
