package iskallia.vault.entity.entity;

import iskallia.vault.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class VaultSandEntity extends FloatingItemEntity {
   public VaultSandEntity(EntityType<? extends ItemEntity> type, Level world) {
      super(type, world);
      this.setColor(-3241472, -3229440);
   }

   public VaultSandEntity(Level worldIn, double x, double y, double z) {
      this(ModEntities.VAULT_SAND, worldIn);
      this.setPos(x, y, z);
      this.setYRot(this.random.nextFloat() * 360.0F);
      this.setDeltaMovement(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
   }

   public VaultSandEntity(Level worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn, x, y, z);
      this.setItem(stack);
      this.lifespan = Integer.MAX_VALUE;
   }

   public static VaultSandEntity create(Level world, BlockPos pos) {
      return new VaultSandEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.SAND));
   }
}
