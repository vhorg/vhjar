package iskallia.vault.block.base;

import iskallia.vault.block.entity.base.HunterHiddenTileEntity;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.LootableBlockGenerationEvent;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class LootableTileEntity extends BlockEntity implements HunterHiddenTileEntity {
   private ResourceLocation lootTable;
   private boolean hidden;

   public LootableTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public ResourceLocation getLootTable() {
      return this.lootTable;
   }

   @Override
   public boolean isHidden() {
      return this.hidden;
   }

   @Override
   public void setHidden(boolean hidden) {
      if (this.hidden != (this.hidden = hidden)) {
         this.setChanged();
      }
   }

   public List<ItemStack> generateLoot(ServerPlayer player) {
      List<ItemStack> loot = new ArrayList<>();
      LootableBlockGenerationEvent.Data data = CommonEvents.LOOTABLE_BLOCK_GENERATION_EVENT
         .invoke(
            player,
            this.getBlockState(),
            this.getBlockPos(),
            this.lootTable,
            this,
            loot,
            Version.latest(),
            JavaRandom.ofNanoTime(),
            LootableBlockGenerationEvent.Phase.PRE
         );
      LootTableKey key = VaultRegistry.LOOT_TABLE.getKey(data.getLootTable());
      if (key != null) {
         LootTableGenerator generator = new LootTableGenerator(Version.latest(), key, 0.0F);
         generator.source = player;
         generator.generate(data.getRandom());
         generator.getItems().forEachRemaining(loot::add);
      }

      CommonEvents.LOOTABLE_BLOCK_GENERATION_EVENT
         .invoke(
            player,
            this.getBlockState(),
            this.getBlockPos(),
            this.lootTable,
            this,
            loot,
            data.getVersion(),
            data.getRandom(),
            LootableBlockGenerationEvent.Phase.POST
         );
      return loot;
   }

   public void load(@NotNull CompoundTag nbt) {
      super.load(nbt);
      if (nbt.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(nbt.getString("LootTable"));
      }

      this.hidden = nbt.getBoolean("Hidden");
   }

   protected void saveAdditional(@NotNull CompoundTag nbt) {
      super.saveAdditional(nbt);
      if (this.lootTable != null) {
         nbt.putString("LootTable", this.lootTable.toString());
      }

      nbt.putBoolean("Hidden", this.hidden);
   }
}
