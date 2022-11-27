package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScavengerChestTileEntity extends ChestBlockEntity {
   private static final Random rand = new Random();

   protected ScavengerChestTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
      super(typeIn, pos, state);
      this.setItems(NonNullList.withSize(45, ItemStack.EMPTY));
   }

   public ScavengerChestTileEntity(BlockPos pos, BlockState state) {
      this(ModBlocks.SCAVENGER_CHEST_TILE_ENTITY, pos, state);
   }

   public static <E extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, ScavengerChestTileEntity e) {
      ChestBlockEntity.lidAnimateTick(level, pos, state, e);
      e.playEffects();
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      ParticleEngine mgr = Minecraft.getInstance().particleEngine;
      BlockPos pos = this.getBlockPos();
      Vec3 rPos = new Vec3(
         pos.getX() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 3.0F,
         pos.getY() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 7.0F,
         pos.getZ() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 3.0F
      );
      SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.createParticle(ParticleTypes.FIREWORK, rPos.x, rPos.y, rPos.z, 0.0, 0.0, 0.0);
      if (p != null) {
         p.setColor(2347008);
      }
   }

   public int getContainerSize() {
      return 54;
   }

   protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
      return new ChestMenu(MenuType.GENERIC_9x5, id, playerInventory, this, 5);
   }

   public Component getDisplayName() {
      return new TranslatableComponent(ModBlocks.SCAVENGER_CHEST.getDescriptionId());
   }
}
