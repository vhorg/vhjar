package iskallia.vault.network.message;

import iskallia.vault.world.data.GlobalDifficultyData;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class GlobalDifficultyMessage {
   public CompoundNBT data;

   public static void encode(GlobalDifficultyMessage message, PacketBuffer buffer) {
      buffer.func_150786_a(message.data);
   }

   public static GlobalDifficultyMessage decode(PacketBuffer buffer) {
      GlobalDifficultyMessage message = new GlobalDifficultyMessage();
      message.data = buffer.func_150793_b();
      return message;
   }

   public static void handle(GlobalDifficultyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayerEntity player = context.getSender();
         if (player != null) {
            CompoundNBT data = message.data;
            GlobalDifficultyData.Difficulty vaultDifficulty = GlobalDifficultyData.Difficulty.values()[data.func_74762_e("VaultDifficulty")];
            GlobalDifficultyData.Difficulty crystalCost = GlobalDifficultyData.Difficulty.values()[data.func_74762_e("CrystalCost")];
            ServerWorld world = player.func_71121_q();
            GlobalDifficultyData difficultyData = GlobalDifficultyData.get(world);
            if (difficultyData.getVaultDifficulty() == null) {
               GlobalDifficultyData.get(world).setVaultDifficulty(vaultDifficulty);
               GlobalDifficultyData.get(world).setCrystalCost(crystalCost);
            }
         }
      });
      context.setPacketHandled(true);
   }

   public static GlobalDifficultyMessage setGlobalDifficultyOptions(CompoundNBT data) {
      GlobalDifficultyMessage message = new GlobalDifficultyMessage();
      message.data = data;
      return message;
   }
}
