package iskallia.vault.block.entity;

import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.ColoredParticleOptions;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.effect.TotemPlayerDamageAbility;
import iskallia.vault.util.MathUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TotemPlayerDamageTileEntity extends TotemTileEntity {
   private float playerDamageIncrease;
   private final List<Player> playerList = new ArrayList<>();
   public static final Vector3f PARTICLE_EFFECT_COLOR = new Vector3f(0.924F, 0.265F, 0.058F);
   private static final String TAG_PLAYER_DAMAGE_INCREASE = "playerDamageIncrease";

   public TotemPlayerDamageTileEntity(BlockPos blockPos, BlockState blockState) {
      super(ModBlocks.TOTEM_PLAYER_DAMAGE_TILE_ENTITY, blockPos, blockState);
   }

   public void initialize(UUID playerUUID, int durationTicks, float effectRadius, float playerDamageIncrease) {
      super.initialize(playerUUID, durationTicks, effectRadius);
      this.playerDamageIncrease = playerDamageIncrease;
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, TotemPlayerDamageTileEntity tile) {
      tile.serverTick();
   }

   @Override
   protected boolean serverTick() {
      if (!super.serverTick()) {
         this.playerList.clear();
         return false;
      } else {
         this.getPlayersInRange(this.getEffectBounds(), this.getEffectOrigin(), this.getEffectRadius(), this.playerList);
         this.updatePlayerEffect(this.playerList);
         this.playerList.clear();
         if (!(this.level instanceof ServerLevel serverLevel)) {
            return true;
         } else {
            if (this.getRemainingDurationTicks() % 5 == 0 && serverLevel.random.nextFloat() < 0.25 || this.getRemainingDurationTicks() % 20 == 0) {
               BlockPos blockPos = this.getBlockPos();
               serverLevel.sendParticles(
                  ParticleTypes.SMALL_FLAME, blockPos.getX() + 0.5, blockPos.getY() + 0.34375, blockPos.getZ() + 0.6875, 1, 0.0125, 0.0, 0.0125, 0.0
               );
               serverLevel.sendParticles(
                  ParticleTypes.SMALL_FLAME, blockPos.getX() + 0.65625, blockPos.getY() + 0.28125, blockPos.getZ() + 0.75, 1, 0.0125, 0.0, 0.0125, 0.0
               );
               serverLevel.sendParticles(
                  ParticleTypes.SMALL_FLAME, blockPos.getX() + 0.5, blockPos.getY() + 0.34375, blockPos.getZ() + 0.3125, 1, 0.0125, 0.0, 0.0125, 0.0
               );
               serverLevel.sendParticles(
                  ParticleTypes.SMALL_FLAME, blockPos.getX() + 0.34375, blockPos.getY() + 0.28125, blockPos.getZ() + 0.25, 1, 0.0125, 0.0, 0.0125, 0.0
               );
            }

            return true;
         }
      }
   }

   private void updatePlayerEffect(List<Player> playerList) {
      for (Player player : playerList) {
         TotemPlayerDamageAbility.TotemPlayerDamageEffect.addTo(player, this.playerDamageIncrease);
      }
   }

   private void getPlayersInRange(AABB area, Vec3 center, float range, List<Player> result) {
      if (this.level != null) {
         for (Player player : this.level.players()) {
            if (area.contains(player.position()) && MathUtilities.isAABBIntersectingOrInsideSphere(player.getBoundingBox(), center, range)) {
               result.add(player);
            }
         }
      }
   }

   @Override
   public Vector3f getParticleEffectColor() {
      return PARTICLE_EFFECT_COLOR;
   }

   @Override
   protected ParticleOptions getFountainParticleOptions() {
      return new ColoredParticleOptions((ParticleType<ColoredParticleOptions>)ModParticles.TOTEM_FOUNTAIN.get(), PARTICLE_EFFECT_COLOR);
   }

   @Override
   protected ParticleOptions getEffectRangeParticleOptions(float effectRadius) {
      return new SphericalParticleOptions((ParticleType<SphericalParticleOptions>)ModParticles.TOTEM_EFFECT_RANGE.get(), effectRadius, PARTICLE_EFFECT_COLOR);
   }

   @Override
   public void load(@Nonnull CompoundTag tag) {
      this.playerDamageIncrease = tag.getFloat("playerDamageIncrease");
      super.load(tag);
   }

   @Override
   protected void saveAdditional(@Nonnull CompoundTag tag) {
      super.saveAdditional(tag);
      tag.putFloat("playerDamageIncrease", this.playerDamageIncrease);
   }
}
