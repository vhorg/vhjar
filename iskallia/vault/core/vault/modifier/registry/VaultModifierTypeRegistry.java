package iskallia.vault.core.vault.modifier.registry;

import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.modifier.modifier.ChanceArtifactModifier;
import iskallia.vault.core.vault.modifier.modifier.ChanceCatalystModifier;
import iskallia.vault.core.vault.modifier.modifier.ChanceChampionModifier;
import iskallia.vault.core.vault.modifier.modifier.ChanceChestTrapModifier;
import iskallia.vault.core.vault.modifier.modifier.ChanceSoulShardModifier;
import iskallia.vault.core.vault.modifier.modifier.CrateItemQuantityModifier;
import iskallia.vault.core.vault.modifier.modifier.DecoratorAddModifier;
import iskallia.vault.core.vault.modifier.modifier.DecoratorCascadeModifier;
import iskallia.vault.core.vault.modifier.modifier.EntityEffectModifier;
import iskallia.vault.core.vault.modifier.modifier.ExperienceModifier;
import iskallia.vault.core.vault.modifier.modifier.GameControlsModifier;
import iskallia.vault.core.vault.modifier.modifier.GroupedModifier;
import iskallia.vault.core.vault.modifier.modifier.HunterModifier;
import iskallia.vault.core.vault.modifier.modifier.InlinePoolModifier;
import iskallia.vault.core.vault.modifier.modifier.LootItemQuantityModifier;
import iskallia.vault.core.vault.modifier.modifier.LootItemRarityModifier;
import iskallia.vault.core.vault.modifier.modifier.MobAttributeModifier;
import iskallia.vault.core.vault.modifier.modifier.MobCurseOnHitModifier;
import iskallia.vault.core.vault.modifier.modifier.MobFrenzyModifier;
import iskallia.vault.core.vault.modifier.modifier.MobSpawnCountModifier;
import iskallia.vault.core.vault.modifier.modifier.NoSoulShardsModifier;
import iskallia.vault.core.vault.modifier.modifier.ObjectiveTargetModifier;
import iskallia.vault.core.vault.modifier.modifier.PlayerAttributeModifier;
import iskallia.vault.core.vault.modifier.modifier.PlayerDurabilityDamageModifier;
import iskallia.vault.core.vault.modifier.modifier.PlayerEffectModifier;
import iskallia.vault.core.vault.modifier.modifier.PlayerHealCutModifier;
import iskallia.vault.core.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import iskallia.vault.core.vault.modifier.modifier.PlayerNoExitModifier;
import iskallia.vault.core.vault.modifier.modifier.PlayerNoVaultFruitModifier;
import iskallia.vault.core.vault.modifier.modifier.PlayerStatModifier;
import iskallia.vault.core.vault.modifier.modifier.SpawnerMobsModifier;
import iskallia.vault.core.vault.modifier.modifier.TemplateProcessorModifier;
import iskallia.vault.core.vault.modifier.modifier.VaultLevelModifier;
import iskallia.vault.core.vault.modifier.modifier.VaultLootableWeightModifier;
import iskallia.vault.core.vault.modifier.modifier.VaultTimeModifier;
import iskallia.vault.core.vault.modifier.spi.AbstractChanceModifier;
import iskallia.vault.core.vault.modifier.spi.EntityAttributeModifier;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;

