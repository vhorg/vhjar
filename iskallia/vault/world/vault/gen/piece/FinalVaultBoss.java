package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.server.ServerWorld;

public class FinalVaultBoss extends VaultPiece {
   public static final ResourceLocation ID = Vault.id("final_vault_boss");

   protected FinalVaultBoss(ResourceLocation id) {
      super(id);
   }

   public FinalVaultBoss() {
      this(ID);
   }

   protected FinalVaultBoss(ResourceLocation id, ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      super(id, template, boundingBox, rotation);
   }

   public FinalVaultBoss(ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      this(ID, template, boundingBox, rotation);
   }

   @Override
   public void tick(ServerWorld world, VaultRaid vault) {
   }
}
