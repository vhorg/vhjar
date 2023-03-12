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

public class TankImmunityModification extends NoOpAbilityModification {
   public static final ResourceLocation ID = VaultMod.id("tank_immunity");

   public TankImmunityModification() {
      super(ID);
   }

   public static SpecialAbilityGearAttribute.SpecialAbilityConfig<?, ?> newConfig() {
      return new SpecialAbilityGearAttribute.SpecialAbilityConfig<>("Tank", ID, NoOpConfig.INSTANCE);
   }

   @Nullable
   public MutableComponent getDisplay(NoOpConfig config, Style style, VaultGearModifier.AffixType type) {
      return new TextComponent(type.getAffixPrefix(true)).withStyle(style).append(this.getDescription()).withStyle(style);
   }

   public void serializeTextElements(JsonArray out, NoOpConfig config, VaultGearModifier.AffixType type) {
      out.add(type.getAffixPrefix(true));
      out.add(this.getDescription());
   }

   private String getDescription() {
      return "Immunity against negative potion effects during Tank";
   }
}
