package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.gen.structure.VaultJigsawHelper;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

public class DebugVaultLayout extends VaultRoomLayoutGenerator {
   public static final ResourceLocation ID = Vault.id("debug");

   public DebugVaultLayout() {
      super(ID);
   }

   @Override
   public void setSize(int size) {
   }

   @Override
   public VaultRoomLayoutGenerator.Layout generateLayout() {
      VaultRoomLayoutGenerator.Layout layout = new VaultRoomLayoutGenerator.Layout();
      int xx = 0;
      VaultRoomLayoutGenerator.Room previousRoom = null;

      for (WeightedList.Entry<JigsawPiece> weightedEntry : VaultJigsawHelper.getVaultRoomList(Integer.MAX_VALUE)) {
         final JigsawPiece piece = weightedEntry.value;
         VaultRoomLayoutGenerator.Room room = new VaultRoomLayoutGenerator.Room(new Vector3i(xx, 0, 0)) {
            @Override
            public JigsawPiece getRandomPiece(JigsawPattern pattern, Random random) {
               return piece;
            }
         };
         xx++;
         layout.putRoom(room);
         if (previousRoom != null) {
            layout.addTunnel(new VaultRoomLayoutGenerator.Tunnel(previousRoom, room));
         }

         previousRoom = room;
      }

      return layout;
   }
}
