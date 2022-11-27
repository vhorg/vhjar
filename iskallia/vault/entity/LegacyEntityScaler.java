package iskallia.vault.entity;

import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.VaultUtils;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.EntityEvent;

@Deprecated
public class LegacyEntityScaler {
   private static final String SCALED_TAG = "vault_scaled";

   public static void scaleVaultEntity(VaultRaid vault, EntityEvent event) {
      if (VaultUtils.inVault(vault, event.getEntity())) {
         scaleVaultEntity(vault, event.getEntity());
      }
   }

   public static void scaleVaultEntity(VaultRaid vault, Entity entity) {
      if (entity instanceof Monster livingEntity && !(entity instanceof EternalEntity) && !isScaled(entity)) {
         vault.getProperties()
            .getBase(VaultRaid.LEVEL)
            .ifPresent(level -> setScaledEquipmentLegacy(livingEntity, vault, level, new Random(), LegacyEntityScaler.Type.MOB));
         setScaled(livingEntity);
         livingEntity.setPersistenceRequired();
      }
   }

   public static boolean isScaled(Entity entity) {
      return entity.getTags().contains("vault_scaled");
   }

   public static void setScaled(Entity entity) {
      entity.addTag("vault_scaled");
   }

   public static void setScaledEquipmentLegacy(LivingEntity entity, VaultRaid vault, int level, Random random, LegacyEntityScaler.Type type) {
      VaultMobsConfig.LevelOverride overrides = ModConfigs.VAULT_MOBS.getForLevel(level);
      if (!isScaled(entity)) {
      }

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         if (slot.getType() != net.minecraft.world.entity.EquipmentSlot.Type.HAND || entity.getItemBySlot(slot).isEmpty()) {
            ItemStack loot = type.loot.apply(overrides, slot);

            for (int i = 0; i < type.trials.apply(overrides); i++) {
               EnchantmentHelper.enchantItem(random, loot, EnchantmentHelper.getEnchantmentCost(random, type.level.apply(overrides), 15, loot), true);
            }

            entity.setItemSlot(slot, loot);
            if (entity instanceof Mob) {
               ((Mob)entity).setDropChance(slot, 0.0F);
            }
         }
      }
   }

   public static void setScaledEquipment(LivingEntity entity, int level, Random random, LegacyEntityScaler.Type type) {
      VaultMobsConfig.LevelOverride overrides = ModConfigs.VAULT_MOBS.getForLevel(level);
      if (!isScaled(entity)) {
      }

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         if (slot.getType() != net.minecraft.world.entity.EquipmentSlot.Type.HAND || entity.getItemBySlot(slot).isEmpty()) {
            ItemStack loot = type.loot.apply(overrides, slot);

            for (int i = 0; i < type.trials.apply(overrides); i++) {
               EnchantmentHelper.enchantItem(random, loot, EnchantmentHelper.getEnchantmentCost(random, type.level.apply(overrides), 15, loot), true);
            }

            entity.setItemSlot(slot, loot);
            if (entity instanceof Mob) {
               ((Mob)entity).setDropChance(slot, 0.0F);
            }
         }
      }
   }

   public static enum Type {
      MOB((levelOverride, equipmentSlot) -> ItemStack.EMPTY, level -> 0, level -> 0),
      BOSS((levelOverride, equipmentSlot) -> ItemStack.EMPTY, level -> 0, level -> 0),
      RAFFLE_BOSS((levelOverride, equipmentSlot) -> ItemStack.EMPTY, level -> 0, level -> 0);

      private final BiFunction<VaultMobsConfig.LevelOverride, EquipmentSlot, ItemStack> loot;
      private final Function<VaultMobsConfig.LevelOverride, Integer> trials;
      private final Function<VaultMobsConfig.LevelOverride, Integer> level;

      private Type(
         BiFunction<VaultMobsConfig.LevelOverride, EquipmentSlot, ItemStack> loot,
         Function<VaultMobsConfig.LevelOverride, Integer> trials,
         Function<VaultMobsConfig.LevelOverride, Integer> level
      ) {
         this.loot = loot;
         this.trials = trials;
         this.level = level;
      }
   }
}
