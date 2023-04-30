package iskallia.vault.core.world.generator;

import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;

public class JigsawData implements INBTSerializable<CompoundTag> {
   private final PartialTile tile;
   private final FrontAndTop orientation;
   private final Direction facing;
   private final Direction side;
   private ResourceLocation name;
   private ResourceLocation target;
   private ResourceLocation pool;
   private BlockState finalState;
   private JointType joint;

   public JigsawData(PartialTile tile) {
      this.tile = tile;
      this.orientation = tile.getState().get(JigsawBlock.ORIENTATION);
      this.facing = this.orientation.front();
      this.side = this.orientation.top();
      this.deserializeNBT(tile.getEntity().asWhole().orElseGet(CompoundTag::new));
   }

   public CompoundTag serializeNBT() {
      return new CompoundTag();
   }

   public void deserializeNBT(CompoundTag nbt) {
      if (nbt.contains("name")) {
         this.name = new ResourceLocation(nbt.getString("name"));
      }

      if (nbt.contains("target")) {
         this.target = new ResourceLocation(nbt.getString("target"));
      }

      if (nbt.contains("pool")) {
         this.pool = new ResourceLocation(nbt.getString("pool"));
      }

      if (nbt.contains("final_state")) {
         this.finalState = PartialTile.parse(nbt.getString("final_state"), true)
            .map(PartialTile::getState)
            .flatMap(PartialBlockState::asWhole)
            .orElse(ModBlocks.ERROR_BLOCK.defaultBlockState());
      }

      if (nbt.contains("joint")) {
         this.joint = JointType.byName(nbt.getString("joint"))
            .orElseGet(
               () -> ((FrontAndTop)this.tile.getState().get(JigsawBlock.ORIENTATION)).front().getAxis().isHorizontal() ? JointType.ALIGNED : JointType.ROLLABLE
            );
      }
   }

   public FrontAndTop getOrientation() {
      return this.orientation;
   }

   public Direction getFacing() {
      return this.facing;
   }

   public Direction getSide() {
      return this.side;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public ResourceLocation getTarget() {
      return this.target;
   }

   public ResourceLocation getPool() {
      return this.pool;
   }

   public BlockState getFinalState() {
      return this.finalState;
   }

   public JointType getJoint() {
      return this.joint;
   }

   public BlockPos getPos() {
      return this.tile.getPos();
   }
}
