package iskallia.vault.entity.entity.mushroom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class Tier5MushroomEntity extends MushroomEntity {
   public Tier5MushroomEntity(EntityType<? extends Monster> entityType, Level world) {
      super(entityType, world);
   }

   @Override
   public int getTier() {
      return 5;
   }
}
