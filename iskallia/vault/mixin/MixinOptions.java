package iskallia.vault.mixin;

import iskallia.vault.client.render.IVaultOptions;
import iskallia.vault.util.ColorOption;
import iskallia.vault.util.CooldownGuiOption;
import net.minecraft.client.Options;
import net.minecraft.client.Options.FieldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Options.class})
public abstract class MixinOptions implements IVaultOptions {
   public CooldownGuiOption cooldownGuiOption = CooldownGuiOption.OFF;
   public boolean doVanillaPotionDamageEffects = false;
   public boolean hunterCustomColorsEnabled = false;
   public boolean abilityScrollingEnabled = true;
   public boolean showPointMessages = true;
   public boolean showRarityNames = false;
   public ColorOption chestHunterSpec = new ColorOption(ColorOption.HunterSpec.BASE, 0.8901961F, 0.5529412F, 0.0F);
   public ColorOption blockHunterSpec = new ColorOption(ColorOption.HunterSpec.OBSERVER, 0.14509805F, 0.6745098F, 0.0F);
   public ColorOption gildedHunterSpec = new ColorOption(ColorOption.HunterSpec.GILDED, 1.0F, 1.0F, 0.0F);
   public ColorOption livingHunterSpec = new ColorOption(ColorOption.HunterSpec.LIVING, 0.0F, 1.0F, 0.0F);
   public ColorOption ornateHunterSpec = new ColorOption(ColorOption.HunterSpec.ORNATE, 0.93333334F, 0.0F, 0.0F);
   public ColorOption coinsHunterSpec = new ColorOption(ColorOption.HunterSpec.COINS, 0.8039216F, 0.44705883F, 0.15294118F);

   @Shadow
   public abstract void save();

   @Inject(
      method = {"processOptions"},
      at = {@At("HEAD")}
   )
   private void processVaultOptions(FieldAccess pAccessor, CallbackInfo ci) {
      this.doVanillaPotionDamageEffects = pAccessor.process("doVanillaPotionDamageEffects", this.doVanillaPotionDamageEffects);
      this.hunterCustomColorsEnabled = pAccessor.process("hunter_CustomColorsEnabled", this.hunterCustomColorsEnabled);
      this.abilityScrollingEnabled = pAccessor.process("abilityScrollingEnabled", this.abilityScrollingEnabled);
      this.showPointMessages = pAccessor.process("showPointMessages", this.showPointMessages);
      this.showRarityNames = pAccessor.process("showRarityNames", this.showRarityNames);
      this.cooldownGuiOption = (CooldownGuiOption)pAccessor.process(
         CooldownGuiOption.OFF.getSerializedName(), this.cooldownGuiOption, CooldownGuiOption::fromString, CooldownGuiOption::getSerializedName
      );
      this.chestHunterSpec = (ColorOption)pAccessor.process(
         ColorOption.HunterSpec.BASE.toString(), this.chestHunterSpec, this::readColorOption, this::writeColorOption
      );
      this.blockHunterSpec = (ColorOption)pAccessor.process(
         ColorOption.HunterSpec.OBSERVER.toString(), this.blockHunterSpec, this::readColorOption, this::writeColorOption
      );
      this.gildedHunterSpec = (ColorOption)pAccessor.process(
         ColorOption.HunterSpec.GILDED.toString(), this.gildedHunterSpec, this::readColorOption, this::writeColorOption
      );
      this.livingHunterSpec = (ColorOption)pAccessor.process(
         ColorOption.HunterSpec.LIVING.toString(), this.livingHunterSpec, this::readColorOption, this::writeColorOption
      );
      this.ornateHunterSpec = (ColorOption)pAccessor.process(
         ColorOption.HunterSpec.ORNATE.toString(), this.ornateHunterSpec, this::readColorOption, this::writeColorOption
      );
      this.coinsHunterSpec = (ColorOption)pAccessor.process(
         ColorOption.HunterSpec.COINS.toString(), this.coinsHunterSpec, this::readColorOption, this::writeColorOption
      );
   }

   private String writeColorOption(ColorOption t) {
      return String.format("%s,%s,%s,%s", t.getHunterSpec().name(), t.getRed(), t.getGreen(), t.getBlue());
   }

