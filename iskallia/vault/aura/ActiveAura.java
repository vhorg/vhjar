package iskallia.vault.aura;

import iskallia.vault.config.EternalAuraConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ActiveAura {
   private final AuraProvider auraProvider;
   private ResourceKey<Level> worldKey;
   private Vec3 offset;
   private float radius;
   private float radiusSq;

   public ActiveAura(AuraProvider auraProvider) {
      this.auraProvider = auraProvider;
      this.updateFromProvider();
   }

   public void updateFromProvider() {
      this.worldKey = this.auraProvider.getWorld();
      this.offset = this.auraProvider.getLocation();
      this.radius = this.auraProvider.getRadius();
      this.radiusSq = this.radius * this.radius;
   }

   public boolean canPersist() {
      return this.auraProvider.isValid();
   }

   public boolean isAffected(Entity entity) {
      ResourceKey<Level> entityWorld = entity.getCommandSenderWorld().dimension();
      if (!this.worldKey.equals(entityWorld)) {
         return false;
      } else {
         Vec3 pos = entity.position();
         return this.offset.distanceToSqr(pos) < this.radiusSq;
      }
   }

   public ResourceKey<Level> getWorldKey() {
      return this.worldKey;
   }

   public Vec3 getOffset() {
      return this.offset;
   }

   public float getRadius() {
      return this.radius;
   }

   public EternalAuraConfig.AuraConfig getAura() {
      return this.auraProvider.getAura();
   }

   public AuraProvider getAuraProvider() {
      return this.auraProvider;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ActiveAura that = (ActiveAura)o;
         return this.auraProvider.equals(that.auraProvider);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.auraProvider.hashCode();
   }
}
