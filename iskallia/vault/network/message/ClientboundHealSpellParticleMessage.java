package iskallia.vault.network.message;

import iskallia.vault.init.ModParticles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundHealSpellParticleMessage(double x, double y, double z, List<Integer> healedEntityIds) {
   public static void encode(ClientboundHealSpellParticleMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeDouble(pkt.x);
      buffer.writeDouble(pkt.y);
      buffer.writeDouble(pkt.z);
      buffer.writeCollection(pkt.healedEntityIds, FriendlyByteBuf::writeInt);
   }

   public static ClientboundHealSpellParticleMessage decode(FriendlyByteBuf buffer) {
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      List<Integer> healedEntityIds = (List<Integer>)buffer.readCollection(ArrayList::new, FriendlyByteBuf::readInt);
      return new ClientboundHealSpellParticleMessage(x, y, z, healedEntityIds);
   }

   public static void handle(ClientboundHealSpellParticleMessage pkt, Supplier<Context> contextSupplier) {
      createParticles(pkt.x, pkt.y, pkt.z, pkt.healedEntityIds);
      contextSupplier.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void createParticles(double x, double y, double z, List<Integer> healedEntityIds) {
      ClientLevel level = Minecraft.getInstance().level;
      healedEntityIds.forEach(healedEntityId -> {
         Random rnd = level.random;

         for (int i = 0; i < 2 + rnd.nextInt(4); i++) {
            double xStart = x + (rnd.nextFloat() - 0.5) * 0.2;
            double yStart = y + (rnd.nextFloat() - 0.5) * 0.2;
            double zStart = z + (rnd.nextFloat() - 0.5) * 0.2;
            level.addParticle((ParticleOptions)ModParticles.HEAL_SPELL.get(), xStart, yStart, zStart, healedEntityId.intValue(), 0.0, 0.0);
         }
      });
   }
}
