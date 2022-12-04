package iskallia.vault.core.event.common;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.core.Version;
import iskallia.vault.core.event.Event;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.CrateLootGenerator;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CrateAwardEvent extends Event<CrateAwardEvent, CrateAwardEvent.Data> {
   public CrateAwardEvent() {
   }

   protected CrateAwardEvent(CrateAwardEvent parent) {
      super(parent);
   }

   public CrateAwardEvent.Data invoke(
      ServerPlayer player,
      ItemStack crate,
      CrateLootGenerator crateLootGenerator,
      Vault vault,
      Listener listener,
      VaultCrateBlock.Type crateType,
      List<ItemStack> loot,
      Version version,
      RandomSource random,
      CrateAwardEvent.Phase phase
   ) {
      return this.invoke(new CrateAwardEvent.Data(player, crate, crateLootGenerator, vault, listener, crateType, loot, version, random, phase));
   }

   public CrateAwardEvent createChild() {
      return new CrateAwardEvent(this);
   }

   public static class Data {
      private final ServerPlayer player;
      private CrateLootGenerator crateLootGenerator;
      private Vault vault;
      private Listener listener;
      private VaultCrateBlock.Type crateType;
      private final List<ItemStack> loot;
      private Version version;
      private RandomSource random;
      private final CrateAwardEvent.Phase phase;
      private final ItemStack crate;

      public Data(
         ServerPlayer player,
         ItemStack crate,
         CrateLootGenerator crateLootGenerator,
         Vault vault,
         Listener listener,
         VaultCrateBlock.Type crateType,
         List<ItemStack> loot,
         Version version,
         RandomSource random,
         CrateAwardEvent.Phase phase
      ) {
         this.player = player;
         this.crateLootGenerator = crateLootGenerator;
         this.vault = vault;
         this.listener = listener;
         this.crateType = crateType;
         this.loot = loot;
         this.version = version;
         this.random = random;
         this.phase = phase;
         this.crate = crate;
      }

      public VaultCrateBlock.Type getCrateType() {
         return this.crateType;
      }

      public CrateLootGenerator getCrateLootGenerator() {
         return this.crateLootGenerator;
      }

      public Vault getVault() {
         return this.vault;
      }

      public Listener getListener() {
         return this.listener;
      }

      public ItemStack getCrate() {
         return this.crate;
      }

      public ServerPlayer getPlayer() {
         return this.player;
      }

      public List<ItemStack> getLoot() {
         return this.loot;
      }

      public Version getVersion() {
         return this.version;
      }

      public RandomSource getRandom() {
         return this.random;
      }

      public CrateAwardEvent.Phase getPhase() {
         return this.phase;
      }
   }

   public static enum Phase {
      PRE,
      POST;
   }
}
