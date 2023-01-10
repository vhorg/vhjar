package iskallia.vault.config.bounty.task;

import iskallia.vault.bounty.task.properties.KillEntityProperties;
import iskallia.vault.config.bounty.task.entry.GenericEntry;
import iskallia.vault.config.bounty.task.entry.TaskEntry;
import iskallia.vault.config.entry.RangeEntry;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class KillEntityTaskConfig extends TaskConfig<TaskEntry<ResourceLocation>, KillEntityProperties> {
   @Override
   public String getName() {
      return super.getName() + "kill_entity";
   }

   public KillEntityProperties getGeneratedTaskProperties(int vaultLevel) {
      TaskEntry<ResourceLocation> taskEntry = this.getEntry(vaultLevel);
      GenericEntry<ResourceLocation> entry = taskEntry.getRandom();
      KillEntityProperties killEntityProperties = new KillEntityProperties(
         entry.getValue(), new ArrayList<>(entry.getValidDimensions()), entry.isVaultOnly(), entry.getRandomAmount()
      );
      killEntityProperties.setRewardPool(entry.getRewardPool());
      return killEntityProperties;
   }

   @Override
   protected TaskEntry<ResourceLocation> generateConfigEntry() {
      return new TaskEntry<>(
            new GenericEntry<>(ForgeRegistries.ENTITIES.getKey(EntityType.SKELETON), new RangeEntry(10, 50))
               .setValidDimensions(Set.of(Level.OVERWORLD.location(), Level.NETHER.location())),
            3
         )
         .addEntry(
            new GenericEntry<>(ForgeRegistries.ENTITIES.getKey(EntityType.ZOMBIE), new RangeEntry(10, 50))
               .setValidDimensions(Set.of(Level.OVERWORLD.location(), Level.NETHER.location())),
            3
         )
         .addEntry(
            new GenericEntry<>(ForgeRegistries.ENTITIES.getKey(EntityType.CREEPER), new RangeEntry(10, 50))
               .setValidDimensions(Set.of(Level.OVERWORLD.location(), Level.NETHER.location())),
            3
         );
   }
}
