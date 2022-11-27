package iskallia.vault.client.gui.helper;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class ArenaScoreboardContainer {
   public List<ArenaScoreboardContainer.ScoreboardEntry> scoreboard = new LinkedList<>();

   public String getMVP() {
      return this.scoreboard.size() == 0 ? null : this.scoreboard.get(0).nickname;
   }

   public List<ArenaScoreboardContainer.ScoreboardEntry> getTop(int n) {
      return (List<ArenaScoreboardContainer.ScoreboardEntry>)(this.scoreboard.size() == 0
         ? new LinkedList<>()
         : this.scoreboard.subList(0, Math.min(n, this.scoreboard.size())));
   }

   public int getSize() {
      return this.scoreboard.size();
   }

   public void onDamageDealt(String nickname, float damageDealt) {
      int index = IntStream.range(0, this.scoreboard.size()).filter(i -> this.scoreboard.get(i).nickname.equals(nickname)).findFirst().orElse(-1);
      if (index == -1) {
         this.scoreboard.add(new ArenaScoreboardContainer.ScoreboardEntry(nickname, damageDealt));
      } else {
         ArenaScoreboardContainer.ScoreboardEntry entry = this.scoreboard.get(index);
         entry.totalDamage += damageDealt;
      }

      this.scoreboard.sort(Comparator.comparingDouble(o -> -o.totalDamage));
   }

   public void reset() {
      this.scoreboard.clear();
   }

   public static class ScoreboardEntry {
      public final String nickname;
      public float totalDamage;

      public ScoreboardEntry(String nickname, float totalDamage) {
         this.nickname = nickname;
         this.totalDamage = totalDamage;
      }

      @Override
      public String toString() {
         return String.format("%s=%f", this.nickname, this.totalDamage);
      }
   }
}
