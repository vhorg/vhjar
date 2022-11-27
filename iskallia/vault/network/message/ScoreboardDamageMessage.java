package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.ArenaScoreboardOverlay;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ScoreboardDamageMessage {
   public String nickname;
   public float damageDealt;

   public ScoreboardDamageMessage() {
   }

   public ScoreboardDamageMessage(String nickname, float damageDealt) {
      this.nickname = nickname;
      this.damageDealt = damageDealt;
   }

   public static void encode(ScoreboardDamageMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.nickname, 32767);
      buffer.writeFloat(message.damageDealt);
   }

   public static ScoreboardDamageMessage decode(FriendlyByteBuf buffer) {
      ScoreboardDamageMessage message = new ScoreboardDamageMessage();
      message.nickname = buffer.readUtf(32767);
      message.damageDealt = buffer.readFloat();
      return message;
   }

   public static void handle(ScoreboardDamageMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ArenaScoreboardOverlay.scoreboard.onDamageDealt(message.nickname, message.damageDealt));
      context.setPacketHandled(true);
   }
}
