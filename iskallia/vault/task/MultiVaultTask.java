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
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.util.VaultListenerMode;
import iskallia.vault.world.data.ServerVaults;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class MultiVaultTask extends NodeTask {
   private VaultListenerMode mode;
   private TaskContext vaultContext;
   public static final EnumAdapter<VaultListenerMode> MODE = Adapters.ofEnum(VaultListenerMode.class, EnumAdapter.Mode.NAME);

   public MultiVaultTask() {
   }

   public MultiVaultTask(VaultListenerMode mode) {
      this.mode = mode;
   }

   @Override
   public boolean isCompleted() {
      for (Task task : this.getChildren()) {
         if (!task.streamSelfAndDescendants().allMatch(Task::isCompleted)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean hasActiveChildren() {
      return this.parent == null || this.parent.hasActiveChildren();
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK.register(this, data -> {
         if (context.getSource() instanceof EntityTaskSource originalSource) {
            this.vaultContext.setServer(context.getServer());
            this.vaultContext.setVault(context.getVault());
            this.vaultContext.setSource(context.getSource().copy());
            if (this.vaultContext.getSource() instanceof EntityTaskSource newSource) {
               newSource.getUuids().clear();

               for (ServerPlayer player : originalSource.getEntities(context.getServer(), ServerPlayer.class)) {
                  Vault vault = ServerVaults.get(player.getLevel()).orElse(null);
                  if (vault != null && vault.get(Vault.LISTENERS).contains(player.getUUID())) {
                     if (this.mode == VaultListenerMode.SOLO) {
                        if (vault.get(Vault.LISTENERS).getAll().size() > 1) {
                           continue;
                        }

                        UUIDList leavers = vault.get(Vault.LISTENERS).get(Listeners.LOGIC).getOptional(ClassicListenersLogic.LEAVERS).orElse(UUIDList.create());
                        if (!leavers.isEmpty()) {
                           continue;
                        }
                     } else if (this.mode == VaultListenerMode.OWNER) {
                        UUID owner = vault.get(Vault.OWNER);
                        if (owner == null || !originalSource.matches(owner)) {
                           continue;
                        }
                     }

                     newSource.getUuids().add(player.getUUID());
                  }
               }
            }
         }
      });
      if (this.vaultContext == null) {
         this.vaultContext = context.copy();
         if (this.vaultContext.getSource() instanceof EntityTaskSource source) {
            source.getUuids().clear();
         }
      }

      for (Task child : this.children) {
         child.onAttach(this.vaultContext);
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      MODE.writeBits(this.mode, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.mode = MODE.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         MODE.writeNbt(this.mode).ifPresent(value -> nbt.put("mode", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.mode = MODE.readNbt(nbt.get("mode")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         MODE.writeJson(this.mode).ifPresent(value -> json.add("mode", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.mode = MODE.readJson(json.get("mode")).orElse(VaultListenerMode.ALL);
   }
}
