package iskallia.vault.block.entity;

import iskallia.vault.block.entity.base.InventoryRetainerTileEntity;
import iskallia.vault.container.VaultJewelApplicationStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.item.tool.ToolItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VaultJewelApplicationStationTileEntity extends BlockEntity implements MenuProvider, InventoryRetainerTileEntity {
   public static final int TOOL_SLOT = 0;
   public static final int JEWEL_SLOTS = 60;
   private final OverSizedInventory inventory = new OverSizedInventory(61, this) {
      @Override
      public void setChanged() {
         super.setChanged();
         VaultJewelApplicationStationTileEntity.this.updateJewelContents();
      }
   };
   private int totalSizeInJewels = 0;
   private ItemStack renderedTool = this.getToolItem();

   public VaultJewelApplicationStationTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_JEWEL_APPLICATION_STATION_ENTITY, pos, state);
   }

   private void updateJewelContents() {
      ItemStack result = this.getToolItem().copy();
      this.totalSizeInJewels = 0;
      this.renderedTool = result.copy();
      if (result.getItem() instanceof ToolItem) {
         for (int slot = 0; slot < 60; slot++) {
            ItemStack jewel = this.getJewelItem(slot);
            if (jewel.getItem() instanceof JewelItem) {
               if (result.getItem() instanceof ToolItem) {
                  ToolItem.applyJewel(result, jewel, false);
               }

               if (VaultGearData.hasData(jewel)) {
                  VaultGearData data = VaultGearData.read(jewel);

                  for (VaultGearAttributeInstance<Integer> sizeAttribute : data.getModifiers(ModGearAttributes.JEWEL_SIZE, VaultGearData.Type.ALL_MODIFIERS)) {
                     this.totalSizeInJewels = this.totalSizeInJewels + sizeAttribute.getValue();
                  }
               }
            }
         }

         this.renderedTool = result.copy();
         this.setChanged();
      }
   }

   public void applyJewels() {
      ItemStack result = this.getToolItem();

      for (int slot = 0; slot < 60; slot++) {
         ItemStack jewel = this.getJewelItem(slot);
         if (jewel.getItem() instanceof JewelItem && result.getItem() instanceof ToolItem && ToolItem.applyJewel(result, jewel, true)) {
            this.setJewelItem(slot, ItemStack.EMPTY);
         }

         this.setToolItem(result);
      }

      if (this.getLevel() != null) {
         this.getLevel()
            .playSound(null, this.getBlockPos(), ModSounds.ARTISAN_SMITHING, SoundSource.BLOCKS, 0.2F, this.getLevel().random.nextFloat() * 0.1F + 0.9F);
      }
   }

   public OverSizedInventory getInventory() {
      return this.inventory;
   }

   public int getTotalSizeInJewels() {
      return this.totalSizeInJewels;
   }

   public ItemStack getRenderedTool() {
      return this.renderedTool;
   }

   public ItemStack getToolItem() {
      return this.getInventory().getItem(0);
   }

   public void setToolItem(ItemStack stack) {
      this.getInventory().setItem(0, stack.copy());
   }

   public ItemStack getJewelItem(int slot) {
      slot = Mth.clamp(slot, 0, 60);
      return this.getInventory().getItem(slot + 1);
   }

   public void setJewelItem(int slot, ItemStack stack) {
      slot = Mth.clamp(slot, 0, 60);
      this.getInventory().setItem(slot + 1, stack);
   }

   public List<ItemStack> getJewels() {
      List<ItemStack> stacks = new ArrayList<>();

      for (int i = 0; i < 60; i++) {
         stacks.add(this.getInventory().getItem(i + 1));
      }

      return stacks;
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this ? this.getInventory().stillValid(player) : false;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.renderedTool = ItemStack.of(tag.getCompound("renderedItem"));
      this.totalSizeInJewels = tag.getInt("size");
      this.getInventory().load(tag);
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.put("renderedItem", this.renderedTool.save(new CompoundTag()));
      tag.putInt("size", this.totalSizeInJewels);
      this.getInventory().save(tag);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultJewelApplicationStationContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
   }

   public static InventoryRetainerTileEntity.ContentDisplayInfo addInventoryTooltip(ItemStack stack, CompoundTag tag) {
      InventoryRetainerTileEntity.ContentDisplayInfo itemContent = InventoryRetainerTileEntity.displayContentsOverSized(
         5,
         stacks -> OverSizedInventory.loadContents("inventory", tag)
            .stream()
            .filter(containedStack -> !containedStack.overSizedStack().is(ModItems.JEWEL))
            .forEach(stacks::add)
      );
      int totalSize = tag.getInt("totalSize");
      if (totalSize > 0) {
         itemContent = itemContent.append(
            List.of(
               new TextComponent("- Jewels totaling ")
                  .withStyle(ChatFormatting.GRAY)
                  .append(new TextComponent(String.valueOf(totalSize)).withStyle(ChatFormatting.WHITE))
                  .append(new TextComponent(" size").withStyle(ChatFormatting.GRAY))
            ),
            5
         );
      }

      return itemContent;
   }

   @Override
   public void storeInventoryContents(CompoundTag tag) {
      this.getInventory().save("inventory", tag);
      tag.put("renderedItem", this.renderedTool.save(new CompoundTag()));
      tag.putInt("totalSize", this.totalSizeInJewels);
   }

   @Override
   public void loadInventoryContents(CompoundTag tag) {
      this.getInventory().load("inventory", tag);
      this.renderedTool = ItemStack.of(tag.getCompound("renderedItem"));
      this.totalSizeInJewels = tag.getInt("totalSize");
   }

   @Override
   public void clearInventoryContents() {
      this.getInventory().clearContent();
      this.renderedTool = ItemStack.EMPTY;
      this.totalSizeInJewels = 0;
   }
}
