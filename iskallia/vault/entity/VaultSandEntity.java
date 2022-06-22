package iskallia.vault.entity;

import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.VaultSandEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VaultSandEntity extends FloatingItemEntity {
   public VaultSandEntity(EntityType<? extends ItemEntity> type, World world) {
      super(type, world);
      this.setColor(-3241472, -3229440);
   }

   public VaultSandEntity(World worldIn, double x, double y, double z) {
      this(ModEntities.VAULT_SAND, worldIn);
      this.func_70107_b(x, y, z);
      this.field_70177_z = this.field_70146_Z.nextFloat() * 360.0F;
      this.func_213293_j(this.field_70146_Z.nextDouble() * 0.2 - 0.1, 0.2, this.field_70146_Z.nextDouble() * 0.2 - 0.1);
   }

   public VaultSandEntity(World worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn, x, y, z);
      this.func_92058_a(stack);
      this.lifespan = Integer.MAX_VALUE;
   }

   public static VaultSandEntity create(World world, BlockPos pos) {
      return new VaultSandEntity(world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, new ItemStack(ModItems.VAULT_SAND));
   }

   @Override
   public void func_70100_b_(PlayerEntity player) {
      boolean wasAlive = this.func_70089_S();
      super.func_70100_b_(player);
      if (wasAlive && !this.func_70089_S() && player instanceof ServerPlayerEntity) {
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)player;
         VaultRaid activeRaid = VaultRaidData.get(sPlayer.func_71121_q()).getActiveFor(sPlayer);
         if (activeRaid != null) {
            activeRaid.getProperties().get(VaultRaid.SAND_EVENT).ifPresent(eventData -> {
               ((VaultSandEvent)eventData.getBaseValue()).pickupSand(sPlayer);
               eventData.updateNBT();
            });
         }
      }
   }
}
