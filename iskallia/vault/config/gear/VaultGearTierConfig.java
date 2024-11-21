package iskallia.vault.config.gear;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.BooleanFlagGenerator;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.config.DoubleAttributeGenerator;
import iskallia.vault.gear.attribute.config.IntegerAttributeGenerator;
import iskallia.vault.gear.attribute.custom.effect.EffectAvoidanceGearAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectCloudAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectGearAttribute;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import java.awt.Color;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.ObjectUtils;

public class VaultGearTierConfig extends Config {
   public static final String NO_LEGENDARY_TAG = "noLegendary";
   public static final String RESILIENT_FOCUS_REROLL = "resilientFocusTarget";
   public static final ResourceLocation UNIQUE_ITEM = VaultMod.id("unique");
   private ResourceLocation key;
   @Expose
   private final Map<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.AttributeGroup> modifierGroup = new LinkedHashMap<>();

   public static Map<ResourceLocation, VaultGearTierConfig> registerConfigs() {
      Map<ResourceLocation, VaultGearTierConfig> gearConfig = new HashMap<>();
      Stream.of(
            ModItems.HELMET,
            ModItems.CHESTPLATE,
            ModItems.LEGGINGS,
            ModItems.BOOTS,
            ModItems.SWORD,
            ModItems.AXE,
            ModItems.SHIELD,
            ModItems.IDOL_BENEVOLENT,
            ModItems.IDOL_MALEVOLENCE,
            ModItems.IDOL_OMNISCIENT,
            ModItems.IDOL_TIMEKEEPER,
            ModItems.JEWEL,
            ModItems.MAGNET,
            ModItems.WAND,
            ModItems.FOCUS
         )
         .map(rec$ -> ((ForgeRegistryEntry)rec$).getRegistryName())
         .forEach(key -> gearConfig.put(key, (VaultGearTierConfig)new VaultGearTierConfig(key).readConfig()));
      gearConfig.put(UNIQUE_ITEM, new VaultGearTierConfig(UNIQUE_ITEM).readConfig());
      return gearConfig;
   }

   public static Optional<VaultGearTierConfig> getConfig(ItemStack stack) {
      return !stack.isEmpty() && GearDataCache.of(stack).getRarity() == VaultGearRarity.UNIQUE
         ? getConfig(UNIQUE_ITEM)
         : getConfig(stack.getItem().getRegistryName());
   }

   static Optional<VaultGearTierConfig> getConfig(ResourceLocation key) {
      return Optional.ofNullable(ModConfigs.VAULT_GEAR_CONFIG.get(key));
   }

   public VaultGearTierConfig(ResourceLocation key) {
      this.key = key;
   }

   @Override
   public String getName() {
      return "gear_modifiers%s%s".formatted(File.separator, this.key.getPath());
   }

   @Nullable
   public Object getTierConfig(VaultGearModifier<?> modifier) {
      for (VaultGearTierConfig.ModifierTier<?> modTier : this.getAllTiers(modifier.getModifierIdentifier())) {
         if (modTier.getModifierTier() == modifier.getRolledTier()) {
            return modTier.getModifierConfiguration();
         }
      }

      return null;
   }

   @Nullable
   @Deprecated(
      forRemoval = true
   )
   public VaultGearTierConfig.ModifierAffixTagGroup getAffixGroup(VaultGearTierConfig.ModifierTierGroup group) {
      return group.getTargetAffixTagGroup();
   }

   public List<Tuple<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.ModifierTierGroup>> getGenericGroupsWithModifierGroup(String group) {
      return this.getGenericGroupsFulfilling(tierGroup -> tierGroup.getModifierGroup().equals(group));
   }

   public List<Tuple<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.ModifierTierGroup>> getGenericGroupsWithModifierTag(String modTag) {
      return this.getGenericGroupsFulfilling(group -> group.getTags().contains(modTag));
   }

