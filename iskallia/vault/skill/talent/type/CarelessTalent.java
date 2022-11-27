package iskallia.vault.skill.talent.type;

import net.minecraft.world.damagesource.DamageSource;

@Deprecated(
   forRemoval = true
)
public class CarelessTalent extends DamageCancellingTalent {
   public CarelessTalent(int cost) {
      super(cost);
   }

   @Override
   protected boolean shouldCancel(DamageSource src) {
      return src == DamageSource.FLY_INTO_WALL;
   }
}
