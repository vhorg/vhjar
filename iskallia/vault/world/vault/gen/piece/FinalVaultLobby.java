package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.server.ServerWorld;

public class FinalVaultLobby extends VaultPiece {
   public static final ResourceLocation ID = Vault.id("final_lobby");

   protected FinalVaultLobby(ResourceLocation id) {
      super(id);
   }

   public FinalVaultLobby() {
      this(ID);
   }

   protected FinalVaultLobby(ResourceLocation id, ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      super(id, template, boundingBox, rotation);
   }

   public FinalVaultLobby(ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      this(ID, template, boundingBox, rotation);
   }

   @Override
   public void tick(ServerWorld world, VaultRaid vault) {
   }
}
