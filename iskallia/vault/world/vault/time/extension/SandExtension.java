package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class SandExtension extends TimeExtension {
   public static final ResourceLocation ID = VaultMod.id("sand");
   protected UUID player;
   protected int amount;

   public SandExtension() {
   }

   public SandExtension(UUID player, int amount, long extraTime) {
      this(ID, player, amount, extraTime);
   }

   public SandExtension(ResourceLocation id, UUID player, int amount, long extraTime) {
      super(id, extraTime);
      this.player = player;
      this.amount = amount;
   }

   public UUID getPlayer() {
      return this.player;
   }

   public int getAmount() {
      return this.amount;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putString("Player", this.player.toString());
      nbt.putInt("Amount", this.amount);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.player = UUID.fromString(nbt.getString("Player"));
      this.amount = nbt.getInt("Amount");
   }
}