public final class VaultModifierTypeRegistry {
   private static final Map<ResourceLocation, VaultModifierType<?, ?>> MODIFIER_TYPE_REGISTRY = new HashMap<ResourceLocation, VaultModifierType<?, ?>>() {
      {
         this.put(
            VaultMod.id("modifier_type/chance_artifact"),
            VaultModifierType.of(ChanceArtifactModifier.class, AbstractChanceModifier.Properties.class, ChanceArtifactModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/chance_catalyst"),
            VaultModifierType.of(ChanceCatalystModifier.class, AbstractChanceModifier.Properties.class, ChanceCatalystModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/chance_chest_trap"),
            VaultModifierType.of(ChanceChestTrapModifier.class, AbstractChanceModifier.Properties.class, ChanceChestTrapModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/chance_soul_shard"),
            VaultModifierType.of(ChanceSoulShardModifier.class, AbstractChanceModifier.Properties.class, ChanceSoulShardModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/chance_champion"),
            VaultModifierType.of(ChanceChampionModifier.class, AbstractChanceModifier.Properties.class, ChanceChampionModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/decorator_add"),
            VaultModifierType.of(DecoratorAddModifier.class, DecoratorAddModifier.Properties.class, DecoratorAddModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/loot_item_quantity"),
            VaultModifierType.of(LootItemQuantityModifier.class, LootItemQuantityModifier.Properties.class, LootItemQuantityModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/loot_item_rarity"),
            VaultModifierType.of(LootItemRarityModifier.class, LootItemRarityModifier.Properties.class, LootItemRarityModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/crate_item_quantity"),
            VaultModifierType.of(CrateItemQuantityModifier.class, CrateItemQuantityModifier.Properties.class, CrateItemQuantityModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/mob_attribute"),
            VaultModifierType.of(MobAttributeModifier.class, EntityAttributeModifier.Properties.class, MobAttributeModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/mob_curse_on_hit"),
            VaultModifierType.of(MobCurseOnHitModifier.class, MobCurseOnHitModifier.Properties.class, MobCurseOnHitModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/mob_frenzy"), VaultModifierType.of(MobFrenzyModifier.class, MobFrenzyModifier.Properties.class, MobFrenzyModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/mob_spawn_count"),
            VaultModifierType.of(MobSpawnCountModifier.class, MobSpawnCountModifier.Properties.class, MobSpawnCountModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/player_attribute"),
            VaultModifierType.of(PlayerAttributeModifier.class, EntityAttributeModifier.Properties.class, PlayerAttributeModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/player_durability_damage"),
            VaultModifierType.of(PlayerDurabilityDamageModifier.class, PlayerDurabilityDamageModifier.Properties.class, PlayerDurabilityDamageModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/player_effect"),
            VaultModifierType.of(PlayerEffectModifier.class, PlayerEffectModifier.Properties.class, PlayerEffectModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/entity_effect"),
            VaultModifierType.of(EntityEffectModifier.class, EntityEffectModifier.Properties.class, EntityEffectModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/player_inventory_restore"),
            VaultModifierType.of(PlayerInventoryRestoreModifier.class, PlayerInventoryRestoreModifier.Properties.class, PlayerInventoryRestoreModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/player_no_exit"),
            VaultModifierType.of(PlayerNoExitModifier.class, PlayerNoExitModifier.Properties.class, PlayerNoExitModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/player_no_vault_fruit"),
            VaultModifierType.of(PlayerNoVaultFruitModifier.class, PlayerNoVaultFruitModifier.Properties.class, PlayerNoVaultFruitModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/player_stat"),
            VaultModifierType.of(PlayerStatModifier.class, PlayerStatModifier.Properties.class, PlayerStatModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/vault_level"),
            VaultModifierType.of(VaultLevelModifier.class, VaultLevelModifier.Properties.class, VaultLevelModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/vault_lootable_weight"),
            VaultModifierType.of(VaultLootableWeightModifier.class, VaultLootableWeightModifier.Properties.class, VaultLootableWeightModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/vault_time"), VaultModifierType.of(VaultTimeModifier.class, VaultTimeModifier.Properties.class, VaultTimeModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/game_controls"),
            VaultModifierType.of(GameControlsModifier.class, GameControlsModifier.Properties.class, GameControlsModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/experience"),
            VaultModifierType.of(ExperienceModifier.class, ExperienceModifier.Properties.class, ExperienceModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/inline_pool"),
            VaultModifierType.of(InlinePoolModifier.class, InlinePoolModifier.Properties.class, InlinePoolModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/decorator_cascade"),
            VaultModifierType.of(DecoratorCascadeModifier.class, DecoratorCascadeModifier.Properties.class, DecoratorCascadeModifier::new)
         );
         this.put(VaultMod.id("modifier_type/grouped"), VaultModifierType.of(GroupedModifier.class, GroupedModifier.Properties.class, GroupedModifier::new));
         this.put(
            VaultMod.id("modifier_type/player_heal_cut"),
            VaultModifierType.of(PlayerHealCutModifier.class, EntityAttributeModifier.Properties.class, PlayerHealCutModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/no_soul_shards"),
            VaultModifierType.of(NoSoulShardsModifier.class, NoSoulShardsModifier.Properties.class, NoSoulShardsModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/spawner_mobs"),
            VaultModifierType.of(SpawnerMobsModifier.class, SpawnerMobsModifier.Properties.class, SpawnerMobsModifier::new)
         );
         this.put(VaultMod.id("modifier_type/hunter"), VaultModifierType.of(HunterModifier.class, HunterModifier.Properties.class, HunterModifier::new));
         this.put(
            VaultMod.id("modifier_type/objective_target"),
            VaultModifierType.of(ObjectiveTargetModifier.class, ObjectiveTargetModifier.Properties.class, ObjectiveTargetModifier::new)
         );
         this.put(
            VaultMod.id("modifier_type/template_processor"),
            VaultModifierType.of(TemplateProcessorModifier.class, TemplateProcessorModifier.Properties.class, TemplateProcessorModifier::new)
         );
      }
   };

   public static Optional<VaultModifierType<?, ?>> get(ResourceLocation id) {
      return Optional.ofNullable(MODIFIER_TYPE_REGISTRY.get(id));
   }

   public static <M extends VaultModifier<?>> Optional<ResourceLocation> getIdFor(Class<M> modifierClass) {
      return MODIFIER_TYPE_REGISTRY.entrySet().stream().filter(entry -> modifierClass == entry.getValue().modifierClass()).map(Entry::getKey).findFirst();
   }

   private VaultModifierTypeRegistry() {
   }
}
