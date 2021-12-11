package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.util.data.WeightedList;
import java.util.stream.StreamSupport;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;

public class MysteryHostileEggConfig extends Config {
   @Expose
   public WeightedList<ProductEntry> POOL = new WeightedList<>();

   @Override
   public String getName() {
      return "mystery_hostile_egg";
   }

   @Override
   protected void reset() {
      this.POOL.add(new ProductEntry(this.getEgg(EntityType.field_200725_aD)), 3);
      this.POOL.add(new ProductEntry(this.getEgg(EntityType.field_200741_ag)), 1);
   }

   private Item getEgg(EntityType<?> type) {
      return StreamSupport.<SpawnEggItem>stream(SpawnEggItem.func_195985_g().spliterator(), false)
         .filter(eggItem -> type.equals(eggItem.func_208076_b(null)))
         .findAny()
         .map(eggItem -> (Item)eggItem)
         .orElse(Items.field_190931_a);
   }
}
