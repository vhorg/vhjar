package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;

public class VaultRoom extends VaultPiece {
   public static final ResourceLocation ID = Vault.id("room");

   protected VaultRoom(ResourceLocation id) {
      super(id);
   }

   public VaultRoom() {
      this(ID);
   }

   protected VaultRoom(ResourceLocation id, ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      super(id, template, boundingBox, rotation);
   }

   public VaultRoom(ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      this(ID, template, boundingBox, rotation);
   }

   @Override
   public void tick(ServerWorld world, VaultRaid vault) {
   }

   public Vector3i getCenter() {
      return this.getBoundingBox().func_215126_f();
   }

   public BlockPos getTunnelConnectorPos(Direction dir) {
      Vector3i center = this.getCenter();
      BlockPos size = new BlockPos(this.getBoundingBox().func_175896_b()).func_177982_a(2, 2, 2);
      return new BlockPos(center).func_177982_a(dir.func_82601_c() * size.func_177958_n(), 0, dir.func_82599_e() * size.func_177952_p());
   }
}