   public List<Tuple<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.ModifierTierGroup>> getGenericGroupsFulfilling(
      Predicate<VaultGearTierConfig.ModifierTierGroup> filter
   ) {
      List<Tuple<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.ModifierTierGroup>> configs = new ArrayList<>();

      for (Entry<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.AttributeGroup> entry : this.modifierGroup.entrySet()) {
         if (entry.getKey().isGenericGroup()) {
            for (VaultGearTierConfig.ModifierTierGroup group : entry.getValue()) {
               if (filter.test(group)) {
                  configs.add(new Tuple(entry.getKey(), group));
               }
            }
         }
      }

      return configs;
   }

   public List<Tuple<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.ModifierTierGroup>> getAnyGroupsFulfilling(
      Predicate<VaultGearTierConfig.ModifierTierGroup> filter
   ) {
      List<Tuple<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.ModifierTierGroup>> configs = new ArrayList<>();

      for (Entry<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.AttributeGroup> entry : this.modifierGroup.entrySet()) {
         for (VaultGearTierConfig.ModifierTierGroup group : entry.getValue()) {
            if (filter.test(group)) {
               configs.add(new Tuple(entry.getKey(), group));
            }
         }
      }

      return configs;
   }

   @Nonnull
   public VaultGearTierConfig.ModifierConfigRange getTierConfigRange(VaultGearModifier<?> modifier, int level) {
      Object currentTierCfg = null;
      VaultGearTierConfig.ModifierTier<?> min = null;
      VaultGearTierConfig.ModifierTier<?> max = null;
      if (modifier.hasAnyCategoryMatching(VaultGearModifier.AffixCategory::onlyDisplayCurrentTierInformation)) {
         VaultGearTierConfig.ModifierTierGroup tierGroup = this.getTierGroup(modifier.getModifierIdentifier());
         if (tierGroup != null) {
            VaultGearTierConfig.ModifierTier<?> tier = tierGroup.getModifierForTier(modifier.getRolledTier());
            if (tier != null) {
               Object configObject = tier.getModifierConfiguration();
               return new VaultGearTierConfig.ModifierConfigRange(configObject, Lists.newArrayList(new Object[]{configObject}), configObject, configObject);
            }
         }
      }

      List<Object> tiers = new ArrayList<>();

      for (VaultGearTierConfig.ModifierTier<?> modTier : this.getAllTiers(modifier.getModifierIdentifier())) {
         if (modTier.getMinLevel() <= level && (modTier.getMaxLevel() == -1 || modTier.getMaxLevel() >= level)) {
            tiers.add(modTier.getModifierConfiguration());
            if (modTier.getModifierTier() == modifier.getRolledTier()) {
               currentTierCfg = modTier.getModifierConfiguration();
            }

            if (min == null || modTier.getMinLevel() < min.getMinLevel()) {
               min = modTier;
            }

            if (max == null || modTier.getMinLevel() > max.getMinLevel()) {
               max = modTier;
            }
         }
      }

      Object minCfg = min == null ? null : min.getModifierConfiguration();
      Object maxCfg = max == null ? null : max.getModifierConfiguration();
      return new VaultGearTierConfig.ModifierConfigRange(currentTierCfg, tiers, minCfg, maxCfg);
   }

   public Optional<VaultGearTierConfig.ModifierOutcome<?>> getConfiguredModifierTier(
      VaultGearTierConfig.ModifierAffixTagGroup affixTagGroup, ResourceLocation identifier, int modifierTier
   ) {
      if (!this.modifierGroup.containsKey(affixTagGroup)) {
         return Optional.empty();
      } else {
         VaultGearTierConfig.ModifierTierGroup group = this.getTierGroup(identifier);
         if (group != null) {
            for (VaultGearTierConfig.ModifierTier<?> tier : group) {
               if (tier.getModifierTier() == modifierTier) {
                  return Optional.of(new VaultGearTierConfig.ModifierOutcome<>(tier, group));
               }
            }
         }

         return Optional.empty();
      }
   }

