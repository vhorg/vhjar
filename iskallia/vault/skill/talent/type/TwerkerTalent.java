package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

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
   public void tick(PlayerEntity player) {
      if (player.func_213453_ef() && player.func_130014_f_() instanceof ServerWorld) {
         ServerWorld world = (ServerWorld)player.func_130014_f_();
         BlockPos playerPos = player.func_233580_cy_();
         BlockPos pos = new BlockPos(
            playerPos.func_177958_n() + player.func_70681_au().nextInt(this.getXRange() * 2 + 1) - this.getXRange(),
            playerPos.func_177956_o() - player.func_70681_au().nextInt(this.getYRange() * 2 + 1) + this.getYRange(),
            playerPos.func_177952_p() + player.func_70681_au().nextInt(this.getZRange() * 2 + 1) - this.getZRange()
         );
         BlockState state = world.func_180495_p(pos);
         Block block = world.func_180495_p(pos).func_177230_c();
         if (block instanceof CropsBlock || block instanceof SaplingBlock) {
            BoneMealItem.applyBonemeal(new ItemStack(Items.field_196106_bc), world, pos, player);
            world.func_195598_a(ParticleTypes.field_197632_y, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), 100, 1.0, 0.5, 1.0, 0.0);
         }

         if (this.growsPumpkinsMelons && block instanceof StemBlock) {
            if (((StemBlock)block).func_176473_a(world, pos, state, false)) {
               BoneMealItem.applyBonemeal(new ItemStack(Items.field_196106_bc), world, pos, player);
            } else {
               for (int i = 0; i < 40; i++) {
                  state.func_227034_b_(world, pos, world.field_73012_v);
               }
            }

            world.func_195598_a(ParticleTypes.field_197632_y, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), 100, 1.0, 0.5, 1.0, 0.0);
         }

         if (this.growsSugarcaneCactus) {
            BlockPos above = new BlockPos(pos).func_177984_a();
            if (!world.func_175623_d(above)) {
               return;
            }

            if (block instanceof SugarCaneBlock || block instanceof CactusBlock) {
               int height = 1;

               while (world.func_180495_p(pos.func_177979_c(height)).func_203425_a(block)) {
                  height++;
               }

               if (height < 3 && rand.nextInt(3) == 0 && ForgeHooks.onCropsGrowPre(world, pos, state, true)) {
                  world.func_175656_a(above, block.func_176223_P());
                  BlockState newState = (BlockState)state.func_206870_a(BlockStateProperties.field_208171_X, 0);
                  world.func_180501_a(pos, newState, 4);
                  newState.func_215697_a(world, above, block, pos, false);
                  world.func_205220_G_().func_205362_a(above, block, 1, TickPriority.EXTREMELY_HIGH);
                  ForgeHooks.onCropsGrowPost(world, above, state);
                  world.func_195598_a(ParticleTypes.field_197632_y, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), 100, 1.0, 0.5, 1.0, 0.0);
               }
            }
         }

         if (this.growsAnimals) {
            AxisAlignedBB searchBox = player.func_174813_aQ().func_72314_b(this.getXRange(), this.getYRange(), this.getZRange());

            for (AgeableEntity entity : world.func_225316_b(
               AgeableEntity.class, searchBox, entityx -> entityx.func_70089_S() && !entityx.func_175149_v() && entityx.func_70631_g_()
            )) {
               if (rand.nextFloat() < 0.4F) {
                  world.func_195598_a(ParticleTypes.field_197632_y, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), 100, 1.0, 0.5, 1.0, 0.0);
               }

               if (rand.nextFloat() < 0.05F) {
                  entity.func_82227_f(false);
               }
            }
         }
      }
   }
}
