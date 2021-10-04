package iskallia.vault.skill.talent.type;

import net.minecraft.util.DamageSource;

public class ElvishTalent extends DamageCancellingTalent {
   public ElvishTalent(int cost) {
      super(cost);
   }

   @Override
   protected boolean shouldCancel(DamageSource src) {
      return src == DamageSource.field_76379_h;
   }
}
