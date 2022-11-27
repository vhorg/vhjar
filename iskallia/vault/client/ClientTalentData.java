package iskallia.vault.client;

import iskallia.vault.network.message.KnownTalentsMessage;
import iskallia.vault.skill.talent.Talent;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientTalentData {
   private static List<TalentNode<?>> learnedTalents = new ArrayList<>();

   @Nonnull
   public static List<TalentNode<?>> getLearnedTalentNodes() {
      return Collections.unmodifiableList(learnedTalents);
   }

   @Nullable
   public static <T extends Talent> TalentNode<T> getLearnedTalentNode(TalentGroup<T> talent) {
      return getLearnedTalentNode(talent.getParentName());
   }

   @Nullable
   public static <T extends Talent> TalentNode<T> getLearnedTalentNode(String talentName) {
      for (TalentNode<?> node : getLearnedTalentNodes()) {
         if (node.getGroup().getParentName().equals(talentName)) {
            return (TalentNode<T>)node;
         }
      }

      return null;
   }

   public static void updateTalents(KnownTalentsMessage pkt) {
      learnedTalents = pkt.getLearnedTalents();
   }
}
