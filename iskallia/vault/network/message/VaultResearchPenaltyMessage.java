package iskallia.vault.network.message;

import iskallia.vault.init.ModGameRules;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.research.ResearchTree;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultResearchPenaltyMessage {
   public static class C2S {
      public static void encode(VaultResearchPenaltyMessage.C2S message, FriendlyByteBuf buffer) {
      }

      public static VaultResearchPenaltyMessage.C2S decode(FriendlyByteBuf buffer) {
         return new VaultResearchPenaltyMessage.C2S();
      }

      public static void handle(VaultResearchPenaltyMessage.C2S message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         boolean gamerule = context.getSender().getServer().getGameRules().getBoolean(ModGameRules.NO_RESEARCH_TEAM_PENALTY);
         ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(context::getSender), new VaultResearchPenaltyMessage.S2C(gamerule));
         ResearchTree.setResearchGamerule(gamerule);
         context.setPacketHandled(true);
      }
   }

   public static class S2C {
      private boolean isPenalty;

      public S2C(boolean pen) {
         this.isPenalty = pen;
      }

      public static void encode(VaultResearchPenaltyMessage.S2C message, FriendlyByteBuf buffer) {
         buffer.writeBoolean(message.isPenalty);
      }

      public static VaultResearchPenaltyMessage.S2C decode(FriendlyByteBuf buffer) {
         return new VaultResearchPenaltyMessage.S2C(buffer.readBoolean());
      }

      public static void handle(VaultResearchPenaltyMessage.S2C message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(message.isPenalty)));
         context.setPacketHandled(true);
      }

      public static void handleClient(boolean isPenalty) {
         ResearchTree.setResearchGamerule(isPenalty);
      }
   }
}
