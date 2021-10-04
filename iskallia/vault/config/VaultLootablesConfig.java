package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.util.data.RandomListAccess;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.modifier.LootableModifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class VaultLootablesConfig extends Config {
   @Expose
   public VaultLootablesConfig.Lootable ORE = VaultLootablesConfig.Lootable.defaultConfig();
   @Expose
   public VaultLootablesConfig.Lootable DOOR = VaultLootablesConfig.Lootable.defaultConfig();
   @Expose
   public VaultLootablesConfig.Lootable RICHITY = VaultLootablesConfig.Lootable.defaultConfig();
   @Expose
   public VaultLootablesConfig.Lootable RESOURCE = VaultLootablesConfig.Lootable.defaultConfig();
   @Expose
   public VaultLootablesConfig.Lootable MISC = VaultLootablesConfig.Lootable.defaultConfig();
   @Expose
   public VaultLootablesConfig.Lootable VAULT_CHEST = VaultLootablesConfig.Lootable.defaultConfig();
   @Expose
   public VaultLootablesConfig.Lootable VAULT_TREASURE = VaultLootablesConfig.Lootable.defaultConfig();

   @Override
   public String getName() {
      return "vault_lootables";
   }

   @Override
   protected void reset() {
   }

   public static class Lootable {
      @Expose
      private WeightedList<String> DEFAULT = new WeightedList<>();
      @Expose
      private Map<String, WeightedList<String>> OVERRIDES = new LinkedHashMap<>();

      @Nonnull
      public BlockState get(ServerWorld world, BlockPos pos, Random random, String poolName, UUID playerUUID) {
         RandomListAccess<String> pool = this.getPool(playerUUID);
         VaultRaid vault = VaultRaidData.get(world).getAt(world, pos);
         if (vault != null) {
            for (LootableModifier modifier : vault.getActiveModifiersFor(PlayerFilter.of(playerUUID), LootableModifier.class)) {
               pool = modifier.adjustLootWeighting(poolName, pool);
            }
         }

         return Registry.field_212618_g.func_241873_b(new ResourceLocation(pool.getRandom(random))).orElse(Blocks.field_150350_a).func_176223_P();
      }

      public WeightedList<String> getPool(@Nullable UUID playerUUID) {
         WeightedList<String> pool = new WeightedList<>();
         if (playerUUID != null) {
            pool.addAll(this.OVERRIDES.getOrDefault(playerUUID.toString(), new WeightedList<>()));
         }

         this.DEFAULT.forEach(entry -> {
            if (!pool.containsValue(entry.value)) {
               pool.add((WeightedList.Entry<String>)entry);
            }
         });
         return pool;
      }

      public static VaultLootablesConfig.Lootable defaultConfig() {
         VaultLootablesConfig.Lootable lootable = new VaultLootablesConfig.Lootable();
         lootable.DEFAULT.add(Blocks.field_150350_a.getRegistryName().toString(), 1);
         lootable.OVERRIDES
            .computeIfAbsent("cc821d6c-a2f4-4307-955d-8b30c2fc505d", key -> new WeightedList<>())
            .add(Blocks.field_150348_b.getRegistryName().toString(), 1);
         return lootable;
      }
   }
}
