package iskallia.vault.network.message;

import iskallia.vault.entity.entity.ElixirOrbEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class SummonElixirOrbMessage {
   private final int id;
   private final double x;
   private final double y;
   private final double z;
   private final int age;
   private final int size;

   public SummonElixirOrbMessage(ElixirOrbEntity entity) {
      this.id = entity.getId();
      this.x = entity.getX();
      this.y = entity.getY();
      this.z = entity.getZ();
      this.age = entity.getAge();
      this.size = entity.getSize();
   }

   public SummonElixirOrbMessage(FriendlyByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.x = buffer.readDouble();
      this.y = buffer.readDouble();
      this.z = buffer.readDouble();
      this.age = buffer.readVarInt();
      this.size = buffer.readVarInt();
   }

   public int getId() {
      return this.id;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public int getAge() {
      return this.age;
   }

   public int getSize() {
      return this.size;
   }

   public static void encode(SummonElixirOrbMessage message, FriendlyByteBuf buffer) {
      buffer.writeVarInt(message.id);
      buffer.writeDouble(message.x);
      buffer.writeDouble(message.y);
      buffer.writeDouble(message.z);
      buffer.writeVarInt(message.age);
      buffer.writeVarInt(message.size);
   }

   public static SummonElixirOrbMessage decode(FriendlyByteBuf buffer) {
      return new SummonElixirOrbMessage(buffer);
   }

   public static void handle(SummonElixirOrbMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> spawnElixirOrb(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void spawnElixirOrb(SummonElixirOrbMessage message) {
      ClientLevel world = Minecraft.getInstance().level;
      if (world != null) {
         Entity entity = new ElixirOrbEntity(world, message.getX(), message.getY(), message.getZ(), message.getSize(), message.getAge());
         entity.setPacketCoordinates(message.getX(), message.getY(), message.getZ());
         entity.setYRot(0.0F);
         entity.setXRot(0.0F);
         entity.setId(message.getId());
         world.putNonPlayerEntity(message.getId(), entity);
      }
   }
}
