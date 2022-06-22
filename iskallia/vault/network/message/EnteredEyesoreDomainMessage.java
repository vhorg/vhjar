package iskallia.vault.network.message;

import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class EnteredEyesoreDomainMessage {
   public static void encode(EnteredEyesoreDomainMessage message, PacketBuffer buffer) {
      CompoundNBT nbt = new CompoundNBT();
      buffer.func_150786_a(nbt);
   }

   public static EnteredEyesoreDomainMessage decode(PacketBuffer buffer) {
      return new EnteredEyesoreDomainMessage();
   }

   public static void handle(EnteredEyesoreDomainMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            Minecraft minecraft = Minecraft.func_71410_x();
            ClientPlayerEntity player = minecraft.field_71439_g;
            ClientWorld world = minecraft.field_71441_e;
            if (world != null) {
               if (player != null) {
                  Vector3d pos = player.func_213303_ch();
                  int count = 4;

                  for (int dx = -count; dx <= count; dx++) {
                     for (int dy = -count; dy <= count; dy++) {
                        for (int dz = -count; dz <= count; dz++) {
                           world.func_195590_a(
                              (IParticleData)ModParticles.EYESORE_APPEARANCE.get(),
                              true,
                              pos.field_72450_a + dx * 8,
                              pos.field_72448_b + dy * 8,
                              pos.field_72449_c + dz * 8,
                              0.0,
                              0.0,
                              0.0
                           );
                        }
                     }
                  }

                  player.func_213823_a(ModSounds.EYESORE_GRAWL, SoundCategory.HOSTILE, 1.0F, 1.0F);
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
