package iskallia.vault.snapshot;

import iskallia.vault.event.event.VaultGearEquipmentChangeEvent;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.integration.IntegrationCurios;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class AttributeSnapshotNotifier {
   private static final Map<UUID, AttributeSnapshotNotifier.PlayerGearSnapshot> gearSnapshots = new HashMap<>();

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.player instanceof ServerPlayer sPlayer) {
         if (event.phase == Phase.END) {
            boolean snapshotRefresh = false;
            UUID playerId = sPlayer.getUUID();
            AttributeSnapshotNotifier.PlayerGearSnapshot snapshot = gearSnapshots.computeIfAbsent(
               playerId, id -> new AttributeSnapshotNotifier.PlayerGearSnapshot()
            );

            for (EquipmentSlot slot : EquipmentSlot.values()) {
               UUID savedId = snapshot.gearIdentifiers.get(slot);
               ItemStack equipment = event.player.getItemBySlot(slot);
               if (!equipment.isEmpty() && AttributeGearData.hasData(equipment)) {
                  UUID equipmentId = AttributeGearData.readUUID(equipment).orElseThrow();
                  if (savedId == null) {
                     gearChanged(sPlayer, slot);
                     snapshotRefresh = true;
                     snapshot.gearIdentifiers.put(slot, equipmentId);
                  } else if (!savedId.equals(equipmentId)) {
                     gearChanged(sPlayer, slot);
                     snapshotRefresh = true;
                     snapshot.gearIdentifiers.put(slot, equipmentId);
                  }
               } else if (savedId != null) {
                  gearChanged(sPlayer, slot);
                  snapshotRefresh = true;
                  snapshot.gearIdentifiers.remove(slot);
               }
            }

            for (Entry<String, List<ItemStack>> entry : IntegrationCurios.getCuriosItemStacks(sPlayer).entrySet()) {
               String slotIdentifier = entry.getKey();
               List<ItemStack> stacks = entry.getValue();

               for (int slotx = 0; slotx < stacks.size(); slotx++) {
                  ItemStack equipped = stacks.get(slotx);
                  AttributeSnapshotNotifier.CurioSlot curioSlot = new AttributeSnapshotNotifier.CurioSlot(slotIdentifier, slotx);
                  UUID savedId = snapshot.curioIdentifiers.get(curioSlot);
                  if (!equipped.isEmpty() && AttributeGearData.hasData(equipped)) {
                     UUID equipmentId = AttributeGearData.readUUID(equipped).orElseThrow();
                     if (savedId == null) {
                        curioChanged(sPlayer, curioSlot);
                        snapshotRefresh = true;
                        snapshot.curioIdentifiers.put(curioSlot, equipmentId);
                     } else if (!savedId.equals(equipmentId)) {
                        curioChanged(sPlayer, curioSlot);
                        snapshotRefresh = true;
                        snapshot.curioIdentifiers.put(curioSlot, equipmentId);
                     }
                  } else if (savedId != null) {
                     curioChanged(sPlayer, curioSlot);
                     snapshotRefresh = true;
                     snapshot.curioIdentifiers.remove(curioSlot);
                  }
               }
            }

            if (snapshotRefresh) {
               AttributeSnapshotHelper.getInstance().refreshSnapshot(sPlayer);
            }
         }
      }
   }

   private static void gearChanged(ServerPlayer player, EquipmentSlot slot) {
      MinecraftForge.EVENT_BUS.post(new VaultGearEquipmentChangeEvent.Gear(player, slot));
   }

   private static void curioChanged(ServerPlayer player, AttributeSnapshotNotifier.CurioSlot slot) {
      MinecraftForge.EVENT_BUS.post(new VaultGearEquipmentChangeEvent.Curio(player, slot.slotIdentifier(), slot.slotId()));
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onPotionAdded(PotionAddedEvent event) {
      if (event.getOldPotionEffect() != null) {
         MobEffectInstance existing = event.getOldPotionEffect();
         MobEffectInstance added = event.getPotionEffect();
         if (existing.getAmplifier() >= added.getAmplifier()) {
            return;
         }
      }

      refreshSnapshotLater(event);
   }

   @SubscribeEvent
   public static void onPotionRemove(PotionRemoveEvent event) {
      refreshSnapshotLater(event);
   }

   @SubscribeEvent
   public static void onPotionExpiry(PotionExpiryEvent event) {
      refreshSnapshotLater(event);
   }

   private static void refreshSnapshotLater(EntityEvent event) {
      if (event.getEntity() instanceof ServerPlayer sPlayer) {
         AttributeSnapshotHelper.getInstance().refreshSnapshotDelayed(sPlayer);
      }
   }

   private record CurioSlot(String slotIdentifier, int slotId) {
      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            AttributeSnapshotNotifier.CurioSlot curioSlot = (AttributeSnapshotNotifier.CurioSlot)o;
            return this.slotId == curioSlot.slotId && Objects.equals(this.slotIdentifier, curioSlot.slotIdentifier);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.slotIdentifier, this.slotId);
      }
   }

   private static class PlayerGearSnapshot {
      private final Map<EquipmentSlot, UUID> gearIdentifiers = new HashMap<>();
      private final Map<AttributeSnapshotNotifier.CurioSlot, UUID> curioIdentifiers = new HashMap<>();
   }
}
