package iskallia.vault.core.data.key;

import iskallia.vault.core.world.loot.LootTable;
import net.minecraft.resources.ResourceLocation;

public class LootTableKey extends NamedKey<LootTableKey, LootTable> {
   protected LootTableKey(ResourceLocation id, String name) {
      super(id, name);
   }

   public static LootTableKey empty() {
      return new LootTableKey(null, null);
   }

   public static LootTableKey create(ResourceLocation id, String name) {
      return new LootTableKey(id, name);
   }
}
