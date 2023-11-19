package iskallia.vault.integration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.CuriosHelper.SlotAttributeWrapper;
import top.theillusivec4.curios.common.network.NetworkHandler;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncStack;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncStack.HandlerType;

public class IntegrationCurios {
   public static Collection<CompoundTag> getSerializedCuriosItemStacks(Player player) {
      return player.getCapability(CuriosCapability.INVENTORY).map(inv -> {
         List<CompoundTag> stacks = new ArrayList<>();

         for (ICurioStacksHandler handle : inv.getCurios().values()) {
            IDynamicStackHandler stackHandler = handle.getStacks();

            for (int index = 0; index < stackHandler.getSlots(); index++) {
               ItemStack stack = stackHandler.getStackInSlot(index);
               if (!stack.isEmpty()) {
                  stacks.add(stack.serializeNBT());
               }
            }
         }

         return stacks;
      }).orElse(Collections.emptyList());
   }

   public static Map<String, List<Tuple<ItemStack, Integer>>> getCuriosItemStacks(LivingEntity entity) {
      return entity.getCapability(CuriosCapability.INVENTORY).map(inv -> {
         Map<String, List<Tuple<ItemStack, Integer>>> contents = new HashMap<>();
         inv.getCurios().forEach((key, handle) -> {
            IDynamicStackHandler stackHandler = handle.getStacks();

            for (int index = 0; index < stackHandler.getSlots(); index++) {
               contents.computeIfAbsent(key, str -> new ArrayList<>()).add(new Tuple(stackHandler.getStackInSlot(index), index));
            }
         });
         return contents;
      }).orElse(Collections.emptyMap());
   }

