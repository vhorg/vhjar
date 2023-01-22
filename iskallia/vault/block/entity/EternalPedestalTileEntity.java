package iskallia.vault.block.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.block.EternalPedestalBlock;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.container.inventory.CryochamberContainer;
import iskallia.vault.entity.IPlayerSkinHolder;
import iskallia.vault.entity.entity.EternalSpiritEntity;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.RenameType;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.world.data.EternalsData;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class EternalPedestalTileEntity extends BlockEntity implements IPlayerSkinHolder, MenuProvider {
   private static final String GAME_PROFILE_TAG = "gameProfile";
   private static final String OWNER_TAG = "ownerUUID";
   private static final String ETERNAL_TAG = "eternalUUID";
   @Nullable
   private GameProfile gameProfile;
   private ResourceLocation skinLocation = null;
   private boolean updatingSkin = false;
   private boolean slimSkin = false;
   private UUID owner;
   protected UUID eternalId;
   private SkinProfile skinProfile = new SkinProfile();
   private boolean updateSkin = false;

   public EternalPedestalTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.ETERNAL_PEDESTAL_TILE_ENTITY, pos, state);
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
   }

   public UUID getOwner() {
      return this.owner;
   }

   @Override
   public void setGameProfile(GameProfile gameProfile) {
      this.gameProfile = gameProfile;
      this.skinLocation = null;
      this.slimSkin = false;
      this.setChanged();
   }

   public UUID getEternalId() {
      return this.eternalId;
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

   public boolean interact(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
      if (hand == InteractionHand.OFF_HAND) {
         return false;
      } else {
         ItemStack stack = player.getItemInHand(hand);
         Vec3 loc = hit.getLocation();
         float[] offsets = new float[]{0.5F, 0.85F, 1.46F, 2.0F};
         float arms = 0.25F;
         float armsY = 1.75F;
         Vec3 base = new Vec3(this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY(), this.getBlockPos().getZ() + 0.5F);
         float yPos = (float)(loc.y - base.y);
         float xPos = (float)(loc.x - base.x);
         float zPos = (float)(loc.z - base.z);
         EternalDataSnapshot eternalSnapshot = null;
         if (player.level instanceof ServerLevel serverLevel) {
            EternalsData eternalsdata = EternalsData.get(serverLevel);
            EternalData eternalData = eternalsdata.getEternal(this.getEternalId());
            if (eternalData != null) {
               eternalSnapshot = EternalDataSnapshot.getFromEternal(eternalsdata.getEternals(this.getOwner()), eternalData);
            }
         } else {
            eternalSnapshot = ClientEternalData.getSnapshot(this.getEternalId());
         }

         if (yPos > 0.4375F && eternalSnapshot != null) {
            if (yPos > 0.5F && yPos < armsY && state.hasProperty(EternalPedestalBlock.FACING)) {
               if (state.getValue(EternalPedestalBlock.FACING) == Direction.WEST) {
                  if (zPos > arms) {
                     if (!stack.isEmpty() || !eternalSnapshot.getEquipment(EquipmentSlot.OFFHAND).isEmpty()) {
                        ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.OFFHAND).copy();
                        if (player.level instanceof ServerLevel) {
                           EternalData data = this.getEternal();
                           if (data != null) {
                              data.setStack(EquipmentSlot.OFFHAND, stack.copy());
                           }
                        }

                        player.level
                           .playSound(
                              null,
                              this.getBlockPos().above(),
                              stack.isEmpty() ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM,
                              SoundSource.BLOCKS,
                              0.6F,
                              1.0F
                           );
                        setItemInPlayerHandNoSound(player, hand, temp);
                        return true;
                     }
                  } else if (zPos < -arms && (!stack.isEmpty() || !eternalSnapshot.getEquipment(EquipmentSlot.MAINHAND).isEmpty())) {
                     ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.MAINHAND).copy();
                     if (player.level instanceof ServerLevel) {
                        EternalData data = this.getEternal();
                        if (data != null) {
                           data.setStack(EquipmentSlot.MAINHAND, stack.copy());
                        }
                     }

                     player.level
                        .playSound(
                           null,
                           this.getBlockPos().above(),
                           stack.isEmpty() ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM,
                           SoundSource.BLOCKS,
                           0.6F,
                           1.0F
                        );
                     setItemInPlayerHandNoSound(player, hand, temp);
                     return true;
                  }
               }

               if (state.getValue(EternalPedestalBlock.FACING) == Direction.EAST) {
                  if (zPos > arms) {
                     if (!stack.isEmpty() || !eternalSnapshot.getEquipment(EquipmentSlot.MAINHAND).isEmpty()) {
                        ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.MAINHAND).copy();
                        if (player.level instanceof ServerLevel) {
                           EternalData data = this.getEternal();
                           if (data != null) {
                              data.setStack(EquipmentSlot.MAINHAND, stack.copy());
                           }
                        }

                        player.level
                           .playSound(
                              null,
                              this.getBlockPos().above(),
                              stack.isEmpty() ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM,
                              SoundSource.BLOCKS,
                              0.6F,
                              1.0F
                           );
                        setItemInPlayerHandNoSound(player, hand, temp);
                        return true;
                     }
                  } else if (zPos < -arms && (!stack.isEmpty() || !eternalSnapshot.getEquipment(EquipmentSlot.OFFHAND).isEmpty())) {
                     ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.OFFHAND).copy();
                     if (player.level instanceof ServerLevel) {
                        EternalData data = this.getEternal();
                        if (data != null) {
                           data.setStack(EquipmentSlot.OFFHAND, stack.copy());
                        }
                     }

                     player.level
                        .playSound(
                           null,
                           this.getBlockPos().above(),
                           stack.isEmpty() ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM,
                           SoundSource.BLOCKS,
                           0.6F,
                           1.0F
                        );
                     setItemInPlayerHandNoSound(player, hand, temp);
                     return true;
                  }
               }

               if (state.getValue(EternalPedestalBlock.FACING) == Direction.NORTH) {
                  if (xPos > arms) {
                     if (!stack.isEmpty() || !eternalSnapshot.getEquipment(EquipmentSlot.MAINHAND).isEmpty()) {
                        ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.MAINHAND).copy();
                        if (player.level instanceof ServerLevel) {
                           EternalData data = this.getEternal();
                           if (data != null) {
                              data.setStack(EquipmentSlot.MAINHAND, stack.copy());
                           }
                        }

                        player.level
                           .playSound(
                              null,
                              this.getBlockPos().above(),
                              stack.isEmpty() ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM,
                              SoundSource.BLOCKS,
                              0.6F,
                              1.0F
                           );
                        setItemInPlayerHandNoSound(player, hand, temp);
                        return true;
                     }
                  } else if (xPos < -arms && (!stack.isEmpty() || !eternalSnapshot.getEquipment(EquipmentSlot.OFFHAND).isEmpty())) {
                     ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.OFFHAND).copy();
                     if (player.level instanceof ServerLevel) {
                        EternalData data = this.getEternal();
                        if (data != null) {
                           data.setStack(EquipmentSlot.OFFHAND, stack.copy());
                        }
                     }

                     player.level
                        .playSound(
                           null,
                           this.getBlockPos().above(),
                           stack.isEmpty() ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM,
                           SoundSource.BLOCKS,
                           0.6F,
                           1.0F
                        );
                     setItemInPlayerHandNoSound(player, hand, temp);
                     return true;
                  }
               }

               if (state.getValue(EternalPedestalBlock.FACING) == Direction.SOUTH) {
                  if (xPos > arms) {
                     if (!stack.isEmpty() || !eternalSnapshot.getEquipment(EquipmentSlot.OFFHAND).isEmpty()) {
                        ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.OFFHAND).copy();
                        if (player.level instanceof ServerLevel) {
                           EternalData data = this.getEternal();
                           if (data != null) {
                              data.setStack(EquipmentSlot.OFFHAND, stack.copy());
                           }
                        }

                        player.level
                           .playSound(
                              null,
                              this.getBlockPos().above(),
                              stack.isEmpty() ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM,
                              SoundSource.BLOCKS,
                              0.6F,
                              1.0F
                           );
                        setItemInPlayerHandNoSound(player, hand, temp);
                        return true;
                     }
                  } else if (xPos < -arms && (!stack.isEmpty() || !eternalSnapshot.getEquipment(EquipmentSlot.MAINHAND).isEmpty())) {
                     ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.MAINHAND).copy();
                     if (player.level instanceof ServerLevel) {
                        EternalData data = this.getEternal();
                        if (data != null) {
                           data.setStack(EquipmentSlot.MAINHAND, stack.copy());
                        }
                     }

                     player.level
                        .playSound(
                           null,
                           this.getBlockPos().above(),
                           stack.isEmpty() ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM,
                           SoundSource.BLOCKS,
                           0.6F,
                           1.0F
                        );
                     setItemInPlayerHandNoSound(player, hand, temp);
                     return true;
                  }
               }
            }

            if (yPos > offsets[3]) {
               if (stack.isEmpty() && !eternalSnapshot.getEquipment(EquipmentSlot.HEAD).isEmpty()) {
                  ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.HEAD).copy();
                  SoundEvent soundEvent = eternalSnapshot.getEquipment(EquipmentSlot.HEAD).getEquipSound();
                  player.level
                     .playSound(
                        null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                     );
                  if (player.level instanceof ServerLevel) {
                     EternalData data = this.getEternal();
                     if (data != null) {
                        data.setStack(EquipmentSlot.HEAD, stack.copy());
                     }
                  }

                  setItemInPlayerHandNoSound(player, hand, temp);
                  return true;
               }

               if (stack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.HEAD) {
                  SoundEvent soundEvent = armor.getEquipSound();
                  player.level
                     .playSound(
                        null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                     );
                  ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.HEAD).copy();
                  if (player.level instanceof ServerLevel) {
                     EternalData data = this.getEternal();
                     if (data != null) {
                        data.setStack(EquipmentSlot.HEAD, stack.copy());
                     }
                  }

                  setItemInPlayerHandNoSound(player, hand, temp);
                  return true;
               }
            } else if (stack.getItem() instanceof ArmorItem armor
               && armor.getSlot() == EquipmentSlot.HEAD
               && eternalSnapshot.getEquipment(EquipmentSlot.HEAD).isEmpty()) {
               ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.HEAD).copy();
               SoundEvent soundEvent = armor.getEquipSound();
               player.level
                  .playSound(
                     null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                  );
               if (player.level instanceof ServerLevel) {
                  EternalData data = this.getEternal();
                  if (data != null) {
                     data.setStack(EquipmentSlot.HEAD, stack.copy());
                  }
               }

               setItemInPlayerHandNoSound(player, hand, temp);
               return false;
            }

            if (yPos > offsets[2] && yPos < offsets[3]) {
               if (stack.isEmpty() && !eternalSnapshot.getEquipment(EquipmentSlot.CHEST).isEmpty()) {
                  ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.CHEST).copy();
                  SoundEvent soundEvent = eternalSnapshot.getEquipment(EquipmentSlot.CHEST).getEquipSound();
                  player.level
                     .playSound(
                        null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                     );
                  if (player.level instanceof ServerLevel) {
                     EternalData data = this.getEternal();
                     if (data != null) {
                        data.setStack(EquipmentSlot.CHEST, stack.copy());
                     }
                  }

                  setItemInPlayerHandNoSound(player, hand, temp);
                  return true;
               }

               if (stack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.CHEST) {
                  SoundEvent soundEvent = armor.getEquipSound();
                  player.level
                     .playSound(
                        null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                     );
                  ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.CHEST).copy();
                  if (player.level instanceof ServerLevel) {
                     EternalData data = this.getEternal();
                     if (data != null) {
                        data.setStack(EquipmentSlot.CHEST, stack.copy());
                     }
                  }

                  setItemInPlayerHandNoSound(player, hand, temp);
                  return true;
               }
            } else if (stack.getItem() instanceof ArmorItem armor
               && armor.getSlot() == EquipmentSlot.CHEST
               && eternalSnapshot.getEquipment(EquipmentSlot.CHEST).isEmpty()) {
               ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.CHEST).copy();
               SoundEvent soundEvent = armor.getEquipSound();
               player.level
                  .playSound(
                     null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                  );
               if (player.level instanceof ServerLevel) {
                  EternalData data = this.getEternal();
                  if (data != null) {
                     data.setStack(EquipmentSlot.CHEST, stack.copy());
                  }
               }

               setItemInPlayerHandNoSound(player, hand, temp);
               return false;
            }

            if (yPos > offsets[1] && yPos < offsets[2]) {
               if (stack.isEmpty() && !eternalSnapshot.getEquipment(EquipmentSlot.LEGS).isEmpty()) {
                  ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.LEGS).copy();
                  SoundEvent soundEvent = eternalSnapshot.getEquipment(EquipmentSlot.LEGS).getEquipSound();
                  player.level
                     .playSound(
                        null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                     );
                  if (player.level instanceof ServerLevel) {
                     EternalData data = this.getEternal();
                     if (data != null) {
                        data.setStack(EquipmentSlot.LEGS, stack.copy());
                     }
                  }

                  setItemInPlayerHandNoSound(player, hand, temp);
                  return true;
               }

               if (stack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.LEGS) {
                  SoundEvent soundEvent = armor.getEquipSound();
                  player.level
                     .playSound(
                        null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                     );
                  ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.LEGS).copy();
                  if (player.level instanceof ServerLevel) {
                     EternalData data = this.getEternal();
                     if (data != null) {
                        data.setStack(EquipmentSlot.LEGS, stack.copy());
                     }
                  }

                  setItemInPlayerHandNoSound(player, hand, temp);
                  return true;
               }
            } else if (stack.getItem() instanceof ArmorItem armor
               && armor.getSlot() == EquipmentSlot.LEGS
               && eternalSnapshot.getEquipment(EquipmentSlot.LEGS).isEmpty()) {
               ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.LEGS).copy();
               SoundEvent soundEvent = armor.getEquipSound();
               player.level
                  .playSound(
                     null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                  );
               if (player.level instanceof ServerLevel) {
                  EternalData data = this.getEternal();
                  if (data != null) {
                     data.setStack(EquipmentSlot.LEGS, stack.copy());
                  }
               }

               setItemInPlayerHandNoSound(player, hand, temp);
               return false;
            }

            if (yPos > offsets[0] && yPos < offsets[1]) {
               if (stack.isEmpty() && !eternalSnapshot.getEquipment(EquipmentSlot.FEET).isEmpty()) {
                  ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.FEET).copy();
                  SoundEvent soundEvent = eternalSnapshot.getEquipment(EquipmentSlot.FEET).getEquipSound();
                  player.level
                     .playSound(
                        null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                     );
                  if (player.level instanceof ServerLevel) {
                     EternalData data = this.getEternal();
                     if (data != null) {
                        data.setStack(EquipmentSlot.FEET, stack.copy());
                     }
                  }

                  setItemInPlayerHandNoSound(player, hand, temp);
                  return true;
               }

               if (stack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.FEET) {
                  SoundEvent soundEvent = armor.getEquipSound();
                  player.level
                     .playSound(
                        null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                     );
                  ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.FEET).copy();
                  if (player.level instanceof ServerLevel) {
                     EternalData data = this.getEternal();
                     if (data != null) {
                        data.setStack(EquipmentSlot.FEET, stack.copy());
                     }
                  }

                  setItemInPlayerHandNoSound(player, hand, temp);
                  return true;
               }
            } else if (stack.getItem() instanceof ArmorItem armor
               && armor.getSlot() == EquipmentSlot.FEET
               && eternalSnapshot.getEquipment(EquipmentSlot.FEET).isEmpty()) {
               ItemStack temp = eternalSnapshot.getEquipment(EquipmentSlot.FEET).copy();
               SoundEvent soundEvent = armor.getEquipSound();
               player.level
                  .playSound(
                     null, this.getBlockPos().above(), soundEvent != null ? soundEvent : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.0F
                  );
               if (player.level instanceof ServerLevel serverLevelx) {
                  EternalData data = this.getEternal();
                  if (data != null) {
                     data.setStack(EquipmentSlot.FEET, stack.copy());
                  }
               }

               setItemInPlayerHandNoSound(player, hand, temp);
               return false;
            }
         } else {
            if (this.eternalId == null && !player.getPassengers().isEmpty() && player.getPassengers().get(0) instanceof EternalSpiritEntity spirit) {
               this.eternalId = spirit.getEternalUUID();
               if (player.level instanceof ServerLevel) {
                  spirit.remove(RemovalReason.DISCARDED);
               }

               if (player.level instanceof ServerLevel serverLevelxx) {
                  EternalData data = EternalsData.get(serverLevelxx).getEternal(this.eternalId);
                  if (data != null) {
                     this.skinProfile.updateSkin(data.getName());
                  }
               } else {
                  EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(this.eternalId);
                  if (snapshot != null) {
                     this.skinProfile.updateSkin(snapshot.getName());
                  }
               }

               this.setChanged();
               return true;
            }

            if (this.eternalId != null) {
               if (player instanceof ServerPlayer serverPlayer) {
                  NetworkHooks.openGui(serverPlayer, this, buffer -> buffer.writeBlockPos(this.worldPosition));
               }

               return true;
            }
         }

         return false;
      }
   }

   @Nullable
   public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
      return this.getLevel() == null ? null : new CryochamberContainer(windowId, this.getLevel(), this.getBlockPos(), playerInventory);
   }

   public static void setItemInPlayerHandNoSound(Player player, InteractionHand hand, ItemStack stack) {
      if (hand == InteractionHand.OFF_HAND) {
         player.getInventory().offhand.set(0, stack);
      }

      if (hand == InteractionHand.MAIN_HAND) {
         player.getInventory().items.set(player.getInventory().selected, stack);
      }
   }

   @Override
   public Optional<ResourceLocation> getSkinLocation() {
      return Optional.ofNullable(this.skinLocation);
   }

   @Override
   public boolean isUpdatingSkin() {
      return this.updatingSkin;
   }

   @Override
   public void setSkinLocation(ResourceLocation skinLocation) {
      this.skinLocation = skinLocation;
   }

   @Override
   public void startUpdatingSkin() {
      this.updatingSkin = true;
   }

   @Override
   public void stopUpdatingSkin() {
      this.updatingSkin = false;
   }

   @Override
   public boolean hasSlimSkin() {
      return this.slimSkin;
   }

   @Override
   public void setSlimSkin(boolean slimSkin) {
      this.slimSkin = slimSkin;
   }

   public SkinProfile getSkinProfile() {
      return this.skinProfile;
   }

   @Override
   public Optional<GameProfile> getGameProfile() {
      return Optional.ofNullable(this.gameProfile);
   }

   public void sendUpdates() {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      this.setChanged();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      if (this.gameProfile != null) {
         tag.put("gameProfile", NbtUtils.writeGameProfile(new CompoundTag(), this.gameProfile));
      }

      if (this.owner != null) {
         tag.putUUID("ownerUUID", this.owner);
      }

      if (this.eternalId != null) {
         tag.putUUID("eternalUUID", this.eternalId);
      }
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.setGameProfile(tag.contains("gameProfile") ? NbtUtils.readGameProfile(tag.getCompound("gameProfile")) : null);
      if (tag.contains("ownerUUID")) {
         this.owner = tag.getUUID("ownerUUID");
      }

      if (tag.contains("eternalUUID")) {
         this.eternalId = tag.getUUID("eternalUUID");
         this.updateSkin = true;
      }
   }

   public static void tick(Level level, BlockPos pos, BlockState state, EternalPedestalTileEntity tile) {
      if (!level.isClientSide()) {
         if (state.hasProperty(EternalPedestalBlock.HALF) && state.getValue(EternalPedestalBlock.HALF) == EternalPedestalBlock.TripleBlock.LOWER) {
            BlockState middle = level.getBlockState(pos.above());
            BlockState up = level.getBlockState(pos.above().above());
            if (!(up.getBlock() instanceof EternalPedestalBlock) && up.getMaterial().isReplaceable()) {
               level.setBlockAndUpdate(
                  pos.above().above(),
                  (BlockState)((BlockState)state.getBlock().defaultBlockState().setValue(EternalPedestalBlock.HALF, EternalPedestalBlock.TripleBlock.UPPER))
                     .setValue(EternalPedestalBlock.FACING, (Direction)state.getValue(EternalPedestalBlock.FACING))
               );
            }

            if (!(middle.getBlock() instanceof EternalPedestalBlock) && middle.getMaterial().isReplaceable()) {
               level.setBlockAndUpdate(
                  pos.above(),
                  (BlockState)((BlockState)state.getBlock().defaultBlockState().setValue(EternalPedestalBlock.HALF, EternalPedestalBlock.TripleBlock.MIDDLE))
                     .setValue(EternalPedestalBlock.FACING, (Direction)state.getValue(EternalPedestalBlock.FACING))
               );
            }
         }
      } else if (tile.updateSkin) {
         EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(tile.eternalId);
         if (snapshot != null) {
            tile.skinProfile.updateSkin(snapshot.getName());
            tile.updateSkin = false;
         }
      }
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

   public AABB getRenderBoundingBox() {
      return super.getRenderBoundingBox().expandTowards(0.0, 2.0, 0.0);
   }

   public Component getDisplayName() {
      EternalData eternal = this.getEternal();
      return eternal != null ? new TextComponent(eternal.getName()) : new TextComponent("Cryo Chamber");
   }
}
