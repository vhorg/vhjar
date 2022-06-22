package iskallia.vault.entity;

import iskallia.vault.entity.ai.eyesore.EyesoreBrain;
import iskallia.vault.entity.ai.eyesore.EyesorePath;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.EntityTeleportEvent.EnderEntity;

public class EyesoreEntity extends GhastEntity implements VaultBoss {
   public static final DataParameter<Integer> STATE = EntityDataManager.func_187226_a(EyesoreEntity.class, DataSerializers.field_187192_b);
   public static final DataParameter<Optional<UUID>> LASER_TARGET = EntityDataManager.func_187226_a(EyesoreEntity.class, DataSerializers.field_187203_m);
   public static final DataParameter<Integer> TENTACLES_REMAINING = EntityDataManager.func_187226_a(EyesoreEntity.class, DataSerializers.field_187192_b);
   public static final DataParameter<Boolean> WATCH_CLIENT = EntityDataManager.func_187226_a(EyesoreEntity.class, DataSerializers.field_187198_h);
   public final ServerBossInfo bossInfo;
   public EyesorePath path = new EyesorePath();
   public float prevHealth;
   public EyesoreBrain field_213378_br = new EyesoreBrain(this);
   public int laserTick = 0;

   public EyesoreEntity(EntityType<? extends GhastEntity> type, World worldIn) {
      super(type, worldIn);
      this.bossInfo = new ServerBossInfo(this.func_145748_c_(), Color.RED, Overlay.NOTCHED_10);
      this.field_70145_X = true;
      this.func_110163_bv();
      this.func_110148_a(Attributes.field_233818_a_).func_111128_a(ModConfigs.EYESORE.getHealth(this));
      this.func_70606_j(ModConfigs.EYESORE.health);
      this.prevHealth = this.func_110143_aJ();
   }

   public int getTentaclesRemaining() {
      return (Integer)this.field_70180_af.func_187225_a(TENTACLES_REMAINING);
   }

   public EyesoreEntity.State getState() {
      Integer ordinal = (Integer)this.field_70180_af.func_187225_a(STATE);
      return EyesoreEntity.State.values()[ordinal];
   }

   public void setState(EyesoreEntity.State state) {
      this.field_70180_af.func_187227_b(STATE, state.ordinal());
   }

   protected void func_213354_a(@Nonnull DamageSource damageSource, boolean attackedRecently) {
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(STATE, EyesoreEntity.State.NORMAL.ordinal());
      this.field_70180_af.func_187214_a(LASER_TARGET, Optional.empty());
      this.field_70180_af.func_187214_a(TENTACLES_REMAINING, 9);
      this.field_70180_af.func_187214_a(WATCH_CLIENT, false);
   }

   protected void func_184651_r() {
   }

   protected float func_213348_b(Pose poseIn, EntitySize sizeIn) {
      return 5.0F;
   }

   @Nonnull
   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB func_184177_bl() {
      return super.func_184177_bl().func_186662_g(100.0);
   }

   @Override
   public ServerBossInfo getServerBossInfo() {
      return this.bossInfo;
   }

   public void func_184178_b(ServerPlayerEntity player) {
      super.func_184178_b(player);
      this.bossInfo.func_186760_a(player);
   }

