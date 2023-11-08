package iskallia.vault.gear.charm;

import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.item.gear.CharmItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CharmHelper {
   public static List<CharmHelper.CharmStack<CharmEffect<?>>> getCharms(LivingEntity entity) {
      return getCharms(IntegrationCurios.getCuriosItemStacks(entity), CharmEffect.class);
   }

   public static <T extends CharmEffect<?>> List<CharmHelper.CharmStack<T>> getCharms(LivingEntity entity, Class<T> charmClass) {
      return getCharms(IntegrationCurios.getCuriosItemStacks(entity), charmClass);
   }

   public static <T extends CharmEffect<?>> List<CharmHelper.CharmStack<T>> getCharms(
      Map<String, List<Tuple<ItemStack, Integer>>> slotCurios, Class<? super T> charmClass
   ) {
      List<CharmHelper.CharmStack<T>> charms = new ArrayList<>();
      slotCurios.values().forEach(curios -> curios.forEach(curioTpl -> {
         ItemStack curioStack = (ItemStack)curioTpl.getA();
         if (curioStack.getItem() instanceof CharmItem) {
            CharmItem.getCharm(curioStack).ifPresent(charm -> {
               if (charmClass.isInstance(charm)) {
                  charms.add(new CharmHelper.CharmStack<>(curioStack, (T)charm));
               }
            });
         }
      }));
      return charms;
   }

   public record CharmStack<T extends CharmEffect<?>>(ItemStack stack, T charm) {
      public boolean isUsable(Player player) {
         return this.charm().isUsable(this.stack(), player);
      }
   }
}
