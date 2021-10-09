package iskallia.vault.entity;

import iskallia.vault.entity.ai.ThrowProjectilesGoal;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.SkinProfile;
import java.util.regex.Pattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

public class FighterEntity extends ZombieEntity {
   public static final ThrowProjectilesGoal.Projectile SNOWBALLS = (world1, shooter) -> new SnowballEntity(world1, shooter) {
      protected void func_213868_a(EntityRayTraceResult raycast) {
         Entity entity = raycast.func_216348_a();
         if (entity != shooter) {
            int i = entity instanceof BlazeEntity ? 3 : 1;
            entity.func_70097_a(DamageSource.func_188403_a(this, shooter), i);
         }
      }
   };
   public SkinProfile skin;
   public String lastName = "Fighter";
   public float sizeMultiplier = 1.0F;
   public ServerBossInfo bossInfo;

   public FighterEntity(EntityType<? extends ZombieEntity> type, World world) {
      super(type, world);
      if (!this.field_70170_p.field_72995_K) {
         this.changeSize(this.sizeMultiplier);
         this.func_110148_a(Attributes.field_233821_d_).func_111128_a(this.field_70146_Z.nextFloat() * 0.15 + 0.2);
      } else {
         this.skin = new SkinProfile();
      }

      this.bossInfo = new ServerBossInfo(this.func_145748_c_(), Color.PURPLE, Overlay.PROGRESS);
      this.bossInfo.func_186741_a(true);
      this.bossInfo.func_186758_d(false);
      this.func_200203_b(new StringTextComponent(this.lastName));
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getLocationSkin() {
      return this.skin.getLocationSkin();
   }

   public boolean func_70631_g_() {
      return false;
   }

   protected boolean func_190730_o() {
      return false;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70729_aU) {
         if (this.field_70170_p.field_72995_K) {
            String name = this.func_200201_e().getString();
            if (name.startsWith("[")) {
               String[] data = name.split(Pattern.quote("]"));
               name = data[1].trim();
            }

            if (!this.lastName.equals(name)) {
               this.skin.updateSkin(name);
               this.lastName = name;
            }
         } else {
            double amplitude = this.func_213322_ci().func_186679_c(0.0, this.func_213322_ci().func_82617_b(), 0.0);
            if (amplitude > 0.004) {
               this.func_70031_b(true);
            } else {
               this.func_70031_b(false);
            }

            this.bossInfo.func_186735_a(this.func_110143_aJ() / this.func_110138_aP());
         }
      }
   }

   protected SoundEvent func_184639_G() {
      return null;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187798_ea;
   }

   protected SoundEvent func_184601_bQ(DamageSource damageSourceIn) {
      return SoundEvents.field_187800_eb;
   }

   public void func_200203_b(ITextComponent name) {
      super.func_200203_b(name);
      this.bossInfo.func_186739_a(this.func_145748_c_());
   }

   public void func_213281_b(CompoundNBT compound) {
      super.func_213281_b(compound);
      compound.func_74776_a("SizeMultiplier", this.sizeMultiplier);
   }

   public void func_70037_a(CompoundNBT compound) {
      super.func_70037_a(compound);
      if (compound.func_150297_b("SizeMultiplier", 5)) {
         this.changeSize(compound.func_74760_g("SizeMultiplier"));
      }

      this.bossInfo.func_186739_a(this.func_145748_c_());
   }

   public void func_184178_b(ServerPlayerEntity player) {
      super.func_184178_b(player);
      this.bossInfo.func_186760_a(player);
   }

   public void func_184203_c(ServerPlayerEntity player) {
      super.func_184203_c(player);
      this.bossInfo.func_186761_b(player);
   }

   public EntitySize func_213305_a(Pose pose) {
      return this.field_213325_aI;
   }

   public float getSizeMultiplier() {
      return this.sizeMultiplier;
   }

   public FighterEntity changeSize(float m) {
      this.sizeMultiplier = m;
      EntityHelper.changeSize(this, this.sizeMultiplier);
      if (!this.field_70170_p.func_201670_d()) {
         ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new FighterSizeMessage(this, this.sizeMultiplier));
      }

      return this;
   }

   protected float func_213348_b(Pose pose, EntitySize size) {
      return super.func_213348_b(pose, size) * this.sizeMultiplier;
   }

   protected void func_207302_dI() {
   }

   public ILivingEntityData func_213386_a(
      IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag
   ) {
      this.func_200203_b(this.func_200201_e());
      this.func_146070_a(true);
      this.func_98053_h(true);
      this.func_110163_bv();
      if (this.field_70146_Z.nextInt(100) == 0) {
         ChickenEntity chicken = (ChickenEntity)EntityType.field_200795_i.func_200721_a(this.field_70170_p);
         chicken.func_70012_b(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.field_70177_z, 0.0F);
         chicken.func_213386_a(world, difficulty, reason, spawnData, dataTag);
         chicken.func_152117_i(true);
         ((ServerWorld)this.field_70170_p).func_217470_d(chicken);
         this.func_184220_m(chicken);
      }

      return spawnData;
   }

   protected void func_213354_a(DamageSource damageSource, boolean attackedRecently) {
      super.func_213354_a(damageSource, attackedRecently);
      if (!this.field_70170_p.func_201670_d()) {
         ;
      }
   }

   public boolean func_70652_k(Entity entity) {
      if (!this.field_70170_p.field_72995_K) {
         ((ServerWorld)this.field_70170_p)
            .func_195598_a(ParticleTypes.field_197603_N, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), 1, 0.0, 0.0, 0.0, 0.0);
         this.field_70170_p
            .func_184133_a(
               null,
               this.func_233580_cy_(),
               SoundEvents.field_187730_dW,
               SoundCategory.PLAYERS,
               1.0F,
               this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()
            );
      }

      return super.func_70652_k(entity);
   }
}
