package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

@Deprecated(
   forRemoval = true
)
public class ArtisanTalent extends PlayerTalent {
   @Expose
   private final String defaultRoll;

   public ArtisanTalent(int cost, String defaultRoll) {
      super(cost);
      this.defaultRoll = defaultRoll;
   }

   public String getDefaultRoll() {
      return this.defaultRoll;
   }
}
