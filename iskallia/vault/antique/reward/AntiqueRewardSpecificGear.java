package iskallia.vault.antique.reward;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.VaultGearLegendaryHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

public class AntiqueRewardSpecificGear extends AntiqueRewardItemList {
   @Nullable
   private String guaranteedModifier;
   @Nullable
   private AntiqueRewardSpecificGear.ModifierReferenceType referenceType;
   @Nullable
   private AntiqueRewardSpecificGear.ModifierOperation operation;

   public void setGuaranteedModifier(@Nullable String guaranteedModifier) {
      this.guaranteedModifier = guaranteedModifier;
   }

   public void setReferenceType(@Nullable AntiqueRewardSpecificGear.ModifierReferenceType referenceType) {
      this.referenceType = referenceType;
   }

   public void setOperation(@Nullable AntiqueRewardSpecificGear.ModifierOperation operation) {
      this.operation = operation;
   }

   @Override
   public List<ItemStack> generateReward(RandomSource random, ServerPlayer player, int level) {
      return super.generateReward(random, player, level).stream().map(stack -> {
         if (this.guaranteedModifier != null && this.referenceType != null) {
            if (!this.applyModifications(stack, random, player, level)) {
               return ItemStack.EMPTY;
            }
         } else if (this.operation == AntiqueRewardSpecificGear.ModifierOperation.MAKE_LEGENDARY && !this.forceLegendaryModifier(stack, player, random)) {
            return ItemStack.EMPTY;
         }

         return (ItemStack)stack;
      }).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
   }

   private boolean forceLegendaryModifier(ItemStack stack, ServerPlayer player, RandomSource random) {
      if (stack.getItem() instanceof VaultGearItem vgi) {
         Random var8 = new Random(random.nextLong());
         VaultGearData data = VaultGearData.read(stack);
         if (data.getState() == VaultGearState.UNIDENTIFIED) {
            vgi.instantIdentify(player, stack);
         }

         GearDataCache cache = GearDataCache.of(stack);
         if (cache.hasModifierOfCategory(VaultGearModifier.AffixCategory.LEGENDARY)) {
            return true;
         } else {
            return VaultGearLegendaryHelper.generateImprovedModifier(stack.copy(), 2, var8, List.of(VaultGearModifier.AffixCategory.LEGENDARY))
               ? VaultGearLegendaryHelper.generateImprovedModifier(stack, 2, var8, List.of(VaultGearModifier.AffixCategory.LEGENDARY))
               : false;
         }
      } else {
         return false;
      }
   }

