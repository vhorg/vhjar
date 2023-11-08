package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerEquipmentSwapEvent extends Event<PlayerEquipmentSwapEvent, PlayerEquipmentSwapEvent.Data> {
   public PlayerEquipmentSwapEvent() {
   }

   protected PlayerEquipmentSwapEvent(PlayerEquipmentSwapEvent parent) {
      super(parent);
   }

   public PlayerEquipmentSwapEvent createChild() {
      return new PlayerEquipmentSwapEvent(this);
   }

   public PlayerEquipmentSwapEvent.Data invoke(Player player, ItemStack from, ItemStack to, EquipmentSlot slot) {
      return this.invoke(new PlayerEquipmentSwapEvent.Data(player, from, to, slot));
   }

   public record Data(Player player, ItemStack from, ItemStack to, EquipmentSlot slot) {
   }
}
