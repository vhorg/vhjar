package iskallia.vault.block.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.container.inventory.CryochamberContainer;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.RenameType;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.world.data.EternalsData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

public class CryoChamberTileEntity extends BlockEntity implements MenuProvider {
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
   public int eternalSoulsConsumed;
   public EternalsData.EternalVariant variant;
   public boolean usingPlayerSkin = false;
   private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
      protected void onContentsChanged(int slot) {
         if (this.getStackInSlot(slot).getItem() == ModItems.ETERNAL_SOUL) {
            CryoChamberTileEntity.this.addEternalSoul();
            this.setStackInSlot(slot, ItemStack.EMPTY);
         }

         CryoChamberTileEntity.this.sendUpdates();
      }

      public boolean isItemValid(int slot, ItemStack stack) {
         return stack.getItem() == ModItems.ETERNAL_SOUL && !CryoChamberTileEntity.this.isFull() && !CryoChamberTileEntity.this.isInfusing();
      }
   };
   private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);

   protected CryoChamberTileEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
      super(tileEntityType, pos, state);
      this.skin = new SkinProfile();
      this.eternalSoulsConsumed = 0;
   }

   public CryoChamberTileEntity(BlockPos pos, BlockState state) {
      this(ModBlocks.CRYO_CHAMBER_TILE_ENTITY, pos, state);
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
      return this.eternalSoulsConsumed;
   }

   public boolean addEternalSoul() {
      if (!this.isFull() && !this.isInfusing() && this.getOwner() != null) {
         if (this.level instanceof ServerLevel sWorld) {
            sWorld.playSound(
               null, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.0F, 1.0F
            );
            Optional<GameProfile> knownProfile = sWorld.getServer().getProfileCache().get(this.getOwner());
            if (knownProfile.isEmpty()) {
               return false;
            } else {
               int eternals = EternalsData.get(sWorld).getEternals(this.getOwner()).getNonAncientEternalCount();
               int cores = this.getMaxCores();
               int newCores = ModConfigs.CRYO_CHAMBER.getPlayerCoreCount(knownProfile.get().getName(), eternals);
               if (cores != newCores) {
                  this.setMaxCores(newCores);
                  this.sendUpdates();
               }

               this.eternalSoulsConsumed++;
               this.infusing = true;
               this.infusionTimeRemaining = ModConfigs.CRYO_CHAMBER.getInfusionTime();
               this.sendUpdates();
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void updateSkin() {
      EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(this.getEternalId());
      if (snapshot != null && snapshot.getName() != null) {
         this.skin.updateSkin(snapshot.getName());
      }
   }

   public void sendUpdates() {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      this.setChanged();
   }

   @Nullable
   public EternalData getEternal() {
      if (this.getLevel() == null) {
         return null;
      } else if (!this.getLevel().isClientSide()) {
         return this.eternalId == null ? null : EternalsData.get((ServerLevel)this.getLevel()).getEternals(this.owner).get(this.eternalId);
      } else {
         return null;
      }
   }

   public UUID getEternalId() {
      return this.eternalId;
   }

   protected boolean isFull() {
      return this.eternalSoulsConsumed != 0 && this.eternalSoulsConsumed >= this.maxCores;
   }

   public static void tick(Level level, BlockPos pos, BlockState state, CryoChamberTileEntity tile) {
      if (level != null && !level.isClientSide && tile.owner != null) {
         if (tile.isFull() && !tile.growingEternal && tile.eternalId == null) {
            tile.growingEternal = true;
            tile.growEternalTimeRemaining = ModConfigs.CRYO_CHAMBER.getGrowEternalTime();
         }

         if (tile.isFull() && !tile.growingEternal && level.getGameTime() % 40L == 0L) {
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CONDUIT_AMBIENT, SoundSource.PLAYERS, 0.25F, 1.0F);
         }

         if (tile.infusing) {
            if (tile.infusionTimeRemaining-- <= 0) {
               tile.infusionTimeRemaining = 0;
               tile.infusing = false;
            }

            tile.sendUpdates();
         } else if (tile.growingEternal) {
            if (tile.growEternalTimeRemaining-- <= 0) {
               tile.growEternalTimeRemaining = 0;
               tile.growingEternal = false;
               tile.createEternal();
            }

            tile.sendUpdates();
         }
      }
   }

   public void renameEternal(ServerPlayer player) {
      final CompoundTag nbt = new CompoundTag();
      nbt.putInt("RenameType", RenameType.CRYO_CHAMBER.ordinal());
      nbt.put("Data", this.getRenameNBT());
      NetworkHooks.openGui(player, new MenuProvider() {
         public Component getDisplayName() {
            return new TextComponent("Rename Eternal");
         }

         @Nullable
         public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
            return new RenamingContainer(windowId, nbt);
         }
      }, buffer -> buffer.writeNbt(nbt));
   }

   private void createEternal() {
      EternalsData.EternalGroup eternals = EternalsData.get((ServerLevel)this.getLevel()).getEternals(this.owner);
      int attempts = 50;

      String name;
      do {
         attempts--;
         name = ModConfigs.ETERNALS_NAMEPOOL.getRandomName();
      } while (attempts > 0 && eternals.containsEternal(name));

      EternalsData.EternalVariant variant = EternalsData.EternalVariant.byId(new Random().nextInt(EternalsData.EternalVariant.values().length));
      this.variant = variant;
      this.eternalId = EternalsData.get((ServerLevel)this.getLevel()).add(this.owner, name, false, variant, false);
   }

   public Component getDisplayName() {
      EternalData eternal = this.getEternal();
      return eternal != null ? new TextComponent(eternal.getName()) : new TextComponent("Cryo Chamber");
   }

   @Nullable
   public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
      return this.getLevel() == null ? null : new CryochamberContainer(windowId, this.getLevel(), this.getBlockPos(), playerInventory);
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      if (this.owner != null) {
         nbt.putUUID("Owner", this.owner);
      }

      if (this.eternalId != null) {
         nbt.putUUID("EternalId", this.eternalId);
      }

      nbt.putInt("MaxCoreCount", this.maxCores);
      nbt.putBoolean("Infusing", this.infusing);
      nbt.putInt("InfusionTimeRemaining", this.infusionTimeRemaining);
      nbt.putBoolean("GrowingEternal", this.growingEternal);
      nbt.putInt("GrowEternalTimeRemaining", this.growEternalTimeRemaining);
      nbt.put("Inventory", this.itemHandler.serializeNBT());
      nbt.putInt("EternalSoulsConsumed", this.eternalSoulsConsumed);
      nbt.putBoolean("UsingPlayerSkin", this.usingPlayerSkin);
      if (this.variant != null) {
         nbt.putInt("EternalVariant", this.variant.getId());
      }
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      if (nbt.contains("Owner")) {
         this.owner = nbt.getUUID("Owner");
      }

      if (nbt.contains("EternalId")) {
         this.eternalId = nbt.getUUID("EternalId");
      }

      this.maxCores = nbt.getInt("MaxCoreCount");
      this.infusing = nbt.getBoolean("Infusing");
      this.infusionTimeRemaining = nbt.getInt("InfusionTimeRemaining");
      this.growingEternal = nbt.getBoolean("GrowingEternal");
      this.growEternalTimeRemaining = nbt.getInt("GrowEternalTimeRemaining");
      this.itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
      this.eternalSoulsConsumed = nbt.getInt("EternalSoulsConsumed");
      this.usingPlayerSkin = nbt.getBoolean("UsingPlayerSkin");
      if (nbt.contains("EternalVariant")) {
         this.variant = EternalsData.EternalVariant.byId(nbt.getInt("EternalVariant"));
      }
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.handler.cast() : super.getCapability(cap, side);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getRenameNBT() {
      CompoundTag nbt = new CompoundTag();
      EternalData eternal = this.getEternal();
      if (eternal == null) {
         return nbt;
      } else {
         nbt.put("BlockPos", NbtUtils.writeBlockPos(this.getBlockPos()));
         nbt.putString("EternalName", eternal.getName());
         return nbt;
      }
   }

   public void renameEternal(String name) {
      if (this.getEternal() != null) {
         this.getEternal().setName(name);
      }
   }
}
