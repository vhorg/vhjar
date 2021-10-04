package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;

public abstract class PlayerTalent {
   protected static final Random rand = new Random();
   @Expose
   private int cost;
   @Expose
   private int levelRequirement = 0;

   public PlayerTalent(int cost) {
      this(cost, 0);
   }

   public PlayerTalent(int cost, int levelRequirement) {
      this.cost = cost;
      this.levelRequirement = levelRequirement;
   }

   public int getCost() {
      return this.cost;
   }

   public int getLevelRequirement() {
      return this.levelRequirement;
   }

   public void onAdded(PlayerEntity player) {
   }

   public void tick(PlayerEntity player) {
   }

   public void onRemoved(PlayerEntity player) {
   }
}
