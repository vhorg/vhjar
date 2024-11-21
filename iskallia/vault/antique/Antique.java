package iskallia.vault.antique;

import iskallia.vault.config.AntiquesConfig;
import iskallia.vault.init.ModConfigs;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class Antique extends ForgeRegistryEntry<Antique> {
   @Nullable
   private final String authorName;

   public Antique(ResourceLocation id) {
      this(id, null);
   }

   public Antique(ResourceLocation id, @Nullable String authorName) {
      this.setRegistryName(id);
      this.authorName = authorName;
   }

   @Nullable
   public AntiquesConfig.Entry getConfig() {
      return !ModConfigs.isInitialized() ? null : ModConfigs.ANTIQUES.getAntiqueConfig(this.getRegistryName());
   }

   @Nullable
   public String getAuthorName() {
      return this.authorName;
   }
}
