package iskallia.vault.config.tool;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootRoll;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ToolPulverizingConfig extends Config {
   @Expose
   private Map<Item, List<LootTable.Entry>> loot;

   @Override
   public String getName() {
      return "tool%spulverizing".formatted(File.separator);
   }

   public LootTable get(Item item) {
      return this.loot.get(item) == null ? null : new LootTable(this.loot.get(item));
   }

   @Override
   protected void reset() {
      this.loot = new LinkedHashMap<>();
      this.put(Items.COBBLESTONE, new LootTable().add(LootRoll.ofConstant(1), new LootPool().addItem(Items.GRAVEL, null, LootRoll.ofConstant(1), 1)));
      this.put(Items.GRAVEL, new LootTable().add(LootRoll.ofConstant(1), new LootPool().addItem(Items.SAND, null, LootRoll.ofConstant(1), 1)));
      this.put(Items.BRICKS, new LootTable().add(LootRoll.ofConstant(1), new LootPool().addItem(Items.BRICK, null, LootRoll.ofConstant(2), 1)));
      this.put(Items.NETHER_BRICKS, new LootTable().add(LootRoll.ofConstant(1), new LootPool().addItem(Items.NETHER_BRICK, null, LootRoll.ofConstant(2), 1)));
      this.put(Items.NETHER_WART_BLOCK, new LootTable().add(LootRoll.ofConstant(1), new LootPool().addItem(Items.NETHER_WART, null, LootRoll.ofConstant(2), 1)));
      this.put(
         ModBlocks.VAULT_STONE.asItem(),
         new LootTable()
            .add(
               LootRoll.ofConstant(1),
               new LootPool().addItem(ModItems.CHIPPED_VAULT_ROCK, null, LootRoll.ofConstant(1), 2).addItem(Items.AIR, null, LootRoll.ofConstant(1), 3)
            )
      );
      this.put(Items.SANDSTONE, new LootTable().add(LootRoll.ofConstant(1), new LootPool().addItem(Items.SAND, null, LootRoll.ofConstant(2), 1)));
      this.put(Items.RED_SANDSTONE, new LootTable().add(LootRoll.ofConstant(1), new LootPool().addItem(Items.RED_SAND, null, LootRoll.ofConstant(2), 1)));
      this.put(Items.PRISMARINE, new LootTable().add(LootRoll.ofConstant(1), new LootPool().addItem(Items.PRISMARINE_SHARD, null, LootRoll.ofConstant(3), 1)));
      this.put(
         Items.PRISMARINE_BRICKS, new LootTable().add(LootRoll.ofConstant(1), new LootPool().addItem(Items.PRISMARINE_SHARD, null, LootRoll.ofConstant(8), 1))
      );
   }

   private void put(Item item, LootTable lootTable) {
      this.loot.put(item, lootTable.getEntries());
   }
}
