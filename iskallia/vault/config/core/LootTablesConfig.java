package iskallia.vault.config.core;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.roll.IntRoll;

public class LootTablesConfig extends KeyRegistryConfig<KeyRegistry<LootTableKey, LootTable>, LootTableKey> {
   @Override
   public KeyRegistry<LootTableKey, LootTable> create() {
      return new KeyRegistry<>();
   }

   @Override
   public String getSimpleName() {
      return "loot_tables";
   }

   @Override
   protected void reset() {
      this.keys
         .add(LootTableKey.create(VaultMod.id("test_table"), "Test").with(Version.v1_0, new LootTable().add(IntRoll.ofUniform(1, 5), LootPoolsConfig.TEST)));
   }
}
