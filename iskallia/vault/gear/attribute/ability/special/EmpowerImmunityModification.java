package iskallia.vault.gear.attribute.ability.special;

import com.google.gson.JsonArray;
import iskallia.vault.VaultMod;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.ability.special.base.template.NoOpAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.NoOpConfig;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class EmpowerImmunityModification extends NoOpAbilityModification {
   public static final ResourceLocation ID = VaultMod.id("tank_immunity");

   public EmpowerImmunityModification() {
      super(ID);
   }

   public static SpecialAbilityGearAttribute.SpecialAbilityConfig<?, ?> newConfig() {
      return new SpecialAbilityGearAttribute.SpecialAbilityConfig<>("Empower", ID, NoOpConfig.INSTANCE);
   }

   @Nullable
   public MutableComponent getDisplay(NoOpConfig config, Style style, VaultGearModifier.AffixType type) {
      return new TextComponent("")
         .withStyle(style)
         .append(type.getAffixPrefix(true))
         .withStyle(getValueStyle())
         .append(new TextComponent("Immunity").withStyle(getValueStyle()))
         .append(" against negative potion effects during ")
         .append(new TextComponent("Empower").withStyle(getAbilityStyle()));
   }

   public void serializeTextElements(JsonArray out, NoOpConfig config, VaultGearModifier.AffixType type) {
      out.add(type.getAffixPrefix(true));
      out.add("Immunity against negative potion effects during Empower");
   }
}