   public Optional<VaultGearModifier<?>> createModifier(
      VaultGearTierConfig.ModifierAffixTagGroup affixTagGroup, ResourceLocation identifier, int modifierTier, Random random
   ) {
      return this.getConfiguredModifierTier(affixTagGroup, identifier, modifierTier).map(outcome -> outcome.makeModifier(random));
   }

   public List<VaultGearTierConfig.ModifierTier<?>> getAllTiers(@Nullable ResourceLocation identifier) {
      VaultGearTierConfig.ModifierTierGroup group = this.getTierGroup(identifier);
      return group != null ? Collections.unmodifiableList(group) : Collections.emptyList();
   }

   @Nullable
   public VaultGearModifier<?> generateModifier(ResourceLocation identifier, int level, Random random) {
      VaultGearTierConfig.ModifierTierGroup group = this.getTierGroup(identifier);
      if (group != null) {
         WeightedList<VaultGearTierConfig.ModifierOutcome<?>> outcomes = new WeightedList<>();
         group.getModifiersForLevel(level)
            .forEach(tier -> outcomes.add(new VaultGearTierConfig.ModifierOutcome<>((VaultGearTierConfig.ModifierTier<?>)tier, group), tier.getWeight()));
         return outcomes.getRandom().map(modifierOutcome -> modifierOutcome.makeModifier(random)).orElse(null);
      } else {
         return null;
      }
   }

   @Nullable
   public VaultGearModifier<?> maxAndIncreaseTier(VaultGearModifier.AffixType type, VaultGearModifier<?> modifier, int level, int tierIncrease, Random random) {
      VaultGearTierConfig.AttributeGroup attributeGroup = this.modifierGroup.get(VaultGearTierConfig.ModifierAffixTagGroup.ofAffixType(type));
      if (attributeGroup != null && !attributeGroup.isEmpty()) {
         VaultGearTierConfig.ModifierTierGroup tierGroup = this.getTierGroup(modifier.getModifierIdentifier());
         if (tierGroup == null) {
            return null;
         } else {
            VaultGearTierConfig.ModifierTier<?> foundTier = tierGroup.getHighestForLevel(level);
            if (foundTier == null) {
               return null;
            } else {
               int index = Math.min(foundTier.getModifierTier() + tierIncrease, tierGroup.size() - 1);
               return tierGroup.get(index).makeModifier(tierGroup, random);
            }
         }
      } else {
         return null;
      }
   }

   @Nullable
   public VaultGearTierConfig.ModifierTierGroup getTierGroup(@Nullable ResourceLocation identifier) {
      for (VaultGearTierConfig.AttributeGroup attributePool : this.modifierGroup.values()) {
         for (VaultGearTierConfig.ModifierTierGroup group : attributePool) {
            if (group.identifier.equals(identifier)) {
               return group;
            }
         }
      }

      return null;
   }

   public List<VaultGearModifier<?>> generateBaseAttributes(int level, Random random) {
      return this.fullyGenerateAllModifiersOf(VaultGearTierConfig.ModifierAffixTagGroup.BASE_ATTRIBUTES, level, random);
   }

   public List<VaultGearModifier<?>> generateImplicits(int level, Random random) {
      return this.fullyGenerateAllModifiersOf(VaultGearTierConfig.ModifierAffixTagGroup.IMPLICIT, level, random);
   }

   private List<VaultGearModifier<?>> fullyGenerateAllModifiersOf(VaultGearTierConfig.ModifierAffixTagGroup affixTagGroup, int level, Random random) {
      VaultGearTierConfig.AttributeGroup attributePool = this.modifierGroup.get(affixTagGroup);
      if (attributePool != null && !attributePool.isEmpty()) {
         Map<String, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>> groupOutcomes = new HashMap<>();
         attributePool.forEach(
            group -> {
               String modGrp = Strings.isNullOrEmpty(group.getModifierGroup()) ? group.getIdentifier().toString() : group.getModifierGroup();
               group.getModifiersForLevel(level)
                  .forEach(
                     tier -> groupOutcomes.computeIfAbsent(modGrp, s -> new WeightedList<>())
                        .add(new VaultGearTierConfig.ModifierOutcome<>((VaultGearTierConfig.ModifierTier<?>)tier, group), tier.getWeight())
                  );
            }
         );
         List<VaultGearModifier<?>> modifiers = new ArrayList<>();
         groupOutcomes.values().forEach(outcomes -> outcomes.getRandom(random).map(outcome -> outcome.makeModifier(random)).ifPresent(modifiers::add));
         return modifiers;
      } else {
         return Collections.emptyList();
      }
   }

