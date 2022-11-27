package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.entity.eyesore.EyesoreEntity;
import java.util.EnumSet;
import java.util.Random;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class RandomFlyGoal extends Goal {
   private final EyesoreEntity eyesore;

   public RandomFlyGoal(EyesoreEntity eyesore) {
      this.eyesore = eyesore;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      MoveControl movementcontroller = this.eyesore.getMoveControl();
      if (!movementcontroller.hasWanted()) {
         return true;
      } else {
         double d0 = movementcontroller.getWantedX() - this.eyesore.getX();
         double d1 = movementcontroller.getWantedY() - this.eyesore.getY();
         double d2 = movementcontroller.getWantedZ() - this.eyesore.getZ();
         double d3 = d0 * d0 + d1 * d1 + d2 * d2;
         return d3 < 1.0 || d3 > 3600.0;
      }
   }

   public boolean canContinueToUse() {
      return false;
   }

   public void start() {
      Random random = this.eyesore.getRandom();
      double d0 = this.eyesore.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
      double d1 = this.eyesore.getY() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
      double d2 = this.eyesore.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
      this.eyesore.getMoveControl().setWantedPosition(d0, d1, d2, 1.0);
   }
}
