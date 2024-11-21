package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.FloatLevelEntryList;
import iskallia.vault.config.entry.MultipleGearAttributeRollOutputEntry;
import iskallia.vault.config.entry.MultipleRollOutputEntry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.SidedHelper;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class VaultGearCraftingConfig extends Config {
   private static final Map<Integer, Integer> proficiencyCaps = new HashMap<>();
   @Expose
   private String defaultCraftedPool;
   @Expose
   private final FloatLevelEntryList<VaultGearCraftingConfig.ProficiencyStep> proficiencyPools = new FloatLevelEntryList<>();

   @Override
   public String getName() {
      return "gear%sgear_crafting".formatted(File.separator);
   }

   public VaultGearTypeConfig.RollType getDefaultCraftedPool() {
      return ModConfigs.VAULT_GEAR_TYPE_CONFIG.getRollPool(this.defaultCraftedPool).orElse(ModConfigs.VAULT_GEAR_TYPE_CONFIG.getDefaultRoll());
   }

   public Optional<VaultGearCraftingConfig.ProficiencyStep> getProficiencyStep(float proficiencyDegree) {
      return this.proficiencyPools.getForValue(proficiencyDegree);
   }

   public List<VaultGearCraftingConfig.ProficiencyStep> getProficiencySteps() {
      return Collections.unmodifiableList(this.proficiencyPools);
   }

   @Override
   protected void reset() {
      this.defaultCraftedPool = "Scrappy";
      this.proficiencyPools.clear();
      this.proficiencyPools
         .add(
            new VaultGearCraftingConfig.ProficiencyStep(
               "Tier 0",
               "Normal",
               0.0F,
               "Scrappy",
               65280,
               1.0F,
               List.of(new MultipleGearAttributeRollOutputEntry(MultipleRollOutputEntry.OutcomeBias.BEST, 1, "the_vault:base_durability")),
               1.0F,
               4,
               0.1F,
               new ResourceLocation("the_vault:base_soulbound")
            )
         );
   }

   @Override
   protected void onLoad(@Nullable Config oldConfigInstance) {
      super.onLoad(oldConfigInstance);
      rebuildProficiencyCaps();
   }

   public static void rebuildProficiencyCaps() {
      proficiencyCaps.clear();

      for (int level = 1; level <= 100; level++) {
         double absProficiencyCapAtLevel = 2.71E-8 * Math.pow(level, 6.49) + 25 * level;
         proficiencyCaps.put(level, (int)Math.round(absProficiencyCapAtLevel));
      }
   }

   public static int getProficiencyCap(int level) {
      return proficiencyCaps.getOrDefault(level, 1);
   }

   public static float calculateRelativeProficiency(int absoluteProficiency, Player player) {
      return calculateRelativeProficiency(absoluteProficiency, SidedHelper.getVaultLevel(player));
   }

   public static float calculateRelativeProficiency(int absoluteProficiency, int playerLevel) {
      int proficiencyCap = getProficiencyCap(playerLevel);
      return absoluteProficiency >= proficiencyCap ? 1.0F : Mth.clamp((float)absoluteProficiency / proficiencyCap, 0.0F, 1.0F);
   }

   public static class ProficiencyStep implements FloatLevelEntryList.FloatLevelEntry {
      @Expose
      private String proficiencyName;
      @Expose
      private String durabilityOutcomeName;
      @Expose
      private float minProficiency;
      @Expose
      private String pool;
      @Expose
      private int color;
      @Expose
      private float craftingPotentialMultiplier;
      @Expose
      private List<MultipleGearAttributeRollOutputEntry> gearRollOutcomeModifiers;
      @Expose
      private float greaterModifierChance;
      @Expose
      private int maximumRepairSlots;
      @Expose
      private float soulboundChance;
      @Expose
      private ResourceLocation soulboundModifierId;

      public ProficiencyStep(
         String proficiencyName,
         String durabilityOutcomeName,
         float minProficiency,
         String pool,
         int color,
         float craftingPotentialMultiplier,
         List<MultipleGearAttributeRollOutputEntry> gearRollOutcomeModifiers,
         float greaterModifierChance,
         int maximumRepairSlots,
         float soulboundChance,
         ResourceLocation soulboundModifierId
      ) {
         this.proficiencyName = proficiencyName;
         this.durabilityOutcomeName = durabilityOutcomeName;
         this.minProficiency = minProficiency;
         this.pool = pool;
         this.color = color;
         this.craftingPotentialMultiplier = craftingPotentialMultiplier;
         this.gearRollOutcomeModifiers = gearRollOutcomeModifiers;
         this.greaterModifierChance = greaterModifierChance;
         this.maximumRepairSlots = maximumRepairSlots;
         this.soulboundChance = soulboundChance;
         this.soulboundModifierId = soulboundModifierId;
      }

      public String getProficiencyName() {
         return this.proficiencyName;
      }

      public String getDurabilityOutcomeName() {
         return this.durabilityOutcomeName;
      }

      public float getMinProficiency() {
         return this.minProficiency;
      }

      public String getPool() {
         return this.pool;
      }

      public int getColor() {
         return this.color;
      }

      public float getCraftingPotentialMultiplier() {
         return this.craftingPotentialMultiplier;
      }

      public List<MultipleGearAttributeRollOutputEntry> getGearRollOutcomeModifiers() {
         return this.gearRollOutcomeModifiers;
      }

      public float getGreaterModifierChance() {
         return this.greaterModifierChance;
      }

      public int getMaximumRepairSlots() {
         return this.maximumRepairSlots;
      }

      public float getSoulboundChance() {
         return this.soulboundChance;
      }

      public ResourceLocation getSoulboundModifierId() {
         return this.soulboundModifierId;
      }

      @Override
      public float getMinValue() {
         return this.getMinProficiency();
      }
   }
}
