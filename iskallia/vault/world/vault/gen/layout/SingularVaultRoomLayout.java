package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.VaultMod;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;

public class SingularVaultRoomLayout extends VaultRoomLayoutGenerator {
   public static final ResourceLocation ID = VaultMod.id("singular");

   public SingularVaultRoomLayout() {
      super(ID);
   }

   @Override
   public void setSize(int size) {
   }

   @Override
   public VaultRoomLayoutGenerator.Layout generateLayout() {
      VaultRoomLayoutGenerator.Layout layout = new VaultRoomLayoutGenerator.Layout();
      layout.putRoom(new Vec3i(0, 0, 0));
      return layout;
   }
}
