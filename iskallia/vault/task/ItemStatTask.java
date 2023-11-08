package iskallia.vault.task;

import iskallia.vault.core.data.adapter.Adapters;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemStatTask extends StatTask<Item> {
   public ItemStatTask() {
      this(new StatTask.Config<>());
   }

   public ItemStatTask(StatTask.Config<Item> config) {
      super(config);
      config.adapter = Adapters.ITEM;
   }

   @Override
   protected Stat<Item> resolve(ServerStatsCounter stats) {
      StatType type = (StatType)ForgeRegistries.STAT_TYPES.getValue(((StatTask.Config)this.getConfig()).statType);
      return type.get(((StatTask.Config)this.getConfig()).value);
   }
}
