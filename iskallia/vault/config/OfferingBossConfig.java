package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.objective.scavenger.ChestScavengerTask;
import iskallia.vault.core.vault.objective.scavenger.CoinStacksScavengerTask;
import iskallia.vault.core.vault.objective.scavenger.MobScavengerTask;
import iskallia.vault.core.vault.objective.scavenger.ScavengeTask;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class OfferingBossConfig extends Config {
   @Expose
   private List<ScavengeTask> tasks;

   @Override
   public String getName() {
      return "offering_boss";
   }

   public List<ScavengeTask> getTasks() {
      return this.tasks;
   }

   @Override
   protected void reset() {
      this.tasks = new ArrayList<>();
      this.tasks
         .add(
            new ChestScavengerTask(
               TilePredicate.of("the_vault:wooden_chest", true).orElse(PartialTile.ERROR),
               0.5,
               VaultMod.id("gui/scav/wooden_chest"),
               new WeightedList<ChestScavengerTask.Entry>().add(new ChestScavengerTask.Entry(new ItemStack(ModItems.OFFERING), 1.0, 16777215), 8)
            )
         );
      this.tasks
         .add(
            new CoinStacksScavengerTask(
               0.5,
               VaultMod.id("gui/scav/coin_stacks"),
               new WeightedList<CoinStacksScavengerTask.Entry>().add(new CoinStacksScavengerTask.Entry(new ItemStack(ModItems.OFFERING), 1.0, 16777215), 8)
            )
         );
      this.tasks
         .add(
            new MobScavengerTask(
               0.5,
               VaultMod.id("gui/scav/mob"),
               16777215,
               new MobScavengerTask.Entry(new ItemStack(ModItems.OFFERING), 1.0, EntityType.ZOMBIE, EntityType.CREEPER, EntityType.SPIDER)
            )
         );
   }
}