   public Optional<VaultGearModifier<?>> getRandomModifier(VaultGearModifier.AffixType type, int level, Random random, Set<String> excludedModGroups) {
      return this.getRandomModifier(VaultGearTierConfig.ModifierAffixTagGroup.ofAffixType(type), level, random, excludedModGroups);
   }

   public Optional<VaultGearModifier<?>> getRandomModifier(
      VaultGearTierConfig.ModifierAffixTagGroup affixTagGroup, int level, Random random, Set<String> excludedModGroups
   ) {
      VaultGearTierConfig.AttributeGroup attributePool = this.modifierGroup.get(affixTagGroup);
      if (attributePool != null && !attributePool.isEmpty()) {
         WeightedList<VaultGearTierConfig.ModifierOutcome<?>> outcomes = new WeightedList<>();
         attributePool.forEach(
            group -> {
               if (group.modifierGroup.isEmpty() || !excludedModGroups.contains(group.modifierGroup)) {
                  group.getModifiersForLevel(level)
                     .forEach(
                        tier -> outcomes.add(new VaultGearTierConfig.ModifierOutcome<>((VaultGearTierConfig.ModifierTier<?>)tier, group), tier.getWeight())
                     );
               }
            }
         );
         return outcomes.getRandom().map(modifierOutcome -> modifierOutcome.makeModifier(random));
      } else {
         return Optional.empty();
      }
   }

