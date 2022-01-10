package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.vault.VaultRaid;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;

public class VaultRoom extends VaultPiece {
   public static final ResourceLocation ID = Vault.id("room");
   private boolean cakeEaten = false;
   private BlockPos cakePos = null;

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

   public void setCakeEaten(boolean cakeEaten) {
      this.cakeEaten = cakeEaten;
   }

   public boolean isCakeEaten() {
      return this.cakeEaten;
   }

   public void setCakePos(BlockPos cakePos) {
      this.cakePos = cakePos;
   }

   @Nullable
   public BlockPos getCakePos() {
      return this.cakePos;
   }

   public Vector3i getCenter() {
      return this.getBoundingBox().func_215126_f();
   }

   public BlockPos getTunnelConnectorPos(Direction dir) {
      Vector3i center = this.getCenter();
      BlockPos size = new BlockPos(this.getBoundingBox().func_175896_b()).func_177982_a(2, 2, 2);
      return new BlockPos(center).func_177982_a(dir.func_82601_c() * size.func_177958_n(), 0, dir.func_82599_e() * size.func_177952_p());
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74757_a("cakeEaten", this.cakeEaten);
      if (this.cakePos != null) {
         tag.func_218657_a("cakePos", NBTHelper.serializeBlockPos(this.cakePos));
      }

      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.cakeEaten = tag.func_74767_n("cakeEaten");
      if (tag.func_150297_b("cakePos", 10)) {
         this.cakePos = NBTHelper.deserializeBlockPos(tag.func_74775_l("cakePos"));
      }
   }
}
