package iskallia.vault.network.message;

import iskallia.vault.init.ModSounds;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class VaultEscapeMessage {
   public static void encode(VaultEscapeMessage message, PacketBuffer buffer) {
   }

   public static VaultEscapeMessage decode(PacketBuffer buffer) {
      return new VaultEscapeMessage();
   }

   public static void handle(VaultEscapeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> playEscapeSound());
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void playEscapeSound() {
      Minecraft minecraft = Minecraft.func_71410_x();
      SoundHandler soundHandler = minecraft.func_147118_V();
      soundHandler.func_147682_a(SimpleSound.func_194007_a(ModSounds.VAULT_PORTAL_LEAVE, 1.0F, 1.0F));
   }
}
