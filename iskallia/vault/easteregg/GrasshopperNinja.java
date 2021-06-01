package iskallia.vault.easteregg;

import iskallia.vault.Vault;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.skill.set.PlayerSet;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class GrasshopperNinja {
   public static void achieve(ServerPlayerEntity playerEntity) {
      Advancement advancement = playerEntity.func_184102_h().func_191949_aK().func_192778_a(Vault.id("grasshopper_ninja"));
      playerEntity.func_192039_O().func_192750_a(advancement, "hopped");
   }

   public static boolean isGrasshopperShape(PlayerEntity playerEntity) {
      return PlayerSet.allMatch(
         playerEntity,
         (slotType, itemStack) -> ModAttributes.GEAR_MODEL.getOrDefault(itemStack, -1).getValue(itemStack) == 0
            && isGrasshopperGreen(((VaultArmorItem)itemStack.func_77973_b()).func_200886_f(itemStack)),
         EquipmentSlotType.HEAD,
         EquipmentSlotType.CHEST,
         EquipmentSlotType.LEGS,
         EquipmentSlotType.FEET
      );
   }

   public static boolean isGrasshopperGreen(int color) {
      float grasshopperGreenR = 0.58431375F;
      float grasshopperGreenG = 0.7607843F;
      float grasshopperGreenB = 0.40784314F;
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      float dr = red - grasshopperGreenR;
      float dg = green - grasshopperGreenG;
      float db = blue - grasshopperGreenB;
      float distance = (float)(Math.sqrt(dr * dr + dg * dg + db * db) / 1.73205080757);
      return distance < 0.35;
   }
}
