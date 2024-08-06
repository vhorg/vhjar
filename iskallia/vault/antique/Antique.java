package iskallia.vault.antique;

import iskallia.vault.config.AntiquesConfig;
import iskallia.vault.init.ModConfigs;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class Antique extends ForgeRegistryEntry<Antique> {
   public Antique(ResourceLocation id) {
      this.setRegistryName(id);
   }

   @Nullable
   public AntiquesConfig.Entry getConfig() {
      return !ModConfigs.isInitialized() ? null : ModConfigs.ANTIQUES.getAntiqueConfig(this.getRegistryName());
   }
}
