package iskallia.vault.entity.champion;

import iskallia.vault.config.ChampionsConfig;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.entity.ai.ChampionGoal;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.WorldSettings;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.Range;

public class ChampionPromoter {
   private static final String NO_CHAMPION_TAG = "no_champion";

   public static void applyRandomChampion(LivingEntity entity) {
      if (!entity.getTags().contains("no_champion")) {
         double chance = ModConfigs.CHAMPIONS.defaultChampionChance;

         for (Entry<EntityPredicate, Float> entry : ModConfigs.CHAMPIONS.entityChampionChance.entrySet()) {
            EntityPredicate predicate = entry.getKey();
            if (predicate.test(entity)) {
               chance = entry.getValue().floatValue();
               break;
            }
         }

         chance = CommonEvents.CHAMPION_PROMOTE.invoke(chance).getProbability();
         if (ChampionLogic.isChampion(entity) || entity.level.random.nextFloat() < chance) {
            applyChampionAttributes(entity);
            applyAffixes(entity);
         }
      }
   }

   private static void applyAffixes(LivingEntity entity) {
      if (entity instanceof ChampionLogic.IChampionLogicHolder championLogicHolder) {
         for (Entry<EntityPredicate, WeightedList<CompoundTag>> entityAffixes : ModConfigs.CHAMPIONS.entityAffixesData.entrySet()) {
            if (entityAffixes.getKey().test(entity)) {
               int numberOfAffixes = Math.min(getNumberOfAffixes(entity.level), entityAffixes.getValue().size());

               for (int i = 0; i < numberOfAffixes; i++) {
                  entityAffixes.getValue()
                     .getRandom(entity.getLevel().random)
                     .flatMap(ChampionAffixRegistry::deserialize)
                     .ifPresent(championLogicHolder.getChampionLogic()::addAffix);
               }
               break;
            }
         }

         championLogicHolder.getChampionLogic().syncClientData(entity, PacketDistributor.DIMENSION.with(() -> entity.level.dimension()));
      }
   }

   private static int getNumberOfAffixes(Level level) {
      return ServerVaults.get(level)
         .map(
            vault -> {
               VaultDifficulty vaultDifficulty = WorldSettings.get(level).getPlayerDifficulty(vault.get(Vault.OWNER));
               Range<Integer> championAffixCount = vaultDifficulty.getChampionAffixCount();
               return championAffixCount.getMinimum() < championAffixCount.getMaximum()
                  ? level.random.nextInt((Integer)championAffixCount.getMinimum(), (Integer)championAffixCount.getMaximum()) + 1
                  : (Integer)championAffixCount.getMinimum();
            }
         )
         .orElse(0);
   }

   public static void applyChampionAttributes(LivingEntity entity) {
      entity.addTag("vault_champion");
      entity.playSound(SoundEvents.ILLUSIONER_CAST_SPELL, 1.0F, 1.0F);
      if (entity instanceof Mob mob) {
         ServerVaults.get(mob.level).ifPresent(vault -> ChampionGoal.registerProjectileGoal(vault, mob));
      }

      Set<String> appliedOverrides = new HashSet<>();
      ModConfigs.CHAMPIONS.entityAttributeOverrides.forEach((predicate, overrides) -> {
         if (predicate.test(entity)) {
            overrides.forEach(override -> {
               applyOverride(entity, override);
               appliedOverrides.add(override.NAME);
            });
         }
      });
      ModConfigs.CHAMPIONS.defaultAttributeOverrides.forEach(attributeOverride -> {
         if (!appliedOverrides.contains(attributeOverride.NAME)) {
            applyOverride(entity, attributeOverride);
         }
      });
      entity.setHealth(1.0F);
      entity.heal(1000000.0F);
   }

   private static void applyOverride(LivingEntity entity, ChampionsConfig.AttributeOverride override) {
      Registry.ATTRIBUTE.getOptional(new ResourceLocation(override.NAME)).ifPresent(attribute -> {
         AttributeInstance instance = entity.getAttribute(attribute);
         if (instance != null) {
            override.applyTo(instance);
         }
      });
   }
}
