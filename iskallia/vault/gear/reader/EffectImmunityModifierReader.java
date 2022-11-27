package iskallia.vault.gear.reader;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public class EffectImmunityModifierReader extends VaultGearModifierReader<MobEffect> {
   public EffectImmunityModifierReader() {
      super("", 10801083);
   }

   @Nullable
   @Override
   public MutableComponent getDisplay(VaultGearAttributeInstance<MobEffect> instance, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(instance.getValue());
      return valueDisplay == null ? null : new TextComponent(type.getAffixPrefix(true)).append(valueDisplay).setStyle(this.getColoredTextStyle());
   }

   @Nullable
   public MutableComponent getValueDisplay(MobEffect value) {
      return new TranslatableComponent(value.getDescriptionId()).append(new TextComponent(" Immunity"));
   }

   @Override
   protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<MobEffect> instance, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(instance.getValue());
      if (valueDisplay != null) {
         out.add(type.getAffixPrefix(true));
         out.add(instance.getValue().getDescriptionId());
         out.add(" Immunity");
      }
   }
}
