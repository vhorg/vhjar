package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MultipleGearAttributeRollOutputEntry extends MultipleRollOutputEntry {
   private static final Random rand = new Random();
   @Expose
   private ResourceLocation gearModifierId;

   public MultipleGearAttributeRollOutputEntry(MultipleRollOutputEntry.OutcomeBias outcomeBias, int rollAttempts, String gearModifierId) {
      this(outcomeBias, rollAttempts, new ResourceLocation(gearModifierId));
   }

   public MultipleGearAttributeRollOutputEntry(MultipleRollOutputEntry.OutcomeBias outcomeBias, int rollAttempts, ResourceLocation gearModifierId) {
      super(outcomeBias, rollAttempts);
      this.gearModifierId = gearModifierId;
   }

   public <T> Optional<MultipleGearAttributeRollOutputEntry.GeneratedModifierOutcome<T>> apply(ItemStack stack) {
      if (this.getRollAttempts() <= 0) {
         return Optional.empty();
      } else {
         return !VaultGearData.hasData(stack)
            ? Optional.empty()
            : VaultGearTierConfig.getConfig(stack)
               .map(
                  cfg -> {
                     VaultGearTierConfig.ModifierTierGroup modTierGroup = cfg.getTierGroup(this.gearModifierId);
                     if (modTierGroup == null) {
                        return null;
                     } else {
                        VaultGearTierConfig.ModifierAffixTagGroup targetGroup = modTierGroup.getTargetAffixTagGroup();
                        if (targetGroup == null) {
                           return null;
                        } else {
                           VaultGearAttribute<T> attribute = (VaultGearAttribute<T>)VaultGearAttributeRegistry.getAttribute(modTierGroup.getAttribute());
                           if (attribute == null) {
                              return null;
                           } else if (attribute.getAttributeComparator() == null) {
                              return null;
                           } else {
                              VaultGearData data = VaultGearData.read(stack);
                              int itemLevel = data.getItemLevel();
                              List<VaultGearModifier.ComparableModifier<T>> generatedValues = new ArrayList<>();

                              for (int i = 0; i < this.getRollAttempts(); i++) {
                                 VaultGearModifier<T> vgm = (VaultGearModifier<T>)cfg.generateModifier(this.gearModifierId, itemLevel, rand);
                                 if (vgm != null) {
                                    VaultGearModifier.ComparableModifier<T> cmpModifier = vgm.getComparable();
                                    if (cmpModifier != null) {
                                       generatedValues.add(cmpModifier);
                                    }
                                 }
                              }

                              return this.getOutcomeBias()
                                 .select(generatedValues)
                                 .map(mod -> new MultipleGearAttributeRollOutputEntry.GeneratedModifierOutcome<>(mod.getModifier(), targetGroup))
                                 .orElse(null);
                           }
                        }
                     }
                  }
               );
      }
   }

   public static class GeneratedModifierOutcome<T> {
      private final VaultGearModifier<T> modifier;
      private final VaultGearTierConfig.ModifierAffixTagGroup targetGroup;

      public GeneratedModifierOutcome(VaultGearModifier<T> modifier, VaultGearTierConfig.ModifierAffixTagGroup targetGroup) {
         this.modifier = modifier;
         this.targetGroup = targetGroup;
      }

      public VaultGearModifier<?> getModifier() {
         return this.modifier;
      }

      public boolean applyModifier(VaultGearData data) {
         return this.targetGroup.addModifier(data, this.getModifier());
      }
   }
}
