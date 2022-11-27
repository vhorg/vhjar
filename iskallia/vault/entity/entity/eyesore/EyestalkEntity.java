package iskallia.vault.entity.entity.eyesore;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.Level;

public class EyestalkEntity extends Vex {
   public EyestalkEntity(EntityType<? extends Vex> p_i50190_1_, Level p_i50190_2_) {
      super(p_i50190_1_, p_i50190_2_);
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 100.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 3.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
         .add(Attributes.ARMOR, 2.0);
   }
}
