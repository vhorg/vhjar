package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.VaultMod;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;

public class LineRoomLayout extends VaultRoomLayoutGenerator {
   public static final ResourceLocation ID = VaultMod.id("line");

   public LineRoomLayout() {
      super(ID);
   }

   @Override
   public void setSize(int size) {
   }

   @Override
   public VaultRoomLayoutGenerator.Layout generateLayout() {
      VaultRoomLayoutGenerator.Layout layout = new VaultRoomLayoutGenerator.Layout();
      layout.putRoom(new Vec3i(0, 0, 0));
      layout.putRoom(new Vec3i(1, 0, 0));
      layout.putRoom(new Vec3i(2, 0, 0));
      layout.putRoom(new Vec3i(3, 0, 0));
      layout.addTunnel(new VaultRoomLayoutGenerator.Tunnel(layout.getRoom(new Vec3i(0, 0, 0)), layout.getRoom(new Vec3i(1, 0, 0))));
      return layout;
   }
}
