package iskallia.vault.gear.trinket;

import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.item.gear.TrinketItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrinketHelper {
   public static List<TrinketHelper.TrinketStack<TrinketEffect<?>>> getTrinkets(LivingEntity entity) {
      return getTrinkets(IntegrationCurios.getCuriosItemStacks(entity), TrinketEffect.class);
   }

   public static <T extends TrinketEffect<?>> List<TrinketHelper.TrinketStack<T>> getTrinkets(LivingEntity entity, Class<T> trinketClass) {
      return getTrinkets(IntegrationCurios.getCuriosItemStacks(entity), trinketClass);
   }

   public static <T extends TrinketEffect<?>> List<TrinketHelper.TrinketStack<T>> getTrinkets(
      Map<String, List<Tuple<ItemStack, Integer>>> slotCurios, Class<? super T> trinketClass
   ) {
      List<TrinketHelper.TrinketStack<T>> trinkets = new ArrayList<>();
      slotCurios.values().forEach(curios -> curios.forEach(curioTpl -> {
         ItemStack curioStack = (ItemStack)curioTpl.getA();
         if (curioStack.getItem() instanceof TrinketItem) {
            if (TrinketItem.hasUsesLeft(curioStack)) {
               TrinketItem.getTrinket(curioStack).ifPresent(trinket -> {
                  if (trinketClass.isInstance(trinket)) {
                     trinkets.add(new TrinketHelper.TrinketStack<>(curioStack, (T)trinket));
                  }
               });
            }
         }
      }));
      return trinkets;
   }

   public record TrinketStack<T extends TrinketEffect<?>>(ItemStack stack, T trinket) {
      public boolean isUsable(Player player) {
         return this.trinket().isUsable(this.stack(), player);
      }
   }
}
