package iskallia.vault.gear.trinket.effects;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.trinket.TrinketEffect;
import net.minecraft.resources.ResourceLocation;

public class VaultTimeExtensionTrinket extends TrinketEffect<VaultTimeExtensionTrinket.Config> {
   private final int defaultTimeAdded;

   public VaultTimeExtensionTrinket(ResourceLocation name, int defaultTimeAdded) {
      super(name);
      this.defaultTimeAdded = defaultTimeAdded;
   }

   @Override
   public Class<VaultTimeExtensionTrinket.Config> getConfigClass() {
      return VaultTimeExtensionTrinket.Config.class;
   }

   public VaultTimeExtensionTrinket.Config getDefaultConfig() {
      return new VaultTimeExtensionTrinket.Config(this.defaultTimeAdded);
   }

   public static class Config extends TrinketEffect.Config {
      @Expose
      private int timeAdded;

      public Config(int timeAdded) {
         this.timeAdded = timeAdded;
      }

      public int getTimeAdded() {
         return this.timeAdded;
      }
   }
}
