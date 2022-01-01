package iskallia.vault.entity;

import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.GlobalDifficultyData;
import iskallia.vault.world.vault.VaultRaid;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.ItemStack;

public class EntityScaler {
   private static final String SCALED_TAG = "vault_scaled";

   public static boolean isScaled(Entity entity) {
      return entity.func_184216_O().contains("vault_scaled");
   }

   public static void setScaled(Entity entity) {
      entity.func_184211_a("vault_scaled");
   }

   public static void setScaledEquipment(
      LivingEntity entity, VaultRaid vault, GlobalDifficultyData.Difficulty vaultDifficulty, int level, Random random, EntityScaler.Type type
   ) {
      VaultMobsConfig.Level overrides = ModConfigs.VAULT_MOBS.getForLevel(level);
      if (!isScaled(entity)) {
         VaultMobsConfig.Mob.scale(entity, vault, vaultDifficulty);
      }

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         if (slot.func_188453_a() != Group.HAND || entity.func_184582_a(slot).func_190926_b()) {
            ItemStack loot = type.loot.apply(overrides, slot);

            for (int i = 0; i < type.trials.apply(overrides); i++) {
               EnchantmentHelper.func_77504_a(random, loot, EnchantmentHelper.func_77514_a(random, type.level.apply(overrides), 15, loot), true);
            }

            entity.func_184201_a(slot, loot);
            if (entity instanceof MobEntity) {
               ((MobEntity)entity).func_184642_a(slot, 0.0F);
            }
         }
      }
   }

   public static enum Type {
      MOB(VaultMobsConfig.Level::getForMob, level -> level.MOB_MISC.ENCH_TRIALS, level -> level.MOB_MISC.ENCH_LEVEL),
      BOSS(VaultMobsConfig.Level::getForBoss, level -> level.BOSS_MISC.ENCH_TRIALS, level -> level.BOSS_MISC.ENCH_LEVEL),
      RAFFLE_BOSS(VaultMobsConfig.Level::getForRaffle, level -> level.RAFFLE_BOSS_MISC.ENCH_TRIALS, level -> level.RAFFLE_BOSS_MISC.ENCH_LEVEL);

      private final BiFunction<VaultMobsConfig.Level, EquipmentSlotType, ItemStack> loot;
      private final Function<VaultMobsConfig.Level, Integer> trials;
      private final Function<VaultMobsConfig.Level, Integer> level;

      private Type(
         BiFunction<VaultMobsConfig.Level, EquipmentSlotType, ItemStack> loot,
         Function<VaultMobsConfig.Level, Integer> trials,
         Function<VaultMobsConfig.Level, Integer> level
      ) {
         this.loot = loot;
         this.trials = trials;
         this.level = level;
      }
   }
}