   private ColorOption readColorOption(String string) {
      String[] split = string.split(",");
      return new ColorOption(split[0], Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
   }

   @Override
   public CooldownGuiOption getCooldownGuiOption() {
      return this.cooldownGuiOption;
   }

   @Override
   public void cycleCooldownGuiOption() {
      this.cooldownGuiOption = this.cooldownGuiOption.cycle();
      this.save();
   }

   @Override
   public boolean doVanillaPotionDamageEffects() {
      return this.doVanillaPotionDamageEffects;
   }

   @Override
   public void setVanillaPotionDamageEffects(boolean vanillaPotionDamageEffects) {
      this.doVanillaPotionDamageEffects = vanillaPotionDamageEffects;
      this.save();
   }

   @Override
   public boolean isHunterCustomColorsEnabled() {
      return this.hunterCustomColorsEnabled;
   }

   @Override
   public void setHunterCustomColorsEnabled(boolean hunterCustomColorsEnabled) {
      this.hunterCustomColorsEnabled = hunterCustomColorsEnabled;
      this.save();
   }

   @Override
   public boolean isAbilityScrollingEnabled() {
      return this.abilityScrollingEnabled;
   }

   @Override
   public void setAbilityScrollingEnabled(boolean abilityScrollingEnabled) {
      this.abilityScrollingEnabled = abilityScrollingEnabled;
      this.save();
   }

   @Override
   public boolean showPointMessages() {
      return this.showPointMessages;
   }

   @Override
   public void setShowPointMessages(boolean showPointMessages) {
      this.showPointMessages = showPointMessages;
      this.save();
   }

   @Override
   public boolean showRarityNames() {
      return this.showRarityNames;
   }

   @Override
   public void setShowRarityNames(boolean showRarityNames) {
      this.showRarityNames = showRarityNames;
      this.save();
   }

   @Override
   public ColorOption getChestHunterSpec() {
      return this.chestHunterSpec;
   }

   @Override
   public void setChestHunterSpec(ColorOption option) {
      this.chestHunterSpec = option;
      this.save();
   }

   @Override
   public ColorOption getBlockHunterSpec() {
      return this.blockHunterSpec;
   }

   @Override
   public void setBlockHunterSpec(ColorOption option) {
      this.blockHunterSpec = option;
      this.save();
   }

   @Override
   public ColorOption getGildedHunterSpec() {
      return this.gildedHunterSpec;
   }

   @Override
   public void setGildedHunterSpec(ColorOption option) {
      this.gildedHunterSpec = option;
      this.save();
   }

   @Override
   public ColorOption getLivingHunterSpec() {
      return this.livingHunterSpec;
   }

   @Override
   public void setLivingHunterSpec(ColorOption option) {
      this.livingHunterSpec = option;
      this.save();
   }

   @Override
   public ColorOption getOrnateHunterSpec() {
      return this.ornateHunterSpec;
   }

   @Override
   public void setOrnateHunterSpec(ColorOption option) {
      this.ornateHunterSpec = option;
      this.save();
   }

   @Override
   public ColorOption getCoinsHunterSpec() {
      return this.coinsHunterSpec;
   }

   @Override
   public void setCoinsHunterSpec(ColorOption option) {
      this.coinsHunterSpec = option;
      this.save();
   }

   @Override
   public ColorOption getBySpec(ColorOption.HunterSpec spec) {
      switch (spec) {
         case BASE:
            return this.chestHunterSpec;
         case OBSERVER:
            return this.blockHunterSpec;
         case GILDED:
            return this.gildedHunterSpec;
         case LIVING:
            return this.livingHunterSpec;
         case ORNATE:
            return this.ornateHunterSpec;
         case COINS:
            return this.coinsHunterSpec;
         default:
            return this.chestHunterSpec;
      }
   }

   @Override
   public ColorOption resetColorOption(ColorOption.HunterSpec spec) {
      switch (spec) {
         case BASE:
            this.chestHunterSpec = new ColorOption(ColorOption.HunterSpec.BASE, 0.7607843F, 0.63529414F, 0.34901962F);
            return this.chestHunterSpec;
         case OBSERVER:
            this.blockHunterSpec = new ColorOption(ColorOption.HunterSpec.OBSERVER, 0.14509805F, 0.6745098F, 0.0F);
            return this.blockHunterSpec;
         case GILDED:
            this.gildedHunterSpec = new ColorOption(ColorOption.HunterSpec.GILDED, 1.0F, 1.0F, 0.0F);
            return this.gildedHunterSpec;
         case LIVING:
            this.livingHunterSpec = new ColorOption(ColorOption.HunterSpec.LIVING, 0.0F, 1.0F, 0.0F);
            return this.livingHunterSpec;
         case ORNATE:
            this.ornateHunterSpec = new ColorOption(ColorOption.HunterSpec.ORNATE, 0.93333334F, 0.0F, 0.0F);
            return this.ornateHunterSpec;
         case COINS:
            this.coinsHunterSpec = new ColorOption(ColorOption.HunterSpec.COINS, 0.8039216F, 0.44705883F, 0.15294118F);
            return this.coinsHunterSpec;
         default:
            this.save();
            return null;
      }
   }

   @Override
   public void setColorOption(ColorOption.HunterSpec spec, ColorOption option) {
      switch (spec) {
         case BASE:
            this.chestHunterSpec = option;
            break;
         case OBSERVER:
            this.blockHunterSpec = option;
            break;
         case GILDED:
            this.gildedHunterSpec = option;
            break;
         case LIVING:
            this.livingHunterSpec = option;
            break;
         case ORNATE:
            this.ornateHunterSpec = option;
            break;
         case COINS:
            this.coinsHunterSpec = option;
      }

      this.save();
   }
}
