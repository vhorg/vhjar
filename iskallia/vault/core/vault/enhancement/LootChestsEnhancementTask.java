package iskallia.vault.core.vault.enhancement;

import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.core.world.roll.IntRoll;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class LootChestsEnhancementTask extends IntFilterEnhancementTask<LootChestsEnhancementTask.Config> {
   public LootChestsEnhancementTask() {
   }

   public LootChestsEnhancementTask(LootChestsEnhancementTask.Config config, UUID vault, UUID player, UUID altar, int requiredCount) {
      super(config, vault, player, altar, requiredCount);
   }

   @Override
   public void initServer(MinecraftServer server) {
      CommonEvents.CHEST_LOOT_GENERATION.post().register(this, event -> {
         if (this.belongsTo(event.getPlayer())) {
            if (this.config.isValid(event.getState())) {
               this.count++;
            }
         }
      });
   }

   @Override
   public void releaseServer() {
      CommonEvents.CHEST_LOOT_GENERATION.release(this);
   }

   public static class Config extends IntFilterEnhancementTask.Config<LootChestsEnhancementTask> {
      private static final ArrayAdapter<VaultChestType> FILTER = Adapters.ofArray(
         VaultChestType[]::new, Adapters.ofEnum(VaultChestType.class, EnumAdapter.Mode.NAME)
      );
      protected VaultChestType[] filter;

      public Config() {
      }

      public Config(String display, IntRoll range, VaultChestType... filter) {
         super(display, range);
         this.filter = filter;
      }

      public LootChestsEnhancementTask create(Vault vault, Player player, VaultEnhancementAltarTileEntity altar, RandomSource random) {
         return new LootChestsEnhancementTask(this, vault.get(Vault.ID), player.getUUID(), altar.getUUID(), this.range.get(random));
      }

      public boolean isValid(BlockState state) {
         if (state.getBlock() instanceof VaultChestBlock block) {
            for (VaultChestType type : this.filter) {
               if (block.getType() == type) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            FILTER.writeNbt(this.filter).ifPresent(tag -> nbt.put("filter", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.filter = FILTER.readNbt(nbt.get("filter")).orElse(new VaultChestType[0]);
      }
   }
}
