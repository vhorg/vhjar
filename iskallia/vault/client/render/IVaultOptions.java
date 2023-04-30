package iskallia.vault.client.render;

import iskallia.vault.util.ColorOption;

public interface IVaultOptions {
   boolean doVanillaPotionDamageEffects();

   void setVanillaPotionDamageEffects(boolean var1);

   boolean isHunterCustomColorsEnabled();

   void setHunterCustomColorsEnabled(boolean var1);

   ColorOption getChestHunterSpec();

   void setChestHunterSpec(ColorOption var1);

   ColorOption getBlockHunterSpec();

   void setBlockHunterSpec(ColorOption var1);

   ColorOption getWoodenHunterSpec();

   void setWoodenHunterSpec(ColorOption var1);

   ColorOption getGildedHunterSpec();

   void setGildedHunterSpec(ColorOption var1);

   ColorOption getLivingHunterSpec();

   void setLivingHunterSpec(ColorOption var1);

   ColorOption getOrnateHunterSpec();

   void setOrnateHunterSpec(ColorOption var1);

   ColorOption getCoinsHunterSpec();

   void setCoinsHunterSpec(ColorOption var1);

   void setColorOption(ColorOption.HunterSpec var1, ColorOption var2);

   ColorOption getBySpec(ColorOption.HunterSpec var1);

   ColorOption resetColorOption(ColorOption.HunterSpec var1);
}
