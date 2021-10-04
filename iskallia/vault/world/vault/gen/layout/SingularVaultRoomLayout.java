package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

public class SingularVaultRoomLayout extends VaultRoomLayoutGenerator {
   public static final ResourceLocation ID = Vault.id("singular");

   public SingularVaultRoomLayout() {
      super(ID);
   }

   @Override
   public void setSize(int size) {
   }

   @Override
   public VaultRoomLayoutGenerator.Layout generateLayout() {
      VaultRoomLayoutGenerator.Layout layout = new VaultRoomLayoutGenerator.Layout();
      layout.putRoom(new Vector3i(0, 0, 0));
      return layout;
   }
}
