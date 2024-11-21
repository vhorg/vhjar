package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.TeamTaskManager;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.data.tile.OrTilePredicate;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.roll.FloatRoll;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.task.BingoObjectiveTask;
import iskallia.vault.task.CakeObjectiveTask;
import iskallia.vault.task.CollectionTask;
import iskallia.vault.task.DealDamageTask;
import iskallia.vault.task.FinishVaultTask;
import iskallia.vault.task.InteractBlockTask;
import iskallia.vault.task.KillEntityTask;
import iskallia.vault.task.LootChestTask;
import iskallia.vault.task.MineBlockTask;
import iskallia.vault.task.MultiVaultTask;
import iskallia.vault.task.NodeTask;
import iskallia.vault.task.ProgressConfiguredTask;
import iskallia.vault.task.RepeatedTask;
import iskallia.vault.task.SingleVaultTask;
import iskallia.vault.task.TakeDamageTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.VaultTimedTask;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.counter.TaskCounterPredicate;
import iskallia.vault.task.renderer.TeamRenderer;
import iskallia.vault.task.util.DamagePhase;
import iskallia.vault.task.util.VaultListenerMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class TeamTasksConfig extends Config {
   public static final String RECLAIM_SUFFIX = "_reclaim_";
   private static final Pattern RECLAIM_PATTERN = Pattern.compile(".*_reclaim_\\d+$");
   @Expose
   public List<Task> tasks;
   @Expose
   public int width;
   @Nullable
   private Map<String, Task> taskMap = null;

   @Override
   public String getName() {
      return "team_tasks";
   }

   @Override
   protected void reset() {
      this.tasks = new ArrayList<>();
      TeamRenderer teamRenderer = new TeamRenderer();
      teamRenderer.name = "Submit 3000 Platinum";
      teamRenderer.stack = new ItemStack(ModBlocks.VAULT_PLATINUM);
      this.tasks
         .add(
            new CollectionTask(
                  new CollectionTask.Config(ItemPredicate.of(ModBlocks.VAULT_PLATINUM.getRegistryName().toString(), true).orElse(null)),
                  TaskCounter.ofTargetInt(IntRoll.ofConstant(3000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
               )
               .<Task>setId("submit_3000_platinum")
               .setRenderer(teamRenderer)
         );
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Submit 500 Companion Gemstones";
      Item mysticGemstone = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("vhsmp", "mystic_gemstone"));
      teamRenderer.stack = new ItemStack(mysticGemstone != null ? mysticGemstone : Items.STICK);
      this.tasks
         .add(
            new CollectionTask(
                  new CollectionTask.Config(ItemPredicate.of("vhsmp:mystic_gemstone", true).orElse(null)),
                  TaskCounter.ofTargetInt(IntRoll.ofConstant(500), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
               )
               .<Task>setId("submit_500_companion_gemstones")
               .setRenderer(teamRenderer)
         );
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Submit 1,000 Knowledge Stars";
      teamRenderer.stack = new ItemStack(ModItems.KNOWLEDGE_STAR);
      this.tasks
         .add(
            new CollectionTask(
                  new CollectionTask.Config(ItemPredicate.of(ModItems.KNOWLEDGE_STAR.getRegistryName().toString(), true).orElse(null)),
                  TaskCounter.ofTargetInt(IntRoll.ofConstant(1000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
               )
               .<Task>setId("submit_1000_knowledge_stars")
               .setRenderer(teamRenderer)
         );
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Submit 100,000 Larimar";
      teamRenderer.stack = new ItemStack(ModItems.LARIMAR_GEM);
      this.tasks
         .add(
            new CollectionTask(
                  new CollectionTask.Config(ItemPredicate.of(ModItems.LARIMAR_GEM.getRegistryName().toString(), true).orElse(null)),
                  TaskCounter.ofTargetInt(IntRoll.ofConstant(100000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
               )
               .<Task>setId("submit_100000_larimar")
               .setRenderer(teamRenderer)
         );
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Submit 50,000 Vault Scrap";
      teamRenderer.stack = new ItemStack(ModItems.VAULT_SCRAP);
      this.tasks
         .add(
            new CollectionTask(
                  new CollectionTask.Config(ItemPredicate.of(ModItems.VAULT_SCRAP.getRegistryName().toString(), true).orElse(null)),
                  TaskCounter.ofTargetInt(IntRoll.ofConstant(50000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
               )
               .<Task>setId("submit_50000_vault_scrap")
               .setRenderer(teamRenderer)
         );
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Submit 10,000 Vault Diamonds";
      teamRenderer.stack = new ItemStack(ModItems.VAULT_DIAMOND);
      this.tasks
         .add(
            new CollectionTask(
                  new CollectionTask.Config(ItemPredicate.of(ModItems.VAULT_DIAMOND.getRegistryName().toString(), true).orElse(null)),
                  TaskCounter.ofTargetInt(IntRoll.ofConstant(10000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
               )
               .<Task>setId("submit_10000_vault_diamonds")
               .setRenderer(teamRenderer)
         );
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Submit 2,500 POGs";
      teamRenderer.stack = new ItemStack(ModItems.POG);
      this.tasks
         .add(
            new CollectionTask(
                  new CollectionTask.Config(ItemPredicate.of(ModItems.POG.getRegistryName().toString(), true).orElse(null)),
                  TaskCounter.ofTargetInt(IntRoll.ofConstant(2500), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
               )
               .<Task>setId("submit_2500_pogs")
               .setRenderer(teamRenderer)
         );
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Submit 1,000 Echo Gems";
      teamRenderer.stack = new ItemStack(ModItems.ECHO_GEM);
      this.tasks
         .add(
            new CollectionTask(
                  new CollectionTask.Config(ItemPredicate.of(ModItems.ECHO_GEM.getRegistryName().toString(), true).orElse(null)),
                  TaskCounter.ofTargetInt(IntRoll.ofConstant(1000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
               )
               .<Task>setId("submit_1000_echo_gems")
               .setRenderer(teamRenderer)
         );
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Loot 12,500 Coin Piles in Vaults";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/lootcoins.png");
      MultiVaultTask root = new MultiVaultTask(VaultListenerMode.ALL);
      root.setId("loot_12500_coin_piles");
      root.setRenderer(teamRenderer);
      root.addChildren(
         new MineBlockTask[]{
            new MineBlockTask(
               new MineBlockTask.Config(TilePredicate.of("the_vault:coin_pile[size=1]", true).orElseThrow()),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(12500), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      this.tasks.add(root);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Loot 25,000 Wooden Chests";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/wooden.png");
      root = new MultiVaultTask(VaultListenerMode.ALL);
      root.setId("loot_25000_wooden_chests");
      root.setRenderer(teamRenderer);
      root.addChildren(
         new LootChestTask[]{
            new LootChestTask(
               new LootChestTask.Config(TilePredicate.of("@the_vault:wooden", true).orElseThrow()),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(25000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      this.tasks.add(root);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Loot 10,000 Living Chests";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/living.png");
      root = new MultiVaultTask(VaultListenerMode.ALL);
      root.setId("loot_10000_living_chests");
      root.setRenderer(teamRenderer);
      root.addChildren(
         new LootChestTask[]{
            new LootChestTask(
               new LootChestTask.Config(TilePredicate.of("@the_vault:living", true).orElseThrow()),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(10000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      this.tasks.add(root);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Loot 10,000 Ornate Chests";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/ornate.png");
      root = new MultiVaultTask(VaultListenerMode.ALL);
      root.setId("loot_10000_ornate_chests");
      root.setRenderer(teamRenderer);
      root.addChildren(
         new LootChestTask[]{
            new LootChestTask(
               new LootChestTask.Config(TilePredicate.of("@the_vault:ornate", true).orElseThrow()),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(10000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      this.tasks.add(root);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Loot 10,000 Gilded Chests";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/gilded.png");
      root = new MultiVaultTask(VaultListenerMode.ALL);
      root.setId("loot_10000_gilded_chests");
      root.setRenderer(teamRenderer);
      root.addChildren(
         new LootChestTask[]{
            new LootChestTask(
               new LootChestTask.Config(TilePredicate.of("@the_vault:gilded", true).orElseThrow()),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(10000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      this.tasks.add(root);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Kill 500 Champions";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/killchampion.png");
      root = new MultiVaultTask(VaultListenerMode.ALL);
      root.setId("kill_500_champions");
      root.setRenderer(teamRenderer);
      root.addChildren(
         new KillEntityTask[]{
            new KillEntityTask(
               new KillEntityTask.Config(EntityPredicate.of("@the_vault:mob_type/champion", true).orElseThrow()),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(500), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      this.tasks.add(root);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Kill 50,000 Horde Mobs";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/killhorde.png");
      root = new MultiVaultTask(VaultListenerMode.ALL);
      root.setId("kill_50000_horde_mobs");
      root.setRenderer(teamRenderer);
      root.addChildren(
         new KillEntityTask[]{
            new KillEntityTask(
               new KillEntityTask.Config(EntityPredicate.of("@the_vault:horde", true).orElseThrow()),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(50000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      this.tasks.add(root);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Kill 2,000 Dungeon Mobs";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/killdungeon.png");
      root = new MultiVaultTask(VaultListenerMode.ALL);
      root.setId("kill_2000_dungeon_mobs");
      root.setRenderer(teamRenderer);
      root.addChildren(
         new KillEntityTask[]{
            new KillEntityTask(
               new KillEntityTask.Config(EntityPredicate.of("@the_vault:dungeon", true).orElseThrow()),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(2000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      this.tasks.add(root);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Kill 2,000 Tanks";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/killtank.png");
      root = new MultiVaultTask(VaultListenerMode.ALL);
      root.setId("kill_2000_tanks");
      root.setRenderer(teamRenderer);
      root.addChildren(
         new KillEntityTask[]{
            new KillEntityTask(
               new KillEntityTask.Config(EntityPredicate.of("@the_vault:tank", true).orElseThrow()),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(2000), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      this.tasks.add(root);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Open 100 Treasure Doors";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/treasure_doors.png");
      this.tasks
         .add(
            new InteractBlockTask(
                  new InteractBlockTask.Config(
                     TilePredicate.of("the_vault:treasure_door[open=false]", true).orElseThrow(),
                     TilePredicate.of("the_vault:treasure_door[open=true]", true).orElseThrow()
                  ),
                  TaskCounter.ofTargetInt(IntRoll.ofConstant(100), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
               )
               .<Task>setId("open_100_treasure_doors")
               .setRenderer(teamRenderer)
         );
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Complete 3 Vaults killing more than 4,000 mobs";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/rapidhorde.png");
      RepeatedTask repeatedTask = new RepeatedTask(
         new RepeatedTask.Config(), TaskCounter.ofTargetInt(IntRoll.ofConstant(3), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
      );
      repeatedTask.setId("complete_3_vaults_killing_4000_mobs");
      repeatedTask.setRenderer(teamRenderer);
      SingleVaultTask singleVaultTask = new SingleVaultTask(VaultListenerMode.OWNER);
      singleVaultTask.addChildren(
         new ProgressConfiguredTask[]{
            new KillEntityTask(
               new KillEntityTask.Config(EntityPredicate.of("@the_vault:mobs", true).orElseThrow()),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(4001), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            ),
            new FinishVaultTask(
               new FinishVaultTask.Config(Set.of(Completion.COMPLETED), null),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(1), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      repeatedTask.addChildren(new SingleVaultTask[]{singleVaultTask});
      this.tasks.add(repeatedTask);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Complete a cake vault eating at least 100 cakes";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/cake_layer.png");
      singleVaultTask = new SingleVaultTask(VaultListenerMode.OWNER);
      repeatedTask = new RepeatedTask(new RepeatedTask.Config(), TaskCounter.ofTargetInt(IntRoll.ofConstant(1), TaskCounterPredicate.GREATER_OR_EQUAL_TO));
      repeatedTask.setId("complete_cake_vault_eating_100_cakes");
      repeatedTask.setRenderer(teamRenderer);
      singleVaultTask.addChildren(
         new CakeObjectiveTask[]{new CakeObjectiveTask(TaskCounter.ofTargetInt(IntRoll.ofConstant(100), TaskCounterPredicate.GREATER_OR_EQUAL_TO))}
      );
      repeatedTask.addChildren(new SingleVaultTask[]{singleVaultTask});
      this.tasks.add(repeatedTask);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Complete 3 Vaults looting more than 2,500 chests";
      teamRenderer.icon = VaultMod.id("textures/gui/bingo/wooden.png");
      repeatedTask = new RepeatedTask(new RepeatedTask.Config(), TaskCounter.ofTargetInt(IntRoll.ofConstant(3), TaskCounterPredicate.GREATER_OR_EQUAL_TO));
      repeatedTask.setId("complete_3_vaults_looting_2500_chests");
      repeatedTask.setRenderer(teamRenderer);
      singleVaultTask = new SingleVaultTask(VaultListenerMode.OWNER);
      singleVaultTask.addChildren(
         new ProgressConfiguredTask[]{
            new LootChestTask(
               new LootChestTask.Config(
                  new OrTilePredicate(
                     TilePredicate.of("@the_vault:living", true).orElseThrow(),
                     TilePredicate.of("@the_vault:wooden", true).orElseThrow(),
                     TilePredicate.of("@the_vault:ornate", true).orElseThrow(),
                     TilePredicate.of("@the_vault:gilded", true).orElseThrow()
                  )
               ),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(2501), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            ),
            new FinishVaultTask(
               new FinishVaultTask.Config(Set.of(Completion.COMPLETED), null),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(1), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            )
         }
      );
      repeatedTask.addChildren(new SingleVaultTask[]{singleVaultTask});
      this.tasks.add(repeatedTask);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Complete 5 Solo Elixir Vaults without dealing any damage";
      teamRenderer.icon = VaultMod.id("textures/item/sublime_vault_elixir.png");
      repeatedTask = new RepeatedTask(new RepeatedTask.Config(), TaskCounter.ofTargetInt(IntRoll.ofConstant(5), TaskCounterPredicate.GREATER_OR_EQUAL_TO));
      repeatedTask.setId("complete_5_solo_elixir_vaults_no_damage_dealt");
      repeatedTask.setRenderer(teamRenderer);
      singleVaultTask = new SingleVaultTask(VaultListenerMode.SOLO);
      singleVaultTask.addChildren(
         new ProgressConfiguredTask[]{
            new FinishVaultTask(
               new FinishVaultTask.Config(Set.of(Completion.COMPLETED), Set.of("elixir")),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(1), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            ),
            new DealDamageTask(
               new DealDamageTask.Config(EntityPredicate.of("@the_vault:mobs", true).orElseThrow(), DamagePhase.POST_MITIGATION),
               TaskCounter.ofTargetFloat(FloatRoll.ofConstant(0.0F), TaskCounterPredicate.EQUAL)
            )
         }
      );
      repeatedTask.addChildren(new SingleVaultTask[]{singleVaultTask});
      this.tasks.add(repeatedTask);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Complete 5 Solo Brazier Vaults without taking any damage";
      teamRenderer.icon = VaultMod.id("textures/item/sublime_vault_elixir.png");
      repeatedTask = new RepeatedTask(new RepeatedTask.Config(), TaskCounter.ofTargetInt(IntRoll.ofConstant(5), TaskCounterPredicate.GREATER_OR_EQUAL_TO));
      repeatedTask.setId("complete_5_solo_brazier_vaults_no_damage_taken");
      repeatedTask.setRenderer(teamRenderer);
      singleVaultTask = new SingleVaultTask(VaultListenerMode.SOLO);
      singleVaultTask.addChildren(
         new ProgressConfiguredTask[]{
            new FinishVaultTask(
               new FinishVaultTask.Config(Set.of(Completion.COMPLETED), Set.of("monolith")),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(1), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            ),
            new TakeDamageTask(
               new TakeDamageTask.Config(EntityPredicate.of("@the_vault:mobs", true).orElseThrow(), DamagePhase.POST_MITIGATION),
               TaskCounter.ofTargetFloat(FloatRoll.ofConstant(0.0F), TaskCounterPredicate.EQUAL)
            )
         }
      );
      repeatedTask.addChildren(new SingleVaultTask[]{singleVaultTask});
      this.tasks.add(repeatedTask);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Complete 10 Scavenger Vaults using less than 10 minutes of the timer";
      teamRenderer.icon = VaultMod.id("textures/item/sublime_vault_elixir.png");
      repeatedTask = new RepeatedTask(new RepeatedTask.Config(), TaskCounter.ofTargetInt(IntRoll.ofConstant(10), TaskCounterPredicate.GREATER_OR_EQUAL_TO));
      repeatedTask.setId("complete_10_scavenger_vaults_in_10_minutes");
      repeatedTask.setRenderer(teamRenderer);
      singleVaultTask = new SingleVaultTask(VaultListenerMode.OWNER);
      singleVaultTask.addChildren(
         new NodeTask[]{
            new FinishVaultTask(
               new FinishVaultTask.Config(Set.of(Completion.COMPLETED), Set.of("scavenger")),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(1), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            ),
            new VaultTimedTask(12000L)
         }
      );
      repeatedTask.addChildren(new SingleVaultTask[]{singleVaultTask});
      this.tasks.add(repeatedTask);
      teamRenderer = new TeamRenderer();
      teamRenderer.name = "Complete 5 Blackout Bingo Vaults using less than 15 minutes of the timer";
      teamRenderer.icon = VaultMod.id("textures/gui/modifiers/bingo.png");
      repeatedTask = new RepeatedTask(new RepeatedTask.Config(), TaskCounter.ofTargetInt(IntRoll.ofConstant(5), TaskCounterPredicate.GREATER_OR_EQUAL_TO));
      repeatedTask.setId("complete_5_blackout_bingo_vaults_in_15_minutes");
      repeatedTask.setRenderer(teamRenderer);
      singleVaultTask = new SingleVaultTask(VaultListenerMode.OWNER);
      singleVaultTask.addChildren(
         new NodeTask[]{
            new BingoObjectiveTask(TaskCounter.ofTargetInt(IntRoll.ofConstant(1), TaskCounterPredicate.GREATER_OR_EQUAL_TO)),
            new FinishVaultTask(
               new FinishVaultTask.Config(Set.of(Completion.COMPLETED), Set.of("bingo")),
               TaskCounter.ofTargetInt(IntRoll.ofConstant(1), TaskCounterPredicate.GREATER_OR_EQUAL_TO)
            ),
            new VaultTimedTask(18000L)
         }
      );
      repeatedTask.addChildren(new SingleVaultTask[]{singleVaultTask});
      this.tasks.add(repeatedTask);
      this.width = 5;
   }

   @Override
   protected void onLoad(@Nullable Config oldConfigInstance) {
      super.onLoad(oldConfigInstance);
      this.taskMap = null;
      TeamTaskManager.onConfigReload();
   }

   public boolean hasTask(String taskId) {
      return this.getTaskMap().containsKey(taskId);
   }

   public Task getTask(String taskId) {
      return this.getTaskMap().get(taskId);
   }

   private Map<String, Task> getTaskMap() {
      if (this.taskMap == null) {
         this.taskMap = new HashMap<>();

         for (Task task : this.tasks) {
            this.taskMap.put(task.getId(), task);
         }
      }

      return this.taskMap;
   }

   public Stream<Task> streamNonReclaimTasks() {
      return this.tasks.stream().filter(task -> task.getId() != null && !task.getId().matches(".*_reclaim_\\d+$"));
   }

   public Optional<Task> getNextTask(@Nullable String taskId) {
      if (taskId == null) {
         return Optional.empty();
      } else {
         String reclaimTaskId = "";
         Matcher matcher = RECLAIM_PATTERN.matcher(taskId);
         if (matcher.matches()) {
            int reclaimIndex = taskId.lastIndexOf("_reclaim_");
            String numberPart = taskId.substring(reclaimIndex + 9);
            int number = Integer.parseInt(numberPart);
            reclaimTaskId = taskId.substring(0, reclaimIndex + 9) + ++number;
         } else {
            reclaimTaskId = taskId + "_reclaim_1";
         }

         return Optional.ofNullable(this.getTaskMap().get(reclaimTaskId));
      }
   }
}
