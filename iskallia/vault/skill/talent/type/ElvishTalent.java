package iskallia.vault.skill.talent.type;

import net.minecraft.world.damagesource.DamageSource;

@Deprecated(
   forRemoval = true
)
public class ElvishTalent extends DamageCancellingTalent {
   public ElvishTalent(int cost) {
      super(cost);
   }

   @Override
   protected boolean shouldCancel(DamageSource src) {
      return src == DamageSource.FALL;
   }
}
