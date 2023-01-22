package iskallia.vault.core.vault.objective.scavenger;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ScavengerObjective;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MobScavengeTask extends ScavengeTask {
   public final double probability;
   public final ResourceLocation icon;
   public final int color;
   public final List<MobScavengeTask.Entry> entries;

   public MobScavengeTask(double probability, ResourceLocation icon, int color, MobScavengeTask.Entry... entries) {
      this.probability = probability;
      this.icon = icon;
      this.color = color;
      this.entries = Arrays.asList(entries);
   }

   @Override
   public Optional<ScavengerGoal> generateGoal(int count, RandomSource random) {
      MobScavengeTask.Entry entry = this.entries.get(random.nextInt(this.entries.size()));
      return Optional.of(new ScavengerGoal(entry.item, (int)Math.ceil(count * entry.multiplier), this.icon, this.color));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ScavengerObjective objective) {
      CommonEvents.ENTITY_DROPS
         .register(
            objective,
            event -> {
               LivingEntity entity = event.getEntityLiving();
               if (entity.level == world) {
                  if (!(world.getRandom().nextDouble() >= this.probability)) {
                     List<MobScavengeTask.Entry> matchingEntries = this.entries
                        .stream()
                        .filter(entryx -> entryx.group.contains(entity.getType().getRegistryName()))
                        .toList();
                     if (!matchingEntries.isEmpty()) {
                        MobScavengeTask.Entry entry = matchingEntries.get(world.getRandom().nextInt(matchingEntries.size()));
                        ItemStack stack = this.createStack(vault, entry.item);
                        ItemEntity item = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), stack);
                        event.getDrops().add(item);
                     }
                  }
               }
            }
         );
   }

   public static class Entry {
      public final Item item;
      public final double multiplier;
      public final Set<ResourceLocation> group;

      public Entry(Item item, double multiplier, EntityType<?>... group) {
         this.item = item;
         this.multiplier = multiplier;
         this.group = new LinkedHashSet<>(Arrays.stream(group).map(ForgeRegistryEntry::getRegistryName).toList());
      }

      public Entry(Item item, double multiplier, Set<ResourceLocation> group) {
         this.item = item;
         this.multiplier = multiplier;
         this.group = group;
      }
   }
}
