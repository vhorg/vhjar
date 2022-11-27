package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.Talent;
import java.util.Random;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class PlayerTalent implements Talent {
   protected static final Random rand = new Random();
   @Expose
   private int cost;
   @Expose
   private int regretCost;
   @Expose
   private int levelRequirement;

   public PlayerTalent(int cost) {
      this(cost, 0);
   }

   public PlayerTalent(int cost, int levelRequirement) {
      this.cost = cost;
      this.regretCost = cost;
      this.levelRequirement = levelRequirement;
   }

   @Override
   public int getLearningCost() {
      return this.cost;
   }

   @Override
   public int getRegretCost() {
      return this.regretCost;
   }

   @Override
   public int getLevelRequirement() {
      return this.levelRequirement;
   }

   @Override
   public void onAdded(Player player) {
   }

   @Override
   public void tick(ServerPlayer player) {
   }

   @Override
   public void onRemoved(Player player) {
   }
}
