package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.loot.LootRoll;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.CrystalModifiers;
import iskallia.vault.item.crystal.layout.ClassicCircleCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicInfiniteCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicPolygonCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicSpiralCrystalLayout;
import iskallia.vault.item.crystal.layout.CrystalLayout;
import iskallia.vault.item.crystal.objective.BossCrystalObjective;
import iskallia.vault.item.crystal.objective.CakeCrystalObjective;
import iskallia.vault.item.crystal.objective.CrystalObjective;
import iskallia.vault.item.crystal.objective.ScavengerCrystalObjective;
import iskallia.vault.item.crystal.theme.CrystalTheme;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Rotation;

public class VaultCrystalConfig extends Config {
   @Expose
   public VaultCrystalConfig.ModifierStability MODIFIER_STABILITY;
   @Expose
   public VaultCrystalConfig.Motes MOTES;
   @Expose
   public Map<ResourceLocation, LevelEntryList<VaultCrystalConfig.ThemeEntry>> THEMES;
   @Expose
   public LevelEntryList<VaultCrystalConfig.LayoutEntry> LAYOUTS;
   @Expose
   public LevelEntryList<VaultCrystalConfig.ObjectiveEntry> OBJECTIVES;
   @Expose
   private Map<ResourceLocation, LevelEntryList<VaultCrystalConfig.SealEntry>> SEALS;

   @Override
   public String getName() {
      return "vault_crystal";
   }

   public Optional<ResourceLocation> getRandomTheme(ResourceLocation id, int level, RandomSource random) {
      return this.THEMES.getOrDefault(id, LevelEntryList.empty()).getForLevel(level).flatMap(entry -> entry.pool.getRandom(random));
   }

   public Optional<CrystalLayout> getRandomLayout(int level, RandomSource random) {
      return this.LAYOUTS.getForLevel(level).flatMap(entry -> entry.pool.getRandom(random));
   }

   public Optional<CrystalObjective> getRandomObjective(int level, RandomSource random) {
      return this.OBJECTIVES.getForLevel(level).flatMap(entry -> entry.pool.getRandom(random));
   }

   public boolean applySeal(Item seal, Item input, CrystalData crystal) {
      return !this.SEALS.containsKey(seal.getRegistryName()) ? false : this.SEALS.get(seal.getRegistryName()).getForLevel(crystal.getLevel()).map(entry -> {
         if (!entry.input.contains(input.getRegistryName())) {
            return false;
         } else {
            crystal.setObjective(entry.objective);
            if (entry.layout != null) {
               crystal.setLayout(entry.layout);
            }

            if (entry.theme != null) {
               crystal.setTheme(entry.theme);
            }

            if (entry.modifiers != null) {
               crystal.setModifiers(entry.modifiers);
            }

            if (entry.preventsRandomModifiers != null) {
               crystal.setPreventsRandomModifiers(entry.preventsRandomModifiers);
            }

            if (entry.canBeModified != null) {
               crystal.setModifiable(entry.canBeModified);
            }

            return true;
         }
      }).orElse(false);
   }

   @Override
   protected void reset() {
      this.MODIFIER_STABILITY = new VaultCrystalConfig.ModifierStability();
      this.MOTES = new VaultCrystalConfig.Motes();
      this.THEMES = new LinkedHashMap<>();
      LevelEntryList<VaultCrystalConfig.ThemeEntry> defaultTheme = new LevelEntryList<>();
      defaultTheme.add(
         new VaultCrystalConfig.ThemeEntry(
            0, new WeightedList<ResourceLocation>().add(VaultMod.id("classic_vault_ice"), 1).add(VaultMod.id("classic_vault_cave"), 1)
         )
      );
      this.THEMES.put(VaultMod.id("default"), defaultTheme);
      new LevelEntryList();
      defaultTheme.add(
         new VaultCrystalConfig.ThemeEntry(0, new WeightedList<ResourceLocation>().add(VaultMod.id("diy_vault_ice"), 1).add(VaultMod.id("diy_vault_cave"), 1))
      );
      this.THEMES.put(VaultMod.id("diy"), defaultTheme);
      this.LAYOUTS = new LevelEntryList<>();
      this.LAYOUTS
         .add(
            new VaultCrystalConfig.LayoutEntry(
               0,
               new WeightedList<CrystalLayout>()
                  .add(new ClassicCircleCrystalLayout(1, 4), 1)
                  .add(new ClassicSpiralCrystalLayout(1, 4, Rotation.CLOCKWISE_90), 1)
                  .add(new ClassicInfiniteCrystalLayout(0), 1)
                  .add(new ClassicPolygonCrystalLayout(1, new int[]{-4, 4, 4, 4, 4, -4, -4, -4}), 1)
                  .add(new ClassicPolygonCrystalLayout(1, new int[]{-4, 0, 0, 4, 4, 0, 0, -4}), 1)
            )
         );
      this.OBJECTIVES = new LevelEntryList<>();
      this.OBJECTIVES
         .add(
            new VaultCrystalConfig.ObjectiveEntry(
               0,
               new WeightedList<CrystalObjective>()
                  .add(new BossCrystalObjective(LootRoll.ofUniform(3, 6), LootRoll.ofUniform(3, 6), 0.1F), 1)
                  .add(new ScavengerCrystalObjective(0.1F), 1)
            )
         );
      this.SEALS = new LinkedHashMap<>();
      LevelEntryList<VaultCrystalConfig.SealEntry> list = new LevelEntryList<>();
      this.SEALS.put(ModItems.CRYSTAL_SEAL_EXECUTIONER.getRegistryName(), list);
      list.add(
         new VaultCrystalConfig.SealEntry(
            0,
            Arrays.asList(ModItems.VAULT_CRYSTAL.getRegistryName()),
            new BossCrystalObjective(LootRoll.ofUniform(3, 6), LootRoll.ofUniform(3, 6), 0.1F),
            new ClassicInfiniteCrystalLayout(1),
            null,
            null,
            null,
            null
         )
      );
      list = new LevelEntryList<>();
      this.SEALS.put(ModItems.CRYSTAL_SEAL_HUNTER.getRegistryName(), list);
      list.add(
         new VaultCrystalConfig.SealEntry(
            0,
            Arrays.asList(ModItems.VAULT_CRYSTAL.getRegistryName()),
            new ScavengerCrystalObjective(0.1F),
            new ClassicCircleCrystalLayout(1, 5),
            null,
            null,
            null,
            null
         )
      );
      list = new LevelEntryList<>();
      this.SEALS.put(ModItems.CRYSTAL_SEAL_ANCIENTS.getRegistryName(), list);
      list.add(
         new VaultCrystalConfig.SealEntry(
            0,
            Arrays.asList(ModItems.VAULT_CRYSTAL.getRegistryName()),
            new CakeCrystalObjective(LootRoll.ofUniform(10, 15)),
            new ClassicSpiralCrystalLayout(1, 99, Rotation.CLOCKWISE_90),
            null,
            null,
            null,
            null
         )
      );
   }

