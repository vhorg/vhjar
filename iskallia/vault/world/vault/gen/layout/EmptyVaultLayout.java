package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.util.ResourceLocation;

public class EmptyVaultLayout extends VaultRoomLayoutGenerator {
   public static final ResourceLocation ID = Vault.id("empty");

   public EmptyVaultLayout() {
      super(ID);
   }

   @Override
   public void setSize(int size) {
   }

   @Override
   public VaultRoomLayoutGenerator.Layout generateLayout() {
      return new VaultRoomLayoutGenerator.Layout();
   }
}
