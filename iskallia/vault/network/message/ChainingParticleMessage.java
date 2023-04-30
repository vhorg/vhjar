package iskallia.vault.network.message;

import iskallia.vault.init.ModParticles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ChainingParticleMessage {
   private final List<Vec3> chainingPos;

   public ChainingParticleMessage(List<Vec3> chainingPos) {
      this.chainingPos = chainingPos;
   }

   public static void encode(ChainingParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.chainingPos.size());

      for (int i = 0; i < message.chainingPos.size(); i++) {
         buffer.writeDouble(message.chainingPos.get(i).x);
         buffer.writeDouble(message.chainingPos.get(i).y);
         buffer.writeDouble(message.chainingPos.get(i).z);
      }
   }

   public static ChainingParticleMessage decode(FriendlyByteBuf buffer) {
      List<Vec3> list = new ArrayList<>();
      int size = buffer.readInt();

      for (int i = 0; i < size; i++) {
         list.add(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
      }

      return new ChainingParticleMessage(list);
   }

   public static void handle(ChainingParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.chainingPos);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(List<Vec3> pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < pos.size(); i++) {
            Random random = level.getRandom();
            if (i + 1 < pos.size()) {
               Vec3 pos1 = pos.get(i);
               Vec3 pos2 = pos.get(i + 1);
               double dist = pos1.distanceTo(pos2);
               double delta = 0.0;

               for (int count = (int)(dist / 0.15F); delta < 1.0; delta += count == 0 ? 0.15F : 1.0F / count) {
                  Vec3 chainingPos = pos1.lerp(pos2, delta);
                  Vec3 offset = new Vec3(0.0, 0.0, 0.0);
                  level.addParticle(
                     (ParticleOptions)ModParticles.CHAINING.get(),
                     true,
                     chainingPos.x() + offset.x,
                     chainingPos.y() + random.nextDouble() * 0.15F,
                     chainingPos.z() + offset.z,
                     offset.x / 2.0,
                     random.nextDouble() * 0.1,
                     offset.z / 2.0
                  );
               }
            }
         }
      }
   }
}
