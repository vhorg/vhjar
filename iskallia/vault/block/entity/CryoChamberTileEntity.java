package iskallia.vault.block.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.container.inventory.CryochamberContainer;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.vending.TraderCore;
import iskallia.vault.world.data.EternalsData;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CryoChamberTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
   protected SkinProfile skin;
   private UUID owner;
   public List<String> coreNames = new ArrayList<>();
   private int maxCores = 0;
   private boolean infusing = false;
   private int infusionTimeRemaining = 0;
   private boolean growingEternal = false;
   private int growEternalTimeRemaining = 0;
   protected UUID eternalId;
   public float lastCoreCount;
   private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
      protected void onContentsChanged(int slot) {
         if (this.getStackInSlot(slot).func_77973_b() == ModItems.TRADER_CORE) {
            CryoChamberTileEntity.this.addTraderCore(ItemTraderCore.getCoreFromStack(this.getStackInSlot(slot)));
            this.setStackInSlot(slot, ItemStack.field_190927_a);
         }

         CryoChamberTileEntity.this.sendUpdates();
      }

      public boolean isItemValid(int slot, ItemStack stack) {
         return stack.func_77973_b() == ModItems.TRADER_CORE && !CryoChamberTileEntity.this.isFull() && !CryoChamberTileEntity.this.isInfusing();
      }
   };
   private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);

   protected CryoChamberTileEntity(TileEntityType<?> tileEntityType) {
      super(tileEntityType);
      this.skin = new SkinProfile();
   }

   public CryoChamberTileEntity() {
      this(ModBlocks.CRYO_CHAMBER_TILE_ENTITY);
   }

   public UUID getOwner() {
      return this.owner;
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
   }

   public int getMaxCores() {
      return this.maxCores;
   }

   public void setMaxCores(int maxCores) {
      this.maxCores = maxCores;
   }

   public boolean isInfusing() {
      return this.infusing;
   }

   public int getInfusionTimeRemaining() {
      return this.infusionTimeRemaining;
   }

   public boolean isGrowingEternal() {
      return this.growingEternal;
   }

   public int getGrowEternalTimeRemaining() {
      return this.growEternalTimeRemaining;
   }

   public SkinProfile getSkin() {
      return this.skin;
   }

   public int getCoreCount() {
      return this.coreNames.size();
   }

   public List<String> getCoreNames() {
      return this.coreNames;
   }

   public boolean addTraderCore(TraderCore core) {
      if (this.isFull() || this.isInfusing() || this.getOwner() == null) {
         return false;
      } else if (!(this.field_145850_b instanceof ServerWorld)) {
         return false;
      } else {
         ServerWorld sWorld = (ServerWorld)this.field_145850_b;
         sWorld.func_184148_a(
            null,
            this.field_174879_c.func_177958_n(),
            this.field_174879_c.func_177956_o(),
            this.field_174879_c.func_177952_p(),
            SoundEvents.field_232831_nS_,
            SoundCategory.PLAYERS,
            1.0F,
            1.0F
         );
         GameProfile knownProfile = sWorld.func_73046_m().func_152358_ax().func_152652_a(this.getOwner());
         if (knownProfile == null) {
            return false;
         } else {
            int eternals = EternalsData.get(sWorld).getEternals(this.getOwner()).getNonAncientEternalCount();
            int cores = this.getMaxCores();
            int newCores = ModConfigs.CRYO_CHAMBER.getPlayerCoreCount(knownProfile.getName(), eternals);
            if (cores != newCores) {
               this.setMaxCores(newCores);
               this.sendUpdates();
            }

            this.coreNames.add(core.getName());
            if (core.getTrade() != null
               && !core.getTrade().wasTradeUsed()
               && sWorld.field_73012_v.nextFloat() < ModConfigs.CRYO_CHAMBER.getUnusedTraderRewardChance()) {
               PlayerEntity player = sWorld.func_217366_a(
                  this.field_174879_c.func_177958_n(), this.field_174879_c.func_177956_o(), this.field_174879_c.func_177952_p(), 3.0, false
               );
               if (player instanceof ServerPlayerEntity) {
                  MiscUtils.giveItem((ServerPlayerEntity)player, new ItemStack(ModItems.PANDORAS_BOX));
               } else {
                  BlockPos.func_239584_a_(this.func_174877_v(), 7, 2, sWorld::func_175623_d)
                     .ifPresent(airPos -> Block.func_180635_a(sWorld, airPos, new ItemStack(ModItems.PANDORAS_BOX)));
               }
            }

            this.infusing = true;
            this.infusionTimeRemaining = ModConfigs.CRYO_CHAMBER.getInfusionTime();
            this.sendUpdates();
            return true;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void updateSkin() {
      if (this.infusing && !this.coreNames.isEmpty()) {
         this.skin.updateSkin(this.coreNames.get(this.coreNames.size() - 1));
      } else {
         EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(this.getEternalId());
         if (snapshot != null && snapshot.getName() != null) {
            this.skin.updateSkin(snapshot.getName());
         }
      }
   }

   public void sendUpdates() {
      this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
      this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
      this.func_70296_d();
   }

   @Nullable
   public EternalData getEternal() {
      if (this.func_145831_w() == null) {
         return null;
      } else if (!this.func_145831_w().func_201670_d()) {
         return this.eternalId == null ? null : EternalsData.get((ServerWorld)this.func_145831_w()).getEternals(this.owner).get(this.eternalId);
      } else {
         return null;
      }
   }

   public UUID getEternalId() {
      return this.eternalId;
   }

   protected boolean isFull() {
      return !this.coreNames.isEmpty() && this.coreNames.size() >= this.maxCores;
   }

   public void func_73660_a() {
      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K && this.owner != null) {
         if (this.isFull() && !this.growingEternal && this.eternalId == null) {
            this.growingEternal = true;
            this.growEternalTimeRemaining = ModConfigs.CRYO_CHAMBER.getGrowEternalTime();
         }

         if (this.isFull() && !this.growingEternal && this.field_145850_b.func_82737_E() % 40L == 0L) {
            this.field_145850_b
               .func_184148_a(
                  null,
                  this.field_174879_c.func_177958_n(),
                  this.field_174879_c.func_177956_o(),
                  this.field_174879_c.func_177952_p(),
                  SoundEvents.field_206934_aN,
                  SoundCategory.PLAYERS,
                  0.25F,
                  1.0F
               );
         }

         if (this.infusing) {
            if (this.infusionTimeRemaining-- <= 0) {
               this.infusionTimeRemaining = 0;
               this.infusing = false;
            }

            this.sendUpdates();
         } else if (this.growingEternal) {
            if (this.growEternalTimeRemaining-- <= 0) {
               this.growEternalTimeRemaining = 0;
               this.growingEternal = false;
               this.createEternal();
            }

            this.sendUpdates();
         }
      }
   }

   private void createEternal() {
      EternalsData.EternalGroup eternals = EternalsData.get((ServerWorld)this.func_145831_w()).getEternals(this.owner);
      int attempts = 100;

      String name;
      do {
         attempts--;
         name = this.coreNames.get(this.func_145831_w().func_201674_k().nextInt(this.coreNames.size()));
      } while (attempts > 0 && eternals.containsEternal(name));

      this.eternalId = EternalsData.get((ServerWorld)this.func_145831_w()).add(this.owner, name, false);
   }

   public ITextComponent func_145748_c_() {
      EternalData eternal = this.getEternal();
      return eternal != null ? new StringTextComponent(eternal.getName()) : new StringTextComponent("Cryo Chamber");
   }

   @Nullable
   public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
      return this.func_145831_w() == null ? null : new CryochamberContainer(windowId, this.func_145831_w(), this.func_174877_v(), playerInventory);
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      super.func_189515_b(nbt);
      if (this.owner != null) {
         nbt.func_186854_a("Owner", this.owner);
      }

      if (this.eternalId != null) {
         nbt.func_186854_a("EternalId", this.eternalId);
      }

      if (!this.coreNames.isEmpty()) {
         ListNBT list = new ListNBT();

         for (int i = 0; i < this.coreNames.size(); i++) {
            CompoundNBT nameNbt = new CompoundNBT();
            String name = this.coreNames.get(i);
            nameNbt.func_74778_a("name" + i, name);
            list.add(nameNbt);
         }

         nbt.func_218657_a("CoresList", list);
      }

      nbt.func_74768_a("MaxCoreCount", this.maxCores);
      nbt.func_74757_a("Infusing", this.infusing);
      nbt.func_74768_a("InfusionTimeRemaining", this.infusionTimeRemaining);
      nbt.func_74757_a("GrowingEternal", this.growingEternal);
      nbt.func_74768_a("GrowEternalTimeRemaining", this.growEternalTimeRemaining);
      nbt.func_218657_a("Inventory", this.itemHandler.serializeNBT());
      return nbt;
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      super.func_230337_a_(state, nbt);
      if (nbt.func_74764_b("Owner")) {
         this.owner = nbt.func_186857_a("Owner");
      }

      if (nbt.func_74764_b("EternalId")) {
         this.eternalId = nbt.func_186857_a("EternalId");
      }

      if (nbt.func_74764_b("CoresList")) {
         ListNBT list = nbt.func_150295_c("CoresList", 10);
         this.coreNames = new LinkedList<>();

         for (int i = 0; i < list.size(); i++) {
            CompoundNBT nameTag = list.func_150305_b(i);
            this.coreNames.add(nameTag.func_74779_i("name" + i));
         }
      }

      this.maxCores = nbt.func_74762_e("MaxCoreCount");
      this.infusing = nbt.func_74767_n("Infusing");
      this.infusionTimeRemaining = nbt.func_74762_e("InfusionTimeRemaining");
      this.growingEternal = nbt.func_74767_n("GrowingEternal");
      this.growEternalTimeRemaining = nbt.func_74762_e("GrowEternalTimeRemaining");
      this.itemHandler.deserializeNBT(nbt.func_74775_l("Inventory"));
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.handler.cast() : super.getCapability(cap, side);
   }

   public CompoundNBT func_189517_E_() {
      return this.func_189515_b(new CompoundNBT());
   }

   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
   }

   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT nbt = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), nbt);
   }

   public CompoundNBT getRenameNBT() {
      CompoundNBT nbt = new CompoundNBT();
      EternalData eternal = this.getEternal();
      if (eternal == null) {
         return nbt;
      } else {
         nbt.func_218657_a("BlockPos", NBTUtil.func_186859_a(this.func_174877_v()));
         nbt.func_74778_a("EternalName", eternal.getName());
         return nbt;
      }
   }

   public void renameEternal(String name) {
      if (this.getEternal() != null) {
         this.getEternal().setName(name);
      }
   }
}
