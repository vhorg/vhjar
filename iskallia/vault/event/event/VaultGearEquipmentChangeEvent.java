package iskallia.vault.event.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.eventbus.api.Event;

public class VaultGearEquipmentChangeEvent extends Event {
   private final ServerPlayer player;

   public VaultGearEquipmentChangeEvent(ServerPlayer player) {
      this.player = player;
   }

   public ServerPlayer getPlayer() {
      return this.player;
   }

   public static class Curio extends VaultGearEquipmentChangeEvent {
      private final String curioIdentifier;
      private final int curioSlotId;

      public Curio(ServerPlayer player, String curioIdentifier, int curioSlotId) {
         super(player);
         this.curioIdentifier = curioIdentifier;
         this.curioSlotId = curioSlotId;
      }

      public String getCurioIdentifier() {
         return this.curioIdentifier;
      }

      public int getCurioSlotId() {
         return this.curioSlotId;
      }
   }

   public static class Gear extends VaultGearEquipmentChangeEvent {
      private final EquipmentSlot slot;

      public Gear(ServerPlayer player, EquipmentSlot slot) {
         super(player);
         this.slot = slot;
      }

      public EquipmentSlot getSlot() {
         return this.slot;
      }
   }
}
