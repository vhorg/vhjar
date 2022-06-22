package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
import iskallia.vault.block.GodEyeBlock;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.server.ServerWorld;

public class VaultGodEye extends VaultPiece {
   public static final ResourceLocation ID = Vault.id("god_eye");

   public VaultGodEye() {
      super(ID);
   }

   public VaultGodEye(ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   public boolean isLit(ServerWorld world) {
      return world.func_180495_p(this.getMin()).func_177229_b(GodEyeBlock.LIT) == Boolean.TRUE;
   }

   @Override
   public void tick(ServerWorld world, VaultRaid vault) {
   }
}
