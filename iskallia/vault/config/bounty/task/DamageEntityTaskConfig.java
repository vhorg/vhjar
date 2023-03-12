package iskallia.vault.config.bounty.task;

import iskallia.vault.VaultMod;
import iskallia.vault.bounty.task.properties.DamageProperties;
import iskallia.vault.config.bounty.task.entry.GenericEntry;
import iskallia.vault.config.bounty.task.entry.TaskEntry;
import iskallia.vault.config.entry.IntRangeEntry;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class DamageEntityTaskConfig extends TaskConfig<TaskEntry<ResourceLocation>, DamageProperties> {
   @Override
   public String getName() {
      return super.getName() + "damage_entity";
   }

   public DamageProperties getGeneratedTaskProperties(int vaultLevel) {
      TaskEntry<ResourceLocation> taskEntry = this.getEntry(vaultLevel);
      GenericEntry<ResourceLocation> entry = taskEntry.getRandom();
      DamageProperties damageProperties = new DamageProperties(
         entry.getValue(), new ArrayList<>(entry.getValidDimensions()), entry.isVaultOnly(), entry.getRandomAmount()
      );
      damageProperties.setRewardPool(entry.getRewardPool());
      return damageProperties;
   }

   @Override
   protected TaskEntry<ResourceLocation> generateConfigEntry() {
      return new TaskEntry<>(
            new GenericEntry<>(ForgeRegistries.ENTITIES.getKey(EntityType.SKELETON), new IntRangeEntry(100, 500))
               .floorToNearestTen()
               .setValidDimensions(Set.of(VaultMod.id("vault")))
               .vaultOnly(),
            3
         )
         .addEntry(
            new GenericEntry<>(ForgeRegistries.ENTITIES.getKey(EntityType.ZOMBIE), new IntRangeEntry(100, 500))
               .floorToNearestTen()
               .setValidDimensions(Set.of(VaultMod.id("vault")))
               .vaultOnly(),
            3
         )
         .addEntry(
            new GenericEntry<>(ForgeRegistries.ENTITIES.getKey(EntityType.CREEPER), new IntRangeEntry(100, 500))
               .floorToNearestTen()
               .setValidDimensions(Set.of(Level.OVERWORLD.location(), Level.NETHER.location())),
            3
         );
   }
}
