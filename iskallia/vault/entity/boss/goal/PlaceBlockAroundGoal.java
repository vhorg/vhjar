package iskallia.vault.entity.boss.goal;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.trait.ITrait;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

public class PlaceBlockAroundGoal extends Goal implements ITrait {
   public static final String TYPE = "place_block_around";
   private final VaultBossEntity boss;
   private int placeTime = -1;
   private int radius;
   private int maxYOffset;
   private int placeIntervalMin;
   private int placeIntervalMax;
   private PartialBlockState blockState;
   private int stackSize = 1;

   public PlaceBlockAroundGoal(VaultBossEntity boss) {
      this.boss = boss;
   }

   public PlaceBlockAroundGoal setAttributes(int radius, int maxYOffset, int placeIntervalMin, int placeIntervalMax, PartialBlockState blockState) {
      this.radius = radius;
      this.maxYOffset = maxYOffset;
      this.placeIntervalMin = placeIntervalMin;
      this.placeIntervalMax = placeIntervalMax;
      this.blockState = blockState;
      return this;
   }

   public boolean canUse() {
      return true;
   }

   public void tick() {
      if (--this.placeTime == 0) {
         this.placeBlock();
         this.placeTime = this.placeIntervalMin + this.boss.getRandom().nextInt(this.placeIntervalMax - this.placeIntervalMin + 1);
      } else if (this.placeTime < 0) {
         this.placeTime = this.placeIntervalMin + this.boss.getRandom().nextInt(this.placeIntervalMax - this.placeIntervalMin + 1);
      }
   }

   private void placeBlock() {
      for (int tries = 0; tries < 10; tries++) {
         int x = Mth.floor(this.boss.getX() + this.boss.getRandom().nextInt(this.radius + 1) * 2 - this.radius);
         int y = Mth.floor(this.boss.getY() + this.boss.getRandom().nextInt(this.maxYOffset + 1) * 2 - this.maxYOffset);
         int z = Mth.floor(this.boss.getZ() + this.boss.getRandom().nextInt(this.radius + 1) * 2 - this.radius);
         BlockPos pos = new BlockPos(x, y, z);
         if (this.boss.level.getBlockState(pos).isAir() && !this.boss.getBoundingBox().contains(x, y, z)) {
            this.blockState.place(this.boss.level, pos, 3);
            return;
         }
      }
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("radius", this.radius);
      tag.putInt("maxYOffset", this.maxYOffset);
      tag.putInt("placeIntervalMin", this.placeIntervalMin);
      tag.putInt("placeIntervalMax", this.placeIntervalMax);
      Adapters.PARTIAL_BLOCK_STATE.writeNbt(this.blockState, tag).ifPresent(nbt -> tag.put("blockState", nbt));
      tag.putInt("stackSize", this.stackSize);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      this.radius = nbt.getInt("radius");
      this.maxYOffset = nbt.getInt("maxYOffset");
      this.placeIntervalMin = nbt.getInt("placeIntervalMin");
      this.placeIntervalMax = nbt.getInt("placeIntervalMax");
      this.blockState = Adapters.PARTIAL_BLOCK_STATE
         .readNbt(nbt.getCompound("blockState"))
         .orElseGet(() -> PartialBlockState.of(Blocks.AIR.defaultBlockState()));
      this.stackSize = nbt.getInt("stackSize");
   }

   @Override
   public String getType() {
      return "place_block_around";
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addTraitGoal(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof PlaceBlockAroundGoal other) {
         this.radius = this.radius + other.radius;
         this.maxYOffset = this.maxYOffset + other.maxYOffset;
         this.placeIntervalMin = this.placeIntervalMin + other.placeIntervalMin;
         this.placeIntervalMax = this.placeIntervalMax + other.placeIntervalMax;
      }
   }
}
