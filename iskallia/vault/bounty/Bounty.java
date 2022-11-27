package iskallia.vault.bounty;

import iskallia.vault.bounty.task.Task;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class Bounty implements INBTSerializable<CompoundTag> {
   private UUID id;
   private UUID playerId;
   private Task<?> task;
   private long expiration;

   public Bounty(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public Bounty(UUID id, UUID playerId, Task<?> task) {
      this.id = id;
      this.playerId = playerId;
      this.task = task;
   }

   public Task<?> getTask() {
      return this.task;
   }

   public UUID getId() {
      return this.id;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public long getExpiration() {
      return this.expiration;
   }

   public void setExpiration(long expiration) {
      this.expiration = expiration;
   }

   public boolean isExpired() {
      return Instant.now().toEpochMilli() >= this.getExpiration();
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putUUID("id", this.id);
      tag.putUUID("playerId", this.playerId);
      tag.put("task", this.task.serializeNBT());
      tag.putLong("expiration", this.expiration);
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.id = tag.getUUID("id");
      this.playerId = tag.getUUID("playerId");
      this.task = Task.fromTag(tag.getCompound("task"));
      this.expiration = tag.getLong("expiration");
   }
}
