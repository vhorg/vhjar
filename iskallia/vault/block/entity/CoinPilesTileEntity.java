package iskallia.vault.block.entity;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.CoinStacksGenerationEvent;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CoinPilesTileEntity extends BlockEntity {
   private ResourceLocation lootTable;

   public CoinPilesTileEntity(BlockPos p_155630_, BlockState p_155631_) {
      super(ModBlocks.COIN_PILE_TILE, p_155630_, p_155631_);
   }

   public List<ItemStack> generateLoot(ServerPlayer player) {
      List<ItemStack> loot = new ArrayList<>();
      CoinStacksGenerationEvent.Data data = CommonEvents.COIN_STACK_LOOT_GENERATION
         .invoke(
            player,
            this.getBlockState(),
            this.getBlockPos(),
            this.lootTable,
            this,
            loot,
            Version.latest(),
            JavaRandom.ofNanoTime(),
            CoinStacksGenerationEvent.Phase.PRE
         );
      LootTableKey key = VaultRegistry.LOOT_TABLE.getKey(data.getLootTable());
      if (key != null) {
         LootTableGenerator generator = new LootTableGenerator(Version.latest(), key, 0.0F);
         generator.source = player;
         generator.generate(data.getRandom());
         generator.getItems().forEachRemaining(loot::add);
      }

      CommonEvents.COIN_STACK_LOOT_GENERATION
         .invoke(
            player,
            this.getBlockState(),
            this.getBlockPos(),
            this.lootTable,
            this,
            loot,
            data.getVersion(),
            data.getRandom(),
            CoinStacksGenerationEvent.Phase.POST
         );
      return loot;
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      if (nbt.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(nbt.getString("LootTable"));
      }
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      if (this.lootTable != null) {
         nbt.putString("LootTable", this.lootTable.toString());
      }
   }
}
