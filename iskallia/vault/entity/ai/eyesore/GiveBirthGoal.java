package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.EyesoreEntity;
import net.minecraft.entity.ai.goal.Goal;

public class GiveBirthGoal extends Goal {
   private final EyesoreEntity eyesore;
   private int birthTicks;

   public GiveBirthGoal(EyesoreEntity eyesore) {
      this.eyesore = eyesore;
   }

   public boolean func_75250_a() {
      return this.eyesore.getState() == EyesoreEntity.State.NORMAL;
   }

   public boolean func_75253_b() {
      return this.birthTicks > 0;
   }

   public boolean func_220685_C_() {
      return false;
   }

   public void func_75249_e() {
      this.eyesore.setState(EyesoreEntity.State.GIVING_BIRTH);
      this.birthTicks = 200;
   }

   public void func_75246_d() {
      super.func_75246_d();
      if (this.birthTicks % 20 == 0) {
         System.out.println("Give birth to one random lil dude here!");
      }

      this.birthTicks--;
      if (this.birthTicks <= 0) {
         this.eyesore.setState(EyesoreEntity.State.NORMAL);
      }
   }
}
