package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.VaultMod;
import iskallia.vault.util.data.WeightedList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class VaultCrystalCatalystConfig extends Config {
   public static final String MODIFIER_POOL_GOOD = "GOOD";
   public static final String MODIFIER_POOL_BAD = "BAD";
   public static final String MODIFIER_POOL_CURSE = "CURSE";
   @Expose
   @SerializedName("MODIFIER_POOLS")
   private final Map<String, VaultCrystalCatalystConfig.ModifierPool> MODIFIER_POOLS = new HashMap<>();
   @Expose
   @SerializedName("MODIFIER_POOL_GROUPS")
   private final WeightedList<VaultCrystalCatalystConfig.ModifierPoolGroup> MODIFIER_POOL_GROUPS = new WeightedList<>();
   private final Map<String, Set<ResourceLocation>> modifierLookupMap = new HashMap<>();

   @Override
   public String getName() {
      return "vault_crystal_catalyst_modifiers";
   }

   @Nullable
   public VaultCrystalCatalystConfig.ModifierPoolGroup getRandomModifierPoolGroup(Random random) {
      return this.MODIFIER_POOL_GROUPS.getRandom(random);
   }

   @Nullable
   public VaultCrystalCatalystConfig.ModifierPool getModifierPoolById(String modifierPoolId) {
      return this.MODIFIER_POOLS.get(modifierPoolId);
   }

   public boolean isUnlisted(ResourceLocation resourceLocation) {
      return this.modifierLookupMap.values().stream().noneMatch(set -> set.contains(resourceLocation));
   }

   public boolean isGood(ResourceLocation resourceLocation) {
      return this.modifierPoolHas("GOOD", resourceLocation);
   }

   public boolean isBad(ResourceLocation resourceLocation) {
      return this.modifierPoolHas("BAD", resourceLocation);
   }

   public boolean isCurse(ResourceLocation resourceLocation) {
      return this.modifierPoolHas("CURSE", resourceLocation);
   }

   public boolean modifierPoolHas(String modifierPoolId, ResourceLocation resourceLocation) {
      return this.modifierLookupMap.getOrDefault(modifierPoolId, Collections.emptySet()).contains(resourceLocation);
   }

   @Override
   public <T extends Config> T readConfig() {
      VaultCrystalCatalystConfig config = super.readConfig();
      config.modifierLookupMap.clear();
      config.MODIFIER_POOLS.forEach((key, value) -> {
         Set<ResourceLocation> set = config.modifierLookupMap.computeIfAbsent(key, s -> new HashSet<>());
         value.modifierIdWeightedList.forEach((resourceLocation, number) -> set.add(resourceLocation));
      });
      return (T)config;
   }

   @Override
   protected void reset() {
      this.MODIFIER_POOLS.clear();
      this.MODIFIER_POOL_GROUPS.clear();
      this.MODIFIER_POOLS
         .put(
            "GOOD",
            new VaultCrystalCatalystConfig.ModifierPool(
               new WeightedList<ResourceLocation>()
                  .add(VaultMod.id("item_rarity"), 1)
                  .add(VaultMod.id("item_quantity"), 1)
                  .add(VaultMod.id("soul_hunter"), 1)
                  .add(VaultMod.id("prismatic"), 1)
                  .add(VaultMod.id("plentiful"), 1)
                  .add(VaultMod.id("speedy"), 1)
                  .add(VaultMod.id("stronk"), 1)
                  .add(VaultMod.id("extended"), 1)
                  .add(VaultMod.id("personal_space"), 1)
                  .add(VaultMod.id("easy"), 1)
                  .add(VaultMod.id("reinforced"), 1)
            )
         );
      this.MODIFIER_POOLS
         .put(
            "BAD",
            new VaultCrystalCatalystConfig.ModifierPool(
               new WeightedList<ResourceLocation>()
                  .add(VaultMod.id("draining"), 1)
                  .add(VaultMod.id("limited"), 1)
                  .add(VaultMod.id("clumsy"), 1)
                  .add(VaultMod.id("vulnerable"), 1)
                  .add(VaultMod.id("inert"), 1)
                  .add(VaultMod.id("poisonous"), 1)
                  .add(VaultMod.id("withering"), 1)
                  .add(VaultMod.id("fatiguing"), 1)
                  .add(VaultMod.id("freezing"), 1)
                  .add(VaultMod.id("hunger"), 1)
                  .add(VaultMod.id("tired"), 1)
                  .add(VaultMod.id("slowed"), 1)
                  .add(VaultMod.id("weakened"), 1)
                  .add(VaultMod.id("chunky_mobs"), 1)
                  .add(VaultMod.id("furious_mobs"), 1)
                  .add(VaultMod.id("speedy_mobs"), 1)
                  .add(VaultMod.id("trapped"), 1)
                  .add(VaultMod.id("rushed"), 1)
                  .add(VaultMod.id("crowded"), 1)
                  .add(VaultMod.id("difficult"), 1)
                  .add(VaultMod.id("frail"), 1)
            )
         );
      this.MODIFIER_POOLS
         .put(
            "CURSE",
            new VaultCrystalCatalystConfig.ModifierPool(
               new WeightedList<ResourceLocation>()
                  .add(VaultMod.id("hunger"), 10)
                  .add(VaultMod.id("tired"), 3)
                  .add(VaultMod.id("slowed"), 6)
                  .add(VaultMod.id("weakened"), 1)
                  .add(VaultMod.id("jupiter_gravity"), 1)
                  .add(VaultMod.id("crab_walk"), 1)
                  .add(VaultMod.id("confused"), 1)
            )
         );
      this.MODIFIER_POOL_GROUPS.add(new VaultCrystalCatalystConfig.ModifierPoolGroup(List.of("GOOD")), 1);
      this.MODIFIER_POOL_GROUPS.add(new VaultCrystalCatalystConfig.ModifierPoolGroup(List.of("GOOD", "BAD")), 1);
   }

   public static class ModifierPool {
      @Expose
      @SerializedName("modifiers")
      private final WeightedList<ResourceLocation> modifierIdWeightedList;

      public ModifierPool(WeightedList<ResourceLocation> modifierIdWeightedList) {
         this.modifierIdWeightedList = modifierIdWeightedList;
      }

      @Nullable
      public ResourceLocation getRandomModifier(Random random) {
         return this.getRandomModifier(random, modifierId -> false);
      }

      @Nullable
      public ResourceLocation getRandomModifier(Random random, Predicate<ResourceLocation> modifierFilter) {
         WeightedList<ResourceLocation> filteredModifiers = this.modifierIdWeightedList.copy();
         filteredModifiers.removeIf(entry -> modifierFilter.test(entry.value));
         return filteredModifiers.getRandom(random);
      }
   }

   public static class ModifierPoolGroup {
      @Expose
      @SerializedName("pools")
      private final List<String> modifierPoolIdList;

      public ModifierPoolGroup(List<String> modifierPoolIdList) {
         this.modifierPoolIdList = modifierPoolIdList;
      }

      public List<String> getModifierPoolIdList() {
         return this.modifierPoolIdList;
      }
   }
}
