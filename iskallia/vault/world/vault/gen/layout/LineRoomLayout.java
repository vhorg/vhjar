package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

public class LineRoomLayout extends VaultRoomLayoutGenerator {
   public static final ResourceLocation ID = Vault.id("line");

   public LineRoomLayout() {
      super(ID);
   }

   @Override
   public void setSize(int size) {
   }

   @Override
   public VaultRoomLayoutGenerator.Layout generateLayout() {
      VaultRoomLayoutGenerator.Layout layout = new VaultRoomLayoutGenerator.Layout();
      layout.putRoom(new Vector3i(0, 0, 0));
      layout.putRoom(new Vector3i(1, 0, 0));
      layout.putRoom(new Vector3i(2, 0, 0));
      layout.putRoom(new Vector3i(3, 0, 0));
      layout.addTunnel(new VaultRoomLayoutGenerator.Tunnel(layout.getRoom(new Vector3i(0, 0, 0)), layout.getRoom(new Vector3i(1, 0, 0))));
      return layout;
   }
}
