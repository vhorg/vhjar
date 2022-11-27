package iskallia.vault.gear.trinket.effects;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.trinket.TrinketEffect;
import net.minecraft.resources.ResourceLocation;

public class VaultExperienceTrinket extends TrinketEffect<VaultExperienceTrinket.Config> {
   public VaultExperienceTrinket(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<VaultExperienceTrinket.Config> getConfigClass() {
      return VaultExperienceTrinket.Config.class;
   }

   public VaultExperienceTrinket.Config getDefaultConfig() {
      return new VaultExperienceTrinket.Config(0.5F);
   }

   public static final class Config extends TrinketEffect.Config {
      @Expose
      private float experienceIncrease;

      public Config(float experienceIncrease) {
         this.experienceIncrease = experienceIncrease;
      }

      public float getExperienceIncrease() {
         return this.experienceIncrease;
      }
   }
}
