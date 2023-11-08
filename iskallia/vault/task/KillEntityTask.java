package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import iskallia.vault.task.util.IProgressTask;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.EventPriority;

public class KillEntityTask extends ProgressConfiguredTask<Integer, KillEntityTask.Config> implements IProgressTask {
   public KillEntityTask() {
      super(new KillEntityTask.Config(), 0, Adapters.INT_SEGMENTED_7, Integer::compare);
   }

   public KillEntityTask(KillEntityTask.Config config) {
      super(config, 0, Adapters.INT_SEGMENTED_7, Integer::compare);
   }

   @Override
   public void onPopulate(TaskSource source) {
      this.targetCount = this.getConfig().count.get(source.getRandom());
   }

   @Override
   public void onAttach(TaskSource source) {
      CommonEvents.ENTITY_DROPS.register(this, EventPriority.HIGHEST, event -> {
         Entity attacker = event.getSource().getEntity();
         if (attacker != null && !attacker.getLevel().isClientSide()) {
            if (source instanceof EntityTaskSource entitySource) {
               if (entitySource.matches(attacker)) {
                  if (this.getConfig().filter.test(event.getEntity())) {
                     Integer var5 = this.currentCount;
                     this.currentCount = this.currentCount + 1;
                  }
               }
            }
         }
      });
      super.onAttach(source);
   }

   @Override
   public void onDetach() {
      CommonEvents.ENTITY_DROPS.release(this);
      super.onDetach();
   }

   public static class Config extends ConfiguredTask.Config {
      public EntityPredicate filter;
      public IntRoll count;

      public Config() {
      }

      public Config(EntityPredicate filter, IntRoll count) {
         this.filter = filter;
         this.count = count;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.ENTITY_PREDICATE.writeBits(this.filter, buffer);
         Adapters.INT_ROLL.writeBits(this.count, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.filter = Adapters.ENTITY_PREDICATE.readBits(buffer).orElseThrow();
         this.count = Adapters.INT_ROLL.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.ENTITY_PREDICATE.writeNbt(this.filter).ifPresent(value -> nbt.put("filter", value));
            Adapters.INT_ROLL.writeNbt(this.count).ifPresent(value -> nbt.put("count", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.filter = Adapters.ENTITY_PREDICATE.readNbt(nbt.get("filter")).orElse(EntityPredicate.FALSE);
         this.count = Adapters.INT_ROLL.readNbt(nbt.get("count")).orElse(IntRoll.ofConstant(0));
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.ENTITY_PREDICATE.writeJson(this.filter).ifPresent(value -> json.add("filter", value));
            Adapters.INT_ROLL.writeJson(this.count).ifPresent(value -> json.add("count", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.filter = Adapters.ENTITY_PREDICATE.readJson(json.get("filter")).orElse(EntityPredicate.FALSE);
         this.count = Adapters.INT_ROLL.readJson(json.get("count")).orElse(IntRoll.ofConstant(0));
      }
   }
}
