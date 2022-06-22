package iskallia.vault.network.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import iskallia.vault.client.util.ClientEffectHelper;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class EffectMessage {
   private final EffectMessage.Type effectType;
   private final Vector3d pos;
   private PacketBuffer data = null;
   private Consumer<PacketBuffer> encoder = buf -> {};

   public EffectMessage(EffectMessage.Type effectType, Vector3d pos) {
      this.effectType = effectType;
      this.pos = pos;
   }

   public static EffectMessage playSound(SoundEvent event, SoundCategory category, float pitch, float volume) {
      EffectMessage msg = new EffectMessage(EffectMessage.Type.PLAY_SOUND, Vector3d.field_186680_a);
      msg.addData(buf -> {
         buf.func_180714_a(event.getRegistryName().toString());
         buf.func_179249_a(category);
         buf.writeFloat(pitch);
         buf.writeFloat(volume);
      });
      return msg;
   }

   public EffectMessage.Type getEffectType() {
      return this.effectType;
   }

   public Vector3d getPos() {
      return this.pos;
   }

   public PacketBuffer getData() {
      return this.data;
   }

   public EffectMessage addData(Consumer<PacketBuffer> encoder) {
      this.encoder = this.encoder.andThen(encoder);
      return this;
   }

   public static void encode(EffectMessage pkt, PacketBuffer buffer) {
      buffer.func_179249_a(pkt.effectType);
      buffer.writeDouble(pkt.pos.field_72450_a);
      buffer.writeDouble(pkt.pos.field_72448_b);
      buffer.writeDouble(pkt.pos.field_72449_c);
      pkt.encoder.accept(buffer);
   }

   public static EffectMessage decode(PacketBuffer buffer) {
      EffectMessage.Type type = (EffectMessage.Type)buffer.func_179257_a(EffectMessage.Type.class);
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      EffectMessage pkt = new EffectMessage(type, new Vector3d(x, y, z));
      ByteBuf buf = Unpooled.buffer(buffer.readableBytes());
      buffer.readBytes(buf);
      pkt.data = new PacketBuffer(buf);
      return pkt;
   }

   public static void handle(EffectMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientEffectHelper.doEffect(pkt));
      context.setPacketHandled(true);
   }

   public static enum Type {
      COLORED_FIREWORK,
      PLAY_SOUND;
   }
}
