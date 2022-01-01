package iskallia.vault.easteregg;

import iskallia.vault.Vault;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModModels;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.skill.set.PlayerSet;
import iskallia.vault.util.AdvancementHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class GrasshopperNinja {
   public static void achieve(ServerPlayerEntity playerEntity) {
      AdvancementHelper.grantCriterion(playerEntity, Vault.id("main/grasshopper_ninja"), "hopped");
   }

   public static boolean isGrasshopperShape(PlayerEntity playerEntity) {
      return PlayerSet.allMatch(playerEntity, (slotType, stack) -> {
         if (!(stack.func_77973_b() instanceof VaultArmorItem)) {
            return false;
         } else {
            Integer gearSpecialModel = ModAttributes.GEAR_SPECIAL_MODEL.getOrDefault(stack, -1).getValue(stack);
            int gearColor = ((VaultArmorItem)stack.func_77973_b()).func_200886_f(stack);
            return gearSpecialModel == ModModels.SpecialGearModel.FAIRY_SET.modelForSlot(slotType).getId() && isGrasshopperGreen(gearColor);
         }
      });
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
