package iskallia.vault.client;

import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.network.message.KnownTalentsMessage;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.TalentTree;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientTalentData {
   private static TalentTree TALENTS = new TalentTree();

   @Nonnull
   public static List<TieredSkill> getLearnedTalentNodes() {
      List<TieredSkill> talents = new ArrayList<>();
      TALENTS.iterate(TieredSkill.class, talent -> {
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

   public static void updateTalents(KnownTalentsMessage pkt) {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      pkt.getTree().writeBits(buffer);
      buffer.setPosition(0);
      TALENTS.readBits(buffer);
   }
}
