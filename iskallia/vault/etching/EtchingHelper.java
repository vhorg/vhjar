package iskallia.vault.etching;

import iskallia.vault.event.event.VaultGearEquipmentChangeEvent;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.world.data.PlayerEtchingData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class EtchingHelper {
   @SubscribeEvent
   public static void onTick(ServerTickEvent event) {
      if (event.phase != Phase.END) {
         MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
         PlayerEtchingData.get(srv).tick(srv);
      }
   }

   @SubscribeEvent
   public static void onGearChange(VaultGearEquipmentChangeEvent event) {
      PlayerEtchingData.get(event.getPlayer().server).refreshEtchingSets(event.getPlayer());
   }

   public static List<EtchingSet<?>> getEquippedEtchings(LivingEntity entity) {
      List<EtchingSet<?>> foundEtchings = new ArrayList<>();
      Map<EtchingSet<?>, Integer> equippedEtchings = getEquippedEtchingCount(entity);
      equippedEtchings.forEach((etchingSet, count) -> {
         if (count >= 4) {
            foundEtchings.add((EtchingSet<?>)etchingSet);
         }
      });
      return foundEtchings;
   }

   public static Map<EtchingSet<?>, Integer> getEquippedEtchingCount(LivingEntity entity) {
      Map<EtchingSet<?>, Integer> etchings = new HashMap<>();

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = entity.getItemBySlot(slot);
         if (!stack.isEmpty() && stack.getItem() instanceof VaultGearItem) {
            VaultGearItem item = VaultGearItem.of(stack);
            if (item.isIntendedForSlot(stack, slot)) {
               VaultGearData data = VaultGearData.read(stack);
               EtchingSet<?> etching = data.getFirstValue(ModGearAttributes.ETCHING).orElse(null);
               if (etching != null) {
                  int count = etchings.getOrDefault(etching, 0);
                  etchings.put(etching, count + 1);
               }
            }
         }
      }

      return etchings;
   }
}
