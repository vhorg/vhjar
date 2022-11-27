package iskallia.vault.world.data;

import java.time.Instant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class GlobalTimeData extends SavedData {
   protected static final String DATA_NAME = "the_vault_GlobalTime";
   private long startTime = Instant.now().getEpochSecond();
   private long endTime = this.startTime + 7776000L;
   private long additionalTime;

   public long getTimeRemaining() {
      return this.endTime - this.startTime + this.additionalTime;
   }

   public long getEndTime() {
      return this.endTime + this.additionalTime;
   }

   public void reset(long dtSeconds) {
      this.startTime = Instant.now().getEpochSecond();
      this.endTime = this.startTime + dtSeconds;
      this.setDirty();
   }

   public void addTime(long seconds) {
      this.additionalTime += seconds;
      this.setDirty();
   }

   public static GlobalTimeData create(CompoundTag nbt) {
      GlobalTimeData data = new GlobalTimeData();
      data.load(nbt);
      return data;
   }

   public void load(CompoundTag tag) {
      this.startTime = tag.getLong("startTime");
      this.endTime = tag.getLong("endTime");
      this.additionalTime = tag.getLong("additionalTime");
   }

   @NotNull
   public CompoundTag save(CompoundTag nbt) {
      nbt.putLong("startTime", this.startTime);
      nbt.putLong("endTime", this.endTime);
      nbt.putLong("additionalTime", this.additionalTime);
      return nbt;
   }

   public static GlobalTimeData get(ServerLevel world) {
      return (GlobalTimeData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(GlobalTimeData::create, GlobalTimeData::new, "the_vault_GlobalTime");
   }
}
