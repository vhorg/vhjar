package iskallia.vault.core.vault.enhancement;

import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.PartialNBT;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.data.TilePredicate;
import iskallia.vault.core.world.roll.IntRoll;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BreakBlocksEnhancementTask extends IntFilterEnhancementTask<BreakBlocksEnhancementTask.Config> {
   public BreakBlocksEnhancementTask() {
   }

   public BreakBlocksEnhancementTask(BreakBlocksEnhancementTask.Config config, UUID vault, UUID player, UUID altar, int requiredCount) {
      super(config, vault, player, altar, requiredCount);
   }

   @Override
   public void initServer(MinecraftServer server) {
      CommonEvents.PLAYER_MINE.register(this, event -> {
         if (this.belongsTo(event.getPlayer())) {
            BlockEntity blockEntity = event.getWorld().getBlockEntity(event.getPos());
            if (this.config.isValid(event.getState(), blockEntity)) {
               this.count++;
            }
         }
      });
   }

   @Override
   public void releaseServer() {
      CommonEvents.PLAYER_MINE.release(this);
   }

   public static class Config extends IntFilterEnhancementTask.Config<BreakBlocksEnhancementTask> {
      private static final ArrayAdapter<String> FILTER = Adapters.ofArray(String[]::new, Adapters.UTF_8);
      protected String[] filter;

      public Config() {
      }

      public Config(String display, IntRoll range, String... filter) {
         super(display, range);
         this.filter = filter;
      }

      public BreakBlocksEnhancementTask create(Vault vault, Player player, VaultEnhancementAltarTileEntity altar, RandomSource random) {
         return new BreakBlocksEnhancementTask(this, vault.get(Vault.ID), player.getUUID(), altar.getUUID(), this.range.get(random));
      }

      public boolean isValid(BlockState state, BlockEntity entity) {
         PartialTile tile = PartialTile.of(state, (CompoundTag)(entity == null ? PartialNBT.empty() : entity.serializeNBT()));

         for (String filter : this.filter) {
            if (TilePredicate.of(filter).test(tile)) {
               return true;
            }
         }

         return false;
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
         this.filter = FILTER.readNbt(nbt.get("filter")).orElse(new String[0]);
      }
   }
}
