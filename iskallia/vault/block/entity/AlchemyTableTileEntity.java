package iskallia.vault.block.entity;

import iskallia.vault.container.AlchemyTableContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.network.message.ClientboundAlchemyParticleMessage;
import iskallia.vault.network.message.ClientboundAlchemySecondParticleMessage;
import iskallia.vault.network.message.ClientboundTESyncMessage;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

public class AlchemyTableTileEntity extends BlockEntity implements MenuProvider {
   private boolean crafting = false;
   private int craftingOutputCooldown = -1;
   public static int CRAFTING_COOLDOWN = 60;
   private float extraSpinDegrees = 0.0F;
   private float extraSpinDegreesPrev = 0.0F;
   private final SimpleContainer inventory = new SimpleContainer(1) {
      public void setChanged() {
         super.setChanged();
         AlchemyTableTileEntity.this.setChanged();
      }
   };
   private ItemStack fakeItemStack = ItemStack.EMPTY;

   public AlchemyTableTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.ALCHEMY_TABLE_TILE_ENTITY, pos, state);
   }

   public float getExtraSpinDegrees() {
      return this.extraSpinDegrees;
   }

   public float getExtraSpinDegreesPrev() {
      return this.extraSpinDegreesPrev;
   }

   public boolean isCrafting() {
      return this.crafting;
   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   public ItemStack getFakeItemStack() {
      return this.fakeItemStack;
   }

   public int getCraftingOutputCooldown() {
      return this.craftingOutputCooldown;
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this && !this.crafting;
   }

   public void startCrafting() {
      this.crafting = true;
      this.craftingOutputCooldown = CRAFTING_COOLDOWN;
      this.fakeItemStack = this.getInventory().getItem(0).copy();
      if (this.level != null && !this.level.isClientSide) {
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)),
               new ClientboundTESyncMessage(this.worldPosition, this.saveWithoutMetadata())
            );
      }
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      NBTHelper.deserializeSimpleContainer(this.inventory, tag.getList("inventory", 10));
      if (tag.contains("fakeItem")) {
         this.fakeItemStack = ItemStack.of(tag.getCompound("fakeItem"));
      }

      this.crafting = tag.getBoolean("crafting");
      this.craftingOutputCooldown = tag.getInt("craftingOutputCooldown");
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.put("inventory", NBTHelper.serializeSimpleContainer(this.inventory));
      tag.put("fakeItem", this.fakeItemStack.save(new CompoundTag()));
      tag.putBoolean("crafting", this.crafting);
      tag.putInt("craftingOutputCooldown", this.craftingOutputCooldown);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
      return this.getLevel() == null ? null : new AlchemyTableContainer(id, this.getLevel(), this.getBlockPos(), inventory);
   }

   public void dropItemStack(Level pLevel, double pX, double pY, double pZ, ItemStack pStack) {
      Random random = new Random();

      while (!pStack.isEmpty()) {
         ItemEntity itementity = new ItemEntity(pLevel, pX, pY, pZ, pStack.split(random.nextInt(21) + 10));
         itementity.setDeltaMovement(0.0, random.nextGaussian() * 0.05F + 0.2F, 0.0);
         itementity.setPickUpDelay(10);
         pLevel.addFreshEntity(itementity);
      }
   }

   public static float ease(double x) {
      if (x == 0.0) {
         return 0.0F;
      } else if (x == 1.0) {
         return 1.0F;
      } else {
         return x < 0.5 ? (float)Math.pow(2.0, 20.0 * x - 10.0) / 2.0F : (float)(2.0 - Math.pow(2.0, -20.0 * x + 10.0)) / 2.0F;
      }
   }

   public void tick(Level level, BlockPos blockPos, BlockState blockState) {
      if (this.level != null) {
         if (!this.level.isClientSide) {
            if (!this.crafting) {
               return;
            }

            if (this.craftingOutputCooldown > 0) {
               if (this.craftingOutputCooldown == CRAFTING_COOLDOWN) {
                  this.level.playSound(null, this.getBlockPos(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.BLOCKS, 0.35F, 1.5F);
               }

               if (this.craftingOutputCooldown == CRAFTING_COOLDOWN - 5) {
                  this.level.playSound(null, this.getBlockPos(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.BLOCKS, 0.35F, 1.5F);
                  this.level.playSound(null, this.getBlockPos(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 2.0F, 1.0F);
               }

               if (this.craftingOutputCooldown == 35) {
                  this.level.playSound(null, this.getBlockPos(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.5F, 1.5F);
               }

               if (this.craftingOutputCooldown == 20) {
                  this.level.playSound(null, this.getBlockPos(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.BLOCKS, 0.5F, 1.5F);
               }

               if (this.craftingOutputCooldown == 19) {
                  this.level.playSound(null, this.getBlockPos(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.BLOCKS, 0.45F, 2.0F);
               }

               int color = BottleItem.getEffectColor(this.getInventory().getItem(0));
               if (this.craftingOutputCooldown > 40 && this.craftingOutputCooldown < CRAFTING_COOLDOWN - 2) {
                  float f = 1.0F - (float)(this.craftingOutputCooldown - 40) / (CRAFTING_COOLDOWN - 40);
                  f = ease(f);
                  float yOffset = (float)Math.sin(f * Math.PI / 1.5) / 8.0F;
                  Direction dir = (Direction)this.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
                  ModNetwork.CHANNEL
                     .send(
                        PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)),
                        new ClientboundAlchemyParticleMessage(this.getBlockPos(), dir, color, yOffset)
                     );
               }

               if (this.craftingOutputCooldown == 20) {
                  ModNetwork.CHANNEL
                     .send(
                        PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)),
                        new ClientboundAlchemySecondParticleMessage(this.getBlockPos(), color)
                     );
               }

               this.craftingOutputCooldown--;
               return;
            }

            this.crafting = false;
            this.dropItemStack(level, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, this.inventory.getItem(0));
            this.inventory.setItem(0, ItemStack.EMPTY);
            ModNetwork.CHANNEL
               .send(
                  PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)),
                  new ClientboundTESyncMessage(this.worldPosition, this.saveWithoutMetadata())
               );
         } else {
            if (!this.crafting) {
               this.extraSpinDegrees = 0.0F;
               this.extraSpinDegreesPrev = 0.0F;
               return;
            }

            if (this.craftingOutputCooldown > 0) {
               this.extraSpinDegreesPrev = this.extraSpinDegrees;
               if (this.craftingOutputCooldown < CRAFTING_COOLDOWN) {
                  float f = 1.0F - (float)this.craftingOutputCooldown / CRAFTING_COOLDOWN;
                  f = ease(f);
                  this.extraSpinDegrees = f * -720.0F;
               }

               this.craftingOutputCooldown--;
            }
         }
      }
   }
}
