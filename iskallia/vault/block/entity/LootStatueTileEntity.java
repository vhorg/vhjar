package iskallia.vault.block.entity;

import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.util.StatueType;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class LootStatueTileEntity extends TileEntity implements ITickableTileEntity {
   private int interval = 0;
   private int currentTick = 0;
   private ItemStack lootItem;
   protected SkinProfile skin;
   private StatueType statueType;
   private boolean hasCrown;
   private int chipCount = 0;

   public LootStatueTileEntity() {
      super(ModBlocks.LOOT_STATUE_TILE_ENTITY);
      this.skin = new SkinProfile();
   }

   public int getInterval() {
      return this.interval;
   }

   public void setInterval(int interval) {
      this.interval = interval;
   }

   public int getCurrentTick() {
      return this.currentTick;
   }

   public void setCurrentTick(int currentTick) {
      this.currentTick = currentTick;
   }

   public ItemStack getLootItem() {
      return this.lootItem;
   }

   public void setLootItem(ItemStack stack) {
      this.lootItem = stack;
   }

   public SkinProfile getSkin() {
      return this.skin;
   }

   public StatueType getStatueType() {
      return this.statueType;
   }

   public void setStatueType(StatueType statueType) {
      this.statueType = statueType;
   }

   public boolean hasCrown() {
      return this.hasCrown;
   }

   public void setHasCrown(boolean hasCrown) {
      this.hasCrown = hasCrown;
   }

   public boolean addChip() {
      if (this.chipCount >= ModConfigs.STATUE_LOOT.getMaxAccelerationChips()) {
         return false;
      } else {
         this.chipCount++;
         this.sendUpdates();
         return true;
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

   public void func_73660_a() {
      if (!this.field_145850_b.field_72995_K) {
         if (this.currentTick++ == this.getModifiedInterval()) {
            this.currentTick = 0;
            ItemStack stack = this.lootItem.func_77946_l();
            if (this.poopItem(stack, true) != ItemStack.field_190927_a) {
               stack = this.poopItem(stack, false);
               if (this.lootItem.func_190916_E() + stack.func_190916_E() > this.lootItem.func_77976_d()) {
                  this.lootItem.func_190920_e(this.lootItem.func_77976_d());
               } else {
                  this.lootItem.func_190920_e(stack.func_190916_E());
               }
            } else {
               this.poopItem(stack, false);
            }
         }
      }
   }

   private int getModifiedInterval() {
      return this.getChipCount() == 0 ? this.interval : this.interval - ModConfigs.STATUE_LOOT.getIntervalDecrease(this.getChipCount());
   }

   public ItemStack poopItem(ItemStack stack, boolean simulate) {
      TileEntity tileEntity = this.field_145850_b.func_175625_s(this.func_174877_v().func_177977_b());
      if (tileEntity == null) {
         return stack;
      } else {
         LazyOptional<IItemHandler> handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
         if (handler.isPresent()) {
            IItemHandler targetHandler = (IItemHandler)handler.orElse(null);
            return ItemHandlerHelper.insertItemStacked(targetHandler, stack, simulate);
         } else {
            return stack;
         }
      }
   }

   public void sendUpdates() {
      this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
      this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
      this.func_70296_d();
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      String nickname = this.skin.getLatestNickname();
      nbt.func_74778_a("PlayerNickname", nickname == null ? "" : nickname);
      if (this.getInterval() == 0) {
         this.migrate(this.func_195044_w());
      }

      nbt.func_74768_a("Interval", this.getInterval());
      nbt.func_74768_a("CurrentTick", this.getCurrentTick());
      nbt.func_74768_a("StatueType", this.getStatueType().ordinal());
      nbt.func_218657_a("LootItem", this.getLootItem().serializeNBT());
      nbt.func_74757_a("HasCrown", this.hasCrown());
      nbt.func_74768_a("ChipCount", this.chipCount);
      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      String nickname = nbt.func_74779_i("PlayerNickname");
      this.skin.updateSkin(nickname);
      if (!nbt.func_74764_b("Interval")) {
         this.migrate(state);
      }

      this.setLootItem(ItemStack.func_199557_a(nbt.func_74775_l("LootItem")));
      this.setCurrentTick(nbt.func_74762_e("CurrentTick"));
      this.setInterval(nbt.func_74762_e("Interval"));
      this.setStatueType(StatueType.values()[nbt.func_74762_e("StatueType")]);
      this.hasCrown = nbt.func_74767_n("HasCrown");
      this.chipCount = nbt.func_74762_e("ChipCount");
      super.func_230337_a_(state, nbt);
   }

   private void migrate(BlockState state) {
      LootStatueBlock block = (LootStatueBlock)state.func_177230_c();
      StatueType type = block.getType();
      this.setStatueType(type);
      this.setInterval(ModConfigs.STATUE_LOOT.getInterval(type));
      this.setLootItem(ModConfigs.STATUE_LOOT.randomLoot(type));
      this.setCurrentTick(0);
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      String nickname = this.skin.getLatestNickname();
      nbt.func_74778_a("PlayerNickname", nickname == null ? "" : nickname);
      nbt.func_74768_a("Interval", this.getInterval());
      nbt.func_74768_a("CurrentTick", this.getCurrentTick());
      nbt.func_74768_a("StatueType", this.getStatueType().ordinal());
      nbt.func_218657_a("LootItem", this.getLootItem().serializeNBT());
      nbt.func_74757_a("HasCrown", this.hasCrown());
      nbt.func_74768_a("ChipCount", this.chipCount);
      return nbt;
   }

   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT nbt = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), nbt);
   }
}
