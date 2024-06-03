package iskallia.vault.block.entity;

import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.hologram.HologramElement;
import iskallia.vault.block.entity.hologram.ItemHologramElement;
import iskallia.vault.block.entity.hologram.ModelHologramElement;
import iskallia.vault.block.entity.hologram.RootHologramElement;
import iskallia.vault.block.entity.hologram.TextHologramElement;
import iskallia.vault.block.entity.hologram.model.MeshPart;
import iskallia.vault.init.ModBlocks;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HologramTileEntity extends BlockEntity {
   public static final Function<HologramTileEntity, HologramElement> DEFAULT_TREE = entity -> new RootHologramElement(entity)
      .<HologramElement>add(
         new HologramElement[]{
            new ModelHologramElement(
                  VaultMod.id("textures/entity/hologram/arrow.png"),
                  64,
                  64,
                  -1,
                  new MeshPart("main")
                     .uv(0.0F, 0.0F)
                     .cuboid(new Vector3f(-1.0F, -2.0F, -8.0F), new Vector3f(2.0F, 2.0F, 16.0F), Vector3f.ZERO)
                     .uv(8.0F, 9.0F)
                     .cuboid(new Vector3f(1.0F, -2.0F, 5.0F), new Vector3f(1.0F, 2.0F, 2.0F), Vector3f.ZERO)
                     .uv(0.0F, 9.0F)
                     .cuboid(new Vector3f(2.0F, -2.0F, 4.0F), new Vector3f(1.0F, 2.0F, 2.0F), Vector3f.ZERO)
                     .uv(4.0F, 7.0F)
                     .cuboid(new Vector3f(3.0F, -2.0F, 3.0F), new Vector3f(1.0F, 2.0F, 2.0F), Vector3f.ZERO)
                     .uv(6.0F, 3.0F)
                     .cuboid(new Vector3f(-2.0F, -2.0F, 5.0F), new Vector3f(1.0F, 2.0F, 2.0F), Vector3f.ZERO)
                     .uv(0.0F, 5.0F)
                     .cuboid(new Vector3f(-3.0F, -2.0F, 4.0F), new Vector3f(1.0F, 2.0F, 2.0F), Vector3f.ZERO)
                     .uv(1.0F, 1.0F)
                     .cuboid(new Vector3f(-4.0F, -2.0F, 3.0F), new Vector3f(1.0F, 2.0F, 2.0F), Vector3f.ZERO)
                     .setPivot(new Vector3f(0.0F, 1.0F, 0.0F))
                     .setRotation(new Vector3f(0.0F, 0.0F, 0.0F))
               )
               .setScale(0.75)
         }
      )
      .<HologramElement>add(new TextHologramElement(new TextComponent("Hello, World!"), true, true, 16777215).setScale(0.0625))
      .add(
         new ItemHologramElement(new ItemStack(Items.DIAMOND_SWORD), true, true)
            .<HologramElement>setTranslation(new Vec3(0.0, -1.0, 0.0))
            .add(new ItemHologramElement(new ItemStack(Items.DIAMOND_PICKAXE), true, true).setTranslation(new Vec3(0.0, 2.0, 0.0)))
      );
   private HologramElement tree;

   public HologramTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.HOLOGRAM_TILE_ENTITY, pos, state);
   }

   public HologramElement getTree() {
      this.ensureNonNullTree();
      return this.tree;
   }

   public void setTree(HologramElement tree) {
      (this.tree = tree).iterate(RootHologramElement.class, root -> root.setEntity(this));
      this.setChanged();
   }

   public void ensureNonNullTree() {
      if (this.tree == null) {
         this.setTree(DEFAULT_TREE.apply(this));
      }
   }

   public static void tick(Level level, BlockPos pos, BlockState state, HologramTileEntity entity) {
      if (level instanceof ServerLevel world) {
         entity.ensureNonNullTree();
      }
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      HologramElement.ADAPTER.writeNbt(this.tree).ifPresent(tag -> nbt.put("tree", tag));
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.setTree(HologramElement.ADAPTER.readNbt(nbt.get("tree")).orElse(null));
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }
}
