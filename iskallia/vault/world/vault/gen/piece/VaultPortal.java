package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.server.ServerWorld;

public class VaultPortal extends VaultPiece {
   public static final ResourceLocation ID = Vault.id("portal");

   public VaultPortal() {
      super(ID);
   }

   public VaultPortal(ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   @Override
   public void tick(ServerWorld world, VaultRaid vault) {
   }
}
