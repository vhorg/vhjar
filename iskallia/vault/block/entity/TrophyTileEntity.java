package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TrophyTileEntity extends SkinnableTileEntity {
   private WeekKey week = null;
   private PlayerVaultStatsData.PlayerRecordEntry recordEntry = null;

   public TrophyTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.TROPHY_STATUE_TILE_ENTITY, pos, state);
   }

   public WeekKey getWeek() {
      return this.week;
   }

   public void setWeek(WeekKey week) {
      this.week = week;
   }

   public PlayerVaultStatsData.PlayerRecordEntry getRecordEntry() {
      return this.recordEntry;
   }

   public void setRecordEntry(PlayerVaultStatsData.PlayerRecordEntry recordEntry) {
      this.recordEntry = recordEntry;
   }

   public void saveAdditional(@NotNull CompoundTag nbt) {
      super.saveAdditional(nbt);
      if (this.week != null) {
         nbt.put("TrophyWeek", this.week.serialize());
      }

      if (this.recordEntry != null) {
         nbt.put("RecordEntry", this.recordEntry.serialize());
      }
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      if (nbt.contains("TrophyWeek", 10)) {
         this.week = WeekKey.deserialize(nbt.getCompound("TrophyWeek"));
      } else {
         this.week = null;
      }

      if (nbt.contains("RecordEntry", 10)) {
         this.recordEntry = PlayerVaultStatsData.PlayerRecordEntry.deserialize(nbt.getCompound("RecordEntry"));
      } else {
         this.recordEntry = null;
      }
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public boolean isEmpty() {
      return this.recordEntry == null || this.week == null;
   }

   @Override
   protected void updateSkin() {
   }
}
