package iskallia.vault.util;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorld;

public class EntityHelper {
   private static final AxisAlignedBB BOX = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

   public static void changeHealth(LivingEntity entity, int healthChange) {
      float health = entity.func_110143_aJ();
      entity.func_70606_j(health + healthChange);
      if (entity.func_233643_dh_()) {
         entity.func_70645_a(entity.func_189748_bU() != null ? entity.func_189748_bU() : DamageSource.field_76377_j);
      }
   }

   public static <T extends Entity> T changeSize(T entity, float size, Runnable callback) {
      changeSize(entity, size);
      callback.run();
      return entity;
   }

   public static <T extends Entity> T changeSize(T entity, float size) {
      entity.field_213325_aI = entity.func_213305_a(Pose.STANDING).func_220313_a(size);
      entity.func_213323_x_();
      return entity;
   }

   public static void giveItem(PlayerEntity player, ItemStack itemStack) {
      boolean added = player.field_71071_by.func_70441_a(itemStack);
      if (!added) {
         player.func_146097_a(itemStack, false, false);
      }
   }

   public static <T extends Entity> List<T> getNearby(IWorld world, Vector3i pos, float radius, Class<T> entityClass) {
      AxisAlignedBB selectBox = BOX.func_72317_d(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p()).func_186662_g(radius);
      return world.func_175647_a(entityClass, selectBox, entity -> entity.func_70089_S() && !entity.func_175149_v());
   }
}
