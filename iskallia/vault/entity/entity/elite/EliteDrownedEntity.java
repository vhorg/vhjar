package iskallia.vault.entity.entity.elite;

import iskallia.vault.entity.champion.OnHitApplyPotionAffix;
import iskallia.vault.init.ModEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.Drowned.DrownedAttackGoal;
import net.minecraft.world.entity.monster.Drowned.DrownedTridentAttackGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class EliteDrownedEntity extends Drowned {
   private final OnHitApplyPotionAffix manaStealOnHit = new OnHitApplyPotionAffix("Mana Stealing", ModEffects.MANA_STEAL, 40, 1, 1.0F);
   private long lastRangedAttackTime;
   private DrownedAttackGoal meleeAttack;

   public EliteDrownedEntity(EntityType<? extends Drowned> entityType, Level world) {
      super(entityType, world);
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(1, new EliteDrownedEntity.PutTridentInHandGoal(this, 80, 120));
      this.goalSelector.addGoal(2, new DrownedTridentAttackGoal(this, 1.0, 20, 10.0F));
      this.meleeAttack = new DrownedAttackGoal(this, 1.0, false);
      this.goalSelector.addGoal(3, this.meleeAttack);
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{Drowned.class}).setAlertOthers(new Class[]{ZombifiedPiglin.class}));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::okTarget));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Axolotl.class, true, false));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public boolean canBeAffected(MobEffectInstance potionEffect) {
      return potionEffect.getEffect() == ModEffects.GLACIAL_SHATTER ? false : super.canBeAffected(potionEffect);
   }

   @SubscribeEvent
   public static void onEntityAttack(LivingAttackEvent event) {
      if (event.getSource().getEntity() instanceof EliteDrownedEntity eliteDrowned && event.getEntityLiving() instanceof Player player) {
         eliteDrowned.manaStealOnHit.onChampionHitPlayer(eliteDrowned, player, event.getAmount());
      }
   }

   public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
      super.performRangedAttack(pTarget, pDistanceFactor);
      this.lastRangedAttackTime = this.level.getGameTime();
   }

   static class PutTridentInHandGoal extends Goal {
      private final EliteDrownedEntity drowned;
      private final int triggerIntervalMin;
      private final int triggerIntervalMax;
      private long nextTriggerTime;
      private boolean putTridentInHand;

      public PutTridentInHandGoal(EliteDrownedEntity drowned, int triggerIntervalMin, int triggerIntervalMax) {
         this.drowned = drowned;
         this.triggerIntervalMin = triggerIntervalMin;
         this.triggerIntervalMax = triggerIntervalMax;
      }

      public boolean canUse() {
         return true;
      }

      public void start() {
         this.nextTriggerTime = this.drowned.level.getGameTime()
            + this.drowned.getRandom().nextInt(this.triggerIntervalMax - this.triggerIntervalMin)
            + this.triggerIntervalMin;
      }

      public void tick() {
         if (!this.putTridentInHand && this.nextTriggerTime <= this.drowned.level.getGameTime()) {
            this.putTridentInHand = true;
            this.drowned.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.TRIDENT));
            this.drowned.meleeAttack.stop();
         } else if (this.putTridentInHand && this.drowned.lastRangedAttackTime > this.nextTriggerTime) {
            this.putTridentInHand = false;
            this.drowned.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            this.nextTriggerTime = this.drowned.level.getGameTime()
               + this.drowned.getRandom().nextInt(this.triggerIntervalMax - this.triggerIntervalMin)
               + this.triggerIntervalMin;
         }
      }
   }
}
