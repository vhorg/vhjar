package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.world.data.ServerVaults;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class InVaultTask extends NodeTask {
   private UUID vaultUuid;
   private boolean cumulative;
   private boolean solo;

   @Override
   public boolean isCompleted() {
      return this.vaultUuid != null;
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK
         .register(
            this,
            event -> {
               if (this.parent == null || this.parent.isCompleted()) {
                  if (this.vaultUuid != null
                     && (
                        this.solo && context.getSource() instanceof EntityTaskSource entitySource && entitySource.getCount() > 1
                           || ServerVaults.get(this.vaultUuid).isEmpty()
                     )) {
                     this.vaultUuid = null;
                     if (!this.cumulative) {
                        for (ResettingTask child : this.getChildren(ResettingTask.class)) {
                           child.onReset(context);
                        }
                     }
                  }

                  if (this.vaultUuid == null && context.getSource() instanceof EntityTaskSource entitySourcex) {
                     Set<ServerPlayer> players = entitySourcex.getEntities(ServerPlayer.class);
                     Set<UUID> vaults = new HashSet<>();

                     for (ServerPlayer player : players) {
                        ServerVaults.get(player.level).flatMap(vault -> vault.getOptional(Vault.ID)).ifPresent(vaults::add);
                     }

                     if (vaults.size() == 1) {
                        this.vaultUuid = vaults.iterator().next();
                     }
                  }
               }
            }
         );
      super.onAttach(context);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.UUID.asNullable().writeBits(this.vaultUuid, buffer);
      Adapters.BOOLEAN.writeBits(this.cumulative, buffer);
      Adapters.BOOLEAN.writeBits(this.solo, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.vaultUuid = Adapters.UUID.asNullable().readBits(buffer).orElse(null);
      this.cumulative = Adapters.BOOLEAN.readBits(buffer).orElse(true);
      this.solo = Adapters.BOOLEAN.readBits(buffer).orElse(false);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.UUID.writeNbt(this.vaultUuid).ifPresent(value -> nbt.put("vaultUuid", value));
         Adapters.BOOLEAN.writeNbt(this.cumulative).ifPresent(value -> nbt.put("cumulative", value));
         Adapters.BOOLEAN.writeNbt(this.solo).ifPresent(value -> nbt.put("solo", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.vaultUuid = Adapters.UUID.readNbt(nbt.get("vaultUuid")).orElse(null);
      this.cumulative = Adapters.BOOLEAN.readNbt(nbt.get("cumulative")).orElse(true);
      this.solo = Adapters.BOOLEAN.readNbt(nbt.get("solo")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.UUID.writeJson(this.vaultUuid).ifPresent(value -> json.add("vaultUuid", value));
         Adapters.BOOLEAN.writeJson(this.cumulative).ifPresent(value -> json.add("cumulative", value));
         Adapters.BOOLEAN.writeJson(this.solo).ifPresent(value -> json.add("solo", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.vaultUuid = Adapters.UUID.readJson(json.get("vaultUuid")).orElse(null);
      this.cumulative = Adapters.BOOLEAN.readJson(json.get("cumulative")).orElse(true);
      this.solo = Adapters.BOOLEAN.readJson(json.get("solo")).orElse(false);
   }
}
