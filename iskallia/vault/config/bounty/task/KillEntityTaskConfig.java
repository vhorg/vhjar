package iskallia.vault.config.bounty.task;

import iskallia.vault.bounty.task.properties.KillEntityProperties;
import iskallia.vault.config.bounty.task.entry.GenericEntry;
import iskallia.vault.config.bounty.task.entry.TaskEntry;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class KillEntityTaskConfig extends TaskConfig<TaskEntry<EntityPredicate>, KillEntityProperties> {
   @Override
   public String getName() {
      return super.getName() + "kill_entity";
   }

   public KillEntityProperties getGeneratedTaskProperties(int vaultLevel) {
      TaskEntry<EntityPredicate> taskEntry = this.getEntry(vaultLevel);
      GenericEntry<EntityPredicate> entry = taskEntry.getRandom();
      KillEntityProperties killEntityProperties = new KillEntityProperties(
         EntityPredicate.of(entry.getValue().toString(), true).orElse(EntityPredicate.FALSE),
         new ArrayList<>(entry.getValidDimensions()),
         entry.isVaultOnly(),
         entry.getRandomAmount()
      );
      killEntityProperties.setRewardPool(entry.getRewardPool());
      return killEntityProperties;
   }

   @Override
   protected TaskEntry<EntityPredicate> generateConfigEntry() {
      EntityPredicate skeleton = EntityPredicate.of(ForgeRegistries.ENTITIES.getKey(EntityType.SKELETON).toString(), true).orElse(EntityPredicate.FALSE);
      EntityPredicate zombie = EntityPredicate.of(ForgeRegistries.ENTITIES.getKey(EntityType.ZOMBIE).toString(), true).orElse(EntityPredicate.FALSE);
      EntityPredicate creeper = EntityPredicate.of(ForgeRegistries.ENTITIES.getKey(EntityType.CREEPER).toString(), true).orElse(EntityPredicate.FALSE);
      return new TaskEntry<>(
            new GenericEntry<>(skeleton, new IntRangeEntry(10, 50)).setValidDimensions(Set.of(Level.OVERWORLD.location(), Level.NETHER.location())), 3
         )
         .addEntry(new GenericEntry<>(zombie, new IntRangeEntry(10, 50)).setValidDimensions(Set.of(Level.OVERWORLD.location(), Level.NETHER.location())), 3)
         .addEntry(new GenericEntry<>(creeper, new IntRangeEntry(10, 50)).setValidDimensions(Set.of(Level.OVERWORLD.location(), Level.NETHER.location())), 3);
   }
}