   @Override
   protected void reset() {
      this.modifierGroup.clear();
      VaultGearTierConfig.AttributeGroup implicits = new VaultGearTierConfig.AttributeGroup();
      this.modifierGroup.put(VaultGearTierConfig.ModifierAffixTagGroup.IMPLICIT, implicits);
      VaultGearTierConfig.ModifierTierGroup armorTierGroup = new VaultGearTierConfig.ModifierTierGroup(ModGearAttributes.ARMOR, "ModArmor", "armor");
      armorTierGroup.add(new VaultGearTierConfig.ModifierTier<>(0, 10, new IntegerAttributeGenerator.Range(1, 2, 1)));
      armorTierGroup.add(new VaultGearTierConfig.ModifierTier<>(15, 10, new IntegerAttributeGenerator.Range(1, 2, 1)));
      armorTierGroup.add(new VaultGearTierConfig.ModifierTier<>(25, 10, new IntegerAttributeGenerator.Range(1, 2, 1)));
      VaultGearTierConfig.ModifierTierGroup attackDamageTierGroup = new VaultGearTierConfig.ModifierTierGroup(
         ModGearAttributes.ATTACK_DAMAGE, "ModAttackDamage", "attackdamage"
      );
      attackDamageTierGroup.add(new VaultGearTierConfig.ModifierTier<>(0, 20, new DoubleAttributeGenerator.Range(1.0, 1.5)));
      attackDamageTierGroup.add(new VaultGearTierConfig.ModifierTier<>(10, 18, new DoubleAttributeGenerator.Range(1.6, 2.0)));
      attackDamageTierGroup.add(new VaultGearTierConfig.ModifierTier<>(20, 15, new DoubleAttributeGenerator.Range(2.1, 2.5)));
      VaultGearTierConfig.ModifierTierGroup effectAvoidTierGroup = new VaultGearTierConfig.ModifierTierGroup(
         ModGearAttributes.EFFECT_AVOIDANCE, "ModPoisonAvoidance", "poisonavoidance"
      );
      effectAvoidTierGroup.add(new VaultGearTierConfig.ModifierTier<>(0, 10, new EffectAvoidanceGearAttribute.Config(MobEffects.POISON, 0.1F, 0.2F)));
      effectAvoidTierGroup.add(new VaultGearTierConfig.ModifierTier<>(15, 10, new EffectAvoidanceGearAttribute.Config(MobEffects.POISON, 0.21F, 0.3F)));
      VaultGearTierConfig.AttributeGroup prefixes = new VaultGearTierConfig.AttributeGroup();
      prefixes.add(armorTierGroup);
      prefixes.add(attackDamageTierGroup);
      prefixes.add(effectAvoidTierGroup);
      this.modifierGroup.put(VaultGearTierConfig.ModifierAffixTagGroup.PREFIX, prefixes);
      EffectCloudAttribute.CloudConfig config = new EffectCloudAttribute.CloudConfig(
         "Healing", Potions.HEALING.getRegistryName(), 40, 4.0F, Color.RED.getRGB(), true, 0.05F
      );
      config.setAdditionalEffect(new EffectCloudAttribute.CloudEffectConfig(MobEffects.HEAL.getRegistryName(), 20, 0));
      VaultGearTierConfig.ModifierTierGroup effectCloudGroup = new VaultGearTierConfig.ModifierTierGroup(
         ModGearAttributes.EFFECT_CLOUD, "ModRegenCloud", "regencloud"
      );
      effectCloudGroup.add(new VaultGearTierConfig.ModifierTier<>(0, 10, config));
      VaultGearTierConfig.ModifierTierGroup effectGroup = new VaultGearTierConfig.ModifierTierGroup(ModGearAttributes.EFFECT, "ModFireEffect", "fireeffect");
      effectGroup.add(new VaultGearTierConfig.ModifierTier<>(15, 10, new EffectGearAttribute.Config(MobEffects.FIRE_RESISTANCE, 0)));
      VaultGearTierConfig.ModifierTierGroup fireImmunityGroup = new VaultGearTierConfig.ModifierTierGroup(
         ModGearAttributes.IS_FIRE_IMMUNE, "ModFireImmunity", "fireimmunity"
      );
      fireImmunityGroup.add(new VaultGearTierConfig.ModifierTier<>(0, 10, new BooleanFlagGenerator.BooleanFlag(true)));
      VaultGearTierConfig.ModifierTierGroup soulboundGroup = new VaultGearTierConfig.ModifierTierGroup(ModGearAttributes.SOULBOUND, "ModSoulbound", "soulbound");
      soulboundGroup.add(new VaultGearTierConfig.ModifierTier<>(0, 10, new BooleanFlagGenerator.BooleanFlag(true)));
      VaultGearTierConfig.AttributeGroup suffixes = new VaultGearTierConfig.AttributeGroup();
      suffixes.add(effectCloudGroup);
      suffixes.add(effectGroup);
      suffixes.add(fireImmunityGroup);
      suffixes.add(soulboundGroup);
      this.modifierGroup.put(VaultGearTierConfig.ModifierAffixTagGroup.SUFFIX, suffixes);
   }

   @Override
   protected boolean isValid() {
      Set<ResourceLocation> foundIdentifiers = new HashSet<>();

      for (VaultGearTierConfig.AttributeGroup group : this.modifierGroup.values()) {
         for (VaultGearTierConfig.ModifierTierGroup tierGroup : group) {
            if (!foundIdentifiers.add(tierGroup.identifier)) {
               throw new IllegalArgumentException(
                  "Invalid Gear configuration (%s) - duplicate identifier found: %s".formatted(this.getName(), tierGroup.identifier.toString())
               );
            }
         }
      }

      return true;
   }

   @Override
   protected void onLoad(@Nullable Config oldConfigInstance) {
      super.onLoad(oldConfigInstance);
      if (oldConfigInstance instanceof VaultGearTierConfig cfg) {
         this.key = cfg.key;
      }

      this.modifierGroup.forEach((group, attrGroup) -> attrGroup.forEach(modTierGroup -> modTierGroup.targetAffixTagGroup = group));
   }