   public void func_184203_c(ServerPlayerEntity player) {
      super.func_184203_c(player);
      this.bossInfo.func_186761_b(player);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K) {
         this.bossInfo.func_186735_a(this.func_110143_aJ() / this.func_110138_aP());
         float maxHealth = ModConfigs.EYESORE.getHealth(this);
         float currentMaxHealth = (float)this.func_110148_a(Attributes.field_233818_a_).func_111125_b();
         if (Math.abs(maxHealth - currentMaxHealth) > 0.1F) {
            this.func_70606_j(this.func_110143_aJ() / currentMaxHealth * maxHealth);
            this.func_110148_a(Attributes.field_233818_a_).func_111128_a(maxHealth);
         }

         ServerWorld serverWorld = (ServerWorld)this.field_70170_p;
         VaultRaid vault = VaultRaidData.get(serverWorld).getAt(serverWorld, this.func_233580_cy_());
         if (vault != null && this.field_70170_p.func_82737_E() % 40L == 0L) {
            vault.getPlayers()
               .stream()
               .map(p -> p.getServerPlayer(((ServerWorld)this.field_70170_p).func_73046_m()))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .forEach(
                  p -> this.field_70170_p
                     .func_184148_a(
                        null, p.func_226277_ct_(), p.func_226278_cu_(), p.func_226281_cx_(), SoundEvents.field_206934_aN, SoundCategory.HOSTILE, 1.0F, 0.5F
                     )
               );
         }

         this.field_213378_br.tick();
         this.path.tick(this);
         float healthPercentage = this.func_110143_aJ() / this.func_110138_aP();
         int tentaclesRemaining = (Integer)this.field_70180_af.func_187225_a(TENTACLES_REMAINING);
         int expectedTentacles = MathHelper.func_76125_a((int)(healthPercentage * 10.0F), 0, 9);
         if (tentaclesRemaining != expectedTentacles) {
            this.field_70180_af.func_187227_b(TENTACLES_REMAINING, expectedTentacles);

            for (int i = 0; i < tentaclesRemaining - expectedTentacles; i++) {
               EyestalkEntity eyestalkEntity = (EyestalkEntity)ModEntities.EYESTALK.func_200721_a(serverWorld);
               if (eyestalkEntity != null) {
                  eyestalkEntity.func_70012_b(
                     this.func_226277_ct_(), this.func_226278_cu_() + 7.0, this.func_226281_cx_(), this.field_70125_A, this.field_70177_z
                  );
                  eyestalkEntity.func_213293_j(0.0, 0.25, 0.0);
                  serverWorld.func_217470_d(eyestalkEntity);
                  eyestalkEntity.mother = this.func_110124_au();
               }
            }
         }
      }

      LivingEntity target = (LivingEntity)((Optional)this.func_184212_Q().func_187225_a(LASER_TARGET))
         .<PlayerEntity>map(playerId -> this.func_130014_f_().func_217371_b(playerId))
         .orElse(null);
      if (target != null) {
         this.laserTick++;
         this.lookAtTarget(target);
      } else {
         this.laserTick = 0;
         PlayerEntity closestPlayer = this.field_70170_p.func_217362_a(this, 40.0);
         if (closestPlayer != null) {
         }
      }

