package iskallia.vault.config.gear;

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
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.BooleanFlagGenerator;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.config.DoubleAttributeGenerator;
import iskallia.vault.gear.attribute.config.IntegerAttributeGenerator;
import iskallia.vault.gear.attribute.custom.EffectAvoidanceGearAttribute;
import iskallia.vault.gear.attribute.custom.EffectCloudAttribute;
import iskallia.vault.gear.attribute.custom.EffectGearAttribute;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import java.awt.Color;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potions;
import org.apache.commons.lang3.ObjectUtils;

public class VaultGearTierConfig extends Config {
   private Item gearItem;
   @Expose
   private final Map<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.AttributeGroup> modifierGroup = new LinkedHashMap<>();

   public static Map<Item, VaultGearTierConfig> registerConfigs() {
      Map<Item, VaultGearTierConfig> gearConfig = new HashMap<>();

      for (Item item : Arrays.asList(
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
         ModItems.MAGNET
      )) {
         gearConfig.put(item, new VaultGearTierConfig(item).readConfig());
      }

      return gearConfig;
   }

   public static Optional<VaultGearTierConfig> getConfig(Item item) {
      return Optional.ofNullable(ModConfigs.VAULT_GEAR_CONFIG.get(item));
   }

   public VaultGearTierConfig(Item gearItem) {
      this.gearItem = gearItem;
   }

   @Override
   public String getName() {
      return "gear_modifiers%s%s".formatted(File.separator, this.gearItem.getRegistryName().getPath());
   }

