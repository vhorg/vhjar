package iskallia.vault.core.vault.objective.elixir;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ElixirObjective;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.UUID;

public class ElixirGoal extends DataObject<ElixirGoal> {
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> CURRENT = FieldKey.of("current", Integer.class)
      .with(Version.v1_12, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> TARGET = FieldKey.of("target", Integer.class)
      .with(Version.v1_12, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> BASE_TARGET = FieldKey.of("base_target", Integer.class)
      .with(Version.v1_25, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<ElixirTask.List> TASKS = FieldKey.of("tasks", ElixirTask.List.class)
      .with(Version.v1_12, CompoundAdapter.of(ElixirTask.List::new), DISK.all())
      .register(FIELDS);

   public ElixirGoal() {
      this.set(TASKS, new ElixirTask.List());
      this.set(CURRENT, Integer.valueOf(0));
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public boolean isCompleted() {
      return this.get(CURRENT) >= this.get(TARGET);
   }

   public void initServer(VirtualWorld world, Vault vault, ElixirObjective objective, UUID listener) {
      for (ElixirTask task : this.get(TASKS)) {
         task.initServer(world, vault, objective, listener);
      }
   }

   public void tickServer(VirtualWorld world, Vault vault, ElixirObjective objective, UUID listener) {
      double increase = CommonEvents.OBJECTIVE_TARGET.invoke(world, vault, 0.0).getIncrease();
      this.set(TARGET, Integer.valueOf((int)Math.round(this.get(BASE_TARGET).intValue() * (1.0 + increase))));
   }

   public void releaseServer() {
      for (ElixirTask task : this.get(TASKS)) {
         task.releaseServer();
      }
   }

   public int add(int elixir) {
      int amount = Math.min(this.get(TARGET) - this.get(CURRENT), elixir);
      this.modify(CURRENT, value -> value + amount);
      return elixir - amount;
   }
}
