package iskallia.vault.util;

import java.lang.reflect.Field;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class EntityHelper {
   public static <T extends Entity> T changeSize(T entity, float size, Runnable callback) {
      changeSize(entity, size);
      callback.run();
      return entity;
   }

   public static <T extends Entity> T changeSize(T entity, float size) {
      Field sizeField = Entity.class.getDeclaredFields()[79];
      sizeField.setAccessible(true);

      try {
         sizeField.set(entity, entity.func_213305_a(Pose.STANDING).func_220313_a(size));
      } catch (IllegalAccessException var4) {
         var4.printStackTrace();
      }

      entity.func_213323_x_();
      return entity;
   }

   public static void giveItem(PlayerEntity player, ItemStack itemStack) {
      boolean added = player.field_71071_by.func_70441_a(itemStack);
      if (!added) {
         player.func_146097_a(itemStack, false, false);
      }
   }
}