   @Nullable
   public Object getTierConfig(VaultGearModifier<?> modifier) {
      ResourceLocation modGroup = modifier.getModifierIdentifier();
      int tier = modifier.getRolledTier();
      if (modGroup != null && tier != -1) {
         for (VaultGearTierConfig.AttributeGroup attributePool : this.modifierGroup.values()) {
            if (!attributePool.isEmpty()) {
               for (VaultGearTierConfig.ModifierTierGroup group : attributePool) {
                  if (group.identifier.equals(modGroup)) {
                     if (tier >= group.size()) {
                        return null;
                     }

                     return group.get(tier).getModifierConfiguration();
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public List<VaultGearTierConfig.ModifierTier<?>> getAllTiers(@Nullable ResourceLocation identifier) {
      if (identifier == null) {
         return Collections.emptyList();
      } else {
         for (VaultGearTierConfig.AttributeGroup attributePool : this.modifierGroup.values()) {
            for (VaultGearTierConfig.ModifierTierGroup group : attributePool) {
               if (group.identifier.equals(identifier)) {
                  return Collections.unmodifiableList(group);
               }
            }
         }

         return Collections.emptyList();
      }
   }

   @Nullable
   public VaultGearModifier<?> generateModifier(ResourceLocation identifier, int level, Random random) {
      for (VaultGearTierConfig.AttributeGroup attributePool : this.modifierGroup.values()) {
         for (VaultGearTierConfig.ModifierTierGroup group : attributePool) {
            if (group.identifier.equals(identifier)) {
               WeightedList<VaultGearTierConfig.ModifierOutcome<?>> outcomes = new WeightedList<>();
               group.getModifiersForLevel(level)
                  .forEach(tier -> outcomes.add(new VaultGearTierConfig.ModifierOutcome<>((VaultGearTierConfig.ModifierTier<?>)tier, group), tier.getWeight()));
               return outcomes.getRandom().map(modifierOutcome -> modifierOutcome.makeModifier(random)).orElse(null);
            }
         }
      }

      return null;
   }

   @Nullable
   public VaultGearModifier<?> maxAndIncreaseTier(VaultGearModifier.AffixType type, VaultGearModifier<?> modifier, int level, int tierIncrease, Random random) {
      VaultGearTierConfig.AttributeGroup attributeGroup = this.modifierGroup.get(VaultGearTierConfig.ModifierAffixTagGroup.ofAffixType(type));
      if (attributeGroup != null && !attributeGroup.isEmpty()) {
         VaultGearTierConfig.ModifierTierGroup tierGroup = null;

         for (VaultGearTierConfig.ModifierTierGroup configTierGroup : attributeGroup) {
            if (configTierGroup.identifier.equals(modifier.getModifierIdentifier())) {
               tierGroup = configTierGroup;
               break;
            }
         }

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

   public List<VaultGearModifier<?>> generateImplicits(int level, Random random) {
      VaultGearTierConfig.AttributeGroup attributePool = this.modifierGroup.get(VaultGearTierConfig.ModifierAffixTagGroup.IMPLICIT);
      if (attributePool != null && !attributePool.isEmpty()) {
         List<VaultGearModifier<?>> modifiers = new ArrayList<>();
         attributePool.forEach(
            group -> {
               WeightedList<VaultGearTierConfig.ModifierOutcome<?>> outcomes = new WeightedList<>();
               group.getModifiersForLevel(level)
                  .forEach(tier -> outcomes.add(new VaultGearTierConfig.ModifierOutcome<>((VaultGearTierConfig.ModifierTier<?>)tier, group), tier.getWeight()));
               outcomes.getRandom(random).map(outcome -> outcome.makeModifier(random)).ifPresent(modifiers::add);
            }
         );
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

   public List<Tuple<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.ModifierTierGroup>> getModifierGroupConfigurations(String modGroup) {
      List<Tuple<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.ModifierTierGroup>> configs = new ArrayList<>();

      for (Entry<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.AttributeGroup> entry : this.modifierGroup.entrySet()) {
         if (entry.getKey().isGenericGroup()) {
            for (VaultGearTierConfig.ModifierTierGroup group : entry.getValue()) {
               if (modGroup.isEmpty() || group.modifierGroup.equals(modGroup)) {
                  configs.add(new Tuple(entry.getKey(), group));
               }
            }
         }
      }

      return configs;
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
   protected void onLoad(Config oldConfigInstance) {
      super.onLoad(oldConfigInstance);
      if (oldConfigInstance instanceof VaultGearTierConfig cfg) {
         this.gearItem = cfg.gearItem;
      }
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
                     JsonArray groupArray = tierGroupObject.getAsJsonArray("tiers");

                     for (int i = 0; i < groupArray.size(); i++) {
                        JsonObject tierObject = groupArray.get(i).getAsJsonObject();
                        int minLevel = tierObject.get("minLevel").getAsInt();
                        int maxLevel = tierObject.get("maxLevel").getAsInt();
                        int weight = tierObject.get("weight").getAsInt();
                        VaultGearTierConfig.ModifierTier<?> tier = new VaultGearTierConfig.ModifierTier(minLevel, weight);
                        tier.modifierTier = i;
                        tier.maxLevel = maxLevel;
                        Class<?> configClass = attribute.getGenerator().getConfigurationObjectClass();
                        if (configClass != null) {
                           tier.modifierConfiguration = context.deserialize(tierObject.get("value"), configClass);
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
                  JsonArray groupArray = new JsonArray();
                  group.forEach(modifierTier -> {
                     JsonObject tierObject = new JsonObject();
                     tierObject.addProperty("minLevel", modifierTier.getMinLevel());
                     tierObject.addProperty("maxLevel", modifierTier.getMaxLevel());
                     tierObject.addProperty("weight", modifierTier.getWeight());
                     if (attribute.getGenerator().getConfigurationObjectClass() != null) {
                        tierObject.add("value", context.serialize(modifierTier.getModifierConfiguration()));
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
      IMPLICIT(VaultGearModifier.AffixType.IMPLICIT),
      PREFIX(VaultGearModifier.AffixType.PREFIX),
      SUFFIX(VaultGearModifier.AffixType.SUFFIX),
      ABYSSAL_IMPLICIT(VaultGearModifier.AffixType.IMPLICIT);

      private final VaultGearModifier.AffixType targetAffixType;

      private ModifierAffixTagGroup(VaultGearModifier.AffixType targetAffixType) {
         this.targetAffixType = targetAffixType;
      }

      public VaultGearModifier.AffixType getTargetAffixType() {
         return this.targetAffixType;
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

   private record ModifierOutcome<C>(VaultGearTierConfig.ModifierTier<C> tier, VaultGearTierConfig.ModifierTierGroup tierGroup) {
      private <T> VaultGearModifier<T> makeModifier(Random random) {
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

      public ResourceLocation getAttribute() {
         return this.attribute;
      }

      public String getModifierGroup() {
         return this.modifierGroup;
      }

      public ResourceLocation getIdentifier() {
         return this.identifier;
      }
   }
}
