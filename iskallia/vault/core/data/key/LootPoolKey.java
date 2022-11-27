package iskallia.vault.core.data.key;

import iskallia.vault.core.world.loot.LootPool;
import net.minecraft.resources.ResourceLocation;

public class LootPoolKey extends NamedKey<LootPoolKey, LootPool> {
   protected LootPoolKey(ResourceLocation id, String name) {
      super(id, name);
   }

   public static LootPoolKey empty() {
      return new LootPoolKey(null, null);
   }

   public static LootPoolKey create(ResourceLocation id, String name) {
      return new LootPoolKey(id, name);
   }
}