   public static class AttributeGroup extends ArrayList<VaultGearTierConfig.ModifierTierGroup> {
      public static class Serializer implements JsonDeserializer<VaultGearTierConfig.AttributeGroup>, JsonSerializer<VaultGearTierConfig.AttributeGroup> {
         public VaultGearTierConfig.AttributeGroup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            VaultGearTierConfig.AttributeGroup attributeGroup = new VaultGearTierConfig.AttributeGroup();
            array.forEach(
               tierGroupElement -> {
                  JsonObject tierGroupObject = tierGroupElement.getAsJsonObject();
                  ResourceLocation attributeKey = new ResourceLocation(tierGroupObject.get("attribute").getAsString());
                  VaultGearAttribute<?> attribute = VaultGearAttributeRegistry.getAttribute(attributeKey);
                  if (attribute == null) {
                     throw new JsonParseException("Unknown Modifier %s".formatted(attributeKey));
                  } else {
                     ResourceLocation identifier = new ResourceLocation(tierGroupObject.get("identifier").getAsString());
                     VaultGearTierConfig.ModifierTierGroup group = new VaultGearTierConfig.ModifierTierGroup(
                        attributeKey, tierGroupObject.get("group").getAsString(), identifier
                     );
                     if (tierGroupObject.has("tags")) {
                        group.tags.addAll((Collection<? extends String>)context.deserialize(tierGroupObject.get("tags"), List.class));
                     }

                     JsonArray groupArray = tierGroupObject.getAsJsonArray("tiers");

                     for (int i = 0; i < groupArray.size(); i++) {
                        JsonObject tierObject = groupArray.get(i).getAsJsonObject();
                        int minLevel = tierObject.get("minLevel").getAsInt();
                        int maxLevel = tierObject.has("maxLevel") ? tierObject.get("maxLevel").getAsInt() : -1;
                        int weight = tierObject.get("weight").getAsInt();
                        VaultGearTierConfig.ModifierTier<?> tier = new VaultGearTierConfig.ModifierTier(minLevel, weight);
                        tier.modifierTier = i;
                        tier.maxLevel = maxLevel;
                        Class<?> configClass = attribute.getGenerator().getConfigurationObjectClass();
                        if (configClass != null) {
                           JsonObject configObject = tierObject.getAsJsonObject("value");
                           tier.modifierConfiguration = context.deserialize(configObject, configClass);
                           if (tier.modifierConfiguration instanceof ConfigurableAttributeGenerator.CustomTierConfig customCfg) {
                              customCfg.deserializeAdditional(configObject, context);
                           }
                        }

                        group.add(tier);
                     }

                     attributeGroup.add(group);
                  }
               }
            );
            return attributeGroup;
         }

