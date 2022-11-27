package iskallia.vault.util.damage;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

public class ThornsReflectDamageSource extends EntityDamageSource {
   public ThornsReflectDamageSource(Entity attacker) {
      super("thorns", attacker);
      this.setThorns();
   }

   public static ThornsReflectDamageSource of(Entity attacker) {
      return new ThornsReflectDamageSource(attacker);
   }
}
