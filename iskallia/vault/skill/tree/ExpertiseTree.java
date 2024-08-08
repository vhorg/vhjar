package iskallia.vault.skill.tree;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.KnownExpertisesMessage;
import iskallia.vault.skill.base.SkillContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

public class ExpertiseTree extends SkillTree {
   public void sync(SkillContext context) {
      this.syncTree(context);
   }

   public void syncTree(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(player -> ModNetwork.CHANNEL.sendTo(new KnownExpertisesMessage(this), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
   }
}
