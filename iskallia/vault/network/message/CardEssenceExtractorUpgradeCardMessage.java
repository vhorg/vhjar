package iskallia.vault.network.message;

import iskallia.vault.block.entity.CardEssenceExtractorTileEntity;
import iskallia.vault.config.CardEssenceExtractorConfig;
import iskallia.vault.container.inventory.CardEssenceExtractorContainer;
import iskallia.vault.core.card.Card;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.CardItem;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class CardEssenceExtractorUpgradeCardMessage {
   public static final CardEssenceExtractorUpgradeCardMessage INSTANCE = new CardEssenceExtractorUpgradeCardMessage();

   private CardEssenceExtractorUpgradeCardMessage() {
   }

   public static void encode(CardEssenceExtractorUpgradeCardMessage msg, FriendlyByteBuf buf) {
   }

   public static CardEssenceExtractorUpgradeCardMessage decode(FriendlyByteBuf buf) {
      return INSTANCE;
   }

   public static void handle(CardEssenceExtractorUpgradeCardMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
               if (sender.containerMenu instanceof CardEssenceExtractorContainer extractorContainer) {
                  CardEssenceExtractorTileEntity tile = extractorContainer.getTileEntity();
                  if (tile != null && !tile.isRemoved()) {
                     ItemStack upgradeable = tile.getCardUpgradeStack();
                     if (!upgradeable.isEmpty() && upgradeable.getItem() instanceof CardItem) {
                        Card card = CardItem.getCard(upgradeable);
                        int tier = card.getTier();
                        CardEssenceExtractorConfig.TierConfig cfg = ModConfigs.CARD_ESSENCE_EXTRACTOR.getConfig(tier).orElse(null);
                        if (cfg != null && tile.getEssence() >= cfg.getEssencePerUpgrade() && card.canUpgrade()) {
                           ItemStack upgraded = upgradeable.copy();
                           card.onUpgrade();
                           CardItem.setCard(upgraded, card);
                           tile.setCardUpgradeStack(ItemStack.EMPTY);
                           tile.setCardUpgradeOutputStack(upgraded);
                           tile.setEssence(tile.getEssence() - cfg.getEssencePerUpgrade());
                           sender.level
                              .playSound(
                                 null,
                                 sender.getX(),
                                 sender.getY(),
                                 sender.getZ(),
                                 SoundEvents.CONDUIT_DEACTIVATE,
                                 SoundSource.PLAYERS,
                                 0.8F,
                                 sender.level.random.nextFloat() * 0.3F + 1.7F
                              );
                        }
                     }
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