   private boolean applyModifications(ItemStack stack, RandomSource random, ServerPlayer player, int level) {
      if (this.referenceType != null && this.guaranteedModifier != null) {
         if (!(stack.getItem() instanceof VaultGearItem vgi)) {
            return false;
         } else {
            VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
            if (cfg == null) {
               return false;
            } else {
               vgi.instantIdentify(player, stack);

               Tuple<VaultGearModifier.AffixType, VaultGearModifier<?>> generated = switch (this.referenceType) {
                  case TAG -> this.getByTag(cfg, level, random);
                  case GROUP -> this.getByGroup(cfg, level, random);
                  case IDENTIFIER -> this.getByIdentifier(cfg, level, random);
               };
               if (generated == null) {
                  return false;
               } else {
                  VaultGearData data = VaultGearData.read(stack);
                  VaultGearModifier.AffixType affixType = (VaultGearModifier.AffixType)generated.getA();
                  VaultGearModifier<?> modifier = (VaultGearModifier<?>)generated.getB();
                  if (modifier.getModifierIdentifier() != null) {
                     data.getModifiersFulfilling(mod -> mod.getModifierIdentifier().equals(modifier.getModifierIdentifier())).forEach(data::removeModifier);
                  }

                  if (modifier.getModifierGroup() != null) {
                     data.getModifiersFulfilling(mod -> mod.getModifierGroup().equals(modifier.getModifierGroup())).forEach(data::removeModifier);
                  }
                  VaultGearAttribute<Integer> affixAttribute = switch (affixType) {
                     case PREFIX -> ModGearAttributes.PREFIXES;
                     case SUFFIX -> ModGearAttributes.SUFFIXES;
                     case IMPLICIT -> null;
                  };
                  if (affixAttribute != null) {
                     int affixSlots = data.getFirstValue(affixAttribute).orElse(0);
                     int attempts = affixSlots;

                     for (int affixes = data.getModifiers(affixType).size();
                        affixes >= affixSlots && attempts > 0;
                        affixes = data.getModifiers(affixType).size()
                     ) {
                        List<VaultGearModifier<?>> removableModifiers = data.getModifiers(affixType).stream().filter(VaultGearModifier::canBeModified).toList();
                        if (removableModifiers.isEmpty()) {
                           return false;
                        }

                        VaultGearModifier<?> randomMod = MiscUtils.getRandomEntry(removableModifiers);
                        if (randomMod == null) {
                           return false;
                        }

                        if (!data.removeModifier(randomMod)) {
                           attempts--;
                        }
                     }

                     if (attempts <= 0) {
                        return false;
                     }
                  }

                  VaultGearModifier<?> modifierToAdd = modifier;
                  if (this.operation == AntiqueRewardSpecificGear.ModifierOperation.MAKE_LEGENDARY) {
                     VaultGearModifier<?> newModifier = cfg.maxAndIncreaseTier(affixType, modifier, level, 2, new Random(random.nextLong()));
                     if (newModifier != null) {
                        newModifier.addCategory(VaultGearModifier.AffixCategory.LEGENDARY);
                        modifierToAdd = newModifier;
                     }
                  }

                  if (affixType == VaultGearModifier.AffixType.IMPLICIT) {
                     data.addModifier(affixType, modifierToAdd);
                  } else {
                     data.addModifierFirst(affixType, modifierToAdd);
                  }

                  data.write(stack);
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   private Tuple<VaultGearModifier.AffixType, VaultGearModifier<?>> getByTag(VaultGearTierConfig cfg, int level, RandomSource random) {
      WeightedList<Tuple<VaultGearModifier.AffixType, VaultGearTierConfig.ModifierOutcome<?>>> outcomes = new WeightedList<>();
      cfg.getGenericGroupsWithModifierTag(this.guaranteedModifier)
         .forEach(
            tpl -> {
               VaultGearModifier.AffixType affix = ((VaultGearTierConfig.ModifierAffixTagGroup)tpl.getA()).getTargetAffixType();
               if (affix != null) {
                  VaultGearTierConfig.ModifierTierGroup group = (VaultGearTierConfig.ModifierTierGroup)tpl.getB();
                  if (this.operation == AntiqueRewardSpecificGear.ModifierOperation.HIGHEST_TIER) {
                     VaultGearTierConfig.ModifierTier<?> foundTier = group.getHighestForLevel(level);
                     if (foundTier != null) {
                        outcomes.add(new Tuple(affix, new VaultGearTierConfig.ModifierOutcome<>(foundTier, group)), foundTier.getWeight());
                     }
                  } else {
                     group.getModifiersForLevel(level)
                        .forEach(
                           tier -> outcomes.add(
                              new Tuple(affix, new VaultGearTierConfig.ModifierOutcome<>((VaultGearTierConfig.ModifierTier<?>)tier, group)), tier.getWeight()
                           )
                        );
                  }
               }
            }
         );
      Tuple<VaultGearModifier.AffixType, VaultGearTierConfig.ModifierOutcome<?>> resultTpl = outcomes.getRandom(random).orElse(null);
      return resultTpl == null
         ? null
         : new Tuple(
            (VaultGearModifier.AffixType)resultTpl.getA(), ((VaultGearTierConfig.ModifierOutcome)resultTpl.getB()).makeModifier(new Random(random.nextLong()))
         );
   }

   private Tuple<VaultGearModifier.AffixType, VaultGearModifier<?>> getByGroup(VaultGearTierConfig cfg, int level, RandomSource random) {
      WeightedList<Tuple<VaultGearModifier.AffixType, VaultGearTierConfig.ModifierOutcome<?>>> outcomes = new WeightedList<>();
      cfg.getGenericGroupsWithModifierGroup(this.guaranteedModifier)
         .forEach(
            tpl -> {
               VaultGearModifier.AffixType affix = ((VaultGearTierConfig.ModifierAffixTagGroup)tpl.getA()).getTargetAffixType();
               if (affix != null) {
                  VaultGearTierConfig.ModifierTierGroup group = (VaultGearTierConfig.ModifierTierGroup)tpl.getB();
                  if (this.operation == AntiqueRewardSpecificGear.ModifierOperation.HIGHEST_TIER) {
                     VaultGearTierConfig.ModifierTier<?> foundTier = group.getHighestForLevel(level);
                     if (foundTier != null) {
                        outcomes.add(new Tuple(affix, new VaultGearTierConfig.ModifierOutcome<>(foundTier, group)), foundTier.getWeight());
                     }
                  } else {
                     group.getModifiersForLevel(level)
                        .forEach(
                           tier -> outcomes.add(
                              new Tuple(affix, new VaultGearTierConfig.ModifierOutcome<>((VaultGearTierConfig.ModifierTier<?>)tier, group)), tier.getWeight()
                           )
                        );
                  }
               }
            }
         );
      Tuple<VaultGearModifier.AffixType, VaultGearTierConfig.ModifierOutcome<?>> resultTpl = outcomes.getRandom(random).orElse(null);
      return resultTpl == null
         ? null
         : new Tuple(
            (VaultGearModifier.AffixType)resultTpl.getA(), ((VaultGearTierConfig.ModifierOutcome)resultTpl.getB()).makeModifier(new Random(random.nextLong()))
         );
   }

   private Tuple<VaultGearModifier.AffixType, VaultGearModifier<?>> getByIdentifier(VaultGearTierConfig cfg, int level, RandomSource random) {
      ResourceLocation key = ResourceLocation.tryParse(this.guaranteedModifier);
      if (key == null) {
         return null;
      } else {
         VaultGearTierConfig.ModifierTierGroup group = cfg.getTierGroup(key);
         if (group == null) {
            return null;
         } else {
            VaultGearTierConfig.ModifierAffixTagGroup affixGroup = group.getTargetAffixTagGroup();
            if (affixGroup == null) {
               return null;
            } else {
               VaultGearModifier.AffixType affixType = affixGroup.getTargetAffixType();
               if (affixType == null) {
                  return null;
               } else {
                  WeightedList<VaultGearTierConfig.ModifierOutcome<?>> outcomes = new WeightedList<>();
                  if (this.operation == AntiqueRewardSpecificGear.ModifierOperation.HIGHEST_TIER) {
                     VaultGearTierConfig.ModifierTier<?> foundTier = group.getHighestForLevel(level);
                     if (foundTier != null) {
                        outcomes.add(new VaultGearTierConfig.ModifierOutcome<>(foundTier, group), foundTier.getWeight());
                     }
                  } else {
                     group.getModifiersForLevel(level)
                        .forEach(
                           tier -> outcomes.add(new VaultGearTierConfig.ModifierOutcome<>((VaultGearTierConfig.ModifierTier<?>)tier, group), tier.getWeight())
                        );
                  }

                  VaultGearModifier<?> generatedModifier = outcomes.getRandom()
                     .map(modifierOutcome -> modifierOutcome.makeModifier(new Random(random.nextLong())))
                     .orElse(null);
                  return generatedModifier == null ? null : new Tuple(affixType, generatedModifier);
               }
            }
         }
      }
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      super.deserialize(ctx, json);
      if (json.has("guaranteedModifier") && json.has("referenceType")) {
         this.guaranteedModifier = json.get("guaranteedModifier").getAsString();
         this.referenceType = AntiqueRewardSpecificGear.ModifierReferenceType.valueOf(json.get("referenceType").getAsString());
      }

      if (json.has("operation")) {
         this.operation = AntiqueRewardSpecificGear.ModifierOperation.valueOf(json.get("operation").getAsString());
      }
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = super.serialize(ctx);
      if (this.guaranteedModifier != null && this.referenceType != null) {
         json.addProperty("guaranteedModifier", this.guaranteedModifier);
         json.addProperty("referenceType", this.referenceType.name());
      }

      if (this.operation != null) {
         json.addProperty("operation", this.operation.name());
      }

      return json;
   }

   public static enum ModifierOperation {
      HIGHEST_TIER,
      MAKE_LEGENDARY;
   }

   public static enum ModifierReferenceType {
      TAG,
      GROUP,
      IDENTIFIER;
   }
}