      if ((Boolean)this.func_184212_Q().func_187225_a(WATCH_CLIENT) && this.field_70170_p.field_72995_K) {
         this.lookAtClientPlayer();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void lookAtClientPlayer() {
      ClientPlayerEntity playerEntity = Minecraft.func_71410_x().field_71439_g;
      this.lookAtTarget(playerEntity);
   }

   protected void lookAtTarget(LivingEntity target) {
      this.field_70125_A = this.getTargetPitch(target);
      this.field_70759_as = this.getTargetYaw(target);
   }

   private double getEyePosition(Entity entity) {
      return entity instanceof LivingEntity ? entity.func_226280_cw_() : (entity.func_174813_aQ().field_72338_b + entity.func_174813_aQ().field_72337_e) / 2.0;
   }

   protected float getTargetPitch(LivingEntity target) {
      double d0 = target.func_226277_ct_() - this.func_226277_ct_();
      double d1 = this.getEyePosition(target) - this.func_226280_cw_();
      double d2 = target.func_226281_cx_() - this.func_226281_cx_();
      double d3 = MathHelper.func_76133_a(d0 * d0 + d2 * d2);
      return (float)(-(MathHelper.func_181159_b(d1, d3) * 180.0F / (float)Math.PI));
   }

   protected float getTargetYaw(LivingEntity target) {
      double d0 = target.func_226277_ct_() - this.func_226277_ct_();
      double d1 = target.func_226281_cx_() - this.func_226281_cx_();
      return (float)(MathHelper.func_181159_b(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
   }

   public boolean func_70097_a(DamageSource source, float amount) {
      if (source instanceof IndirectEntityDamageSource) {
         for (int i = 0; i < 64; i++) {
            if (this.teleportRandomly()) {
               return true;
            }
         }

         return false;
      } else {
         return super.func_70097_a(source, amount);
      }
   }

   protected boolean teleportRandomly() {
      if (!this.field_70170_p.func_201670_d() && this.func_70089_S()) {
         double d0 = this.func_226277_ct_() + (this.field_70146_Z.nextDouble() - 0.5) * 64.0;
         double d1 = this.func_226278_cu_() + (this.field_70146_Z.nextInt(64) - 32);
         double d2 = this.func_226281_cx_() + (this.field_70146_Z.nextDouble() - 0.5) * 64.0;
         return this.teleportTo(d0, d1, d2);
      } else {
         return false;
      }
   }

   private boolean teleportTo(double x, double y, double z) {
      Mutable blockpos$mutable = new Mutable(x, y, z);

      while (blockpos$mutable.func_177956_o() > 0 && !this.field_70170_p.func_180495_p(blockpos$mutable).func_185904_a().func_76230_c()) {
         blockpos$mutable.func_189536_c(Direction.DOWN);
      }

      BlockState blockstate = this.field_70170_p.func_180495_p(blockpos$mutable);
      boolean flag = blockstate.func_185904_a().func_76230_c();
      boolean flag1 = blockstate.func_204520_s().func_206884_a(FluidTags.field_206959_a);
      if (flag && !flag1) {
         EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
         if (event.isCanceled()) {
            return false;
         } else {
            boolean flag2 = this.func_213373_a(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2 && !this.func_174814_R()) {
               this.field_70170_p
                  .func_184148_a(
                     (PlayerEntity)null,
                     this.field_70169_q,
                     this.field_70167_r,
                     this.field_70166_s,
                     SoundEvents.field_187534_aX,
                     this.func_184176_by(),
                     1.0F,
                     1.0F
                  );
               this.func_184185_a(SoundEvents.field_187534_aX, 1.0F, 1.0F);
            }

            return flag2;
         }
      } else {
         return false;
      }
   }

   public void func_70100_b_(@Nonnull PlayerEntity playerEntity) {
      if (playerEntity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)playerEntity;
         Vector3d posPlayer = player.func_213303_ch();
         Vector3d posEyesore = this.func_213303_ch();
         player.func_70097_a(DamageSource.func_76358_a(this), ModConfigs.EYESORE.meleeAttack.getDamage(this));
         this.applyKnockback(
            player,
            ModConfigs.EYESORE.meleeAttack.knockback,
            posEyesore.field_72450_a - posPlayer.field_72450_a,
            posEyesore.field_72449_c - posPlayer.field_72449_c
         );
      }
   }

   public void applyKnockback(Entity target, float strength, double ratioX, double ratioZ) {
      if (strength > 0.0F) {
         target.field_70160_al = true;
         Vector3d vector3d = target.func_213322_ci();
         Vector3d vector3d1 = new Vector3d(ratioX, 0.0, ratioZ).func_72432_b().func_186678_a(strength);
         target.func_213293_j(
            vector3d.field_72450_a / 2.0 - vector3d1.field_72450_a,
            this.field_70122_E ? Math.min(0.4, vector3d.field_72448_b / 2.0 + strength) : vector3d.field_72448_b,
            vector3d.field_72449_c / 2.0 - vector3d1.field_72449_c
         );
      }
   }

   @Nonnull
   public SoundCategory func_184176_by() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent func_184615_bR() {
      return super.func_184615_bR();
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187521_aK;
   }

   protected SoundEvent func_184601_bQ(DamageSource damageSource) {
      return SoundEvents.field_187526_aP;
   }

   protected float func_70647_i() {
      return (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.3F;
   }

   public void func_200203_b(ITextComponent name) {
      super.func_200203_b(name);
      this.bossInfo.func_186739_a(this.func_145748_c_());
   }

   public void func_213281_b(CompoundNBT compound) {
      super.func_213281_b(compound);
   }

   public void func_70037_a(CompoundNBT compound) {
      super.func_70037_a(compound);
      this.bossInfo.func_186739_a(this.func_145748_c_());
   }

   public static MutableAttribute getAttributes() {
      return MonsterEntity.func_234295_eP_()
         .func_233815_a_(Attributes.field_233819_b_, 100.0)
         .func_233815_a_(Attributes.field_233821_d_, 0.25)
         .func_233815_a_(Attributes.field_233823_f_, 3.0)
         .func_233815_a_(Attributes.field_233824_g_, 3.0)
         .func_233815_a_(Attributes.field_233820_c_, 0.4)
         .func_233815_a_(Attributes.field_233826_i_, 2.0);
   }

   public static enum State {
      NORMAL,
      GIVING_BIRTH;
   }
}
