package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.EventPriority;

public class KillEntityTask extends ProgressConfiguredTask<Integer, KillEntityTask.Config> {
   public KillEntityTask() {
      super(new KillEntityTask.Config(), TaskCounter.Adapter.INT);
   }

   public KillEntityTask(KillEntityTask.Config config, TaskCounter<Integer, ?> counter) {
      super(config, counter, TaskCounter.Adapter.INT);
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.ENTITY_DROPS.register(this, EventPriority.HIGHEST, event -> {
         if (this.parent == null || this.parent.hasActiveChildren()) {
            Entity attacker = event.getSource().getEntity();
            if (attacker != null && !attacker.getLevel().isClientSide()) {
               if (context.getSource() instanceof EntityTaskSource entitySource) {
                  if (attacker.getLevel() == event.getEntity().getLevel()) {
                     if (entitySource.matches(attacker)) {
                        if (this.getConfig().filter.test(event.getEntity())) {
                           this.counter.onAdd(1, context);
                        }
                     }
                  }
               }
            }
         }
      });
      super.onAttach(context);
   }

   public static class Config extends ConfiguredTask.Config {
      public EntityPredicate filter;

      public Config() {
      }

      public Config(EntityPredicate filter) {
         this.filter = filter;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.ENTITY_PREDICATE.writeBits(this.filter, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.filter = Adapters.ENTITY_PREDICATE.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.ENTITY_PREDICATE.writeNbt(this.filter).ifPresent(value -> nbt.put("filter", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.filter = Adapters.ENTITY_PREDICATE.readNbt(nbt.get("filter")).orElse(EntityPredicate.FALSE);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.ENTITY_PREDICATE.writeJson(this.filter).ifPresent(value -> json.add("filter", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.filter = Adapters.ENTITY_PREDICATE.readJson(json.get("filter")).orElse(EntityPredicate.FALSE);
      }
   }
}
