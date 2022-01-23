package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.StatueType;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class LootStatueTileEntity extends SkinnableTileEntity implements ITickableTileEntity {
   private StatueType statueType;
   private int currentTick = 0;
   private int itemsRemaining = 0;
   private int totalItems = 0;
   private ItemStack lootItem = ItemStack.field_190927_a;
   private boolean master;
   private BlockPos masterPos;
   private int chipCount = 0;
   private float playerScale;

   protected LootStatueTileEntity(TileEntityType<?> tileEntityType) {
      super(tileEntityType);
   }

   public LootStatueTileEntity() {
      this(ModBlocks.LOOT_STATUE_TILE_ENTITY);
   }

   public float getPlayerScale() {
      return this.playerScale;
   }

   public void setPlayerScale(float playerScale) {
      this.playerScale = playerScale;
   }

   public boolean isMaster() {
      return this.master;
   }

   public void setMaster(boolean master) {
      this.master = master;
   }

   public BlockPos getMasterPos() {
      return this.masterPos;
   }

   public void setMasterPos(BlockPos masterPos) {
      this.masterPos = masterPos;
   }

   public int getCurrentTick() {
      return this.currentTick;
   }

   public void setCurrentTick(int currentTick) {
      this.currentTick = currentTick;
   }

   @Nonnull
   public ItemStack getLootItem() {
      return this.lootItem;
   }

   public void setLootItem(@Nonnull ItemStack stack) {
      this.lootItem = stack;
      this.func_70296_d();
      this.sendUpdates();
   }

   @Override
   protected void updateSkin() {
   }

   public StatueType getStatueType() {
      return this.statueType;
   }

   public void setStatueType(StatueType statueType) {
      this.statueType = statueType;
   }

   public boolean addChip() {
      if (this.statueType.isOmega() && this.chipCount < ModConfigs.STATUE_LOOT.getMaxAccelerationChips()) {
         this.chipCount++;
         this.sendUpdates();
         return true;
      } else {
         return false;
      }
   }

   public ItemStack removeChip() {
      ItemStack stack = ItemStack.field_190927_a;
      if (this.chipCount > 0) {
         this.chipCount--;
         stack = new ItemStack(ModItems.ACCELERATION_CHIP, 1);
         this.sendUpdates();
      }

      return stack;
   }

   public int getChipCount() {
      return this.chipCount;
   }

   public int getItemsRemaining() {
      return this.itemsRemaining;
   }

   public void setItemsRemaining(int itemsRemaining) {
      this.itemsRemaining = itemsRemaining;
   }

   public int getTotalItems() {
      return this.totalItems;
   }

   public void setTotalItems(int totalItems) {
      this.totalItems = totalItems;
   }

   public void func_73660_a() {
      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K && this.itemsRemaining != 0 && this.statueType.dropsItems()) {
         if (this.statueType != StatueType.OMEGA || this.master) {
            if (this.currentTick++ >= this.getModifiedInterval()) {
               this.currentTick = 0;
               if (!this.lootItem.func_190926_b()) {
                  ItemStack stack = this.lootItem.func_77946_l();
                  if (this.poopItem(stack, false)) {
                     this.func_70296_d();
                     this.func_145831_w().func_184138_a(this.func_174877_v(), this.func_195044_w(), this.func_195044_w(), 3);
                  }
               }
            }
         }
      }
   }

   private int getModifiedInterval() {
      int interval = ModConfigs.STATUE_LOOT.getInterval(this.getStatueType());
      return this.getChipCount() != 0 && this.getStatueType().isOmega() ? interval - ModConfigs.STATUE_LOOT.getIntervalDecrease(this.getChipCount()) : interval;
   }

   public boolean poopItem(ItemStack stack, boolean simulate) {
      assert this.field_145850_b != null;

      BlockPos down = this.func_174877_v().func_177977_b();
      if (this.statueType == StatueType.OMEGA) {
         for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
               BlockPos offset = down.func_177982_a(x, 0, z);
               TileEntity tileEntity = this.field_145850_b.func_175625_s(offset);
               if (tileEntity != null) {
                  LazyOptional<IItemHandler> handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
                  if (handler.isPresent()) {
                     ItemStack remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)handler.orElse(null), stack, true);
                     if (remainder.func_190926_b()) {
                        ItemHandlerHelper.insertItemStacked((IItemHandler)handler.orElse(null), stack, false);
                        if (this.itemsRemaining != -1) {
                           this.itemsRemaining--;
                        }

                        return true;
                     }
                  }
               }
            }
         }
      } else {
         TileEntity tileEntity = this.field_145850_b.func_175625_s(down);
         if (tileEntity != null) {
            LazyOptional<IItemHandler> handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
            handler.ifPresent(h -> {
               ItemHandlerHelper.insertItemStacked(h, stack, simulate);
               if (this.itemsRemaining != -1) {
                  this.itemsRemaining--;
               }
            });
            return true;
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return super.getRenderBoundingBox().func_72321_a(0.0, 6.0, 0.0);
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      if (this.statueType == null) {
         return super.func_189515_b(nbt);
      } else {
         nbt.func_74768_a("StatueType", this.getStatueType().ordinal());
         if (this.statueType == StatueType.OMEGA) {
            if (!this.master) {
               nbt.func_74757_a("Master", false);
               nbt.func_218657_a("MasterPos", NBTUtil.func_186859_a(this.masterPos));
               return super.func_189515_b(nbt);
            }

            nbt.func_74757_a("Master", true);
            nbt.func_218657_a("MasterPos", NBTUtil.func_186859_a(this.func_174877_v()));
         }

         String nickname = this.skin.getLatestNickname();
         nbt.func_74778_a("PlayerNickname", nickname == null ? "" : nickname);
         nbt.func_74768_a("CurrentTick", this.getCurrentTick());
         nbt.func_218657_a("LootItem", this.getLootItem().serializeNBT());
         nbt.func_74768_a("ChipCount", this.chipCount);
         nbt.func_74768_a("ItemsRemaining", this.itemsRemaining);
         nbt.func_74768_a("TotalItems", this.totalItems);
         nbt.func_74776_a("playerScale", this.playerScale);
         return super.func_189515_b(nbt);
      }
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      if (!nbt.func_150297_b("StatueType", 3)) {
         throw new IllegalStateException("Invalid State NBT " + nbt.toString());
      } else {
         this.setStatueType(StatueType.values()[nbt.func_74762_e("StatueType")]);
         if (this.statueType == StatueType.OMEGA) {
            if (!nbt.func_74767_n("Master")) {
               this.master = false;
               this.masterPos = NBTUtil.func_186861_c(nbt.func_74775_l("MasterPos"));
               super.func_230337_a_(state, nbt);
               return;
            }

            this.master = true;
            this.masterPos = this.func_174877_v();
         }

         String nickname = nbt.func_74779_i("PlayerNickname");
         this.skin.updateSkin(nickname);
         this.lootItem = ItemStack.func_199557_a(nbt.func_74775_l("LootItem"));
         this.setCurrentTick(nbt.func_74762_e("CurrentTick"));
         this.chipCount = nbt.func_74762_e("ChipCount");
         this.itemsRemaining = nbt.func_74762_e("ItemsRemaining");
         this.totalItems = nbt.func_74762_e("TotalItems");
         if (nbt.func_74764_b("playerScale")) {
            this.playerScale = nbt.func_74760_g("playerScale");
         } else {
            this.playerScale = MathUtilities.randomFloat(2.0F, 4.0F);
         }

         super.func_230337_a_(state, nbt);
      }
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      if (this.getStatueType() == null) {
         return nbt;
      } else {
         nbt.func_74768_a("StatueType", this.getStatueType().ordinal());
         if (this.statueType == StatueType.OMEGA) {
            if (!this.master) {
               nbt.func_74757_a("Master", false);
               nbt.func_218657_a("MasterPos", NBTUtil.func_186859_a(this.masterPos));
               return nbt;
            }

            nbt.func_74757_a("Master", true);
            nbt.func_218657_a("MasterPos", NBTUtil.func_186859_a(this.func_174877_v()));
         }

         String nickname = this.skin.getLatestNickname();
         nbt.func_74778_a("PlayerNickname", nickname == null ? "" : nickname);
         nbt.func_74768_a("CurrentTick", this.getCurrentTick());
         nbt.func_218657_a("LootItem", this.getLootItem().serializeNBT());
         nbt.func_74768_a("ChipCount", this.chipCount);
         nbt.func_74768_a("ItemsRemaining", this.itemsRemaining);
         nbt.func_74768_a("TotalItems", this.totalItems);
         nbt.func_74776_a("playerScale", this.playerScale);
         return nbt;
      }
   }
}
