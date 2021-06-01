package iskallia.vault.block;

import iskallia.vault.client.gui.overlay.VaultRaidOverlay;
import iskallia.vault.entity.EntityScaler;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.entity.VaultBoss;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.ObeliskInscriptionItem;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.raid.VaultRaid;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ObeliskBlock extends Block {
   public static final IntegerProperty COMPLETION = IntegerProperty.func_177719_a("completion", 0, 4);

   public ObeliskBlock() {
      super(Properties.func_200945_a(Material.field_151576_e).func_200947_a(SoundType.field_185852_e).func_200948_a(-1.0F, 3600000.0F).func_222380_e());
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(COMPLETION, 0));
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return Block.func_208617_a(4.0, 0.0, 4.0, 12.0, 32.0, 12.0);
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      ItemStack heldStack = player.func_184586_b(hand);
      if (heldStack.func_77973_b() instanceof ObeliskInscriptionItem) {
         if (!player.func_184812_l_()) {
            heldStack.func_190918_g(1);
         }

         BlockState newState = (BlockState)state.func_206870_a(COMPLETION, MathHelper.func_76125_a((Integer)state.func_177229_b(COMPLETION) + 1, 0, 4));
         world.func_175656_a(pos, newState);
         if (world.field_72995_K) {
            if ((Integer)newState.func_177229_b(COMPLETION) == 4) {
               this.startBossLoop();
            }

            return ActionResultType.SUCCESS;
         } else {
            this.spawnParticles(world, pos);
            if ((Integer)newState.func_177229_b(COMPLETION) == 4) {
               VaultRaid raid = VaultRaidData.get((ServerWorld)world).getAt(pos);
               if (raid != null) {
                  this.spawnBoss(raid, (ServerWorld)world, pos, EntityScaler.Type.BOSS);
               }

               world.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
            }

            return ActionResultType.SUCCESS;
         }
      } else {
         return ActionResultType.PASS;
      }
   }

   public void spawnBoss(VaultRaid raid, ServerWorld world, BlockPos pos, EntityScaler.Type type) {
      if (type == EntityScaler.Type.BOSS) {
         LivingEntity boss = ModConfigs.VAULT_MOBS.getForLevel(raid.level).BOSS_POOL.getRandom(world.func_201674_k()).create(world);
         if (boss instanceof FighterEntity) {
            ((FighterEntity)boss).changeSize(2.0F);
         }

         boss.func_70012_b(pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.2, pos.func_177952_p() + 0.5, 0.0F, 0.0F);
         world.func_217470_d(boss);
         boss.func_184216_O().add("VaultBoss");
         raid.addBoss(boss);
         if (boss instanceof FighterEntity) {
            ((FighterEntity)boss).bossInfo.func_186758_d(true);
         }

         if (boss instanceof VaultBoss) {
            ((VaultBoss)boss).getServerBossInfo().func_186758_d(true);
         }

         EntityScaler.scaleVault(boss, raid.level, new Random(), EntityScaler.Type.BOSS);
         if (raid.playerBossName != null) {
            boss.func_200203_b(new StringTextComponent(raid.playerBossName));
         } else {
            boss.func_200203_b(new StringTextComponent("Boss"));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void startBossLoop() {
      VaultRaidOverlay.bossSummoned = true;
   }

   private void spawnParticles(World world, BlockPos pos) {
      for (int i = 0; i < 20; i++) {
         double d0 = world.field_73012_v.nextGaussian() * 0.02;
         double d1 = world.field_73012_v.nextGaussian() * 0.02;
         double d2 = world.field_73012_v.nextGaussian() * 0.02;
         ((ServerWorld)world)
            .func_195598_a(
               ParticleTypes.field_197598_I,
               pos.func_177958_n() + world.field_73012_v.nextDouble() - d0,
               pos.func_177956_o() + world.field_73012_v.nextDouble() - d1,
               pos.func_177952_p() + world.field_73012_v.nextDouble() - d2,
               10,
               d0,
               d1,
               d2,
               1.0
            );
      }

      world.func_184133_a(null, pos, SoundEvents.field_193781_bp, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      super.func_206840_a(builder);
      builder.func_206894_a(new Property[]{COMPLETION});
   }
}
