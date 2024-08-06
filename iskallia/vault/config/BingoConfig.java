package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.task.BingoTask;
import iskallia.vault.task.Task;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public class BingoConfig extends Config {
   @Expose
   public Map<ResourceLocation, LevelEntryList<BingoTask>> pools;

   @Override
   public String getName() {
      return "bingo";
   }

   public Optional<BingoTask> generate(ResourceLocation pool, int level) {
      return !this.pools.containsKey(pool) ? Optional.empty() : this.pools.get(pool).getForLevel(level).map(Task::copy);
   }

   @Override
   protected void reset() {
      this.pools = new LinkedHashMap<>();
   }
}
