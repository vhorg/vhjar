package iskallia.vault.world.vault.gen;

import iskallia.vault.Vault;
import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

public class VaultRoomLevelRestrictions {
   public static void addGenerationPreventions(VaultRoomLayoutGenerator.Layout layout, int vaultLevel) {
      if (vaultLevel < 250) {
         layout.getRooms().forEach(room -> room.andFilter(key -> !key.toString().startsWith(getVaultRoomPrefix("vendor"))));
      }

      if (vaultLevel < 100) {
         layout.getRooms().forEach(room -> room.andFilter(key -> !key.toString().startsWith(getVaultRoomPrefix("contest_pixel"))));
      }
   }

   public static boolean canGenerate(JigsawPiece vaultPiece, int vaultLevel) {
      return vaultLevel < 250 && isJigsawPieceOfName(vaultPiece, getVaultRoomPrefix("vendor"))
         ? false
         : vaultLevel >= 100 || !isJigsawPieceOfName(vaultPiece, getVaultRoomPrefix("contest_pixel"));
   }

   private static String getVaultRoomPrefix(String roomName) {
      return Vault.sId("vault/enigma/rooms/" + roomName);
   }

   private static boolean isJigsawPieceOfName(JigsawPiece piece, String name) {
      if (piece instanceof PalettedListPoolElement) {
         List<JigsawPiece> elements = ((PalettedListPoolElement)piece).getElements();

         for (JigsawPiece elementPiece : elements) {
            if (!isJigsawPieceOfName(elementPiece, name)) {
               return false;
            }
         }

         return !elements.isEmpty();
      } else {
         if (piece instanceof PalettedSinglePoolElement) {
            ResourceLocation key = (ResourceLocation)((PalettedSinglePoolElement)piece).getTemplate().left().orElse(null);
            if (key != null) {
               return key.toString().startsWith(name);
            }
         }

         return false;
      }
   }
}
