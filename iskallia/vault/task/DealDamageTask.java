package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.util.DamagePhase;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.EventPriority;

public class DealDamageTask extends ProgressConfiguredTask<Float, DealDamageTask.Config> {
   public DealDamageTask() {
      super(new DealDamageTask.Config(), TaskCounter.Adapter.FLOAT);
   }

   public DealDamageTask(DealDamageTask.Config config, TaskCounter<Float, ?> counter) {
      super(config, counter, TaskCounter.Adapter.FLOAT);
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.ENTITY_HURT.register(this, EventPriority.HIGHEST, event -> {
         if (this.parent == null || this.parent.hasActiveChildren()) {
            if (this.getConfig().phase == DamagePhase.PRE_MITIGATION) {
               this.onDealDamage(context, event.getSource().getEntity(), event.getEntity(), event.getAmount());
            }
         }
      });
      CommonEvents.ENTITY_DAMAGE.register(this, EventPriority.HIGHEST, event -> {
         if (this.parent == null || this.parent.hasActiveChildren()) {
            if (this.getConfig().phase == DamagePhase.POST_MITIGATION) {
               this.onDealDamage(context, event.getSource().getEntity(), event.getEntity(), event.getAmount());
            }
         }
      });
      super.onAttach(context);
   }

   public void onDealDamage(TaskContext context, Entity attacker, Entity attacked, float amount) {
      if (context.getSource() instanceof EntityTaskSource source) {
         if (attacked != null && attacker != null && !attacked.getLevel().isClientSide()) {
            if (attacker.getLevel() == attacked.getLevel()) {
               if (source.matches(attacker)) {
                  if (this.getConfig().filter.test(attacked)) {
                     this.counter.onAdd(amount, context);
                  }
               }
            }
         }
      }
   }

   public static class Config extends ConfiguredTask.Config {
      public static final EnumAdapter<DamagePhase> PHASE = Adapters.ofEnum(DamagePhase.class, EnumAdapter.Mode.NAME);
      public EntityPredicate filter;
      public DamagePhase phase;

      public Config() {
      }

      public Config(EntityPredicate filter, DamagePhase phase) {
         this.filter = filter;
         this.phase = phase;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.ENTITY_PREDICATE.writeBits(this.filter, buffer);
         PHASE.writeBits(this.phase, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.filter = Adapters.ENTITY_PREDICATE.readBits(buffer).orElseThrow();
         this.phase = PHASE.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.ENTITY_PREDICATE.writeNbt(this.filter).ifPresent(value -> nbt.put("filter", value));
            PHASE.writeNbt(this.phase).ifPresent(value -> nbt.put("phase", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.filter = Adapters.ENTITY_PREDICATE.readNbt(nbt.get("filter")).orElse(EntityPredicate.FALSE);
         this.phase = PHASE.readNbt(nbt.get("phase")).orElseThrow();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.ENTITY_PREDICATE.writeJson(this.filter).ifPresent(value -> json.add("filter", value));
            PHASE.writeJson(this.phase).ifPresent(value -> json.add("phase", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.filter = Adapters.ENTITY_PREDICATE.readJson(json.get("filter")).orElse(EntityPredicate.FALSE);
         this.phase = PHASE.readJson(json.get("phase")).orElseThrow();
      }
   }
}
