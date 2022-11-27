package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class VaultPortal extends VaultPiece {
   public static final ResourceLocation ID = VaultMod.id("portal");

   public VaultPortal() {
      super(ID);
   }

   public VaultPortal(ResourceLocation template, BoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   @Override
   public void tick(ServerLevel world, VaultRaid vault) {
   }
}
