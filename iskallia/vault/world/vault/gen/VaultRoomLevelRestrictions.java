package iskallia.vault.world.vault.gen;

import iskallia.vault.VaultMod;
import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public class VaultRoomLevelRestrictions {
   public static void addGenerationPreventions(VaultRoomLayoutGenerator.Layout layout, int vaultLevel) {
      if (vaultLevel < 250) {
         layout.getRooms().forEach(room -> room.andFilter(key -> !key.toString().startsWith(getVaultRoomPrefix("vendor"))));
      }

      if (vaultLevel < 100) {
         layout.getRooms().forEach(room -> room.andFilter(key -> !key.toString().startsWith(getVaultRoomPrefix("contest_pixel"))));
      }
   }

   public static boolean canGenerate(StructurePoolElement vaultPiece, int vaultLevel) {
      return vaultLevel < 250 && isJigsawPieceOfName(vaultPiece, getVaultRoomPrefix("vendor"))
         ? false
         : vaultLevel >= 100 || !isJigsawPieceOfName(vaultPiece, getVaultRoomPrefix("contest_pixel"));
   }

   private static String getVaultRoomPrefix(String roomName) {
      return VaultMod.sId("vault/enigma/rooms/" + roomName);
   }

   private static boolean isJigsawPieceOfName(StructurePoolElement piece, String name) {
      if (piece instanceof PalettedListPoolElement) {
         List<StructurePoolElement> elements = ((PalettedListPoolElement)piece).getElements();

         for (StructurePoolElement elementPiece : elements) {
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
