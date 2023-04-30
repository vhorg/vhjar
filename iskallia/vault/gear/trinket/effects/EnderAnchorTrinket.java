package iskallia.vault.gear.trinket.effects;

import iskallia.vault.gear.trinket.TrinketEffect;
import net.minecraft.resources.ResourceLocation;

public class EnderAnchorTrinket extends TrinketEffect<TrinketEffect.Config> {
   public EnderAnchorTrinket(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<TrinketEffect.Config> getConfigClass() {
      return TrinketEffect.Config.class;
   }

   @Override
   public TrinketEffect.Config getDefaultConfig() {
      return new TrinketEffect.Config();
   }
}
