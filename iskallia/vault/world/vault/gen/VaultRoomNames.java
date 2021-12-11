package iskallia.vault.world.vault.gen;

import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class VaultRoomNames {
   @Nullable
   public static ITextComponent getName(String filterKey) {
      switch (filterKey) {
         case "crystal_caves":
            return new StringTextComponent("Crystal Cave").func_240699_a_(TextFormatting.DARK_PURPLE);
         case "contest":
            return new StringTextComponent("Contest").func_240699_a_(TextFormatting.DARK_AQUA);
         case "digsite":
            return new StringTextComponent("Digsite").func_240699_a_(TextFormatting.YELLOW);
         case "dungeons":
            return new StringTextComponent("Dungeon").func_240699_a_(TextFormatting.WHITE);
         case "forest":
            return new StringTextComponent("Forest").func_240699_a_(TextFormatting.DARK_GREEN);
         case "graves":
            return new StringTextComponent("Grave").func_240699_a_(TextFormatting.DARK_GRAY);
         case "lakes":
            return new StringTextComponent("Lake").func_240699_a_(TextFormatting.BLUE);
         case "lava":
            return new StringTextComponent("Lava").func_240699_a_(TextFormatting.RED);
         case "mineshaft":
            return new StringTextComponent("Mine").func_240699_a_(TextFormatting.GOLD);
         case "mushroom_forest":
            return new StringTextComponent("Mushroom Forest").func_240699_a_(TextFormatting.LIGHT_PURPLE);
         case "nether_flowers":
            return new StringTextComponent("Nether Flowers").func_240699_a_(TextFormatting.RED);
         case "pirate_cove":
            return new StringTextComponent("Pirate Cove").func_240699_a_(TextFormatting.DARK_AQUA);
         case "puzzle_cube":
            return new StringTextComponent("Puzzle").func_240699_a_(TextFormatting.YELLOW);
         case "rainbow_forest":
            return new StringTextComponent("Rainbow Forest").func_240699_a_(TextFormatting.GREEN);
         case "vendor":
            return new StringTextComponent("Vendor").func_240699_a_(TextFormatting.GOLD);
         case "viewer":
            return new StringTextComponent("Viewer").func_240699_a_(TextFormatting.GOLD);
         case "village":
            return new StringTextComponent("Village").func_240699_a_(TextFormatting.AQUA);
         case "wildwest":
            return new StringTextComponent("Wild West").func_240699_a_(TextFormatting.YELLOW);
         case "x_spot":
            return new StringTextComponent("X-Mark").func_240699_a_(TextFormatting.YELLOW);
         default:
            return null;
      }
   }
}
