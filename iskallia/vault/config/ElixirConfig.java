package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.objective.elixir.ChestElixirTask;
import iskallia.vault.core.vault.objective.elixir.CoinStacksElixirTask;
import iskallia.vault.core.vault.objective.elixir.ElixirTask;
import iskallia.vault.core.vault.objective.elixir.MobElixirTask;
import iskallia.vault.core.vault.objective.elixir.OreElixirTask;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.core.world.roll.IntRoll;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ElixirConfig extends Config {
   @Expose
   private Map<Integer, Integer> elixirToSize;
   @Expose
   private Map<ResourceLocation, List<ResourceLocation>> mobGroups;
   @Expose
   private LevelEntryList<ElixirConfig.Entry> entries;

   @Override
   public String getName() {
      return "elixir";
   }

   @Override
   protected void reset() {
      this.elixirToSize = new LinkedHashMap<>();

      for (int i = 0; i < 11; i++) {
         this.elixirToSize.put(i, i);
      }

      this.mobGroups = new LinkedHashMap<>();
      this.mobGroups.put(new ResourceLocation("main"), Arrays.asList(new ResourceLocation("zombie"), new ResourceLocation("skeleton")));
      this.entries = new LevelEntryList<>();
      List<ElixirTask.Config<?>> tasks = new ArrayList<>();
      tasks.add(new ChestElixirTask.Config(new WeightedList<IntRoll>().add(IntRoll.ofUniform(1, 10), 1), VaultChestType.WOODEN));
      tasks.add(new ChestElixirTask.Config(new WeightedList<IntRoll>().add(IntRoll.ofUniform(1, 10), 1), VaultChestType.LIVING));
      tasks.add(new ChestElixirTask.Config(new WeightedList<IntRoll>().add(IntRoll.ofUniform(1, 10), 1), VaultChestType.GILDED));
      tasks.add(new ChestElixirTask.Config(new WeightedList<IntRoll>().add(IntRoll.ofUniform(1, 10), 1), VaultChestType.ORNATE));
      tasks.add(new CoinStacksElixirTask.Config(new WeightedList<IntRoll>().add(IntRoll.ofUniform(1, 10), 1)));
      tasks.add(new OreElixirTask.Config(new WeightedList<IntRoll>().add(IntRoll.ofUniform(1, 10), 1)));
      tasks.add(new MobElixirTask.Config(new WeightedList<IntRoll>().add(IntRoll.ofUniform(1, 10), 1), new ResourceLocation("main")));
      this.entries.add(new ElixirConfig.Entry(0, new WeightedList<IntRoll>().add(IntRoll.ofUniform(80, 100), 1), tasks));
   }

   public int getSize(int elixir) {
      List<Map.Entry<Integer, Integer>> list = new ArrayList<>(this.elixirToSize.entrySet());

      for (int i = list.size() - 1; i >= 0; i--) {
         if (list.get(i).getKey() <= elixir) {
            return list.get(i).getValue();
         }
      }

      return 0;
   }

   public boolean isEntityInGroup(Entity entity, ResourceLocation group) {
      return this.mobGroups.containsKey(group) && this.mobGroups.get(group).contains(entity.getType().getRegistryName());
   }

   public int generateTarget(int level, RandomSource random) {
      ElixirConfig.Entry entry = this.entries.getForLevel(level).orElse(null);
      return entry == null ? 0 : entry.getTarget().getRandom(random).map(roll -> roll.get(random)).orElse(0);
   }

   public List<ElixirTask> generateGoals(int level, RandomSource random) {
      ElixirConfig.Entry entry = this.entries.getForLevel(level).orElse(null);
      return (List<ElixirTask>)(entry == null
         ? new ArrayList<>()
         : entry.getTasks().stream().map(config -> config.generate(random)).collect(Collectors.toList()));
   }

   public static class Entry implements LevelEntryList.ILevelEntry {
      @Expose
      private int level;
      @Expose
      private WeightedList<IntRoll> target;
      @Expose
      private List<ElixirTask.Config<?>> tasks;

      public Entry(int level, WeightedList<IntRoll> target, List<ElixirTask.Config<?>> tasks) {
         this.level = level;
         this.target = target;
         this.tasks = tasks;
      }

      @Override
      public int getLevel() {
         return this.level;
      }

      public WeightedList<IntRoll> getTarget() {
         return this.target;
      }

      public List<ElixirTask.Config<?>> getTasks() {
         return this.tasks;
      }
   }
}