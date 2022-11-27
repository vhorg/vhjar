package iskallia.vault.config.core;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootPoolKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootRoll;
import net.minecraft.world.item.Items;

public class LootPoolsConfig extends KeyRegistryConfig<KeyRegistry<LootPoolKey, LootPool>, LootPoolKey> {
   public static LootPool TEST = new LootPool()
      .addItem(Items.DIORITE, null, LootRoll.ofConstant(2), 2)
      .addItem(Items.POLISHED_DIORITE, null, LootRoll.ofUniform(1, 5), 1);

   @Override
   public KeyRegistry<LootPoolKey, LootPool> create() {
      return new KeyRegistry<>();
   }

   @Override
   public String getSimpleName() {
      return "loot_pools";
   }

   @Override
   protected void reset() {
      this.keys.add(LootPoolKey.create(VaultMod.id("test_pool"), "Test").with(Version.v1_0, TEST));
   }
}
