package iskallia.vault.core.vault.objective.offering;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OfferingBossFights {
   private final List<OfferingBossFight> fights = new ArrayList<>();
   private final List<OfferingBossFight> scheduledFights = new ArrayList<>();

   public void add(OfferingBossFight fight) {
      this.scheduledFights.add(fight);
   }

   public OfferingBossFight getFight(UUID player) {
      for (OfferingBossFight fight : this.fights) {
         if (fight.getPlayers().contains(player)) {
            return fight;
         }
      }

      return null;
   }

   public void onAttach(VirtualWorld world, Vault vault) {
      for (OfferingBossFight fight : this.fights) {
         if (!fight.isCompleted()) {
            fight.onAttach(world, vault);
         }
      }
   }

   public void onTick(VirtualWorld world, Vault vault) {
      for (OfferingBossFight fight : this.scheduledFights) {
         fight.onAttach(world, vault);
         this.fights.add(fight);
      }

      this.scheduledFights.clear();

      for (OfferingBossFight fight : this.fights) {
         if (fight.isCompleted()) {
            fight.onDetach();
         }
      }
   }

   public void onDetach() {
      for (OfferingBossFight fight : this.fights) {
         fight.onDetach();
      }
   }

   public void writeBits(BitBuffer buffer, SyncContext context) {
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.fights.size()), buffer);

      for (OfferingBossFight fight : this.fights) {
         fight.writeBits(buffer, context);
      }

      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.scheduledFights.size()), buffer);

      for (OfferingBossFight fight : this.scheduledFights) {
         fight.writeBits(buffer, context);
      }
   }

   public void readBits(BitBuffer buffer, SyncContext context) {
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.fights.clear();

      for (int i = 0; i < size; i++) {
         OfferingBossFight fight = new OfferingBossFight();
         fight.readBits(buffer, context);
         this.fights.add(fight);
      }

      size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.scheduledFights.clear();

      for (int i = 0; i < size; i++) {
         OfferingBossFight fight = new OfferingBossFight();
         fight.readBits(buffer, context);
         this.scheduledFights.add(fight);
      }
   }
}
