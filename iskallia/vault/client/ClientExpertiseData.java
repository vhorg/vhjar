package iskallia.vault.client;

import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.network.message.KnownExpertisesMessage;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.ExpertiseTree;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientExpertiseData {
   private static ExpertiseTree EXPERTISES = new ExpertiseTree();

   @Nonnull
   public static List<TieredSkill> getLearnedTalentNodes() {
      List<TieredSkill> talents = new ArrayList<>();
      EXPERTISES.iterate(TieredSkill.class, talent -> {
         if (talent.isUnlocked()) {
            talents.add(talent);
         }
      });
      return talents;
   }

   @Nullable
   public static TieredSkill getLearnedTalentNode(String talentName) {
      for (TieredSkill node : getLearnedTalentNodes()) {
         if (node.getId().equals(talentName)) {
            return node;
         }
      }

      return null;
   }

   public static void updateTalents(KnownExpertisesMessage pkt) {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      pkt.getTree().writeBits(buffer);
      buffer.setPosition(0);
      EXPERTISES.readBits(buffer);
   }
}
