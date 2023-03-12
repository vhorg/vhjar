package iskallia.vault.gear.attribute.config;

import iskallia.vault.gear.reader.VaultGearModifierReader;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectImmunityGenerator extends RegistryAttributeGenerator<MobEffect> {
   public EffectImmunityGenerator() {
      super(ForgeRegistries.MOB_EFFECTS);
   }

   @Nullable
   public MutableComponent getConfigDisplay(VaultGearModifierReader<MobEffect> reader, RegistryAttributeGenerator.RegistryLookup object) {
      return reader.getValueDisplay(this.getEntry(object)).withStyle(reader.getColoredTextStyle());
   }
}
