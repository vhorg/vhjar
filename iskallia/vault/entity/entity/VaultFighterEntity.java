package iskallia.vault.entity.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class VaultFighterEntity extends FighterEntity {
   public VaultFighterEntity(EntityType<? extends Zombie> type, Level world) {
      super(type, world);
   }
}
