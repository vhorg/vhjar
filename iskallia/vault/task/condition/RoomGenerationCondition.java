package iskallia.vault.task.condition;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.list.ListAdapter;
import iskallia.vault.core.data.adapter.number.IntAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.VaultGenerator;
import iskallia.vault.core.world.generator.layout.VaultLayout;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.world.template.JigsawTemplate;
import iskallia.vault.core.world.template.Template;
import iskallia.vault.task.ProgressConfiguredTask;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.counter.TargetTaskCounter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class RoomGenerationCondition extends TaskCondition<RoomGenerationCondition.Config> {
   public RoomGenerationCondition() {
      super(new RoomGenerationCondition.Config());
   }

   @Override
   public boolean isConditionFulfilled(ProgressConfiguredTask<?, ?> task, TaskContext context) {
      List<ResourceLocation> conditions = this.getConfig().getFilter();
      if (conditions.isEmpty()) {
         return true;
      } else if (task.getCounter() instanceof TargetTaskCounter<?, ?> taskCounter) {
         if (taskCounter.getTarget() instanceof Integer targetRoomCount) {
            if (targetRoomCount <= 0) {
               return true;
            } else {
               if (this.getConfig().getMinimum().isPresent()) {
                  targetRoomCount = this.getConfig().getMinimum().get();
               }

               Vault vault = context.getVault();
               if (vault == null) {
                  return true;
               } else {
                  int foundRooms = 0;
                  VaultGenerator gen = vault.get(Vault.WORLD).get(WorldManager.GENERATOR);
                  Iterator<VaultLayout.LayoutEntry> layoutIterator = gen.getLayout().expandingIterator(vault, this.getConfig().getSearchRadius().orElse(36));

                  while (layoutIterator.hasNext()) {
                     VaultLayout.LayoutEntry entry = layoutIterator.next();
                     if (entry.type() == VaultLayout.PieceType.ROOM) {
                        Template tpl = entry.template();
                        if (tpl instanceof JigsawTemplate jigsawTemplate) {
                           tpl = jigsawTemplate.getRoot();
                        }

                        if (tpl != null && conditions.contains(tpl.getKey().getId())) {
                           if (++foundRooms >= targetRoomCount) {
                              return true;
                           }
                        }
                     }
                  }

                  if (foundRooms > 0 && taskCounter.getConfig().getTarget() instanceof IntRoll intCountTarget && intCountTarget.contains(foundRooms)) {
                     taskCounter.setTarget(foundRooms);
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public static class Config extends TaskCondition.Config {
      private static final ListAdapter<ResourceLocation> ADAPTER = Adapters.<ResourceLocation>ofArrayList(Adapters.IDENTIFIER).asNullable();
      private static final IntAdapter INT_NULLABLE_ADAPTER = Adapters.INT.asNullable();
      private List<ResourceLocation> filter = new ArrayList<>();
      private Integer minimum;
      private Integer searchRadius;

      public List<ResourceLocation> getFilter() {
         return this.filter;
      }

      public void setFilter(List<ResourceLocation> filter) {
         this.filter = filter;
      }

      public Optional<Integer> getMinimum() {
         return Optional.ofNullable(this.minimum);
      }

      public void setMinimum(Integer minimum) {
         this.minimum = minimum;
      }

      public Optional<Integer> getSearchRadius() {
         return Optional.ofNullable(this.searchRadius);
      }

      public void setSearchRadius(Integer searchRadius) {
         this.searchRadius = searchRadius;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         this.writeNbt().ifPresent(tag -> Adapters.COMPOUND_NBT.writeBits(tag, buffer));
      }

      @Override
      public void readBits(BitBuffer buffer) {
         this.readNbt(Adapters.COMPOUND_NBT.readBits(buffer).orElse(new CompoundTag()));
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag tag = new CompoundTag();
         ADAPTER.writeNbt(this.getFilter()).ifPresent(value -> tag.put("filter", value));
         INT_NULLABLE_ADAPTER.writeNbt(this.minimum).ifPresent(value -> tag.put("minimum", value));
         INT_NULLABLE_ADAPTER.writeNbt(this.searchRadius).ifPresent(value -> tag.put("searchRadius", value));
         return Optional.of(tag);
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         this.setFilter(ADAPTER.readNbt(nbt.get("filter")).orElse(new ArrayList<>()));
         this.setMinimum(INT_NULLABLE_ADAPTER.readNbt(nbt.get("minimum")).orElse(null));
         this.setSearchRadius(INT_NULLABLE_ADAPTER.readNbt(nbt.get("searchRadius")).orElse(null));
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         ADAPTER.writeJson(this.getFilter()).ifPresent(value -> json.add("filter", value));
         INT_NULLABLE_ADAPTER.writeJson(this.minimum).ifPresent(value -> json.add("minimum", value));
         INT_NULLABLE_ADAPTER.writeJson(this.searchRadius).ifPresent(value -> json.add("searchRadius", value));
         return Optional.of(json);
      }

      @Override
      public void readJson(JsonObject json) {
         this.setFilter(ADAPTER.readJson(json.get("filter")).orElse(new ArrayList<>()));
         this.setMinimum(INT_NULLABLE_ADAPTER.readJson(json.get("minimum")).orElse(null));
         this.setSearchRadius(INT_NULLABLE_ADAPTER.readJson(json.get("searchRadius")).orElse(null));
      }
   }
}
