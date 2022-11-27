package iskallia.vault.aura;

import iskallia.vault.config.EternalAuraConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EntityAuraProvider extends AuraProvider {
   private final EternalAuraConfig.AuraConfig aura;
   private final LivingEntity entity;

   protected EntityAuraProvider(LivingEntity entity, EternalAuraConfig.AuraConfig aura) {
      super(entity.getUUID(), entity.getCommandSenderWorld().dimension());
      this.aura = aura;
      this.entity = entity;
   }

   public static EntityAuraProvider ofEntity(LivingEntity entity, EternalAuraConfig.AuraConfig aura) {
      return new EntityAuraProvider(entity, aura);
   }

   public LivingEntity getSource() {
      return this.entity;
   }

   @Override
   public boolean isValid() {
      return this.entity.isAlive();
   }

   @Override
   public Vec3 getLocation() {
      return new Vec3(this.entity.getX(), this.entity.getY(), this.entity.getZ());
   }

   @Override
   public EternalAuraConfig.AuraConfig getAura() {
      return this.aura;
   }
}
