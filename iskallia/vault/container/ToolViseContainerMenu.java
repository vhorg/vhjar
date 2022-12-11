package iskallia.vault.container;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.block.entity.ToolViseTile;
import iskallia.vault.config.PaxelConfigs;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModSlotIcons;
import iskallia.vault.item.paxel.PaxelItem;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ToolViseContainerMenu extends AbstractContainerMenu {
   private static final Pair<ResourceLocation, ResourceLocation>[] EMPTY_SLOT_TEXTURES = new Pair[]{
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_0_NO_ITEM),
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_1_NO_ITEM),
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_2_NO_ITEM),
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_3_NO_ITEM),
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_4_NO_ITEM),
      Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_VISE_SLOT_5_NO_ITEM)
   };
   private static final int SIZE = 7;
   public final Map<PaxelItem.Stat, PaxelConfigs.Upgrade> upgrades = ModConfigs.PAXEL_CONFIGS.getAllUpgrades();
   public final Container container;

   public ToolViseContainerMenu(int id, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
      this(id, playerInventory, (Container)null);
   }

   public ToolViseContainerMenu(int id, Inventory playerInventory, @Nullable Container container) {
      super(ModContainers.TOOL_VISE_CONTAINER, id);
      this.container = Objects.requireNonNullElseGet(container, () -> new SimpleContainer(7) {
         public void setChanged() {
            super.setChanged();
            ToolViseContainerMenu.this.slotsChanged(this);
         }
      });
      checkContainerSize(this.container, 7);
      this.container.startOpen(playerInventory.player);
      this.addSlot(new Slot(this.container, 0, 55, 24) {
         public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof PaxelItem;
         }
      });
      this.addSlot(
         new ToolViseContainerMenu.MaterialSlot(
            this.container, 1, 43, 64, itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(0)), EMPTY_SLOT_TEXTURES[0]
         )
      );
      this.addSlot(
         new ToolViseContainerMenu.MaterialSlot(
            this.container, 2, 66, 64, itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(1)), EMPTY_SLOT_TEXTURES[1]
         )
      );
      this.addSlot(
         new ToolViseContainerMenu.MaterialSlot(
            this.container, 3, 43, 86, itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(2)), EMPTY_SLOT_TEXTURES[2]
         )
      );
      this.addSlot(
         new ToolViseContainerMenu.MaterialSlot(
            this.container, 4, 66, 86, itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(3)), EMPTY_SLOT_TEXTURES[3]
         )
      );
      this.addSlot(
         new ToolViseContainerMenu.MaterialSlot(
            this.container, 5, 43, 108, itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(4)), EMPTY_SLOT_TEXTURES[4]
         )
      );
      this.addSlot(
         new ToolViseContainerMenu.MaterialSlot(
            this.container, 6, 66, 108, itemStack -> itemStack.is(ModConfigs.PAXEL_CONFIGS.getMaterialItem(5)), EMPTY_SLOT_TEXTURES[5]
         )
      );

      for (int si = 0; si < 3; si++) {
         for (int sj = 0; sj < 9; sj++) {
            this.addSlot(new Slot(playerInventory, sj + (si + 1) * 9, 8 + sj * 18, 136 + si * 18));
         }
      }

      for (int si = 0; si < 9; si++) {
         this.addSlot(new Slot(playerInventory, si, 8 + si * 18, 194));
      }
   }

   public void slotsChanged(Container pInventory) {
      super.slotsChanged(pInventory);
   }

   public boolean stillValid(Player playerIn) {
      return this.container.stillValid(playerIn);
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack otherStack = slot.getItem();
         itemstack = otherStack.copy();
         if (index < this.container.getContainerSize()) {
            if (!this.moveItemStackTo(otherStack, this.container.getContainerSize(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(otherStack, 0, this.container.getContainerSize(), false)) {
            return ItemStack.EMPTY;
         }

         if (otherStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }

      return itemstack;
   }

   public void removed(Player playerIn) {
      super.removed(playerIn);
      this.container.stopOpen(playerIn);
   }

   public boolean clickMenuButton(Player pPlayer, int pId) {
      if (pId >= PaxelItem.Stat.values().length) {
         return super.clickMenuButton(pPlayer, pId);
      } else {
         PaxelItem.Stat stat = PaxelItem.Stat.values()[pId];
         PaxelConfigs.Upgrade upgrade = this.upgrades.get(stat);
         if (!pPlayer.isCreative()) {
            for (int i = 1; i <= 6; i++) {
               ((Slot)this.slots.get(i)).getItem().shrink(upgrade.getMaterialCost(i - 1));
            }
         }

         ItemStack paxel = ((Slot)this.slots.get(0)).getItem();
         PaxelItem.increaseStatUpgrade(paxel, stat, upgrade.getYield(pPlayer.getRandom()));
         int sturdiness = PaxelItem.getSturdiness(paxel);
         if (this.container instanceof ToolViseTile tile) {
            boolean playSound = true;
            int level = PaxelItem.getLevel(paxel);
            int delta = ModConfigs.PAXEL_CONFIGS.getTierValues(paxel).getLevelsPerSocket();
            PaxelItem.setLevel(paxel, level + 1);
            if (sturdiness < tile.getLevel().random.nextFloat() * 100.0F && !pPlayer.isCreative()) {
               tile.getLevel().playSound(null, tile.getBlockPos(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.1F);
               ((Slot)this.slots.get(0)).set(ItemStack.EMPTY);
               playSound = false;
            } else if (level / delta != (level + 1) / delta) {
               PaxelItem.setSockets(paxel, PaxelItem.getSockets(paxel) + 1);
            }

            if (playSound) {
               tile.getLevel().playSound(null, tile.getBlockPos(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 1.1F);
            }
         }

         this.broadcastChanges();
         return true;
      }
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