   private static class LayoutEntry implements LevelEntryList.ILevelEntry {
      @Expose
      public int minLevel;
      @Expose
      public WeightedList<CrystalLayout> pool;

      public LayoutEntry(int minLevel, WeightedList<CrystalLayout> pool) {
         this.minLevel = minLevel;
         this.pool = pool;
      }

      @Override
      public int getLevel() {
         return this.minLevel;
      }
   }

   public static class ModifierStability {
      @Expose
      public int craftsBeforeInstability = 5;
      @Expose
      public float instabilityPerCraft = 0.1F;
      @Expose
      public float instabilityCap = 0.9F;
      @Expose
      public float curseInstabilityThreshold = 0.5F;
      @Expose
      public float curseChanceMin = 0.25F;
      @Expose
      public float curseChanceMax = 0.5F;
      @Expose
      public TextColor curseColor = TextColor.parseColor("#9C6E3B");

      public float calculateCurseChance(int instability) {
         float instabilityPercentage = instability / 100.0F;
         if (instabilityPercentage < this.curseInstabilityThreshold) {
            return 0.0F;
         } else {
            float t = (instabilityPercentage - this.curseInstabilityThreshold) / (1.0F - this.curseInstabilityThreshold);
            return this.curseChanceMin + (this.curseChanceMax - this.curseChanceMin) * t;
         }
      }
   }

   public static class Motes {
      @Expose
      public int clarityLevelCost = 1;
      @Expose
      public int purityLevelCost = 2;
      @Expose
      public int sanctityLevelCost = 4;
   }

   private static class ObjectiveEntry implements LevelEntryList.ILevelEntry {
      @Expose
      public int minLevel;
      @Expose
      public WeightedList<CrystalObjective> pool;

      public ObjectiveEntry(int minLevel, WeightedList<CrystalObjective> pool) {
         this.minLevel = minLevel;
         this.pool = pool;
      }

      @Override
      public int getLevel() {
         return this.minLevel;
      }
   }

   public static class SealEntry implements LevelEntryList.ILevelEntry {
      @Expose
      private final int level;
      @Expose
      private final List<ResourceLocation> input;
      @Expose
      private final CrystalObjective objective;
      @Expose
      private final CrystalLayout layout;
      @Expose
      private final CrystalTheme theme;
      @Expose
      private final CrystalModifiers modifiers;
      @Expose
      private final Boolean preventsRandomModifiers;
      @Expose
      private final Boolean canBeModified;

      public SealEntry(
         int level,
         List<ResourceLocation> input,
         CrystalObjective objective,
         CrystalLayout layout,
         CrystalTheme theme,
         CrystalModifiers modifiers,
         Boolean preventsRandomModifiers,
         Boolean canBeModified
      ) {
         this.level = level;
         this.input = input;
         this.objective = objective;
         this.layout = layout;
         this.theme = theme;
         this.modifiers = modifiers;
         this.preventsRandomModifiers = preventsRandomModifiers;
         this.canBeModified = canBeModified;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }

   private static class ThemeEntry implements LevelEntryList.ILevelEntry {
      @Expose
      public int minLevel;
      @Expose
      public WeightedList<ResourceLocation> pool;

      public ThemeEntry(int minLevel, WeightedList<ResourceLocation> pool) {
         this.minLevel = minLevel;
         this.pool = pool;
      }

      @Override
      public int getLevel() {
         return this.minLevel;
      }
   }
}
