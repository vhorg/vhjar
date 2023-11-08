package iskallia.vault.network.message;

import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.ArtifactBossImmunityParticleOptions;
import iskallia.vault.init.ModParticles;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundArtifactBossImmunityParticleMessage {
   public final Vec3 pos;
   public final int color;
   public final int color2;

   public ClientboundArtifactBossImmunityParticleMessage(Vec3 pos, int color, int color2) {
      this.pos = pos;
      this.color = color;
      this.color2 = color2;
   }

   public static void encode(ClientboundArtifactBossImmunityParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.pos.x);
      buffer.writeDouble(message.pos.y);
      buffer.writeDouble(message.pos.z);
      buffer.writeInt(message.color);
      buffer.writeInt(message.color2);
   }

   public static ClientboundArtifactBossImmunityParticleMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundArtifactBossImmunityParticleMessage(
         new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readInt(), buffer.readInt()
      );
   }

   public static void handle(ClientboundArtifactBossImmunityParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> renderParticles(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void renderParticles(ClientboundArtifactBossImmunityParticleMessage message) {
      renderParticlesFirst(new Vec3(message.pos.x, message.pos.y, message.pos.z), message.color, message.color2);
   }

   @OnlyIn(Dist.CLIENT)
   public static void renderParticlesFirst(Vec3 pos, int color, int color2) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         float r = (color >> 16 & 0xFF) / 255.0F;
         float g = (color >> 8 & 0xFF) / 255.0F;
         float b = (color & 0xFF) / 255.0F;
         float r2 = (color2 >> 16 & 0xFF) / 255.0F;
         float g2 = (color2 >> 8 & 0xFF) / 255.0F;
         float b2 = (color2 & 0xFF) / 255.0F;

         for (int i = 0; i < 40; i++) {
            Vec3 vec33 = new Vec3(4.0, 9.0, 0.0).yRot((float)Math.toRadians(i * 9.0F + 180.0F));
            level.addParticle(
               new ArtifactBossImmunityParticleOptions(
                  (ParticleType<ArtifactBossImmunityParticleOptions>)ModParticles.ARTIFACT_BOSS_IMMUNITY.get(),
                  15,
                  new Vector3f(r, g, b),
                  new Vector3f(r2, g2, b2)
               ),
               true,
               pos.x,
               pos.y - 0.5,
               pos.z,
               vec33.x,
               vec33.y,
               vec33.z
            );
         }
      }
   }
}
