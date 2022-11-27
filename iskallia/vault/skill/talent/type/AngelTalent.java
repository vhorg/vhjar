package iskallia.vault.skill.talent.type;

import iskallia.vault.util.VHSmpUtil;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class AngelTalent extends PlayerTalent {
   public AngelTalent(int cost) {
      super(cost);
   }

   @Override
   public void tick(ServerPlayer player) {
      if ((ServerVaults.isVaultWorld(player.level) || VHSmpUtil.isArenaWorld(player.level)) && !player.isSpectator() && !player.isCreative()) {
         player.getAbilities().mayfly = false;
         player.getAbilities().flying = false;
      } else if (!player.getAbilities().mayfly) {
         player.getAbilities().mayfly = true;
      }

      player.onUpdateAbilities();
   }

   @Override
   public void onRemoved(Player player) {
      if (!player.isSpectator() && !player.isCreative()) {
         player.getAbilities().mayfly = false;
         player.getAbilities().flying = false;
         player.onUpdateAbilities();
      }
   }
}
