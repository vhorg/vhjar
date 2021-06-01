package iskallia.vault.skill.talent.type;

import net.minecraft.entity.player.PlayerEntity;

public class AngelTalent extends PlayerTalent {
   public AngelTalent(int cost) {
      super(cost);
   }

   @Override
   public void tick(PlayerEntity player) {
      if (!player.field_71075_bZ.field_75101_c) {
         player.field_71075_bZ.field_75101_c = true;
      }

      player.func_71016_p();
   }

   @Override
   public void onRemoved(PlayerEntity player) {
      player.field_71075_bZ.field_75101_c = false;
      player.func_71016_p();
   }
}
