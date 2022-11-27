package iskallia.vault.network.message;

import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.entity.FighterEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class FighterSizeMessage {
   private int entityId;
   private float size;

   public FighterSizeMessage() {
   }

   public FighterSizeMessage(int entityId, float size) {
      this.entityId = entityId;
      this.size = size;
   }

   public FighterSizeMessage(Entity entity, float size) {
      this(entity.getId(), size);
   }

   public static void encode(FighterSizeMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.entityId);
      buffer.writeFloat(message.size);
   }

   public static FighterSizeMessage decode(FriendlyByteBuf buffer) {
      FighterSizeMessage message = new FighterSizeMessage();
      message.entityId = buffer.readInt();
      message.size = buffer.readFloat();
      return message;
   }

   public static void handle(FighterSizeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer player = minecraft.player;
         Level world = player.level;
         Entity entity = world.getEntity(message.entityId);
         if (entity != null && entity.isAlive()) {
            if (entity instanceof FighterEntity) {
               ((FighterEntity)entity).changeSize(message.size);
            }

            if (entity instanceof EternalEntity) {
               ((EternalEntity)entity).changeSize(message.size);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
