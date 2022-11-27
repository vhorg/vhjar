package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.VaultMod;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.gen.structure.VaultJigsawHelper;
import java.util.Random;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class DebugVaultLayout extends VaultRoomLayoutGenerator {
   public static final ResourceLocation ID = VaultMod.id("debug");

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

      for (WeightedList.Entry<StructurePoolElement> weightedEntry : VaultJigsawHelper.getVaultRoomList(Integer.MAX_VALUE)) {
         final StructurePoolElement piece = weightedEntry.value;
         VaultRoomLayoutGenerator.Room room = new VaultRoomLayoutGenerator.Room(new Vec3i(xx, 0, 0)) {
            @Override
            public StructurePoolElement getRandomPiece(StructureTemplatePool pattern, Random random) {
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
