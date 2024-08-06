package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class AchievementTask extends NodeTask {
   private boolean active;

   @Override
   public boolean isCompleted() {
      return this.active;
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK.register(this, event -> {
         this.active = true;
         if (context.getSource() instanceof EntityTaskSource entitySource) {
            for (ServerPlayer player : entitySource.getEntities(ServerPlayer.class)) {
               if (player.isSpectator()) {
                  this.active = false;
                  break;
               }
            }
         }
      });
      super.onAttach(context);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.BOOLEAN.writeBits(this.active, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.active = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.BOOLEAN.writeNbt(this.active).ifPresent(tag -> nbt.put("active", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.active = Adapters.BOOLEAN.readNbt(nbt.get("active")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.BOOLEAN.writeJson(this.active).ifPresent(tag -> json.add("active", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.active = Adapters.BOOLEAN.readJson(json.get("active")).orElse(false);
   }
}
