package iskallia.vault.item.paxel.enhancement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public abstract class PaxelEnhancement implements INBTSerializable<CompoundNBT> {
   private static final Map<UUID, Integer> PLAYER_HELD_SLOT = new HashMap<>();
   private static final Map<UUID, ItemStack> PLAYER_HELD_STACK = new HashMap<>();
   protected ResourceLocation resourceLocation;

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
      UUID playerUUID = player.func_110124_au();
      int currentHeldSlotIndex = player.field_71071_by.field_70461_c;
      ItemStack currentStack = (ItemStack)player.field_71071_by.field_70462_a.get(currentHeldSlotIndex);
      PLAYER_HELD_SLOT.put(playerUUID, currentHeldSlotIndex);
      PLAYER_HELD_STACK.put(playerUUID, currentStack);
      PaxelEnhancement enhancement = PaxelEnhancements.getEnhancement(currentStack);
      if (enhancement != null) {
         enhancement.onEnhancementActivated(player, currentStack);
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
      ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
      UUID playerUUID = player.func_110124_au();
      int currentHeldSlotIndex = player.field_71071_by.field_70461_c;
      ItemStack currentStack = (ItemStack)player.field_71071_by.field_70462_a.get(currentHeldSlotIndex);
      PLAYER_HELD_SLOT.remove(playerUUID);
      PLAYER_HELD_STACK.remove(playerUUID);
      PaxelEnhancement enhancement = PaxelEnhancements.getEnhancement(currentStack);
      if (enhancement != null) {
         enhancement.onEnhancementDeactivated(player, currentStack);
      }
   }

   @SubscribeEvent
   public static void onInventoryTick(PlayerTickEvent event) {
      if (!event.side.isClient()) {
         if (event.phase == Phase.END) {
            ServerPlayerEntity player = (ServerPlayerEntity)event.player;
            UUID playerUUID = player.func_110124_au();
            int currentHeldSlotIndex = player.field_71071_by.field_70461_c;
            int previousHeldSlotIndex = PLAYER_HELD_SLOT.computeIfAbsent(playerUUID, uuid -> currentHeldSlotIndex);
            ItemStack currentStack = (ItemStack)player.field_71071_by.field_70462_a.get(currentHeldSlotIndex);
            PaxelEnhancement currentEnhancement = PaxelEnhancements.getEnhancement(currentStack);
            ItemStack prevStack = PLAYER_HELD_STACK.computeIfAbsent(
               playerUUID, uuid -> ((ItemStack)player.field_71071_by.field_70462_a.get(previousHeldSlotIndex)).func_77946_l()
            );
            PaxelEnhancement prevEnhancement = PaxelEnhancements.getEnhancement(prevStack);
            if (currentHeldSlotIndex != previousHeldSlotIndex || !ItemStack.func_77989_b(currentStack, prevStack)) {
               PLAYER_HELD_SLOT.put(playerUUID, currentHeldSlotIndex);
               PLAYER_HELD_STACK.put(playerUUID, currentStack.func_77946_l());
               if (prevEnhancement != null) {
                  prevEnhancement.onEnhancementDeactivated(player, prevStack);
               }

               if (currentEnhancement != null) {
                  currentEnhancement.onEnhancementActivated(player, currentStack);
               }
            }

            if (currentEnhancement != null) {
               currentEnhancement.heldTick(player, currentStack, currentHeldSlotIndex);
            }
         }
      }
   }

   public IFormattableTextComponent getName() {
      return new TranslationTextComponent(
         String.format("paxel_enhancement.%s.%s", this.resourceLocation.func_110624_b(), this.resourceLocation.func_110623_a())
      );
   }

   public IFormattableTextComponent getDescription() {
      return new TranslationTextComponent(
         String.format("paxel_enhancement.%s.%s.desc", this.resourceLocation.func_110624_b(), this.resourceLocation.func_110623_a())
      );
   }

   public abstract Color getColor();

   public void setResourceLocation(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   public ResourceLocation getResourceLocation() {
      return this.resourceLocation;
   }

   public void heldTick(ServerPlayerEntity player, ItemStack paxelStack, int slotIndex) {
   }

   public void onEnhancementActivated(ServerPlayerEntity player, ItemStack paxelStack) {
   }

   public void onEnhancementDeactivated(ServerPlayerEntity player, ItemStack paxelStack) {
   }

   public void inventoryTick(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean isSelected) {
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Id", this.resourceLocation.toString());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.resourceLocation = new ResourceLocation(nbt.func_74779_i("Id"));
   }
}
