package iskallia.vault.skill.talent.type;

import net.minecraft.entity.player.PlayerEntity;

public class ElvishTalent extends PlayerTalent {
   public ElvishTalent(int cost) {
      super(cost);
   }

   @Override
   public void tick(PlayerEntity player) {
      player.field_70143_R = 0.0F;
   }
}
