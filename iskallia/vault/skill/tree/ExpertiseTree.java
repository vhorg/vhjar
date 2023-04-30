package iskallia.vault.skill.tree;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.KnownExpertisesMessage;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import java.util.ArrayList;
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

   @Override
   public Skill mergeFrom(Skill other, SkillContext context) {
      if (super.mergeFrom(other, context) instanceof ExpertiseTree tree) {
         ArrayList copy = new ArrayList();

         for (Skill skill : tree.skills) {
            Skill merging = this.getForId(skill.getId()).orElse(null);
            Skill merged;
            if (merging != null) {
               merged = merging.mergeFrom(skill, context);
            } else {
               merged = skill;
            }

            if (merged != null) {
               merged.setParent(this);
               copy.add(merged);
            }
         }

         this.skills = copy;
         return this;
      } else {
         return this;
      }
   }
}
