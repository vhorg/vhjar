package iskallia.vault.aura;

import iskallia.vault.config.EternalAuraConfig;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AuraProvider {
   private final UUID id;
   private final ResourceKey<Level> world;

   protected AuraProvider(UUID id, ResourceKey<Level> world) {
      this.id = id;
      this.world = world;
   }

   public final ResourceKey<Level> getWorld() {
      return this.world;
   }

   public final UUID getId() {
      return this.id;
   }

   public abstract boolean isValid();

   public abstract Vec3 getLocation();

   public abstract EternalAuraConfig.AuraConfig getAura();

   public float getRadius() {
      return this.getAura().getRadius();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AuraProvider that = (AuraProvider)o;
         return Objects.equals(this.id, that.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}
