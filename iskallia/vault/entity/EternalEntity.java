package iskallia.vault.entity;

import com.mojang.datafixers.util.Either;
import iskallia.vault.aura.AuraManager;
import iskallia.vault.aura.EntityAuraProvider;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.entity.ai.FollowEntityGoal;
import iskallia.vault.entity.eternal.ActiveEternalData;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.SummonEternalDebuffConfig;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.util.DamageUtil;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
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

public class EternalEntity extends ZombieEntity {
   private static final DataParameter<String> ETERNAL_NAME = EntityDataManager.func_187226_a(EternalEntity.class, DataSerializers.field_187194_d);
   public SkinProfile skin;
   public float sizeMultiplier = 1.0F;
   private boolean ancient = false;
   private long despawnTime = Long.MAX_VALUE;
   private final ServerBossInfo bossInfo;
   private UUID owner;
   private UUID eternalId;
   private String providedAura;

   public EternalEntity(EntityType<? extends ZombieEntity> type, World world) {
      super(type, world);
      if (this.field_70170_p.field_72995_K) {
         this.skin = new SkinProfile();
      }

      this.bossInfo = new ServerBossInfo(this.func_145748_c_(), Color.PURPLE, Overlay.PROGRESS);
      this.bossInfo.func_186741_a(true);
      this.bossInfo.func_186758_d(false);
      this.func_98053_h(false);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(ETERNAL_NAME, "Eternal");
   }

   protected void func_175456_n() {
      this.field_70714_bg.func_75776_a(2, new ZombieAttackGoal(this, 1.1, false));
      this.field_70714_bg.func_75776_a(6, new MoveThroughVillageGoal(this, 1.1, true, 4, this::func_146072_bX));
      this.field_70714_bg.func_75776_a(7, new WaterAvoidingRandomWalkingGoal(this, 1.1));
      this.field_70715_bh.func_75776_a(2, new FollowEntityGoal(this, 1.1, 32.0F, 3.0F, false, () -> this.getOwner().right()));
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getLocationSkin() {
      return this.skin.getLocationSkin();
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
   }

   public void setSkinName(String skinName) {
      this.field_70180_af.func_187227_b(ETERNAL_NAME, skinName);
   }

   public String getSkinName() {
      return (String)this.field_70180_af.func_187225_a(ETERNAL_NAME);
   }

   public void setAncient(boolean ancient) {
      this.ancient = ancient;
   }

   public boolean isAncient() {
      return this.ancient;
   }

   public void setEternalId(UUID eternalId) {
      this.eternalId = eternalId;
   }

   public UUID getEternalId() {
      return this.eternalId;
   }

   public void setProvidedAura(String providedAura) {
      this.providedAura = providedAura;
   }

   public String getProvidedAura() {
      return this.providedAura;
   }

   public void setDespawnTime(long despawnTime) {
      this.despawnTime = despawnTime;
   }

   public boolean func_70631_g_() {
      return false;
   }

   protected boolean func_190730_o() {
      return false;
   }

   protected void func_207302_dI() {
   }

   protected boolean func_204703_dA() {
      return false;
   }

   public boolean func_70662_br() {
      return false;
   }

   public EntityClassification getClassification(boolean forSpawnCount) {
      return EntityClassification.MONSTER;
   }

   public Either<UUID, ServerPlayerEntity> getOwner() {
      if (this.field_70170_p.func_201670_d()) {
         return Either.left(this.owner);
      } else {
         ServerPlayerEntity player = this.func_184102_h().func_184103_al().func_177451_a(this.owner);
         return player == null ? Either.left(this.owner) : Either.right(player);
      }
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p instanceof ServerWorld) {
         ServerWorld sWorld = (ServerWorld)this.field_70170_p;
         int tickCounter = sWorld.func_73046_m().func_71259_af();
         if (tickCounter < this.despawnTime) {
            ActiveEternalData.getInstance().updateEternal(this);
         }

         if (this.field_70729_aU) {
            return;
         }

         if (tickCounter >= this.despawnTime) {
            this.func_174812_G();
         }

         double amplitude = this.func_213322_ci().func_186679_c(0.0, this.func_213322_ci().func_82617_b(), 0.0);
         if (amplitude > 0.004) {
            this.func_70031_b(true);
         } else {
            this.func_70031_b(false);
         }

         this.bossInfo.func_186735_a(this.func_110143_aJ() / this.func_110138_aP());
         if (this.field_70173_aa % 10 == 0) {
            this.updateAttackTarget();
         }

         if (this.providedAura != null && this.field_70173_aa % 4 == 0) {
            this.getOwner().ifRight(sPlayer -> {
               EternalAuraConfig.AuraConfig auraCfg = ModConfigs.ETERNAL_AURAS.getByName(this.providedAura);
               if (auraCfg != null) {
                  AuraManager.getInstance().provideAura(EntityAuraProvider.ofEntity(this, auraCfg));
               }
            });
         }

         Map<Effect, EffectTalent.CombinedEffects> combinedEffects = EffectTalent.getGearEffectData(this);
         EffectTalent.applyEffects(this, combinedEffects);
      } else {
         if (this.field_70729_aU) {
            return;
         }

         if (!Objects.equals(this.getSkinName(), this.skin.getLatestNickname())) {
            this.skin.updateSkin(this.getSkinName());
         }
      }
   }

   protected void func_70609_aI() {
      super.func_70609_aI();
   }

