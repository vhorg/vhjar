package iskallia.vault.skill.talent.type;

import net.minecraft.util.DamageSource;

public class CarelessTalent extends DamageCancellingTalent {
   public CarelessTalent(int cost) {
      super(cost);
   }

   @Override
   protected boolean shouldCancel(DamageSource src) {
      return src == DamageSource.field_188406_j;
   }
}
