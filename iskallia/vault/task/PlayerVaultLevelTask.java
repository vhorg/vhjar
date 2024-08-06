package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class PlayerVaultLevelTask extends NodeTask {
   private int minimumLevel;
   private boolean completed;

   public int getMinimumLevel() {
      return this.minimumLevel;
   }

   @Override
   public boolean isCompleted() {
      return this.completed;
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK.register(this, event -> {
         this.completed = true;
         if (context.getSource() instanceof EntityTaskSource entitySource) {
            for (Player player : entitySource.getEntities(Player.class)) {
               PlayerVaultStats stats = PlayerVaultStatsData.get(context.getServer()).getVaultStats(player);
               if (stats.getVaultLevel() < this.minimumLevel) {
                  this.completed = false;
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
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.minimumLevel), buffer);
      Adapters.BOOLEAN.writeBits(this.completed, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.minimumLevel = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.completed = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.minimumLevel)).ifPresent(value -> nbt.put("minimumLevel", value));
         Adapters.BOOLEAN.writeNbt(this.completed).ifPresent(value -> nbt.put("completed", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.minimumLevel = Adapters.INT.readNbt(nbt.get("minimumLevel")).orElse(0);
      this.completed = Adapters.BOOLEAN.readNbt(nbt.get("completed")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.minimumLevel)).ifPresent(value -> json.add("minimumLevel", value));
         Adapters.BOOLEAN.writeJson(this.completed).ifPresent(value -> json.add("completed", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.minimumLevel = Adapters.INT.readJson(json.get("minimumLevel")).orElse(0);
      this.completed = Adapters.BOOLEAN.readJson(json.get("completed")).orElse(false);
   }
}
