package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.logic.objective.raid.modifier.ArtifactFragmentModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.BlockPlacementModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.DamageTakenModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.FloatingItemModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.ModifierDoublingModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.MonsterAmountModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.MonsterDamageModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.MonsterHealthModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.MonsterLevelModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.MonsterSpeedModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.RaidModifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class FinalRaidModifierConfig extends Config {
   @Expose
   private List<DamageTakenModifier> DAMAGE_TAKEN_MODIFIERS = new ArrayList<>();
   @Expose
   private List<MonsterAmountModifier> MONSTER_AMOUNT_MODIFIERS = new ArrayList<>();
   @Expose
   private List<MonsterDamageModifier> MONSTER_DAMAGE_MODIFIERS = new ArrayList<>();
   @Expose
   private List<MonsterHealthModifier> MONSTER_HEALTH_MODIFIERS = new ArrayList<>();
   @Expose
   private List<MonsterSpeedModifier> MONSTER_SPEED_MODIFIERS = new ArrayList<>();
   @Expose
   private List<MonsterLevelModifier> MONSTER_LEVEL_MODIFIERS = new ArrayList<>();
   @Expose
   private List<BlockPlacementModifier> BLOCK_PLACEMENT_MODIFIERS = new ArrayList<>();
   @Expose
   private List<FloatingItemModifier> ITEM_PLACEMENT_MODIFIERS = new ArrayList<>();
   @Expose
   private List<ArtifactFragmentModifier> ARTIFACT_FRAGMENT_MODIFIERS = new ArrayList<>();
   @Expose
   private List<ModifierDoublingModifier> MODIFIER_DOUBLING_MODIFIERS = new ArrayList<>();
   @Expose
   private List<FinalRaidModifierConfig.Level> levels = new ArrayList<>();

   public List<RaidModifier> getAll() {
      return Stream.of(
            this.DAMAGE_TAKEN_MODIFIERS,
            this.MONSTER_AMOUNT_MODIFIERS,
            this.MONSTER_DAMAGE_MODIFIERS,
            this.MONSTER_HEALTH_MODIFIERS,
            this.MONSTER_LEVEL_MODIFIERS,
            this.BLOCK_PLACEMENT_MODIFIERS,
            this.MONSTER_SPEED_MODIFIERS,
            this.ITEM_PLACEMENT_MODIFIERS,
            this.ARTIFACT_FRAGMENT_MODIFIERS,
            this.MODIFIER_DOUBLING_MODIFIERS
         )
         .flatMap(Collection::stream)
         .collect(Collectors.toList());
   }

   @Nullable
   public RaidModifier getByName(String name) {
      return this.getAll().stream().filter(modifier -> modifier.getName().equals(name)).findFirst().orElse(null);
   }

   public Optional<FinalRaidModifierConfig.RollableModifier> getRandomModifier(int level, boolean preventArtifacts) {
      return this.getForLevel(this.levels, level).map(modifierLevel -> {
         WeightedList<FinalRaidModifierConfig.RollableModifier> modifierList = modifierLevel.modifiers;
         if (preventArtifacts) {
            modifierList = modifierList.copyFiltered(modifierName -> {
               RaidModifier modifier = this.getByName(modifierName.modifier);
               return !(modifier instanceof ArtifactFragmentModifier);
            });
         }

         return modifierList.getRandom(rand);
      });
   }

   @Override
   public String getName() {
      return "final_raid_modifiers";
   }

   @Override
   protected void reset() {
      this.DAMAGE_TAKEN_MODIFIERS = new ArrayList<>();
      this.DAMAGE_TAKEN_MODIFIERS.add(new DamageTakenModifier("damageTaken"));
      this.MONSTER_AMOUNT_MODIFIERS = new ArrayList<>();
      this.MONSTER_AMOUNT_MODIFIERS.add(new MonsterAmountModifier("monsterAmount"));
      this.MONSTER_DAMAGE_MODIFIERS = new ArrayList<>();
      this.MONSTER_DAMAGE_MODIFIERS.add(new MonsterDamageModifier("monsterDamage"));
      this.MONSTER_HEALTH_MODIFIERS = new ArrayList<>();
      this.MONSTER_HEALTH_MODIFIERS.add(new MonsterHealthModifier("monsterHealth"));
      this.MONSTER_SPEED_MODIFIERS = new ArrayList<>();
      this.MONSTER_SPEED_MODIFIERS.add(new MonsterSpeedModifier("monsterSpeed"));
      this.MONSTER_LEVEL_MODIFIERS = new ArrayList<>();
      this.MONSTER_LEVEL_MODIFIERS.add(new MonsterLevelModifier("monsterLevel"));
      this.BLOCK_PLACEMENT_MODIFIERS = new ArrayList<>();
      this.BLOCK_PLACEMENT_MODIFIERS.add(new BlockPlacementModifier("gildedChests", ModBlocks.VAULT_BONUS_CHEST, 5, "Gilded Chests"));
      this.ITEM_PLACEMENT_MODIFIERS = new ArrayList<>();
      this.ITEM_PLACEMENT_MODIFIERS.add(new FloatingItemModifier("vaultGems", 10, FloatingItemModifier.defaultGemList(), "Vault Gems"));
      this.ARTIFACT_FRAGMENT_MODIFIERS = new ArrayList<>();
      this.ARTIFACT_FRAGMENT_MODIFIERS.add(new ArtifactFragmentModifier("artifactFragment"));
      this.MODIFIER_DOUBLING_MODIFIERS = new ArrayList<>();
      this.MODIFIER_DOUBLING_MODIFIERS.add(new ModifierDoublingModifier("modifierDoubling"));
      this.levels = new ArrayList<>();
      this.levels
         .add(
            new FinalRaidModifierConfig.Level(
               0,
               new WeightedList<FinalRaidModifierConfig.RollableModifier>()
                  .add(new FinalRaidModifierConfig.RollableModifier("gildedChests", 1.0F, 1.0F), 1)
                  .add(new FinalRaidModifierConfig.RollableModifier("vaultGems", 1.0F, 1.0F), 1)
                  .add(new FinalRaidModifierConfig.RollableModifier("artifactFragment", 0.01F, 0.05F), 1)
            )
         );
   }

   private Optional<FinalRaidModifierConfig.Level> getForLevel(List<FinalRaidModifierConfig.Level> levels, int level) {
      for (int i = 0; i < levels.size(); i++) {
         if (level < levels.get(i).level) {
            if (i != 0) {
               return Optional.of(levels.get(i - 1));
            }
            break;
         }

         if (i == levels.size() - 1) {
            return Optional.of(levels.get(i));
         }
      }

      return Optional.empty();
   }

   public static class Level {
      @Expose
      private final int level;
      @Expose
      private final WeightedList<FinalRaidModifierConfig.RollableModifier> modifiers;

      public Level(int level, WeightedList<FinalRaidModifierConfig.RollableModifier> modifiers) {
         this.level = level;
         this.modifiers = modifiers;
      }
   }

   public static class RollableModifier {
      @Expose
      private String modifier;
      @Expose
      private float minValue;
      @Expose
      private float maxValue;

      public RollableModifier(String modifier, float minValue, float maxValue) {
         this.modifier = modifier;
         this.minValue = minValue;
         this.maxValue = maxValue;
      }

      @Nullable
      public RaidModifier getModifier() {
         return ModConfigs.RAID_MODIFIER_CONFIG.getByName(this.modifier);
      }

      public float getRandomValue() {
         RaidModifier modifier = this.getModifier();
         if (modifier == null) {
            return 0.0F;
         } else {
            float value = MathUtilities.randomFloat(this.minValue, this.maxValue);
            return modifier.isPercentage() ? Math.round(value * 100.0F) / 100.0F : Math.round(value);
         }
      }
   }
}