         public JsonElement serialize(VaultGearTierConfig.AttributeGroup src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray array = new JsonArray();
            src.forEach(group -> {
               VaultGearAttribute<?> attribute = VaultGearAttributeRegistry.getAttribute(group.attribute);
               if (attribute != null) {
                  JsonObject groupObject = new JsonObject();
                  groupObject.addProperty("attribute", group.attribute.toString());
                  groupObject.addProperty("group", group.modifierGroup);
                  groupObject.addProperty("identifier", group.identifier.toString());
                  groupObject.add("tags", context.serialize(group.tags, List.class));
                  JsonArray groupArray = new JsonArray();
                  group.forEach(modifierTier -> {
                     JsonObject tierObject = new JsonObject();
                     tierObject.addProperty("minLevel", modifierTier.getMinLevel());
                     tierObject.addProperty("maxLevel", modifierTier.getMaxLevel());
                     tierObject.addProperty("weight", modifierTier.getWeight());
                     if (attribute.getGenerator().getConfigurationObjectClass() != null) {
                        Object configuration = modifierTier.getModifierConfiguration();
                        JsonObject configObject = context.serialize(configuration).getAsJsonObject();
                        if (configuration instanceof ConfigurableAttributeGenerator.CustomTierConfig customCfg) {
                           customCfg.serializeAdditional(configObject, context);
                        }

                        tierObject.add("value", configObject);
                     }

                     groupArray.add(tierObject);
                  });
                  groupObject.add("tiers", groupArray);
                  array.add(groupObject);
               }
            });
            return array;
         }
      }
   }

   public static enum ModifierAffixTagGroup {
      BASE_ATTRIBUTES(null, AttributeGearData::addAttribute),
      IMPLICIT(VaultGearModifier.AffixType.IMPLICIT, withTarget(VaultGearModifier.AffixType.IMPLICIT)),
      PREFIX(VaultGearModifier.AffixType.PREFIX, withTarget(VaultGearModifier.AffixType.PREFIX)),
      SUFFIX(VaultGearModifier.AffixType.SUFFIX, withTarget(VaultGearModifier.AffixType.SUFFIX)),
      ABILITY_ENHANCEMENT(VaultGearModifier.AffixType.IMPLICIT, withTarget(VaultGearModifier.AffixType.IMPLICIT)),
      CRAFTED_PREFIX(VaultGearModifier.AffixType.PREFIX, withTarget(VaultGearModifier.AffixType.PREFIX)),
      CRAFTED_SUFFIX(VaultGearModifier.AffixType.SUFFIX, withTarget(VaultGearModifier.AffixType.SUFFIX)),
      CORRUPTED_IMPLICIT(VaultGearModifier.AffixType.IMPLICIT, withTarget(VaultGearModifier.AffixType.IMPLICIT));

      @Nullable
      private final VaultGearModifier.AffixType targetAffixType;
      private final BiPredicate<VaultGearData, VaultGearModifier<?>> applyFn;

      private ModifierAffixTagGroup(@Nullable VaultGearModifier.AffixType targetAffixType, BiPredicate<VaultGearData, VaultGearModifier<?>> apply) {
         this.targetAffixType = targetAffixType;
         this.applyFn = apply;
      }

      private static BiPredicate<VaultGearData, VaultGearModifier<?>> withTarget(VaultGearModifier.AffixType type) {
         return (data, mod) -> data.addModifier(type, mod);
      }

      @Nullable
      public VaultGearModifier.AffixType getTargetAffixType() {
         return this.targetAffixType;
      }

      public boolean addModifier(VaultGearData data, VaultGearModifier<?> modifier) {
         return this.applyFn.test(data, modifier);
      }

      public boolean isGenericGroup() {
         return this == IMPLICIT || this == PREFIX || this == SUFFIX;
      }

      @Nonnull
      public static VaultGearTierConfig.ModifierAffixTagGroup ofAffixType(VaultGearModifier.AffixType type) {
         switch (type) {
            case IMPLICIT:
               return IMPLICIT;
            case PREFIX:
               return PREFIX;
            case SUFFIX:
               return SUFFIX;
            default:
               throw new IllegalArgumentException("Unknown AffixType: " + type.name());
         }
      }
   }

   public record ModifierConfigRange(
      @Nullable Object tierConfig, @Nullable List<Object> allTierConfigs, @Nullable Object minAvailableConfig, @Nullable Object maxAvailableConfig
   ) {
      public static VaultGearTierConfig.ModifierConfigRange empty() {
         return new VaultGearTierConfig.ModifierConfigRange(null, null, null, null);
      }
   }

   public record ModifierOutcome<C>(VaultGearTierConfig.ModifierTier<C> tier, VaultGearTierConfig.ModifierTierGroup tierGroup) {
      public <T> VaultGearModifier<T> makeModifier(Random random) {
         return this.tier().makeModifier(this.tierGroup(), random);
      }
   }

   public static class ModifierTier<T> {
      private final int minLevel;
      private int maxLevel = -1;
      private final int weight;
      private int modifierTier = -1;
      private T modifierConfiguration;

      public ModifierTier(int minLevel, int weight) {
         this(minLevel, weight, null);
      }

      public ModifierTier(int minLevel, int weight, T defaultConfig) {
         this.minLevel = minLevel;
         this.weight = weight;
         this.modifierConfiguration = defaultConfig;
      }

      public int getMinLevel() {
         return this.minLevel;
      }

      public int getMaxLevel() {
         return this.maxLevel;
      }

      public int getWeight() {
         return this.weight;
      }

      public int getModifierTier() {
         return this.modifierTier;
      }

      public T getModifierConfiguration() {
         return this.modifierConfiguration;
      }

      public <C> VaultGearModifier<C> makeModifier(VaultGearTierConfig.ModifierTierGroup tierGroup, Random random) {
         VaultGearAttribute<C> attribute = (VaultGearAttribute<C>)VaultGearAttributeRegistry.getAttribute(tierGroup.getAttribute());
         ConfigurableAttributeGenerator<C, T> generator = (ConfigurableAttributeGenerator<C, T>)attribute.getGenerator();
         C value = generator.generateRandomValue(this.getModifierConfiguration(), random);
         VaultGearModifier<C> modifier = new VaultGearModifier<>(attribute, value);
         modifier.setRolledTier(this.getModifierTier());
         modifier.setModifierGroup((String)ObjectUtils.firstNonNull(new String[]{tierGroup.modifierGroup, ""}));
         modifier.setModifierIdentifier(tierGroup.identifier);
         return modifier;
      }
   }

   public static class ModifierTierGroup extends ArrayList<VaultGearTierConfig.ModifierTier<?>> {
      private final ResourceLocation attribute;
      private final String modifierGroup;
      private final ResourceLocation identifier;
      private final List<String> tags = new ArrayList<>();
      private VaultGearTierConfig.ModifierAffixTagGroup targetAffixTagGroup = null;

      public ModifierTierGroup(VaultGearAttribute<?> attribute, String modifierGroup, String identifierStr) {
         this(attribute.getRegistryName(), modifierGroup, VaultMod.id(identifierStr));
      }

      public ModifierTierGroup(ResourceLocation attribute, String modifierGroup, ResourceLocation identifier) {
         this.attribute = attribute;
         this.modifierGroup = modifierGroup;
         this.identifier = identifier;
      }

      public List<VaultGearTierConfig.ModifierTier<?>> getModifiersForLevel(int level) {
         List<VaultGearTierConfig.ModifierTier<?>> list = new ArrayList<>();

         for (VaultGearTierConfig.ModifierTier<?> tier : this) {
            if (tier.getMinLevel() <= level && (tier.getMaxLevel() == -1 || level <= tier.getMaxLevel())) {
               list.add(tier);
            }
         }

         return list;
      }

      @Nullable
      public VaultGearTierConfig.ModifierTier<?> getHighestForLevel(int level) {
         VaultGearTierConfig.ModifierTier<?> highest = null;

         for (VaultGearTierConfig.ModifierTier<?> tier : this) {
            if (tier.getMinLevel() <= level && (highest == null || highest.getMinLevel() < tier.getMinLevel())) {
               highest = tier;
            }
         }

         return highest;
      }

      @Nullable
      public VaultGearTierConfig.ModifierTier<?> getModifierForTier(int tier) {
         for (VaultGearTierConfig.ModifierTier<?> modifierTier : this) {
            if (modifierTier.modifierTier == tier) {
               return modifierTier;
            }
         }

         return null;
      }

      public ResourceLocation getAttribute() {
         return this.attribute;
      }

      public String getModifierGroup() {
         return this.modifierGroup;
      }

      public List<String> getTags() {
         return this.tags;
      }

      public ResourceLocation getIdentifier() {
         return this.identifier;
      }

      public VaultGearTierConfig.ModifierAffixTagGroup getTargetAffixTagGroup() {
         return this.targetAffixTagGroup;
      }
   }
}
