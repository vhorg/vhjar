package iskallia.vault.skill.tree;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.KnownTalentsMessage;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
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

   @Override
   public Skill mergeFrom(Skill other, SkillContext context) {
      if (!(super.mergeFrom(other, context) instanceof SkillTree tree)) {
         return this;
      } else {
         ArrayList copy = new ArrayList();
         HashSet removed = new HashSet<>(this.skills.stream().map(Skill::getId).filter(Objects::nonNull).toList());

         for (Skill skill : tree.skills) {
            removed.remove(skill.getId());
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

         for (String id : removed) {
            this.getForId(id).ifPresent(skill -> {
               if (skill instanceof LearnableSkill learnable) {
                  context.setLearnPoints(context.getLearnPoints() + learnable.getLearnPointCost());
               }
            });
         }

         return this;
      }
   }
}