   public static ItemStack getCurioItemStack(LivingEntity entity, String slotKey, int slotIndex) {
      return entity.getCapability(CuriosCapability.INVENTORY).map(handler -> handler.getStacksHandler(slotKey).map(stackHandler -> {
         int slotCount = stackHandler.getSlots();
         return slotIndex >= slotCount ? ItemStack.EMPTY : stackHandler.getStacks().getStackInSlot(slotIndex);
      }).orElse(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
   }

   public static boolean setCurioItemStack(LivingEntity entity, ItemStack stack, String slotKey, int slotIndex) {
      return entity.getCapability(CuriosCapability.INVENTORY).map(handler -> handler.getStacksHandler(slotKey).map(stackHandler -> {
         int slotCount = stackHandler.getSlots();
         if (slotIndex >= slotCount) {
            return false;
         } else {
            stackHandler.getStacks().setStackInSlot(slotIndex, stack);
            return true;
         }
      }).orElse(false)).orElse(false);
   }

   public static void clearCurios(LivingEntity entity) {
      CuriosApi.getCuriosHelper()
         .getCuriosHandler(entity)
         .ifPresent(
            handler -> handler.getCurios()
               .values()
               .forEach(
                  stacksHandler -> {
                     IDynamicStackHandler stackHandler = stacksHandler.getStacks();
                     IDynamicStackHandler cosmeticStackHandler = stacksHandler.getCosmeticStacks();
                     String id = stacksHandler.getIdentifier();

                     for (int i = 0; i < stackHandler.getSlots(); i++) {
                        UUID uuid = UUID.nameUUIDFromBytes((id + i).getBytes());
                        NonNullList<Boolean> renderStates = stacksHandler.getRenders();
                        SlotContext slotContext = new SlotContext(id, entity, i, false, renderStates.size() > i && (Boolean)renderStates.get(i));
                        ItemStack stack = stackHandler.getStackInSlot(i);
                        Multimap<Attribute, AttributeModifier> map = CuriosApi.getCuriosHelper().getAttributeModifiers(slotContext, uuid, stack);
                        Multimap<String, AttributeModifier> slots = HashMultimap.create();
                        Set<SlotAttributeWrapper> toRemove = new HashSet<>();

                        for (Attribute attribute : map.keySet()) {
                           if (attribute instanceof SlotAttributeWrapper wrapper) {
                              slots.putAll(wrapper.identifier, map.get(attribute));
                              toRemove.add(wrapper);
                           }
                        }

                        for (Attribute attributex : toRemove) {
                           map.removeAll(attributex);
                        }

                        entity.getAttributes().removeAttributeModifiers(map);
                        handler.removeSlotModifiers(slots);
                        CuriosApi.getCuriosHelper().getCurio(stack).ifPresent(curio -> curio.onUnequip(slotContext, stack));
                        stackHandler.setStackInSlot(i, ItemStack.EMPTY);
                        NetworkHandler.INSTANCE
                           .send(
                              PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                              new SPacketSyncStack(entity.getId(), id, i, ItemStack.EMPTY, HandlerType.EQUIPMENT, new CompoundTag())
                           );
                        cosmeticStackHandler.setStackInSlot(i, ItemStack.EMPTY);
                        NetworkHandler.INSTANCE
                           .send(
                              PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                              new SPacketSyncStack(entity.getId(), id, i, ItemStack.EMPTY, HandlerType.COSMETIC, new CompoundTag())
                           );
                     }
                  }
               )
         );
   }

   public static CompoundTag getMappedSerializedCuriosItemStacks(Player player, BiPredicate<Player, ItemStack> stackFilter, boolean removeSnapshotItems) {
      return player.getCapability(CuriosCapability.INVENTORY).map(inv -> {
         CompoundTag tag = new CompoundTag();
         inv.getCurios().forEach((key, handle) -> {
            CompoundTag keyMap = new CompoundTag();
            IDynamicStackHandler stackHandler = handle.getStacks();

            for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
               ItemStack stack = stackHandler.getStackInSlot(slot);
               if (stackFilter.test(player, stack) && !stack.isEmpty()) {
                  ItemStack stackCopy = stack.copy();
                  if (ModList.get().isLoaded("sophisticatedbackpacksvh")) {
                     IntegrationSB.addSnapshotDataIfBackpack(stackCopy);
                  }

                  keyMap.put(String.valueOf(slot), stackCopy.serializeNBT());
                  if (removeSnapshotItems) {
                     stackHandler.setStackInSlot(slot, ItemStack.EMPTY);
                  }
               }
            }

            tag.put(key, keyMap);
         });
         return tag;
      }).orElse(new CompoundTag());
   }

   public static List<ItemStack> applyMappedSerializedCuriosItemStacks(Player player, CompoundTag tag, boolean replaceExisting) {
      return player.getCapability(CuriosCapability.INVENTORY).map(inv -> {
         List<ItemStack> filledItems = new ArrayList<>();

         for (String handlerKey : tag.getAllKeys()) {
            inv.getStacksHandler(handlerKey).ifPresent(handle -> {
               IDynamicStackHandler stackHandler = handle.getStacks();
               CompoundTag handlerKeyMap = tag.getCompound(handlerKey);

               for (String strSlot : handlerKeyMap.getAllKeys()) {
                  int slot;
                  try {
                     slot = Integer.parseInt(strSlot);
                  } catch (NumberFormatException var11) {
                     continue;
                  }

                  if (slot >= 0 && slot < stackHandler.getSlots()) {
                     ItemStack stack = ItemStack.of(handlerKeyMap.getCompound(strSlot));
                     if (ModList.get().isLoaded("sophisticatedbackpacksvh")) {
                        IntegrationSB.restoreSnapshotIfBackpack(stack);
                     }

                     if (!replaceExisting && !stackHandler.getStackInSlot(slot).isEmpty()) {
                        filledItems.add(stack);
                     } else {
                        stackHandler.setStackInSlot(slot, stack);
                     }
                  }
               }
            });
         }

         return filledItems;
      }).orElse(Collections.emptyList());
   }

   public static List<ItemStack> getCuriosItemStacksFromTag(CompoundTag tag) {
      List<ItemStack> stacks = new ArrayList<>();

      for (String handlerKey : tag.getAllKeys()) {
         CompoundTag handlerKeyMap = tag.getCompound(handlerKey);

         for (String strSlot : handlerKeyMap.getAllKeys()) {
            stacks.add(ItemStack.of(handlerKeyMap.getCompound(strSlot)));
         }
      }

      return stacks;
   }

   public static ItemStack getItemFromCuriosHeadSlot(Player player, Predicate<ItemStack> stackMatcher) {
      return CuriosApi.getCuriosHelper().getCuriosHandler(player).map(h -> h.getStacksHandler(SlotTypePreset.HEAD.getIdentifier()).map(stackHandler -> {
         IDynamicStackHandler stacks = stackHandler.getStacks();

         for (int slot = 0; slot < stacks.getSlots(); slot++) {
            ItemStack stackInSlot = stacks.getStackInSlot(slot);
            if (stackMatcher.test(stackInSlot)) {
               return stackInSlot;
            }
         }

         return ItemStack.EMPTY;
      }).orElse(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
   }

   public static void registerHeadSlot(InterModEnqueueEvent evt) {
      InterModComms.sendTo("curios", "register_type", () -> SlotTypePreset.HEAD.getMessageBuilder().build());
   }
}
