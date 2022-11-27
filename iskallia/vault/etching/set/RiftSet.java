package iskallia.vault.etching.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.etching.EtchingSet;
import net.minecraft.resources.ResourceLocation;

public class RiftSet extends EtchingSet<RiftSet.Config> {
   public RiftSet(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<RiftSet.Config> getConfigClass() {
      return RiftSet.Config.class;
   }

   public RiftSet.Config getDefaultConfig() {
      return new RiftSet.Config(0.5F);
   }

   public float getCooldownMultiplier() {
      return this.getConfig().getCooldownMultiplier();
   }

   public static class Config {
      @Expose
      private float cooldownMultiplier;

      public Config(float cooldownMultiplier) {
         this.cooldownMultiplier = cooldownMultiplier;
      }

      public float getCooldownMultiplier() {
         return this.cooldownMultiplier;
      }
   }
}
