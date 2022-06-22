package iskallia.vault.world.vault.time.extension;

import iskallia.vault.Vault;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class SandExtension extends TimeExtension {
   public static final ResourceLocation ID = Vault.id("sand");
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
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      nbt.func_74778_a("Player", this.player.toString());
      nbt.func_74768_a("Amount", this.amount);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.player = UUID.fromString(nbt.func_74779_i("Player"));
      this.amount = nbt.func_74762_e("Amount");
   }
}
