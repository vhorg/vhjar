package iskallia.vault.container;

import iskallia.vault.container.slot.CurioTabSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.mixin.AccessorAbstractContainerMenu;
import iskallia.vault.network.message.ClientboundCuriosScrollMessage;
import iskallia.vault.network.message.ServerboundCuriosScrollMessage;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class CurioContainerHandler {
   protected final CurioContainerHandler.IContainerAccess containerAccess;
   private final int slotStartIndex;
   protected final int offsetX;
   private final int offsetY;
   protected final int maxSlotsDisplayed;
   private int lastScrollIndex;

   public CurioContainerHandler(CurioContainerHandler.IContainerAccess containerAccess, int slotStartIndex, int offsetX, int offsetY, int maxSlotsDisplayed) {
      this.containerAccess = containerAccess;
      this.slotStartIndex = slotStartIndex;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.maxSlotsDisplayed = maxSlotsDisplayed;
   }

   public void scrollToIndex(int targetIndex) {
      NonNullList<Slot> slots = this.containerAccess.getSlots();
      slots.subList(this.slotStartIndex, slots.size()).clear();
      NonNullList<ItemStack> lastSlots = this.containerAccess.getLastSlots();
      lastSlots.subList(this.slotStartIndex, lastSlots.size()).clear();
      NonNullList<ItemStack> remoteSlots = this.containerAccess.getRemoteSlots();
      remoteSlots.subList(this.slotStartIndex, remoteSlots.size()).clear();
      this.addCurioSlots(targetIndex);
      if (!this.containerAccess.isClient()) {
         ClientboundCuriosScrollMessage.send((ServerPlayer)this.containerAccess.getPlayer(), this.containerAccess.getContainerId(), targetIndex);
      }

      this.lastScrollIndex = targetIndex;
   }

   private void addCurioSlots(int targetIndex) {
      this.getCuriosHandler().ifPresent(itemHandler -> {
         Map<String, ICurioStacksHandler> curioMap = itemHandler.getCurios();
         int startingIndex = this.calculateScrollStartIndex(targetIndex, curioMap);
         this.addSlots(curioMap, startingIndex, this.offsetY);
      });
   }

   private int calculateScrollStartIndex(int targetIndex, Map<String, ICurioStacksHandler> curioMap) {
      int slotCount = 0;
      int index = 0;

      for (Entry<String, ICurioStacksHandler> entry : curioMap.entrySet()) {
         ICurioStacksHandler curioStacksHandler = entry.getValue();
         if (curioStacksHandler.isVisible()) {
            IDynamicStackHandler dynamicStackHandler = curioStacksHandler.getStacks();

            for (int i = 0; i < dynamicStackHandler.getSlots() && slotCount < this.maxSlotsDisplayed; i++) {
               if (index >= targetIndex) {
                  slotCount++;
               }

               index++;
            }
         }
      }

      return Mth.clamp(index - this.maxSlotsDisplayed, 0, targetIndex);
   }

   protected void addSlots(Map<String, ICurioStacksHandler> curioMap, int startingIndex, int offsetY) {
      int index = 0;
      int slotCount = 0;

      for (Entry<String, ICurioStacksHandler> entry : curioMap.entrySet()) {
         ICurioStacksHandler curioStacksHandler = entry.getValue();
         if (curioStacksHandler.isVisible()) {
            IDynamicStackHandler dynamicStackHandler = curioStacksHandler.getStacks();

            for (int slotIndex = 0; slotIndex < dynamicStackHandler.getSlots() && slotCount < this.maxSlotsDisplayed; slotIndex++) {
               if (index >= startingIndex) {
                  this.containerAccess
                     .addSlot(
                        new CurioTabSlot(
                           this.containerAccess.getPlayer(),
                           dynamicStackHandler,
                           slotIndex,
                           entry.getKey(),
                           this.offsetX,
                           offsetY,
                           curioStacksHandler.getRenders()
                        )
                     );
                  slotCount++;
                  offsetY += 18;
               }

               index++;
            }
         }
      }
   }

   public void scrollTo(float pos) {
      this.getCuriosHandler().ifPresent(itemHandler -> {
         int newScrollIndex = (int)(pos * (itemHandler.getVisibleSlots() - this.maxSlotsDisplayed) + 0.5);
         if (newScrollIndex < 0) {
            newScrollIndex = 0;
         }

         if (newScrollIndex != this.lastScrollIndex) {
            if (this.containerAccess.isClient()) {
               ServerboundCuriosScrollMessage.send(this.containerAccess.getContainerId(), newScrollIndex);
            }
         }
      });
   }

   public int getVisibleSlotCount() {
      return Math.min(this.getCuriosHandler().map(ICuriosItemHandler::getVisibleSlots).orElse(0), this.maxSlotsDisplayed);
   }

   public boolean canScroll() {
      return this.getCuriosHandler().map(itemHandler -> itemHandler.getVisibleSlots() > this.maxSlotsDisplayed).orElse(false);
   }

   protected LazyOptional<ICuriosItemHandler> getCuriosHandler() {
      return CuriosApi.getCuriosHelper().getCuriosHandler(this.containerAccess.getPlayer());
   }

   public interface IContainerAccess {
      int getContainerId();

      void addSlot(Slot var1);

      NonNullList<Slot> getSlots();

      NonNullList<ItemStack> getLastSlots();

      NonNullList<ItemStack> getRemoteSlots();

      Player getPlayer();

      boolean isClient();
   }

   public static class TabContainerAccessDecorator<T extends AbstractElementContainer> implements CurioContainerHandler.IContainerAccess {
      private final T container;

      public static <T extends AbstractElementContainer> CurioContainerHandler.TabContainerAccessDecorator<T> of(T container) {
         return new CurioContainerHandler.TabContainerAccessDecorator<>(container);
      }

      private TabContainerAccessDecorator(T container) {
         this.container = container;
      }

      @Override
      public int getContainerId() {
         return this.container.containerId;
      }

      @Override
      public void addSlot(Slot slot) {
         this.container.addSlot(slot);
      }

      @Override
      public NonNullList<Slot> getSlots() {
         return this.container.slots;
      }

      @Override
      public NonNullList<ItemStack> getLastSlots() {
         return this.container.lastSlots;
      }

      @Override
      public NonNullList<ItemStack> getRemoteSlots() {
         return ((AccessorAbstractContainerMenu)this.container).getRemoteSlots();
      }

      @Override
      public Player getPlayer() {
         return this.container.getPlayer();
      }

      @Override
      public boolean isClient() {
         return this.getPlayer().level.isClientSide;
      }
   }
}