   public void func_70624_b(LivingEntity entity) {
      if (entity != this.getOwner().right().orElse(null) && !(entity instanceof EternalEntity) && !(entity instanceof PlayerEntity)) {
         super.func_70624_b(entity);
      }
   }

   public void func_70604_c(LivingEntity entity) {
      if (entity != this.getOwner().right().orElse(null) && !(entity instanceof EternalEntity) && !(entity instanceof PlayerEntity)) {
         super.func_70604_c(entity);
      }
   }

   private void updateAttackTarget() {
      AxisAlignedBB box = this.func_174813_aQ().func_186662_g(32.0);
      this.field_70170_p.func_225316_b(LivingEntity.class, box, e -> {
         Either<UUID, ServerPlayerEntity> owner = this.getOwner();
         return owner.right().isPresent() && owner.right().get() == e ? false : !(e instanceof EternalEntity) && !(e instanceof PlayerEntity);
      }).stream().sorted(Comparator.comparingDouble(e -> e.func_213303_ch().func_72438_d(this.func_213303_ch()))).findFirst().ifPresent(this::func_70624_b);
   }

   private Predicate<LivingEntity> ignoreEntities() {
      return e -> !(e instanceof EternalEntity) && !(e instanceof PlayerEntity);
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

   public void func_213281_b(CompoundNBT tag) {
      super.func_213281_b(tag);
      tag.func_74757_a("ancient", this.ancient);
      tag.func_74776_a("SizeMultiplier", this.sizeMultiplier);
      tag.func_74772_a("DespawnTime", this.despawnTime);
      if (this.providedAura != null) {
         tag.func_74778_a("providedAura", this.providedAura);
      }

      if (this.owner != null) {
         tag.func_74778_a("Owner", this.owner.toString());
      }

      if (this.eternalId != null) {
         tag.func_74778_a("eternalId", this.eternalId.toString());
      }
   }

   public void func_70037_a(CompoundNBT tag) {
      super.func_70037_a(tag);
      this.ancient = tag.func_74767_n("ancient");
      this.sizeMultiplier = tag.func_74760_g("SizeMultiplier");
      this.changeSize(this.sizeMultiplier);
      this.despawnTime = tag.func_74763_f("DespawnTime");
      if (tag.func_150297_b("providedAura", 8)) {
         this.providedAura = tag.func_74779_i("providedAura");
      }

      if (tag.func_150297_b("Owner", 8)) {
         this.owner = UUID.fromString(tag.func_74779_i("Owner"));
      }

      if (tag.func_150297_b("eternalId", 8)) {
         this.eternalId = UUID.fromString(tag.func_74779_i("eternalId"));
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

   public EternalEntity changeSize(float m) {
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

   public void func_70645_a(DamageSource cause) {
      super.func_70645_a(cause);
      if (this.field_70170_p instanceof ServerWorld && this.field_70729_aU && this.owner != null && this.eternalId != null && !cause.func_76357_e()) {
         ServerWorld sWorld = (ServerWorld)this.field_70170_p;
         TalentTree tree = PlayerTalentsData.get(sWorld).getTalents(this.owner);
         if (tree.hasLearnedNode(ModConfigs.TALENTS.COMMANDER)) {
            return;
         }

         EternalData eternal = EternalsData.get(sWorld).getEternal(this.eternalId);
         if (eternal != null) {
            eternal.setAlive(false);
         }
      }
   }

   public ILivingEntityData func_213386_a(
      IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag
   ) {
      this.func_200203_b(this.func_200201_e());
      this.func_146070_a(true);
      this.func_98053_h(false);
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

   public boolean func_70097_a(DamageSource source, float amount) {
      Entity src = source.func_76346_g();
      if (src instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)src;
         if (player.func_110124_au().equals(this.owner)) {
            return false;
         }
      }

      return super.func_70097_a(source, amount);
   }

   public boolean func_70652_k(Entity entity) {
      if (!this.field_70170_p.func_201670_d() && this.field_70170_p instanceof ServerWorld) {
         ServerWorld sWorld = (ServerWorld)this.field_70170_p;
         sWorld.func_195598_a(ParticleTypes.field_197603_N, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), 1, 0.0, 0.0, 0.0, 0.0);
         this.field_70170_p
            .func_184133_a(
               null,
               this.func_233580_cy_(),
               SoundEvents.field_187730_dW,
               SoundCategory.PLAYERS,
               1.0F,
               this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()
            );
         if (entity instanceof LivingEntity) {
            this.getOwner()
               .ifRight(
                  owner -> {
                     AbilityTree abilityData = PlayerAbilitiesData.get(sWorld).getAbilities(owner);
                     AbilityNode<?, ?> eternalsNode = abilityData.getNodeByName("Summon Eternal");
                     if ("Summon Eternal_Debuffs".equals(eternalsNode.getSpecialization())) {
                        SummonEternalDebuffConfig cfg = (SummonEternalDebuffConfig)eternalsNode.getAbilityConfig();
                        if (this.field_70146_Z.nextFloat() < cfg.getApplyDebuffChance()) {
                           Effect debuff = MiscUtils.eitherOf(
                              this.field_70146_Z, Effects.field_76436_u, Effects.field_82731_v, Effects.field_76421_d, Effects.field_76419_f
                           );
                           ((LivingEntity)entity).func_195064_c(new EffectInstance(debuff, cfg.getDebuffDurationTicks(), cfg.getDebuffAmplifier()));
                        }
                     }
                  }
               );
         }
      }

      return DamageUtil.shotgunAttackApply((LivingEntity & Entity)entity, x$0 -> super.func_70652_k(x$0));
   }
}
