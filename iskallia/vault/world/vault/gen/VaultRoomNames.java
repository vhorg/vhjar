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
         case "contest_alien":
            return new StringTextComponent("Contest: Alien").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_birdcage":
            return new StringTextComponent("Contest: Ancient Temple").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_city":
            return new StringTextComponent("Contest: City Streets").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_dragon":
            return new StringTextComponent("Contest: Dragon").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_fishtank":
            return new StringTextComponent("Contest: Aquarium").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_mine":
            return new StringTextComponent("Contest: Mine").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_mustard":
            return new StringTextComponent("Contest: Yellow Brick Road").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_oriental":
            return new StringTextComponent("Contest: Oriental").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_pixel":
            return new StringTextComponent("Contest: Pixelart").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_prismarine":
            return new StringTextComponent("Contest: Atlantis").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_tree":
            return new StringTextComponent("Contest: Tree").func_240699_a_(TextFormatting.DARK_AQUA);
         case "contest_web":
            return new StringTextComponent("Contest: Spiderweb").func_240699_a_(TextFormatting.DARK_AQUA);
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
