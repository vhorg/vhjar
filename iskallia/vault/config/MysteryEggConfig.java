package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.util.data.WeightedList;
import java.util.stream.StreamSupport;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;

public class MysteryEggConfig extends Config {
   @Expose
   public WeightedList<ProductEntry> POOL = new WeightedList<>();

   @Override
   public String getName() {
      return "mystery_egg";
   }

   @Override
   protected void reset() {
      this.POOL.add(new ProductEntry(this.getEgg(EntityType.COW)), 3);
      this.POOL.add(new ProductEntry(this.getEgg(EntityType.PIG)), 1);
   }

   private Item getEgg(EntityType<?> type) {
      return StreamSupport.<SpawnEggItem>stream(SpawnEggItem.eggs().spliterator(), false)
         .filter(eggItem -> type.equals(eggItem.getType(null)))
         .findAny()
         .map(eggItem -> (Item)eggItem)
         .orElse(Items.AIR);
   }
}
