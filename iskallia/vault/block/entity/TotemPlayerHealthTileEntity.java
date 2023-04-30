package iskallia.vault.block.entity;

import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.ColoredParticleOptions;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.effect.TotemAbility;
import iskallia.vault.util.MathUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TotemPlayerHealthTileEntity extends TotemTileEntity {
   private float healthPerTick;
   private final List<Player> playerList = new ArrayList<>();
   public static final Vector3f PARTICLE_EFFECT_COLOR = new Vector3f(0.533F, 1.0F, 0.0F);
   private static final String TAG_HEALTH_PER_TICK = "healthPerTick";

   public TotemPlayerHealthTileEntity(BlockPos blockPos, BlockState blockState) {
      super(ModBlocks.TOTEM_PLAYER_HEALTH_TILE_ENTITY, blockPos, blockState);
   }

   public void initialize(UUID playerUUID, int durationTicks, float effectRadius, float healthPerSecond) {
      super.initialize(playerUUID, durationTicks, effectRadius);
      this.healthPerTick = healthPerSecond / 20.0F;
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, TotemPlayerHealthTileEntity tile) {
      tile.serverTick();
   }

   @Override
   protected boolean serverTick() {
      if (!super.serverTick()) {
         this.playerList.clear();
         return false;
      } else {
         this.getPlayersInRange(this.getEffectBounds(), this.getEffectOrigin(), this.getEffectRadius(), this.playerList);
         this.updatePlayerEffect(this.playerList, this.healthPerTick);
         this.playerList.clear();
         return true;
      }
   }

   private void updatePlayerEffect(List<Player> playerList, float healthPerTick) {
      for (Player player : playerList) {
         TotemAbility.TotemPlayerHealthEffect.addTo(player, healthPerTick);
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
      this.healthPerTick = tag.getFloat("healthPerTick");
      super.load(tag);
   }

   @Override
   protected void saveAdditional(@Nonnull CompoundTag tag) {
      super.saveAdditional(tag);
      tag.putFloat("healthPerTick", this.healthPerTick);
   }
}
