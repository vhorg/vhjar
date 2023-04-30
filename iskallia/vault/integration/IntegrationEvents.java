package iskallia.vault.integration;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class IntegrationEvents {
   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onInteractWithTrader(EntityInteract event) {
      if (event.getTarget() instanceof AbstractVillager trader) {
         MerchantOffers offers = trader.getOffers();
         List<MerchantOffer> toRemove = new ArrayList<>();

         for (MerchantOffer offer : offers) {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(offer.getResult().getItem());
            if (itemId != null && ModConfigs.TRADER_EXCLUSIONS.shouldExclude(itemId)) {
               VaultMod.LOGGER.debug("Removing Trader Item: {}", itemId);
               toRemove.add(offer);
            }
         }

         offers.removeAll(toRemove);
      }
   }
}
