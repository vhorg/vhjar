package iskallia.vault.network.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import iskallia.vault.client.util.ParticleHelper;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

public class EffectMessage {
   private final EffectMessage.Type effectType;
   private final Vec3 pos;
   private FriendlyByteBuf data = null;
   private Consumer<FriendlyByteBuf> encoder = buf -> {};

   public EffectMessage(EffectMessage.Type effectType, Vec3 pos) {
      this.effectType = effectType;
      this.pos = pos;
   }

   public EffectMessage.Type getEffectType() {
      return this.effectType;
   }

   public Vec3 getPos() {
      return this.pos;
   }

   public FriendlyByteBuf getData() {
      return this.data;
   }

   public EffectMessage addData(Consumer<FriendlyByteBuf> encoder) {
      this.encoder = this.encoder.andThen(encoder);
      return this;
   }

   public static void encode(EffectMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeEnum(pkt.effectType);
      buffer.writeDouble(pkt.pos.x);
      buffer.writeDouble(pkt.pos.y);
      buffer.writeDouble(pkt.pos.z);
      pkt.encoder.accept(buffer);
   }

   public static EffectMessage decode(FriendlyByteBuf buffer) {
      EffectMessage.Type type = (EffectMessage.Type)buffer.readEnum(EffectMessage.Type.class);
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      EffectMessage pkt = new EffectMessage(type, new Vec3(x, y, z));
      ByteBuf buf = Unpooled.buffer(buffer.readableBytes());
      buffer.readBytes(buf);
      pkt.data = new FriendlyByteBuf(buf);
      return pkt;
   }

   public static void handle(EffectMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ParticleHelper.spawnParticle(pkt));
      context.setPacketHandled(true);
   }

   public static enum Type {
      COLORED_FIREWORK;
   }
}
