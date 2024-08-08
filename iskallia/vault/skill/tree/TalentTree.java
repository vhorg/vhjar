package iskallia.vault.skill.tree;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.KnownTalentsMessage;
import iskallia.vault.skill.base.SkillContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

public class TalentTree extends SkillTree {
   public void sync(SkillContext context) {
      this.syncTree(context);
   }

   public void syncTree(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(player -> ModNetwork.CHANNEL.sendTo(new KnownTalentsMessage(this), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
   }
}
