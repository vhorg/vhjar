package iskallia.vault.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class AnvilInventoryTick {
   @SubscribeEvent
   public static void on(PlayerTickEvent event) {
      Player player = event.player;
      if (player.containerMenu instanceof AnvilMenu anvilMenu) {
         inventoryTick(anvilMenu, 0, player.level, player);
         inventoryTick(anvilMenu, 1, player.level, player);
      }
   }

   private static void inventoryTick(AnvilMenu anvilMenu, int slot, Level level, Player player) {
      ItemStack itemStack = anvilMenu.getSlot(slot).getItem();
      if (!itemStack.isEmpty()) {
         itemStack.inventoryTick(level, player, slot, false);
      }
   }
}
