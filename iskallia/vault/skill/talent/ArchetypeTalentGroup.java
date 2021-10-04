package iskallia.vault.skill.talent;

import iskallia.vault.skill.talent.type.PlayerTalent;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class ArchetypeTalentGroup<T extends PlayerTalent> extends TalentGroup<T> {
   public ArchetypeTalentGroup(String name, T... levels) {
      super(name, levels);
   }

   public static <T extends PlayerTalent> ArchetypeTalentGroup<T> of(String name, int maxLevel, IntFunction<T> supplier) {
      PlayerTalent[] talents = IntStream.range(0, maxLevel).mapToObj(supplier).toArray(PlayerTalent[]::new);
      return new ArchetypeTalentGroup<>(name, (T[])talents);
   }
}
