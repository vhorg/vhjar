package iskallia.vault.skill.ability.effect;

import iskallia.vault.Vault;
import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.config.HunterConfig;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.ServerScheduler;
import java.awt.Color;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

public class HunterAbility<C extends HunterConfig> extends AbilityEffect<C> {
   private static final AxisAlignedBB SEARCH_BOX = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

   @Override
   public String getAbilityGroupName() {
      return "Hunter";
   }

   public boolean onAction(C config, ServerPlayerEntity player, boolean active) {
      World world = player.func_130014_f_();
      if (player instanceof ServerPlayerEntity && world instanceof ServerWorld && world.func_234923_W_() == Vault.VAULT_KEY) {
         ServerWorld sWorld = (ServerWorld)world;
         ServerPlayerEntity sPlayer = player;

         for (int delay = 0; delay < config.getTickDuration() / 5; delay++) {
            ServerScheduler.INSTANCE
               .schedule(
                  delay * 5,
                  () -> this.selectPositions(config, world, player)
                     .forEach(
                        tpl -> {
                           Color c = (Color)tpl.func_76340_b();

                           for (int i = 0; i < 8; i++) {
                              Vector3d v = MiscUtils.getRandomOffset((BlockPos)tpl.func_76341_a(), rand);
                              sWorld.func_195600_a(
                                 sPlayer,
                                 (IParticleData)ModParticles.DEPTH_FIREWORK.get(),
                                 true,
                                 v.field_72450_a,
                                 v.field_72448_b,
                                 v.field_72449_c,
                                 0,
                                 c.getRed() / 255.0F,
                                 c.getGreen() / 255.0F,
                                 c.getBlue() / 255.0F,
                                 1.0
                              );
                           }
                        }
                     )
               );
         }

         return true;
      } else {
         return false;
      }
   }

   protected Predicate<LivingEntity> getEntityFilter() {
      return e -> e.func_70089_S() && !e.func_175149_v() && e.getClassification(false) == EntityClassification.MONSTER;
   }

   protected List<Tuple<BlockPos, Color>> selectPositions(C config, World world, PlayerEntity player) {
      Color c = new Color(config.getColor(), false);
      return world.func_225316_b(
            LivingEntity.class, SEARCH_BOX.func_186670_a(player.func_233580_cy_()).func_186662_g(config.getSearchRadius()), this.getEntityFilter()
         )
         .stream()
         .<BlockPos>map(Entity::func_233580_cy_)
         .map(pos -> new Tuple(pos, c))
         .collect(Collectors.toList());
   }

   protected void forEachTileEntity(C config, World world, PlayerEntity player, BiConsumer<BlockPos, TileEntity> tileFn) {
      BlockPos playerOffset = player.func_233580_cy_();
      double radius = config.getSearchRadius();
      double radiusSq = radius * radius;
      int iRadius = MathHelper.func_76143_f(radius);
      Vector3i radVec = new Vector3i(iRadius, iRadius, iRadius);
      ChunkPos posMin = new ChunkPos(player.func_233580_cy_().func_177973_b(radVec));
      ChunkPos posMax = new ChunkPos(player.func_233580_cy_().func_177971_a(radVec));

      for (int xx = posMin.field_77276_a; xx <= posMax.field_77276_a; xx++) {
         for (int zz = posMin.field_77275_b; zz <= posMax.field_77275_b; zz++) {
            Chunk ch = world.func_72863_F().func_225313_a(xx, zz);
            if (ch != null) {
               ch.func_177434_r().forEach((pos, tile) -> {
                  if (tile != null && pos.func_177951_i(playerOffset) <= radiusSq) {
                     tileFn.accept(pos, tile);
                  }
               });
            }
         }
      }
   }
}
