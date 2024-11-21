package iskallia.vault.container.modifier;

import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.ModifierScrollItem;
import iskallia.vault.network.message.DiscoverModifierScrollMessage;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ModifierScrollContainer extends AbstractElementContainer implements IModifierDiscoveryContainer {
   private final int inventorySlot;
   private final UUID scrollUuid;
   private final List<DiscoverableModifier> gearModifiers;

   public ModifierScrollContainer(int windowId, int inventorySlot, UUID scrollUuid, Player player, List<DiscoverableModifier> gearModifiers) {
      super(ModContainers.MODIFIER_SCROLL_CONTAINER, windowId, player);
      this.inventorySlot = inventorySlot;
      this.scrollUuid = scrollUuid;
      this.gearModifiers = gearModifiers;
   }

   public boolean stillValid(Player player) {
      ItemStack scroll = player.getInventory().getItem(this.inventorySlot);
      if (!scroll.isEmpty() && scroll.getItem() instanceof ModifierScrollItem) {
         UUID playerId = ModifierScrollItem.getPlayerUuid(scroll);
         if (playerId != null && playerId.equals(player.getUUID())) {
            UUID scrollId = ModifierScrollItem.getUuid(scroll);
            return scrollId != null && scrollId.equals(this.scrollUuid);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public List<DiscoverableModifier> getGearModifiers() {
      return Collections.unmodifiableList(this.gearModifiers);
   }

   @Override
   public void tryDiscoverModifier(DiscoverableModifier gearModifier) {
      ModNetwork.CHANNEL.sendToServer(new DiscoverModifierScrollMessage(this.scrollUuid, this.inventorySlot, gearModifier));
   }
}
