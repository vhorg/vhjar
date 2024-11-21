package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.compound.UUIDList;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.util.VaultListenerMode;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class SingleVaultTask extends NodeTask implements ResettingTask, RepeatingTask {
   private VaultListenerMode mode;
   private final Map<UUID, SingleVaultTask.VaultHolder> vaultHolders;
   public static final EnumAdapter<VaultListenerMode> MODE = Adapters.ofEnum(VaultListenerMode.class, EnumAdapter.Mode.NAME);

   public SingleVaultTask() {
      this.vaultHolders = new HashMap<>();
   }

   public SingleVaultTask(VaultListenerMode mode) {
      this.mode = mode;
      this.vaultHolders = new HashMap<>();
   }

   @Override
   public Iterable<Task> getChildren() {
      return new ArrayList<>();
   }

   @Override
   public boolean isCompleted() {
      return this.vaultHolders.values().stream().anyMatch(holder -> {
         if (holder.context.getSource() instanceof EntityTaskSource entityTaskSource && entityTaskSource.getCount() == 0) {
            return false;
         } else {
            for (Task task : holder.getChildren()) {
               if (!task.streamSelfAndDescendants().allMatch(Task::isCompleted)) {
                  return false;
               }
            }

            return true;
         }
      });
   }

   @Override
   public boolean hasActiveChildren() {
      return this.parent == null || this.parent.hasActiveChildren();
   }

   public SingleVaultTask.VaultHolder getOrCreateHolder(TaskContext context, UUID vaultId) {
      return this.vaultHolders.computeIfAbsent(vaultId, uuid -> {
         TaskContext newContext = context.copy();
         if (newContext.getSource() instanceof EntityTaskSource source) {
            source.getUuids().clear();
         }

         SingleVaultTask.VaultHolder holder = new SingleVaultTask.VaultHolder(this, newContext);

         for (Task child : this.children) {
            Task copy = child.copy();
            copy.parent = this;
            holder.getChildren().add(copy);
            copy.onAttach(newContext);
         }

         return holder;
      });
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK.register(this, data -> {
         if (context.getSource() instanceof EntityTaskSource source) {
            for (Entry<UUID, SingleVaultTask.VaultHolder> entry : this.vaultHolders.entrySet()) {
               entry.getValue().getContext().setServer(context.getServer());
               entry.getValue().getContext().setVault(ServerVaults.get(entry.getKey()).orElse(null));
               entry.getValue().getUuids().clear();
            }

            for (ServerPlayer player : source.getEntities(context.getServer(), ServerPlayer.class)) {
               Vault vault = ServerVaults.get(player.level).orElse(null);
               if (vault != null && vault.get(Vault.LISTENERS).contains(player.getUUID())) {
                  SingleVaultTask.VaultHolder holder = this.getOrCreateHolder(context, vault.get(Vault.ID));
                  holder.getUuids().add(player.getUUID());
               }
            }

            this.vaultHolders.entrySet().removeIf(entryx -> {
               if (!ServerVaults.get((UUID)entryx.getKey()).isEmpty()) {
                  return false;
               } else {
                  for (Task child : ((SingleVaultTask.VaultHolder)entryx.getValue()).getChildren()) {
                     child.onDetach();
                  }

                  return true;
               }
            });
            if (this.mode == VaultListenerMode.SOLO) {
               for (Entry<UUID, SingleVaultTask.VaultHolder> entry : this.vaultHolders.entrySet()) {
                  Vault vault = ServerVaults.get(entry.getKey()).orElse(null);
                  if (vault == null) {
                     throw new IllegalStateException("This is literally impossible");
                  }

                  UUIDList leavers = vault.get(Vault.LISTENERS).get(Listeners.LOGIC).getOptional(ClassicListenersLogic.LEAVERS).orElse(UUIDList.create());
                  if (entry.getValue().getUuids().size() > 1 || !leavers.isEmpty()) {
                     entry.getValue().getUuids().clear();
                  }
               }
            } else if (this.mode == VaultListenerMode.OWNER) {
               for (Entry<UUID, SingleVaultTask.VaultHolder> entry : this.vaultHolders.entrySet()) {
                  Vault vaultx = ServerVaults.get(entry.getKey()).orElse(null);
                  if (vaultx == null) {
                     throw new IllegalStateException("This is literally impossible");
                  }

                  UUID owner = vaultx.get(Vault.OWNER);
                  if (owner == null || !source.matches(owner)) {
                     entry.getValue().getUuids().clear();
                  }
               }
            }
         }
      });
      this.vaultHolders.forEach((key, holder) -> {
         for (Task child : holder.children) {
            child.onAttach(holder.getContext());
         }
      });
   }

   @Override
   public void onDetach() {
      this.vaultHolders.forEach((key, holder) -> {
         for (Task child : holder.children) {
            child.onDetach();
         }
      });
      super.onDetach();
   }

   @Override
   public void onReset(TaskContext context) {
      this.vaultHolders.entrySet().removeIf(entry -> {
         for (Task task : entry.getValue().getChildren()) {
            task.onDetach();
         }

         return true;
      });
   }

   @Override
   public void onRepeat(TaskContext context) {
      Iterator<Entry<UUID, SingleVaultTask.VaultHolder>> it = this.vaultHolders.entrySet().iterator();

      label30:
      while (it.hasNext()) {
         Entry<UUID, SingleVaultTask.VaultHolder> entry = it.next();

         for (Task task : entry.getValue().getChildren()) {
            if (!task.streamSelfAndDescendants().allMatch(Task::isCompleted)) {
               continue label30;
            }
         }

         for (Task child : entry.getValue().getChildren()) {
            child.onDetach();
         }

         it.remove();
         break;
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      MODE.writeBits(this.mode, buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.vaultHolders.size()), buffer);
      this.vaultHolders.forEach((uuid, holder) -> {
         Adapters.UUID.writeBits(uuid, buffer);
         holder.writeBits(buffer);
      });
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.mode = MODE.readBits(buffer).orElseThrow();
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.vaultHolders.clear();

      for (int i = 0; i < size; i++) {
         UUID uuid = Adapters.UUID.readBits(buffer).orElseThrow();
         SingleVaultTask.VaultHolder holder = new SingleVaultTask.VaultHolder(this, new TaskContext());
         holder.readBits(buffer);
         this.vaultHolders.put(uuid, holder);
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         MODE.writeNbt(this.mode).ifPresent(value -> nbt.put("mode", value));
         CompoundTag holders = new CompoundTag();
         this.vaultHolders.forEach((uuid, holder) -> holder.writeNbt().ifPresent(tag -> holders.put(uuid.toString(), tag)));
         nbt.put("holders", holders);
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.mode = MODE.readNbt(nbt.get("mode")).orElseThrow();
      CompoundTag holders = nbt.getCompound("holders");
      this.vaultHolders.clear();

      for (String key : holders.getAllKeys()) {
         SingleVaultTask.VaultHolder holder = new SingleVaultTask.VaultHolder(this, new TaskContext());
         holder.readNbt(holders.getCompound(key));
         this.vaultHolders.put(UUID.fromString(key), holder);
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         MODE.writeJson(this.mode).ifPresent(value -> json.add("mode", value));
         JsonObject holders = new JsonObject();
         this.vaultHolders.forEach((uuid, holder) -> holder.writeJson().ifPresent(tag -> holders.add(uuid.toString(), tag)));
         holders.add("holders", holders);
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.mode = MODE.readJson(json.get("mode")).orElse(VaultListenerMode.ALL);
      this.vaultHolders.clear();
      if (json.get("holders") instanceof JsonObject holders) {
         for (String key : holders.keySet()) {
            SingleVaultTask.VaultHolder holder = new SingleVaultTask.VaultHolder(this, new TaskContext());
            holder.readJson(holders.getAsJsonObject(key));
            this.vaultHolders.put(UUID.fromString(key), holder);
         }
      }
   }

   public static class VaultHolder implements ISerializable<CompoundTag, JsonObject> {
      private Task parent;
      private final TaskContext context;
      private List<Task> children;

      public VaultHolder(Task parent, TaskContext context) {
         this.context = context;
         this.children = new ArrayList<>();
      }

      public TaskContext getContext() {
         return this.context;
      }

      public Set<UUID> getUuids() {
         return (Set<UUID>)(this.context.getSource() instanceof EntityTaskSource source ? source.getUuids() : new HashSet<>());
      }

      public List<Task> getChildren() {
         return this.children;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.TASK_SOURCE.writeBits(this.context.getSource(), buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.children.size()), buffer);

         for (Task child : this.children) {
            Adapters.TASK.writeBits(child, buffer);
         }
      }

      @Override
      public void readBits(BitBuffer buffer) {
         this.context.setSource(Adapters.TASK_SOURCE.readBits(buffer).orElseThrow());
         this.children.clear();
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

         for (int i = 0; i < size; i++) {
            Task task = Adapters.TASK.readBits(buffer).orElseThrow();
            task.parent = this.parent;
            this.children.add(task);
         }
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Adapters.TASK_SOURCE.writeNbt(this.context.getSource()).ifPresent(tag -> nbt.put("source", tag));
            NodeTask.CHILDREN.writeNbt(this.children.toArray(Task[]::new)).ifPresent(tag -> nbt.put("children", tag));
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         this.context.setSource(Adapters.TASK_SOURCE.readNbt(nbt.get("source")).orElseThrow());
         this.children = Arrays.stream(NodeTask.CHILDREN.readNbt(nbt.get("children")).orElse(new Task[0])).collect(Collectors.toList());
         this.children.forEach(task -> task.parent = this.parent);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return Optional.of(new JsonObject()).map(json -> {
            Adapters.TASK_SOURCE.writeJson(this.context.getSource()).ifPresent(tag -> json.add("source", tag));
            NodeTask.CHILDREN.writeJson(this.children.toArray(Task[]::new)).ifPresent(tag -> json.add("children", tag));
            return (JsonObject)json;
         });
      }

      public void readJson(JsonObject json) {
         this.context.setSource(Adapters.TASK_SOURCE.readJson(json.get("source")).orElseThrow());
         this.children = Arrays.stream(NodeTask.CHILDREN.readJson(json.get("children")).orElse(new Task[0])).collect(Collectors.toList());
         this.children.forEach(task -> task.parent = this.parent);
      }
   }
}
