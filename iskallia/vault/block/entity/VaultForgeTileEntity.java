package iskallia.vault.block.entity;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.container.VaultForgeContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultForgeTileEntity extends BlockEntity implements MenuProvider {
   private final OverSizedInventory inventory = new OverSizedInventory(6, this);
   private final ResultContainer output = new ResultContainer() {
      public void setChanged() {
         super.setChanged();
         VaultForgeTileEntity.this.setChanged();
      }
   };

   public VaultForgeTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.VAULT_FORGE_TILE_ENTITY, pWorldPosition, pBlockState);
   }

   public OverSizedInventory getInventory() {
      return this.inventory;
   }

   public ResultContainer getResultContainer() {
      return this.output;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.inventory.load(tag);
      this.getResultContainer().setItem(0, ItemStack.of(tag.getCompound("result")));
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      this.inventory.save(tag);
      tag.put("result", this.getResultContainer().getItem(0).serializeNBT());
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
      return this.getLevel() == null ? null : new VaultForgeContainer(windowId, this.getLevel(), this.getBlockPos(), playerInventory);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnForgeParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < 8; i++) {
            Random random = level.getRandom();
            Direction direction = level.getBlockState(pos).hasProperty(FacedBlock.FACING)
               ? (Direction)level.getBlockState(pos).getValue(FacedBlock.FACING)
               : Direction.NORTH;
            Axis direction$axis = direction.getAxis();
            double d0 = pos.getX() + 0.5;
            double d1 = pos.getY();
            double d2 = pos.getZ() + 0.5;
            double d3 = -0.52;
            double d4 = random.nextDouble() * 0.6 - 0.3;
            double d5 = direction$axis == Axis.X ? direction.getStepX() * d3 : d4;
            double d6 = random.nextDouble() * 6.0 / 16.0;
            double d7 = direction$axis == Axis.Z ? direction.getStepZ() * d3 : d4;
            level.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.LAVA, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0);
         }
      }
   }
}
