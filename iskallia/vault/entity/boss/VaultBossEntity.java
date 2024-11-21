package iskallia.vault.entity.boss;

import iskallia.vault.config.VaultBossConfig;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.VaultBoss;
import iskallia.vault.entity.boss.attack.IMeleeAttack;
import iskallia.vault.entity.boss.trait.IOnHitEffect;
import iskallia.vault.entity.boss.trait.ITrait;
import iskallia.vault.entity.boss.trait.VaultBossTraitRegistry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundBossSyncTraitsMessage;
import iskallia.vault.util.LootInitialization;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

@EventBusSubscriber
public class VaultBossEntity extends VaultBossBaseEntity implements IAnimatable, VaultBoss {
   private VaultBossEntity.CompositeGoal compositeGoal = new VaultBossEntity.CompositeGoal(this);
   private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);
   private boolean bossInitialized = false;
   private Set<IOnHitEffect> onHitEffects = new HashSet<>();
   private Map<String, ITrait> traits = new LinkedHashMap<>();
   private List<ItemStack> loot = new ArrayList<>();

   @Override
   public Map<String, BiFunction<VaultBossBaseEntity, Double, IMeleeAttack>> getMeleeAttackFactories() {
      return Map.of();
   }

   public VaultBossEntity(EntityType<? extends Monster> entityType, Level level) {
      super(entityType, level);
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.FOLLOW_RANGE, 18.0).add(Attributes.MAX_HEALTH, 32.0);
   }

   @Override
   public WeightedList<VaultBossBaseEntity.AttackData> getMeleeAttacks() {
      return WeightedList.empty();
   }

   @Override
   public WeightedList<VaultBossBaseEntity.AttackData> getRageAttacks() {
      return WeightedList.empty();
   }

   @Override
   public double getAttackReach() {
      return this.getBbWidth() * 2.0;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
   }

   @Override
   public ServerBossEvent getServerBossInfo() {
      return new ServerBossEvent(this.getDisplayName(), BossBarColor.RED, BossBarOverlay.PROGRESS);
   }

   public void registerControllers(AnimationData data) {
   }

   public AnimationFactory getFactory() {
      return this.animationFactory;
   }

   public void tick() {
      if (!this.bossInitialized) {
         if (!this.level.isClientSide) {
            this.goalSelector.addGoal(3, this.compositeGoal);
         }

         this.bossInitialized = true;
      }

      super.tick();
   }

   private void addTrait(VaultBossConfig.TraitDefinition traitDefinition, String type, CompoundTag attributesNbt) {
      VaultBossTraitRegistry.createTrait(type, this, attributesNbt).ifPresent(trait -> {
         if (this.traits.containsKey(traitDefinition.id())) {
            this.traits.get(traitDefinition.id()).addStack(trait);
         } else {
            this.traits.put(traitDefinition.id(), trait);
            trait.apply(this);
         }
      });
   }

   public void startSeenByPlayer(ServerPlayer serverPlayer) {
      super.startSeenByPlayer(serverPlayer);
      ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ClientboundBossSyncTraitsMessage(this.getId(), this.traits));
   }

   protected void tickDeath() {
      super.tickDeath();
      this.compositeGoal.stop();
   }

   public void addOnHitEffect(IOnHitEffect effect) {
      this.onHitEffects.add(effect);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag nbt) {
      super.addAdditionalSaveData(nbt);
      ListTag traitsNbt = new ListTag();
      this.traits.forEach((id, trait) -> {
         CompoundTag traitNbt = trait.serializeNBT();
         traitNbt.putString("Id", id);
         traitNbt.putString("Type", trait.getType());
         traitsNbt.add(traitNbt);
      });
      nbt.put("Traits", traitsNbt);
      ListTag itemsNbt = new ListTag();
      this.loot.forEach(stack -> itemsNbt.add(stack.save(new CompoundTag())));
      nbt.put("Loot", itemsNbt);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag nbt) {
      super.readAdditionalSaveData(nbt);
      this.compositeGoal.clear();
      ListTag traitsNbt = nbt.getList("Traits", 10);
      this.traits.clear();
      traitsNbt.forEach(tag -> {
         CompoundTag traitNbt = (CompoundTag)tag;
         VaultBossTraitRegistry.createTrait(traitNbt.getString("Type"), this, traitNbt).ifPresent(trait -> {
            String id = traitNbt.getString("Id");
            this.traits.put(id, trait);
            if (this.level != null && !this.level.isClientSide) {
               trait.apply(this);
            }
         });
      });
      ListTag lootNbt = nbt.getList("Loot", 10);
      this.loot.clear();
      lootNbt.forEach(tag -> this.loot.add(ItemStack.of((CompoundTag)tag)));
   }

   private void runOnHitEffects(Player playerHit, float damage) {
      this.onHitEffects.forEach(effect -> effect.onHit(this, playerHit, damage));
   }

   @SubscribeEvent
   public static void onEntityAttack(LivingAttackEvent event) {
      if (event.getSource().getEntity() instanceof VaultBossEntity boss && event.getEntityLiving() instanceof Player player) {
         boss.runOnHitEffects(player, event.getAmount());
      }
   }

   public Collection<ITrait> getTraits() {
      return this.traits.values();
   }

   public void addTraitGoal(Goal goal) {
      this.compositeGoal.addGoal(goal);
   }

   public void setTraits(Map<String, ITrait> traits) {
      this.traits = traits;
   }

   protected void dropAllDeathLoot(DamageSource pDamageSource) {
      super.dropAllDeathLoot(pDamageSource);
      this.loot.forEach(d -> ServerVaults.get(this.level).ifPresent(vault -> {
         ItemStack drop = LootInitialization.initializeVaultLoot(d, vault, this.blockPosition());
         this.spawnAtLocation(drop);
      }));
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag
   ) {
      this.addTraits(Collections.emptyMap());
      return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
   }

   public void setLoot(List<ItemStack> loot) {
      this.loot = loot;
   }

   public void addTraits(Map<String, Integer> modifiers) {
      ModConfigs.VAULT_BOSS.getBossBaseTraits(this.getType().getRegistryName()).forEach(this::addTrait);
      ModNetwork.CHANNEL
         .send(PacketDistributor.DIMENSION.with(() -> this.getLevel().dimension()), new ClientboundBossSyncTraitsMessage(this.getId(), this.traits));
      Map<String, VaultBossConfig.TraitDefinition> modifierTraits = ModConfigs.VAULT_BOSS
         .getBossModifierTraits(this.getType().getRegistryName(), modifiers.keySet());
      modifiers.forEach((modifierName, count) -> {
         VaultBossConfig.TraitDefinition traitDefinition = modifierTraits.get(modifierName);
         if (traitDefinition != null) {
            for (int i = 0; i < count; i++) {
               this.addTrait(traitDefinition);
            }
         }
      });
   }

   private void addTrait(VaultBossConfig.TraitDefinition traitDefinition) {
      VaultBossTraitRegistry.createTrait(traitDefinition.type(), this, traitDefinition.attributesNbt()).ifPresent(trait -> {
         if (this.traits.containsKey(traitDefinition.id())) {
            this.traits.get(traitDefinition.id()).addStack(trait);
         } else {
            this.traits.put(traitDefinition.id(), trait);
            trait.apply(this);
         }
      });
   }

   private static class CompositeGoal extends Goal {
      private final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
      private final VaultBossEntity boss;
      private final Set<WrappedGoal> goals = new HashSet<>();
      private int currentMaxPriority = 1;

      public CompositeGoal(VaultBossEntity boss) {
         this.boss = boss;
      }

      public void addGoal(Goal goal) {
         WrappedGoal wrappedGoal = new WrappedGoal(this.currentMaxPriority, goal);
         if (!this.goals.contains(wrappedGoal)) {
            this.currentMaxPriority++;
            this.goals.add(wrappedGoal);
            this.flags.addAll(goal.getFlags());
         }
      }

      public boolean canUse() {
         for (Goal goal : this.goals) {
            if (goal.canUse()) {
               return true;
            }
         }

         return false;
      }

      public boolean canContinueToUse() {
         for (Goal goal : this.goals) {
            if (goal.canContinueToUse()) {
               return true;
            }
         }

         return false;
      }

      public void start() {
         this.goals.forEach(goal -> {
            if (!goal.isRunning() && goal.canUse()) {
               goal.start();
            }
         });
      }

      public void stop() {
         this.goals.forEach(goal -> {
            if (goal.isRunning()) {
               goal.stop();
            }
         });
      }

      public void tick() {
         this.goals.forEach(goal -> {
            if (!goal.isRunning() && goal.canUse()) {
               goal.start();
            }

            if (goal.isRunning() && !goal.canContinueToUse()) {
               goal.stop();
            }

            if (goal.isRunning()) {
               goal.tick();
            }
         });
      }

      public EnumSet<Flag> getFlags() {
         return this.flags;
      }

      public boolean requiresUpdateEveryTick() {
         for (Goal goal : this.goals) {
            if (goal.requiresUpdateEveryTick()) {
               return true;
            }
         }

         return false;
      }

      public void clear() {
         this.goals.forEach(WrappedGoal::stop);
         this.goals.clear();
      }
   }
}
