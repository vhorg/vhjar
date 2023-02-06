package iskallia.vault.network.message;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundPlayerLastDamageSourceMessage {
   private static final BiMap<Byte, DamageSource> DAMAGE_SOURCES = HashBiMap.create();
   private final DamageSource damageSource;

   public ClientboundPlayerLastDamageSourceMessage(DamageSource damageSource) {
      this.damageSource = damageSource;
   }

   public static void encode(ClientboundPlayerLastDamageSourceMessage message, FriendlyByteBuf buffer) {
      buffer.writeByte((Byte)DAMAGE_SOURCES.inverse().get(message.damageSource));
   }

   public static ClientboundPlayerLastDamageSourceMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundPlayerLastDamageSourceMessage((DamageSource)DAMAGE_SOURCES.get(buffer.readByte()));
   }

   public static void handle(ClientboundPlayerLastDamageSourceMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> updatePlayerDamageSource(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void updatePlayerDamageSource(ClientboundPlayerLastDamageSourceMessage message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         player.lastDamageSource = message.damageSource;
         player.lastDamageStamp = player.level.getGameTime();
      }
   }

   static {
      DAMAGE_SOURCES.put((byte)0, DamageSource.MAGIC);
      DAMAGE_SOURCES.put((byte)1, DamageSource.WITHER);
   }
}
