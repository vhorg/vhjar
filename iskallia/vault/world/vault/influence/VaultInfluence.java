package iskallia.vault.world.vault.influence;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultInfluence implements INBTSerializable<CompoundTag> {
   private final ResourceLocation key;

   public VaultInfluence(ResourceLocation key) {
      this.key = key;
   }

   public final ResourceLocation getKey() {
      return this.key;
   }

   public void apply(VaultRaid vault, VaultPlayer player, ServerLevel world, Random random) {
   }

   public void remove(VaultRaid vault, VaultPlayer player, ServerLevel world, Random random) {
   }

   public void tick(VaultRaid vault, VaultPlayer player, ServerLevel world) {
   }

   public CompoundTag serializeNBT() {
      return new CompoundTag();
   }

   public void deserializeNBT(CompoundTag tag) {
   }
}
