package iskallia.vault.entity;

import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.GlobalDifficultyData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.VaultUtils;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectSummonAndKillBossesObjective;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityEvent;

public class EntityScaler {
   private static final String SCALED_TAG = "vault_scaled";

   public static void scaleVaultEntity(VaultRaid vault, EntityEvent event) {
      if (VaultUtils.inVault(vault, event.getEntity())) {
         scaleVaultEntity(vault, event.getEntity());
      }
   }

   public static void scaleVaultEntity(VaultRaid vault, Entity entity) {
      if (entity instanceof MonsterEntity && !(entity instanceof EternalEntity) && !isScaled(entity)) {
         World world = entity.func_130014_f_();
         if (world instanceof ServerWorld) {
            ServerWorld sWorld = (ServerWorld)world;
            MonsterEntity livingEntity = (MonsterEntity)entity;
            GlobalDifficultyData.Difficulty difficulty = GlobalDifficultyData.get(sWorld).getVaultDifficulty();
            vault.getProperties()
               .getBase(VaultRaid.LEVEL)
               .ifPresent(level -> setScaledEquipment(livingEntity, vault, difficulty, level, new Random(), EntityScaler.Type.MOB));
            setScaled(livingEntity);
            livingEntity.func_110163_bv();
         }
      }
   }

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

      vault.getActiveObjective(ArchitectSummonAndKillBossesObjective.class)
         .ifPresent(
            objective -> {
               if (entity.func_233645_dx_().func_233790_b_(Attributes.field_233818_a_)) {
                  UUID randomId;
                  do {
                     randomId = UUID.randomUUID();
                  } while (entity.func_233645_dx_().func_233782_a_(Attributes.field_233818_a_, randomId));

                  entity.func_110148_a(Attributes.field_233818_a_)
                     .func_233769_c_(
                        new AttributeModifier(randomId, "Final Architect Health", objective.getCombinedMobHealthMultiplier(), Operation.MULTIPLY_BASE)
                     );
               }

               entity.func_70691_i(1000000.0F);
            }
         );

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
