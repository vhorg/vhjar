package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class FinishVaultTask extends ProgressConfiguredTask<Integer, FinishVaultTask.Config> {
   public FinishVaultTask() {
      super(new FinishVaultTask.Config(), TaskCounter.Adapter.INT);
   }

   public FinishVaultTask(FinishVaultTask.Config config, TaskCounter<Integer, ?> counter) {
      super(config, counter, TaskCounter.Adapter.INT);
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.LISTENER_LEAVE.register(this, event -> {
         if (this.parent == null || this.parent.hasActiveChildren()) {
            if (context.getSource() instanceof EntityTaskSource source) {
               if (source.matches(event.getListener().getId())) {
                  Completion completion = event.getVault().get(Vault.STATS).get(event.getListener()).get(StatCollector.COMPLETION);
                  if (this.getConfig().completion == null || this.getConfig().completion.contains(completion)) {
                     String objective = event.getVault().get(Vault.OBJECTIVES).get(Objectives.KEY);
                     if (this.getConfig().objective == null || this.getConfig().objective.contains(objective)) {
                        this.counter.onAdd(1, context);
                     }
                  }
               }
            }
         }
      });
      super.onAttach(context);
   }

   public static class Config extends ConfiguredTask.Config {
      protected static final ArrayAdapter<Completion> COMPLETION = Adapters.ofArray(Completion[]::new, Adapters.ofEnum(Completion.class, EnumAdapter.Mode.NAME));
      protected static final ArrayAdapter<String> OBJECTIVE = Adapters.ofArray(String[]::new, Adapters.UTF_8);
      private Set<Completion> completion;
      @Nullable
      private Set<String> objective;

      public Config() {
      }

      public Config(Set<Completion> completion, @Nullable Set<String> objective) {
         this.completion = completion;
         this.objective = objective;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         COMPLETION.writeBits(this.completion.toArray(Completion[]::new), buffer);
         OBJECTIVE.asNullable().writeBits(this.objective == null ? null : this.objective.toArray(String[]::new), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.completion = Arrays.stream(COMPLETION.readBits(buffer).orElse(new Completion[0])).collect(Collectors.toSet());
         this.objective = OBJECTIVE.asNullable().readBits(buffer).map(arr -> Arrays.stream(arr).collect(Collectors.toSet())).orElse(null);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            COMPLETION.writeNbt(this.completion.toArray(Completion[]::new)).ifPresent(tag -> nbt.put("completion", tag));
            OBJECTIVE.asNullable().writeNbt(this.objective == null ? null : this.objective.toArray(String[]::new)).ifPresent(tag -> nbt.put("objective", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.completion = Arrays.stream(COMPLETION.readNbt(nbt.get("completion")).orElse(new Completion[0])).collect(Collectors.toSet());
         this.objective = OBJECTIVE.asNullable().readNbt(nbt.get("objective")).map(arr -> Arrays.stream(arr).collect(Collectors.toSet())).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson()
            .map(
               json -> {
                  COMPLETION.writeJson(this.completion.toArray(Completion[]::new)).ifPresent(tag -> json.add("completion", tag));
                  OBJECTIVE.asNullable()
                     .writeJson(this.objective == null ? null : this.objective.toArray(String[]::new))
                     .ifPresent(tag -> json.add("objective", tag));
                  return (JsonObject)json;
               }
            );
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.completion = Arrays.stream(COMPLETION.readJson(json.get("completion")).orElse(new Completion[0])).collect(Collectors.toSet());
         this.objective = OBJECTIVE.asNullable().readJson(json.get("objective")).map(arr -> Arrays.stream(arr).collect(Collectors.toSet())).orElse(null);
      }
   }
}
