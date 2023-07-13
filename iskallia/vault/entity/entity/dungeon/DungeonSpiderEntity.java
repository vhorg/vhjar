package iskallia.vault.entity.entity.dungeon;

import iskallia.vault.entity.entity.VaultSpiderEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class DungeonSpiderEntity extends VaultSpiderEntity {
   public DungeonSpiderEntity(EntityType<? extends VaultSpiderEntity> entityType, Level world) {
      super(entityType, world);
   }
}
