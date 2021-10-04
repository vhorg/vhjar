package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.chest.ExplosionEffect;
import iskallia.vault.world.vault.chest.MobTrapEffect;
import iskallia.vault.world.vault.chest.PotionCloudEffect;
import iskallia.vault.world.vault.chest.VaultChestEffect;
import iskallia.vault.world.vault.logic.VaultSpawner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.potion.Potions;
import net.minecraft.world.Explosion.Mode;

public class VaultChestConfig extends Config {
   @Expose
   public WeightedList<String> RARITY_POOL = new WeightedList<>();
   @Expose
   public List<MobTrapEffect> MOB_TRAP_EFFECTS;
   @Expose
   public List<ExplosionEffect> EXPLOSION_EFFECTS;
   @Expose
   public List<PotionCloudEffect> POTION_CLOUD_EFFECTS;
   @Expose
   public List<VaultChestConfig.Level> LEVELS;
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

   @Override
   protected void reset() {
      this.RARITY_POOL.add(VaultRarity.COMMON.name(), 16);
      this.RARITY_POOL.add(VaultRarity.RARE.name(), 4);
      this.RARITY_POOL.add(VaultRarity.EPIC.name(), 2);
      this.RARITY_POOL.add(VaultRarity.OMEGA.name(), 1);
      this.LEVELS = new ArrayList<>();
      this.MOB_TRAP_EFFECTS = Arrays.asList(
         new MobTrapEffect("Mob Trap", 5, new VaultSpawner.Config().withExtraMaxMobs(15).withMinDistance(1.0).withMaxDistance(12.0).withDespawnDistance(32.0))
      );
      this.EXPLOSION_EFFECTS = Arrays.asList(new ExplosionEffect("Explosion", 4.0F, 0.0, 3.0, 0.0, true, 10.0F, Mode.BREAK));
      this.POTION_CLOUD_EFFECTS = Arrays.asList(new PotionCloudEffect("Poison", Potions.field_185219_B));
      VaultChestConfig.Level level = new VaultChestConfig.Level(5);
      level.DEFAULT_POOL.add("Dummy", 20);
      level.DEFAULT_POOL.add("Explosion", 4);
      level.DEFAULT_POOL.add("Mob Trap", 4);
      level.DEFAULT_POOL.add("Poison", 4);
      level.RAFFLE_POOL.add("Dummy", 20);
      level.RAFFLE_POOL.add("Explosion", 4);
      level.RAFFLE_POOL.add("Mob Trap", 4);
      level.RAFFLE_POOL.add("Poison", 4);
      this.LEVELS.add(level);
   }

   @Nullable
   public WeightedList<String> getEffectPool(int level, boolean raffle) {
      VaultChestConfig.Level override = this.getForLevel(level);
      return raffle ? override.RAFFLE_POOL : override.DEFAULT_POOL;
   }

   @Nullable
   public VaultChestEffect getEffectByName(String effect) {
      return ModConfigs.VAULT_CHEST.getByName(effect);
   }

   public VaultChestConfig.Level getForLevel(int level) {
      for (int i = 0; i < this.LEVELS.size(); i++) {
         if (level < this.LEVELS.get(i).MIN_LEVEL) {
            if (i != 0) {
               return this.LEVELS.get(i - 1);
            }
            break;
         }

         if (i == this.LEVELS.size() - 1) {
            return this.LEVELS.get(i);
         }
      }

      return VaultChestConfig.Level.EMPTY;
   }

   public static class Level {
      public static VaultChestConfig.Level EMPTY = new VaultChestConfig.Level(0);
      @Expose
      public int MIN_LEVEL;
      @Expose
      public WeightedList<String> DEFAULT_POOL = new WeightedList<>();
      @Expose
      public WeightedList<String> RAFFLE_POOL = new WeightedList<>();

      public Level(int minLevel) {
         this.MIN_LEVEL = minLevel;
      }
   }
}
