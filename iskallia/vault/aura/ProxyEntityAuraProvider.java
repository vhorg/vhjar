package iskallia.vault.aura;

import iskallia.vault.config.EternalAuraConfig;
import net.minecraft.entity.LivingEntity;

public class ProxyEntityAuraProvider extends EntityAuraProvider {
   private final LivingEntity owner;

   protected ProxyEntityAuraProvider(LivingEntity entity, LivingEntity owner, EternalAuraConfig.AuraConfig aura) {
      super(entity, aura);
      this.owner = owner;
   }

   public static ProxyEntityAuraProvider ofEntity(LivingEntity entity, LivingEntity owner, EternalAuraConfig.AuraConfig aura) {
      return new ProxyEntityAuraProvider(entity, owner, aura);
   }

   @Override
   public LivingEntity getTrueSource() {
      return this.owner;
   }
}
