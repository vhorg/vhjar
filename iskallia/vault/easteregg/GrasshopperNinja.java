package iskallia.vault.easteregg;

import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.util.AdvancementHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GrasshopperNinja {
   public static void achieve(ServerPlayer playerEntity) {
      AdvancementHelper.grantCriterion(playerEntity, VaultMod.id("main/grasshopper_ninja"), "hopped");
   }

   public static boolean isGrasshopperShape(Player playerEntity) {
      return ModDynamicModels.Armor.FAIRY.getPieces().entrySet().stream().allMatch(entry -> {
         EquipmentSlot equipmentSlot = entry.getKey();
         ArmorPieceModel pieceModel = entry.getValue();
         ItemStack equipmentStack = (ItemStack)playerEntity.getInventory().armor.get(equipmentSlotToInventoryIndex(equipmentSlot));
         if (equipmentStack.getItem() instanceof VaultArmorItem vaultArmorItem) {
            VaultGearData gearData = VaultGearData.read(equipmentStack);
            ResourceLocation modelId = gearData.getFirstValue(ModGearAttributes.GEAR_MODEL).orElse(null);
            if (modelId == null) {
               return false;
            } else if (!pieceModel.getId().equals(modelId)) {
               return false;
            } else {
               Integer gearColor = gearData.getFirstValue(ModGearAttributes.GEAR_COLOR).orElse(vaultArmorItem.getColor(equipmentStack));
               return isGrasshopperGreen(gearColor);
            }
         } else {
            return false;
         }
      });
   }

   private static int equipmentSlotToInventoryIndex(EquipmentSlot equipmentSlot) {
      return switch (equipmentSlot) {
         case HEAD -> 3;
         case CHEST -> 2;
         case LEGS -> 1;
         case FEET -> 0;
         default -> -1;
      };
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
