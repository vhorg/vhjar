package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.vault.chest.ExplosionEffect;
import iskallia.vault.world.vault.chest.MobTrapEffect;
import iskallia.vault.world.vault.chest.PotionCloudEffect;
import iskallia.vault.world.vault.chest.VaultChestEffect;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Explosion.BlockInteraction;

public class VaultChestConfig extends Config {
   @Expose
   public Map<VaultRarity, Double> RARITY_DISTRIBUTION = new LinkedHashMap<>();
   @Expose
   public List<MobTrapEffect> MOB_TRAP_EFFECTS;
   @Expose
   public List<ExplosionEffect> EXPLOSION_EFFECTS;
   @Expose
   public List<PotionCloudEffect> POTION_CLOUD_EFFECTS;
   @Expose
   public LevelEntryList<VaultChestConfig.Level> LEVELS;
   private final String name;

   public VaultChestConfig(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public List<VaultChestEffect> getAll() {
      return Stream.of(this.MOB_TRAP_EFFECTS, this.EXPLOSION_EFFECTS, this.POTION_CLOUD_EFFECTS).flatMap(Collection::stream).collect(Collectors.toList());
   }

   public VaultChestEffect getByName(String name) {
      return this.getAll().stream().filter(group -> group.getName().equals(name)).findFirst().orElse(null);
   }

   public VaultRarity getRarity(double cdf) {
      Entry<VaultRarity, Double> result = null;

      for (Entry<VaultRarity, Double> entry : this.RARITY_DISTRIBUTION.entrySet()) {
         if (result == null) {
            result = entry;
         } else if (cdf < entry.getValue()) {
            result = entry;
         }
      }

      return result == null ? null : result.getKey();
   }

   @Override
   protected void reset() {
      this.RARITY_DISTRIBUTION.put(VaultRarity.OMEGA, 0.05);
      this.RARITY_DISTRIBUTION.put(VaultRarity.EPIC, 0.2);
      this.RARITY_DISTRIBUTION.put(VaultRarity.RARE, 0.4);
      this.RARITY_DISTRIBUTION.put(VaultRarity.COMMON, 1.0);
      this.LEVELS = new LevelEntryList<>();
      this.MOB_TRAP_EFFECTS = Arrays.asList(new MobTrapEffect("Mob Trap", 5));
      this.EXPLOSION_EFFECTS = Arrays.asList(new ExplosionEffect("Explosion", 4.0F, 0.0, 3.0, 0.0, true, 10.0F, BlockInteraction.BREAK));
      this.POTION_CLOUD_EFFECTS = Arrays.asList(new PotionCloudEffect("Poison", Potions.STRONG_POISON));
      VaultChestConfig.Level level = new VaultChestConfig.Level(5);
      level.probability = 0.1;
      level.pool.add("Explosion", 4);
      level.pool.add("Mob Trap", 4);
      level.pool.add("Poison", 4);
      this.LEVELS.add(level);
   }

   @Nullable
   public double getTrapProbability(int level) {
      return this.getForLevel(level).probability;
   }

   @Nullable
   public WeightedList<String> getEffectPool(int level) {
      return this.getForLevel(level).pool;
   }

   @Nullable
   public VaultChestEffect getEffectByName(String effect) {
      return ModConfigs.VAULT_CHEST.getByName(effect);
   }

   public VaultChestConfig.Level getForLevel(int level) {
      return this.LEVELS.getForLevel(level).orElse(VaultChestConfig.Level.EMPTY);
   }

   public static class Level implements LevelEntryList.ILevelEntry {
      public static VaultChestConfig.Level EMPTY = new VaultChestConfig.Level(0);
      @Expose
      public int level;
      @Expose
      public double probability;
      @Expose
      public WeightedList<String> pool = new WeightedList<>();

      public Level(int level) {
         this.level = level;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }
}
