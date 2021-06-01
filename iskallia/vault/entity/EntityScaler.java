package iskallia.vault.entity;

import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.init.ModConfigs;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

public class EntityScaler {
   public static void scaleVault(LivingEntity entity, int level, Random random, EntityScaler.Type type) {
      VaultMobsConfig.Level overrides = ModConfigs.VAULT_MOBS.getForLevel(level);

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         if (slot.func_188453_a() != Group.HAND || entity.func_184582_a(slot).func_190926_b()) {
            ItemStack loot = new ItemStack((IItemProvider)type.loot.apply(overrides, slot));

            for (int i = 0; i < type.trials.apply(overrides); i++) {
               EnchantmentHelper.func_77504_a(random, loot, EnchantmentHelper.func_77514_a(random, type.level.apply(overrides), 15, loot), true);
            }

            entity.func_184201_a(slot, loot);
         }
      }
   }

   public static enum Type {
      MOB(VaultMobsConfig.Level::getForMob, level -> level.MOB_MISC.ENCH_TRIALS, level -> level.MOB_MISC.ENCH_LEVEL),
      BOSS(VaultMobsConfig.Level::getForBoss, level -> level.BOSS_MISC.ENCH_TRIALS, level -> level.BOSS_MISC.ENCH_LEVEL);

      private final BiFunction<VaultMobsConfig.Level, EquipmentSlotType, Item> loot;
      private final Function<VaultMobsConfig.Level, Integer> trials;
      private final Function<VaultMobsConfig.Level, Integer> level;

      private Type(
         BiFunction<VaultMobsConfig.Level, EquipmentSlotType, Item> loot,
         Function<VaultMobsConfig.Level, Integer> trials,
         Function<VaultMobsConfig.Level, Integer> level
      ) {
         this.loot = loot;
         this.trials = trials;
         this.level = level;
      }
   }
}
