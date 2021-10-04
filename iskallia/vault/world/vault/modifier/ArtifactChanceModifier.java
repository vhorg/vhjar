package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import net.minecraft.util.ResourceLocation;

public class ArtifactChanceModifier extends TexturedVaultModifier {
   @Expose
   private final float artifactChanceIncrease;

   public ArtifactChanceModifier(String name, ResourceLocation icon, float chanceIncrease) {
      super(name, icon);
      this.artifactChanceIncrease = chanceIncrease;
   }

   public float getArtifactChanceIncrease() {
      return this.artifactChanceIncrease;
   }
}
