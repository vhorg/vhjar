package iskallia.vault.core.vault.enhancement;

import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.roll.IntRoll;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.EventPriority;

public class BreakBlocksEnhancementTask extends IntFilterEnhancementTask<BreakBlocksEnhancementTask.Config> {
   public BreakBlocksEnhancementTask() {
   }

   public BreakBlocksEnhancementTask(BreakBlocksEnhancementTask.Config config, UUID vault, UUID player, UUID altar, int requiredCount) {
      super(config, vault, player, altar, requiredCount);
   }

   @Override
   public void initServer(MinecraftServer server) {
      CommonEvents.PLAYER_MINE.register(this, EventPriority.LOW, event -> {
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
      private static final ArrayAdapter<TilePredicate> FILTER = Adapters.ofArray(TilePredicate[]::new, Adapters.TILE_PREDICATE);
      protected TilePredicate[] filter;

      public Config() {
      }

      public Config(String display, IntRoll range, TilePredicate... filter) {
         super(display, range);
         this.filter = filter;
      }

      public BreakBlocksEnhancementTask create(Vault vault, Player player, VaultEnhancementAltarTileEntity altar, RandomSource random) {
         return new BreakBlocksEnhancementTask(this, vault.get(Vault.ID), player.getUUID(), altar.getUUID(), this.range.get(random));
      }

      public boolean isValid(BlockState state, BlockEntity entity) {
         PartialTile tile = PartialTile.of(PartialBlockState.of(state), PartialCompoundNbt.of(entity));

         for (TilePredicate filter : this.filter) {
            if (filter.test(tile)) {
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
         this.filter = FILTER.readNbt(nbt.get("filter")).orElse(new TilePredicate[0]);
      }
   }
}
