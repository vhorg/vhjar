package iskallia.vault.world.vault.influence;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultInfluence implements INBTSerializable<CompoundNBT> {
   private final ResourceLocation key;

   public VaultInfluence(ResourceLocation key) {
      this.key = key;
   }

   public final ResourceLocation getKey() {
      return this.key;
   }

   public void apply(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
   }

   public void remove(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
   }

   public void tick(VaultRaid vault, VaultPlayer player, ServerWorld world) {
   }

   public CompoundNBT serializeNBT() {
      return new CompoundNBT();
   }

   public void deserializeNBT(CompoundNBT tag) {
   }
}
