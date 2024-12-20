package iskallia.vault.config.tool;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.roll.IntRoll;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ToolPulverizingConfig extends Config {
   @Expose
   private Map<ResourceLocation, List<LootTable.Entry>> loot;

   @Override
   public String getName() {
      return "tool%spulverizing".formatted(File.separator);
   }

   public LootTable get(Item item) {
      return this.get(item.getRegistryName());
   }

   public LootTable get(ResourceLocation item) {
      return this.loot.get(item) == null ? null : new LootTable(this.loot.get(item));
   }

   public Map<ResourceLocation, List<LootTable.Entry>> getLoot() {
      return this.loot;
   }

   @Override
   protected void reset() {
      this.loot = new LinkedHashMap<>();
      this.put(Items.COBBLESTONE, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.GRAVEL, null, IntRoll.ofConstant(1), 1)));
      this.put(Items.GRAVEL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.SAND, null, IntRoll.ofConstant(1), 1)));
      this.put(Items.BRICKS, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.BRICK, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.NETHER_BRICKS, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.NETHER_BRICK, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.NETHER_WART_BLOCK, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.NETHER_WART, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.SANDSTONE, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.SAND, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.RED_SANDSTONE, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.RED_SAND, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.PRISMARINE, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.PRISMARINE_SHARD, null, IntRoll.ofConstant(3), 1)));
      this.put(
         Items.PRISMARINE_BRICKS, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.PRISMARINE_SHARD, null, IntRoll.ofConstant(8), 1))
      );
      this.put(Items.WHITE_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.ORANGE_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.MAGENTA_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.LIGHT_BLUE_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.YELLOW_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.LIME_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.PINK_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.GRAY_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.LIGHT_GRAY_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.CYAN_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.PURPLE_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.BLUE_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.BROWN_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.GREEN_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.RED_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
      this.put(Items.BLACK_WOOL, new LootTable().add(IntRoll.ofConstant(1), new LootPool().addItem(Items.STRING, null, IntRoll.ofConstant(2), 1)));
   }

   private void put(Item item, LootTable lootTable) {
      this.put(item.getRegistryName(), lootTable);
   }

   private void put(ResourceLocation item, LootTable lootTable) {
      this.loot.put(item, lootTable.getEntries());
   }
}
