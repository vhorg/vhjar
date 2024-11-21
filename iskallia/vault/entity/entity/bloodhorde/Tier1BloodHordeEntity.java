package iskallia.vault.entity.entity.bloodhorde;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class Tier1BloodHordeEntity extends BloodHordeEntity {
   public Tier1BloodHordeEntity(EntityType<? extends Zombie> type, Level world) {
      super(type, world);
   }
}
