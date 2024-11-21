package iskallia.vault.task.condition;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.list.ListAdapter;
import iskallia.vault.core.data.adapter.number.IntAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.task.ProgressConfiguredTask;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.counter.TargetTaskCounter;
import iskallia.vault.util.InventoryUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ItemsAvailableCondition extends TaskCondition<ItemsAvailableCondition.Config> {
   public ItemsAvailableCondition() {
      super(new ItemsAvailableCondition.Config());
   }

   @Override
   public boolean isConditionFulfilled(ProgressConfiguredTask<?, ?> task, TaskContext context) {
      List<ItemPredicate> conditions = this.getConfig().getFilter();
      if (conditions.isEmpty()) {
         return true;
      } else if (task.getCounter() instanceof TargetTaskCounter<?, ?> taskCounter) {
         if (taskCounter.getTarget() instanceof Integer targetItemCount) {
            if (targetItemCount <= 0) {
               return true;
            } else {
               if (this.getConfig().getMinimum().isPresent()) {
                  targetItemCount = this.getConfig().getMinimum().get();
               }

               int found = 0;
               Vault vault = context.getVault();
               if (vault == null) {
                  return true;
               } else if (!vault.has(Vault.OWNER)) {
                  return false;
               } else {
                  MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
                  if (srv == null) {
                     return false;
                  } else {
                     ServerPlayer player = srv.getPlayerList().getPlayer(vault.get(Vault.OWNER));
                     if (player == null) {
                        return false;
                     } else {
                        for (InventoryUtil.ItemAccess access : InventoryUtil.findAllItems(player)) {
                           if (conditions.stream().anyMatch(predicate -> predicate.test(access.getStack()))) {
                              found += access.getStack().getCount();
                              if (found >= targetItemCount) {
                                 return true;
                              }
                           }
                        }

                        if (found > 0 && taskCounter.getConfig().getTarget() instanceof IntRoll intCountTarget && intCountTarget.contains(found)) {
                           taskCounter.setTarget(found);
                           return true;
                        } else {
                           return false;
                        }
                     }
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
      private static final ListAdapter<ItemPredicate> ADAPTER = Adapters.ofArrayList(Adapters.ITEM_PREDICATE);
      private static final IntAdapter MIN_ADAPTER = Adapters.INT.asNullable();
      private List<ItemPredicate> filter = new ArrayList<>();
      private Integer minimum;

      public Optional<Integer> getMinimum() {
         return Optional.ofNullable(this.minimum);
      }

      public void setMinimum(Integer minimum) {
         this.minimum = minimum;
      }

      public List<ItemPredicate> getFilter() {
         return this.filter;
      }

      public void setFilter(List<ItemPredicate> filter) {
         this.filter = filter;
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
         MIN_ADAPTER.writeNbt(this.minimum).ifPresent(value -> tag.put("minimum", value));
         return Optional.of(tag);
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         this.setFilter(ADAPTER.readNbt(nbt.get("filter")).orElse(new ArrayList<>()));
         this.setMinimum(MIN_ADAPTER.readNbt(nbt.get("minimum")).orElse(null));
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         ADAPTER.writeJson(this.getFilter()).ifPresent(value -> json.add("filter", value));
         MIN_ADAPTER.writeJson(this.minimum).ifPresent(value -> json.add("minimum", value));
         return Optional.of(json);
      }

      @Override
      public void readJson(JsonObject json) {
         this.setFilter(ADAPTER.readJson(json.get("filter")).orElse(new ArrayList<>()));
         this.setMinimum(MIN_ADAPTER.readJson(json.get("minimum")).orElse(null));
      }
   }
}
