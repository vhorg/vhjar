package iskallia.vault.container;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.block.entity.ToolViseTile;
import iskallia.vault.config.PaxelConfigs;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSlotIcons;
import iskallia.vault.item.tool.PaxelItem;
import iskallia.vault.network.message.ClientboundRefreshToolViseMessage;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;

public class ToolViseContainerMenu extends OverSizedSlotContainer {
   private static final Pair<ResourceLocation, ResourceLocation>[] EMPTY_SLOT_TEXTURES = new Pair[]{
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_0_NO_ITEM),
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_1_NO_ITEM),
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_2_NO_ITEM),
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_3_NO_ITEM),
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_4_NO_ITEM),
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_5_NO_ITEM)
   };
   private final BlockPos tilePos;
   private final ToolViseTile tileEntity;
   public final Map<PaxelItem.Stat, PaxelConfigs.Upgrade> upgrades = ModConfigs.PAXEL_CONFIGS.getAllUpgrades();

   public ToolViseContainerMenu(int id, Level level, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.TOOL_VISE_CONTAINER, id, playerInventory.player);
      this.tilePos = pos;
      if (level.getBlockEntity(pos) instanceof ToolViseTile toolViseTile) {
         this.tileEntity = toolViseTile;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   private void initSlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, 136 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 194));
      }

      Container pickaxeContainer = this.tileEntity.getPickaxeInput();
      this.addSlot(new TabSlot(pickaxeContainer, 0, 55, 24) {
         public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof PaxelItem;
         }

         public void setChanged() {
            ToolViseContainerMenu.this.slotsChanged(this.container);
            super.setChanged();
         }
      });
      Container inventoryContainer = this.tileEntity.getInventory();
      this.addSlot(
         (new OverSizedTabSlot(inventoryContainer, 0, 43, 64) {
               public void setChanged() {
                  ToolViseContainerMenu.this.slotsChanged(this.container);
                  super.setChanged();
               }
            })
            .setFilter(itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(0)))
            .setBackground((ResourceLocation)EMPTY_SLOT_TEXTURES[0].getFirst(), (ResourceLocation)EMPTY_SLOT_TEXTURES[0].getSecond())
      );
      this.addSlot(
         (new OverSizedTabSlot(inventoryContainer, 1, 66, 64) {
               public void setChanged() {
                  ToolViseContainerMenu.this.slotsChanged(this.container);
                  super.setChanged();
               }
            })
            .setFilter(itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(1)))
            .setBackground((ResourceLocation)EMPTY_SLOT_TEXTURES[1].getFirst(), (ResourceLocation)EMPTY_SLOT_TEXTURES[1].getSecond())
      );
      this.addSlot(
         (new OverSizedTabSlot(inventoryContainer, 2, 43, 86) {
               public void setChanged() {
                  ToolViseContainerMenu.this.tileEntity.setChanged();
                  ToolViseContainerMenu.this.slotsChanged(this.container);
                  super.setChanged();
               }
            })
            .setFilter(itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(2)))
            .setBackground((ResourceLocation)EMPTY_SLOT_TEXTURES[2].getFirst(), (ResourceLocation)EMPTY_SLOT_TEXTURES[2].getSecond())
      );
      this.addSlot(
         (new OverSizedTabSlot(inventoryContainer, 3, 66, 86) {
               public void setChanged() {
                  ToolViseContainerMenu.this.slotsChanged(this.container);
                  super.setChanged();
               }
            })
            .setFilter(itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(3)))
            .setBackground((ResourceLocation)EMPTY_SLOT_TEXTURES[3].getFirst(), (ResourceLocation)EMPTY_SLOT_TEXTURES[3].getSecond())
      );
      this.addSlot(
         (new OverSizedTabSlot(inventoryContainer, 4, 43, 108) {
               public void setChanged() {
                  ToolViseContainerMenu.this.slotsChanged(this.container);
                  super.setChanged();
               }
            })
            .setFilter(itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(4)))
            .setBackground((ResourceLocation)EMPTY_SLOT_TEXTURES[4].getFirst(), (ResourceLocation)EMPTY_SLOT_TEXTURES[4].getSecond())
      );
      this.addSlot(
         (new OverSizedTabSlot(inventoryContainer, 5, 66, 108) {
               public void setChanged() {
                  ToolViseContainerMenu.this.slotsChanged(this.container);
                  super.setChanged();
               }
            })
            .setFilter(itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(5)))
            .setBackground((ResourceLocation)EMPTY_SLOT_TEXTURES[5].getFirst(), (ResourceLocation)EMPTY_SLOT_TEXTURES[5].getSecond())
      );
   }

   public void slotsChanged(Container pInventory) {
      if (this.player instanceof ServerPlayer serverPlayer) {
         ModNetwork.CHANNEL.sendTo(new ClientboundRefreshToolViseMessage(this.tilePos), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      }

      this.tileEntity.setChanged();
      super.slotsChanged(pInventory);
   }

   public boolean stillValid(Player playerIn) {
      return this.tileEntity == null ? false : this.tileEntity.stillValid(playerIn);
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveOverSizedItemStackTo(slotStack, slot, 36, this.slots.size(), false)) {
            return itemstack;
         }

         if (index >= 0 && index < 27) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 36, false)) {
            return ItemStack.EMPTY;
         }

         if (slotStack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (slotStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(this.player, slotStack);
      }

      return itemstack;
   }

   public boolean clickMenuButton(Player pPlayer, int pId) {
      return super.clickMenuButton(pPlayer, pId);
   }

   private static class MaterialSlot extends Slot {
      private final Predicate<ItemStack> itemFilter;
      private final Pair<ResourceLocation, ResourceLocation> emptyIconLocation;

      public MaterialSlot(
         Container pContainer, int pIndex, int pX, int pY, Predicate<ItemStack> itemFilter, Pair<ResourceLocation, ResourceLocation> emptyIconLocation
      ) {
         super(pContainer, pIndex, pX, pY);
         this.itemFilter = itemFilter;
         this.emptyIconLocation = emptyIconLocation;
      }

      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
         return this.emptyIconLocation;
      }

      public boolean mayPlace(@Nonnull ItemStack pStack) {
         return this.itemFilter.test(pStack);
      }
   }
}
