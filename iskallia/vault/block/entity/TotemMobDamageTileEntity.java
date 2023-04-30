package iskallia.vault.block.entity;

import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.ColoredParticleOptions;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.effect.TotemMobDamageAbility;
import iskallia.vault.util.EntityHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class TotemMobDamageTileEntity extends TotemTileEntity {
   private static final Predicate<Entity> ENTITY_SELECTION_FILTER = entity -> !(entity instanceof Player)
      && entity instanceof LivingEntity livingEntity
      && livingEntity.isAlive()
      && !livingEntity.hasEffect(ModEffects.TOTEM_MOB_DAMAGE);
   private int damageIntervalTicks;
   private float damagePerInterval;
   private final List<LivingEntity> entityList = new ArrayList<>();
   public static final Vector3f PARTICLE_EFFECT_COLOR = new Vector3f(1.0F, 0.322F, 0.824F);
   private static final String TAG_DAMAGE_PER_INTERVAL = "damagePerInterval";
   private static final String TAG_DAMAGE_INTERVAL_TICKS = "damageIntervalTicks";

   public TotemMobDamageTileEntity(BlockPos blockPos, BlockState blockState) {
      super(ModBlocks.TOTEM_MOB_DAMAGE_TILE_ENTITY, blockPos, blockState);
   }

   public void initialize(UUID playerUUID, int durationTicks, float effectRadius, float damagePerInterval, int damageIntervalTicks) {
      super.initialize(playerUUID, durationTicks, effectRadius);
      this.damagePerInterval = damagePerInterval;
      this.damageIntervalTicks = damageIntervalTicks;
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, TotemMobDamageTileEntity tile) {
      tile.serverTick();
   }

   @Override
   protected boolean serverTick() {
      if (super.serverTick() && this.level != null) {
         EntityHelper.getEntitiesInRange(
            this.level, this.getEffectBounds(), this.getEffectOrigin(), this.getEffectRadius(), ENTITY_SELECTION_FILTER, this.entityList
         );
         if (!this.entityList.isEmpty()) {
            Player player = this.level.getPlayerByUUID(this.getPlayerUUID());
            if (player == null) {
               return false;
            }

            this.updateEffects(this.entityList, this.damageIntervalTicks);
            this.hurtEntities(this.entityList, player, this.damagePerInterval);
            this.entityList.clear();
         }

         return true;
      } else {
         this.entityList.clear();
         return false;
      }
   }

   private void hurtEntities(List<LivingEntity> entityList, Player player, float damagePercent) {
      AttributeInstance attributeInstance = player.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attributeInstance != null) {
         float damage = (float)(attributeInstance.getValue() * damagePercent);
         ActiveFlags.IS_TOTEM_ATTACKING.runIfNotSet(() -> {
            for (LivingEntity livingEntity : entityList) {
               Vec3 movement = livingEntity.getDeltaMovement();
               livingEntity.hurt(DamageSource.playerAttack(player), damage);
               livingEntity.setDeltaMovement(movement);
            }
         });
      }
   }

   private void updateEffects(List<LivingEntity> entityList, int damageIntervalTicks) {
      for (LivingEntity livingEntity : entityList) {
         TotemMobDamageAbility.TotemMobDamageEffect.addTo(livingEntity, damageIntervalTicks);
      }
   }

   @Nonnull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public Vector3f getParticleEffectColor() {
      return PARTICLE_EFFECT_COLOR;
   }

   @Override
   public ParticleOptions getFountainParticleOptions() {
      return new ColoredParticleOptions((ParticleType<ColoredParticleOptions>)ModParticles.TOTEM_FOUNTAIN.get(), PARTICLE_EFFECT_COLOR);
   }

   @Override
   protected ParticleOptions getEffectRangeParticleOptions(float effectRadius) {
      return new SphericalParticleOptions((ParticleType<SphericalParticleOptions>)ModParticles.TOTEM_EFFECT_RANGE.get(), effectRadius, PARTICLE_EFFECT_COLOR);
   }

   @Override
   public void load(@Nonnull CompoundTag tag) {
      this.damagePerInterval = tag.getFloat("damagePerInterval");
      this.damageIntervalTicks = tag.getInt("damageIntervalTicks");
      super.load(tag);
   }

   @Override
   protected void saveAdditional(@Nonnull CompoundTag tag) {
      super.saveAdditional(tag);
      tag.putFloat("damagePerInterval", this.damagePerInterval);
      tag.putInt("damageIntervalTicks", this.damageIntervalTicks);
   }

   @OnlyIn(Dist.CLIENT)
   @NotNull
   protected TotemMobDamageTileEntity.RenderContext createRenderContext() {
      return new TotemMobDamageTileEntity.RenderContext();
   }

   @OnlyIn(Dist.CLIENT)
   public static class RenderContext extends TotemTileEntity.RenderContext {
      public List<LivingEntity> targetList;
   }
}
