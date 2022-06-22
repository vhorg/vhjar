package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.EyesoreEntity;
import java.util.EnumSet;
import java.util.Random;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.Goal.Flag;

public class RandomFlyGoal extends Goal {
   private final EyesoreEntity eyesore;

   public RandomFlyGoal(EyesoreEntity eyesore) {
      this.eyesore = eyesore;
      this.func_220684_a(EnumSet.of(Flag.MOVE));
   }

   public boolean func_75250_a() {
      MovementController movementcontroller = this.eyesore.func_70605_aq();
      if (!movementcontroller.func_75640_a()) {
         return true;
      } else {
         double d0 = movementcontroller.func_179917_d() - this.eyesore.func_226277_ct_();
         double d1 = movementcontroller.func_179919_e() - this.eyesore.func_226278_cu_();
         double d2 = movementcontroller.func_179918_f() - this.eyesore.func_226281_cx_();
         double d3 = d0 * d0 + d1 * d1 + d2 * d2;
         return d3 < 1.0 || d3 > 3600.0;
      }
   }

   public boolean func_75253_b() {
      return false;
   }

   public void func_75249_e() {
      Random random = this.eyesore.func_70681_au();
      double d0 = this.eyesore.func_226277_ct_() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
      double d1 = this.eyesore.func_226278_cu_() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
      double d2 = this.eyesore.func_226281_cx_() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
      this.eyesore.func_70605_aq().func_75642_a(d0, d1, d2, 1.0);
   }
}
