package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.roll.IntRoll;
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
import iskallia.vault.item.crystal.time.CrystalTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
   public Map<ResourceLocation, LevelEntryList<VaultCrystalConfig.TimeEntry>> TIMES;
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

   public Optional<CrystalTime> getRandomTime(ResourceLocation id, int level, RandomSource random) {
      return this.TIMES.getOrDefault(id, LevelEntryList.empty()).getForLevel(level).flatMap(entry -> entry.pool.getRandom(random));
   }

   public boolean applySeal(ItemStack input, ItemStack seal, ItemStack output, CrystalData crystal) {
      return !this.SEALS.containsKey(seal.getItem().getRegistryName())
         ? false
         : this.SEALS.get(seal.getItem().getRegistryName()).getForLevel(crystal.getLevel()).map(entry -> {
            if (!entry.input.contains(input.getItem().getRegistryName())) {
               return false;
            } else if (crystal.isUnmodifiable()) {
               return false;
            } else {
               crystal.setObjective(entry.objective);
               if (entry.layout != null) {
                  crystal.setLayout(entry.layout);
               }

               if (entry.theme != null) {
                  crystal.setTheme(entry.theme);
               }

               if (entry.time != null) {
                  crystal.setTime(entry.time);
               }

               if (entry.modifiers != null) {
                  crystal.setModifiers(entry.modifiers);
               }

               if (entry.randomModifiers != null) {
                  crystal.getModifiers().setRandomModifiers(entry.randomModifiers);
               }

               if (entry.exhausted != null) {
                  crystal.setUnmodifiable(entry.exhausted);
               }

               crystal.write(output);
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
                  .add(new BossCrystalObjective(IntRoll.ofUniform(3, 6), IntRoll.ofUniform(3, 6), 0.1F), 1)
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
            new BossCrystalObjective(IntRoll.ofUniform(3, 6), IntRoll.ofUniform(3, 6), 0.1F),
            new ClassicInfiniteCrystalLayout(1),
            null,
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
            new CakeCrystalObjective(IntRoll.ofUniform(10, 15)),
            new ClassicSpiralCrystalLayout(1, 99, Rotation.CLOCKWISE_90),
            null,
            null,
            null,
            null,
            null
         )
      );
   }

   private static class LayoutEntry implements LevelEntryList.ILevelEntry {
      @Expose
      public int level;
      @Expose
      public WeightedList<CrystalLayout> pool;

      public LayoutEntry(int level, WeightedList<CrystalLayout> pool) {
         this.level = level;
         this.pool = pool;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }

   public static class ModifierStability {
      @Expose
      public float instabilityPerCraft = 0.1F;
      @Expose
      public float exhaustProbability = 0.25F;
      @Expose
      public TextColor curseColor = TextColor.parseColor("#9C6E3B");
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
      public int level;
      @Expose
      public WeightedList<CrystalObjective> pool;

      public ObjectiveEntry(int level, WeightedList<CrystalObjective> pool) {
         this.level = level;
         this.pool = pool;
      }

      @Override
      public int getLevel() {
         return this.level;
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
      private final CrystalTime time;
      @Expose
      private final CrystalModifiers modifiers;
      @Expose
      private final Boolean randomModifiers;
      @Expose
      private final Boolean exhausted;

      public SealEntry(
         int level,
         List<ResourceLocation> input,
         CrystalObjective objective,
         CrystalLayout layout,
         CrystalTheme theme,
         CrystalTime time,
         CrystalModifiers modifiers,
         Boolean randomModifiers,
         Boolean exhausted
      ) {
         this.level = level;
         this.input = input;
         this.objective = objective;
         this.layout = layout;
         this.theme = theme;
         this.time = time;
         this.modifiers = modifiers;
         this.randomModifiers = randomModifiers;
         this.exhausted = exhausted;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }

   private static class ThemeEntry implements LevelEntryList.ILevelEntry {
      @Expose
      public int level;
      @Expose
      public WeightedList<ResourceLocation> pool;

      public ThemeEntry(int level, WeightedList<ResourceLocation> pool) {
         this.level = level;
         this.pool = pool;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }

   private static class TimeEntry implements LevelEntryList.ILevelEntry {
      @Expose
      public int level;
      @Expose
      public WeightedList<CrystalTime> pool;

      public TimeEntry(int level, WeightedList<CrystalTime> pool) {
         this.level = level;
         this.pool = pool;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }
}
