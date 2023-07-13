package iskallia.vault.skill.ability.cooldown;

import net.minecraft.network.FriendlyByteBuf;

public class CooldownInstance {
   public static final CooldownInstance EMPTY = new CooldownInstance(0, 0, 0);
   private final int originalCooldownTicks;
   private int remainingCooldownTicks;
   private int remainingCooldownDelayTicks;

   CooldownInstance(int originalCooldownTicks, int remainingCooldownTicks, int remainingCooldownDelayTicks) {
      this.originalCooldownTicks = originalCooldownTicks;
      this.remainingCooldownTicks = remainingCooldownTicks;
      this.remainingCooldownDelayTicks = remainingCooldownDelayTicks;
   }

   void decrement() {
      if (this.remainingCooldownDelayTicks > 0) {
         this.remainingCooldownDelayTicks--;
      } else {
         this.remainingCooldownTicks--;
      }
   }

   public int getOriginalTicks() {
      return this.originalCooldownTicks;
   }

   public int getRemainingTicks() {
      return this.remainingCooldownTicks;
   }

   public int getRemainingDelayTicks() {
      return this.remainingCooldownDelayTicks;
   }

   public static CooldownInstance read(FriendlyByteBuf buffer) {
      return new CooldownInstance(buffer.readInt(), buffer.readInt(), buffer.readInt());
   }

   public void write(FriendlyByteBuf buffer) {
      buffer.writeInt(this.originalCooldownTicks);
      buffer.writeInt(this.remainingCooldownTicks);
      buffer.writeInt(this.remainingCooldownDelayTicks);
   }
}
