package iskallia.vault.network.message;

import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.BlackMarketExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerBlackMarketData;
import iskallia.vault.world.data.PlayerExpertisesData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundResetBlackMarketTradesMessage {
   public static final ServerboundResetBlackMarketTradesMessage INSTANCE = new ServerboundResetBlackMarketTradesMessage();

   public static void encode(ServerboundResetBlackMarketTradesMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundResetBlackMarketTradesMessage decode(FriendlyByteBuf buffer) {
      return INSTANCE;
   }

   public static void handle(ServerboundResetBlackMarketTradesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      if (context.getSender() != null) {
         PlayerBlackMarketData.BlackMarket playerMarket = PlayerBlackMarketData.get(context.getSender().server).getBlackMarket(context.getSender());
         ExpertiseTree expertises = PlayerExpertisesData.get((ServerLevel)context.getSender().level).getExpertises(context.getSender());

         for (BlackMarketExpertise expertise : expertises.getAll(BlackMarketExpertise.class, Skill::isUnlocked)) {
            if (playerMarket.getResetRolls() >= expertise.getNumberOfRolls()) {
               return;
            }
         }

         playerMarket.resetTradesWithoutTimer(context.getSender());
      }

      context.setPacketHandled(true);
   }
}
