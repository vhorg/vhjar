package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FinalVaultLobby extends VaultPiece {
   public static final ResourceLocation ID = VaultMod.id("final_lobby");

   protected FinalVaultLobby(ResourceLocation id) {
      super(id);
   }

   public FinalVaultLobby() {
      this(ID);
   }

   protected FinalVaultLobby(ResourceLocation id, ResourceLocation template, BoundingBox boundingBox, Rotation rotation) {
      super(id, template, boundingBox, rotation);
   }

   public FinalVaultLobby(ResourceLocation template, BoundingBox boundingBox, Rotation rotation) {
      this(ID, template, boundingBox, rotation);
   }

   @Override
   public void tick(ServerLevel world, VaultRaid vault) {
   }
}
