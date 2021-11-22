package iskallia.vault.util.calc;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGearHelper;
import iskallia.vault.skill.set.DreamSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.set.TreasureSet;
import iskallia.vault.world.data.PlayerSetsData;
import net.minecraft.entity.player.ServerPlayerEntity;

public class ChestRarityHelper {
   public static float getIncreasedChestRarity(ServerPlayerEntity sPlayer) {
      float increasedRarity = 0.0F;
      increasedRarity += VaultGearHelper.getAttributeValueOnGearSumFloat(sPlayer, ModAttributes.CHEST_RARITY);
      SetTree sets = PlayerSetsData.get(sPlayer.func_71121_q()).getSets(sPlayer);

      for (SetNode<?> node : sets.getNodes()) {
         if (node.getSet() instanceof TreasureSet) {
            TreasureSet set = (TreasureSet)node.getSet();
            increasedRarity += set.getIncreasedChestRarity();
         }

         if (node.getSet() instanceof DreamSet) {
            DreamSet set = (DreamSet)node.getSet();
            increasedRarity += set.getIncreasedChestRarity();
         }
      }

      return increasedRarity;
   }
}
