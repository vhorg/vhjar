package iskallia.vault.core.vault.objective.bingo;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.task.ProgressConfiguredTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class BingoItem {
   private final String name;
   private final String icon;
   private final Task task;
   private final float scaleFactor;
   private boolean completed;
   private boolean partOfCompletedBingo;
   private UUID completedBy = null;

   private BingoItem(String name, String icon, Task task, float scaleFactor, boolean completed, boolean partOfCompletedBingo) {
      this.name = name;
      this.icon = icon;
      this.task = task;
      if (task instanceof ProgressConfiguredTask var7) {
         ;
      }

      this.scaleFactor = scaleFactor;
      this.completed = completed;
      this.partOfCompletedBingo = partOfCompletedBingo;
   }

   public BingoItem(String name, String icon, Task task, float scaleFactor) {
      this(name, icon, task, scaleFactor, false, false);
   }

   public void adjustToOneMorePlayer(int newNumberOfPlayers, EntityTaskSource taskSource) {
   }

   public boolean onTick(TaskContext context) {
      return this.completed ? false : false;
   }

   public BingoItem copy() {
      return new BingoItem(this.name, this.icon, this.task.copy(), this.scaleFactor);
   }

   public String name() {
      return this.name;
   }

   public String icon() {
      return this.icon;
   }

   public Task task() {
      return this.task;
   }

   public void onAttach(TaskContext context) {
      if (!this.completed) {
         this.task.onAttach(context);
      }
   }

   public void onDetach() {
      this.task.onDetach();
   }

   public void markPartOfCompletedBingo() {
      this.partOfCompletedBingo = true;
   }

   public boolean isPartOfCompletedBingo() {
      return this.partOfCompletedBingo;
   }

   public String getCompletedByName(VirtualWorld world) {
      Player completedByPlayer = world.getPlayerByUUID(this.completedBy);
      return completedByPlayer != null ? completedByPlayer.getGameProfile().getName() : "";
   }

   public static class Adapter implements ISimpleAdapter<BingoItem, CompoundTag, JsonObject> {
      public Optional<BingoItem> readJson(@Nullable JsonObject json) {
         return json == null
            ? Optional.empty()
            : Adapters.TASK
               .readJson(json.get("task"))
               .map(t -> new BingoItem(json.get("name").getAsString(), json.get("icon").getAsString(), t, json.get("scaleFactor").getAsFloat()));
      }

      public Optional<JsonObject> writeJson(@Nullable BingoItem value) {
         if (value == null) {
            return Optional.empty();
         } else {
            JsonObject ret = new JsonObject();
            ret.addProperty("name", value.name);
            ret.addProperty("icon", value.icon);
            Adapters.TASK.writeJson(value.task).ifPresent(taskJson -> ret.add("task", taskJson));
            ret.addProperty("scaleFactor", value.scaleFactor);
            return Optional.of(ret);
         }
      }

      public Optional<CompoundTag> writeNbt(@Nullable BingoItem bingoItem) {
         if (bingoItem == null) {
            return Optional.empty();
         } else {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("name", bingoItem.name);
            nbt.putString("icon", bingoItem.icon);
            nbt.putFloat("scaleFactor", bingoItem.scaleFactor);
            nbt.putBoolean("completed", bingoItem.completed);
            nbt.putBoolean("partOfCompletedBingo", bingoItem.partOfCompletedBingo);
            Adapters.TASK.writeNbt(bingoItem.task).ifPresent(taskNbt -> nbt.put("task", taskNbt));
            return Optional.of(nbt);
         }
      }

      public Optional<BingoItem> readNbt(@Nullable CompoundTag nbt) {
         return nbt == null
            ? Optional.empty()
            : Adapters.TASK
               .readNbt(nbt.get("task"))
               .map(
                  t -> new BingoItem(
                     nbt.getString("name"),
                     nbt.getString("icon"),
                     t,
                     nbt.getFloat("scaleFactor"),
                     nbt.getBoolean("completed"),
                     nbt.getBoolean("partOfCompletedBingo")
                  )
               );
      }

      public void writeBits(@Nullable BingoItem value, BitBuffer buffer) {
         if (value == null) {
            buffer.writeBoolean(false);
         } else {
            buffer.writeBoolean(true);
            buffer.writeString(value.name);
            buffer.writeString(value.icon);
            buffer.writeFloat(value.scaleFactor);
            buffer.writeBoolean(value.completed);
            buffer.writeBoolean(value.partOfCompletedBingo);
            Adapters.TASK.writeBits(value.task, buffer);
         }
      }

      @Override
      public Optional<BingoItem> readBits(BitBuffer buffer) {
         if (!buffer.readBoolean()) {
            return Optional.empty();
         } else {
            String name = buffer.readString();
            String icon = buffer.readString();
            float scaleFactor = buffer.readFloat();
            boolean completed = buffer.readBoolean();
            boolean partOfCompletedBingo = buffer.readBoolean();
            return Adapters.TASK.readBits(buffer).map(t -> new BingoItem(name, icon, t, scaleFactor, completed, partOfCompletedBingo));
         }
      }
   }
}
