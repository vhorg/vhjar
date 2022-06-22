package iskallia.vault.skill.talent.type;

import iskallia.vault.Vault;
import net.minecraft.entity.player.PlayerEntity;

public class AngelTalent extends PlayerTalent {
   public AngelTalent(int cost) {
      super(cost);
   }

   @Override
   public void tick(PlayerEntity player) {
      if (player.field_70170_p.func_234923_W_() == Vault.VAULT_KEY && !player.func_175149_v() && !player.func_184812_l_()) {
         player.field_71075_bZ.field_75101_c = false;
         player.field_71075_bZ.field_75100_b = false;
      } else if (!player.field_71075_bZ.field_75101_c) {
         player.field_71075_bZ.field_75101_c = true;
      }

      player.func_71016_p();
   }

   @Override
   public void onRemoved(PlayerEntity player) {
      if (!player.func_175149_v()) {
         player.field_71075_bZ.field_75101_c = false;
         player.field_71075_bZ.field_75100_b = false;
         player.func_71016_p();
      }
   }
}
