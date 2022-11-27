package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.common.ForgeHooks;

@Deprecated(
   forRemoval = true
)
public class TwerkerTalent extends PlayerTalent {
   @Expose
   private final int tickDelay = 5;
   @Expose
   private final int xRange = 2;
   @Expose
   private final int yRange = 1;
   @Expose
   private final int zRange = 2;
   @Expose
   private boolean growsPumpkinsMelons = false;
   @Expose
   private boolean growsSugarcaneCactus = false;
   @Expose
   private boolean growsAnimals = false;

   public TwerkerTalent(int cost) {
      super(cost);
   }

   public int getTickDelay() {
      return 5;
   }

   public int getXRange() {
      return 2;
   }

   public int getYRange() {
      return 1;
   }

   public int getZRange() {
      return 2;
   }

   @Override
   public void tick(ServerPlayer player) {
      if (player.isCrouching() && player.getCommandSenderWorld() instanceof ServerLevel world) {
         BlockPos playerPos = player.blockPosition();
         BlockPos pos = new BlockPos(
            playerPos.getX() + player.getRandom().nextInt(this.getXRange() * 2 + 1) - this.getXRange(),
            playerPos.getY() - player.getRandom().nextInt(this.getYRange() * 2 + 1) + this.getYRange(),
            playerPos.getZ() + player.getRandom().nextInt(this.getZRange() * 2 + 1) - this.getZRange()
         );
         BlockState state = world.getBlockState(pos);
         Block block = world.getBlockState(pos).getBlock();
         if (block instanceof CropBlock || block instanceof SaplingBlock) {
            BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, pos, player);
            world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX(), pos.getY(), pos.getZ(), 100, 1.0, 0.5, 1.0, 0.0);
         }

         if (this.growsPumpkinsMelons && block instanceof StemBlock) {
            if (((StemBlock)block).isValidBonemealTarget(world, pos, state, false)) {
               BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, pos, player);
            } else {
               for (int i = 0; i < 40; i++) {
                  state.randomTick(world, pos, world.random);
               }
            }

            world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX(), pos.getY(), pos.getZ(), 100, 1.0, 0.5, 1.0, 0.0);
         }

         if (this.growsSugarcaneCactus) {
            BlockPos above = new BlockPos(pos).above();
            if (!world.isEmptyBlock(above)) {
               return;
            }

            if (block instanceof SugarCaneBlock || block instanceof CactusBlock) {
               int height = 1;

               while (world.getBlockState(pos.below(height)).is(block)) {
                  height++;
               }

               if (height < 3 && rand.nextInt(3) == 0 && ForgeHooks.onCropsGrowPre(world, pos, state, true)) {
                  world.setBlockAndUpdate(above, block.defaultBlockState());
                  BlockState newState = (BlockState)state.setValue(BlockStateProperties.AGE_15, 0);
                  world.setBlock(pos, newState, 4);
                  newState.neighborChanged(world, above, block, pos, false);
                  world.scheduleTick(above, block, 1, TickPriority.EXTREMELY_HIGH);
                  ForgeHooks.onCropsGrowPost(world, above, state);
                  world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX(), pos.getY(), pos.getZ(), 100, 1.0, 0.5, 1.0, 0.0);
               }
            }
         }

         if (this.growsAnimals) {
            AABB searchBox = player.getBoundingBox().inflate(this.getXRange(), this.getYRange(), this.getZRange());

            for (AgeableMob entity : world.getEntitiesOfClass(
               AgeableMob.class, searchBox, entityx -> entityx.isAlive() && !entityx.isSpectator() && entityx.isBaby()
            )) {
               if (rand.nextFloat() < 0.4F) {
                  world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX(), pos.getY(), pos.getZ(), 100, 1.0, 0.5, 1.0, 0.0);
               }

               if (rand.nextFloat() < 0.05F) {
                  entity.setBaby(false);
               }
            }
         }
      }
   }
}
