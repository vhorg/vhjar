package iskallia.vault.world.vault.gen;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class VaultRoomNames {
   @Nullable
   public static Component getName(String filterKey) {
      switch (filterKey) {
         case "crystal_caves":
            return new TextComponent("Crystal Cave").withStyle(ChatFormatting.DARK_PURPLE);
         case "contest_alien":
            return new TextComponent("Contest: Alien").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_birdcage":
            return new TextComponent("Contest: Ancient Temple").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_city":
            return new TextComponent("Contest: City Streets").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_dragon":
            return new TextComponent("Contest: Dragon").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_fishtank":
            return new TextComponent("Contest: Aquarium").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_mine":
            return new TextComponent("Contest: Mine").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_mustard":
            return new TextComponent("Contest: Yellow Brick Road").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_oriental":
            return new TextComponent("Contest: Oriental").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_pixel":
            return new TextComponent("Contest: Pixelart").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_prismarine":
            return new TextComponent("Contest: Atlantis").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_tree":
            return new TextComponent("Contest: Tree").withStyle(ChatFormatting.DARK_AQUA);
         case "contest_web":
            return new TextComponent("Contest: Spiderweb").withStyle(ChatFormatting.DARK_AQUA);
         case "digsite":
            return new TextComponent("Digsite").withStyle(ChatFormatting.YELLOW);
         case "dungeons":
            return new TextComponent("Dungeon").withStyle(ChatFormatting.WHITE);
         case "forest":
            return new TextComponent("Forest").withStyle(ChatFormatting.DARK_GREEN);
         case "graves":
            return new TextComponent("Grave").withStyle(ChatFormatting.DARK_GRAY);
         case "lakes":
            return new TextComponent("Lake").withStyle(ChatFormatting.BLUE);
         case "lava":
            return new TextComponent("Lava").withStyle(ChatFormatting.RED);
         case "mineshaft":
            return new TextComponent("Mine").withStyle(ChatFormatting.GOLD);
         case "mushroom_forest":
            return new TextComponent("Mushroom Forest").withStyle(ChatFormatting.LIGHT_PURPLE);
         case "nether_flowers":
            return new TextComponent("Nether Flowers").withStyle(ChatFormatting.RED);
         case "pirate_cove":
            return new TextComponent("Pirate Cove").withStyle(ChatFormatting.DARK_AQUA);
         case "puzzle_cube":
            return new TextComponent("Puzzle").withStyle(ChatFormatting.YELLOW);
         case "rainbow_forest":
            return new TextComponent("Rainbow Forest").withStyle(ChatFormatting.GREEN);
         case "vendor":
            return new TextComponent("Vendor").withStyle(ChatFormatting.GOLD);
         case "viewer":
            return new TextComponent("Viewer").withStyle(ChatFormatting.GOLD);
         case "village":
            return new TextComponent("Village").withStyle(ChatFormatting.AQUA);
         case "wildwest":
            return new TextComponent("Wild West").withStyle(ChatFormatting.YELLOW);
         case "x_spot":
            return new TextComponent("X-Mark").withStyle(ChatFormatting.YELLOW);
         default:
            return null;
      }
   }
}
