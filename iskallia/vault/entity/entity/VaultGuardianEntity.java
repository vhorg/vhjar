package iskallia.vault.entity.entity;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.entity.entity.guardian.CrossbowHelper;
import iskallia.vault.entity.entity.guardian.FixedArrowEntity;
import iskallia.vault.entity.entity.guardian.GuardianStats;
import iskallia.vault.entity.entity.guardian.GuardianType;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.effect.sub.NovaDotAbility;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class VaultGuardianEntity extends Piglin {
   private GuardianType type;
   private GuardianStats stats = new GuardianStats.Empty();

   public VaultGuardianEntity(EntityType<? extends Piglin> entityType, GuardianType type, Level world) {
      super(entityType, world);
      this.type = type;
      this.setImmuneToZombification(true);
      this.setCanPickUpLoot(false);
      this.setPersistenceRequired();
   }

   public void setType(GuardianType type) {
      this.type = type;
   }

   public boolean canAttack(LivingEntity target) {
      return true;
   }

   public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
      return InteractionResult.PASS;
   }

   public void performCrossbowAttack(LivingEntity entity, float distance) {
      InteractionHand interactionhand = ProjectileUtil.getWeaponHoldingHand(entity, item -> item instanceof CrossbowItem);
      ItemStack itemstack = entity.getItemInHand(interactionhand).copy();
      ListTag list = new ListTag();
      list.add(new ItemStack(Items.ARROW).serializeNBT());
      itemstack.getOrCreateTag().put("ChargedProjectiles", list);
      if (entity.isHolding(is -> is.getItem() instanceof CrossbowItem)) {
         CrossbowHelper.performShooting(entity.level, entity, interactionhand, itemstack, 2.2F, 0.0F);
      }

      this.onCrossbowAttackPerformed();
   }

   public void shootCrossbowProjectile(LivingEntity p_32323_, LivingEntity p_32324_, Projectile p_32325_, float p_32326_, float p_32327_) {
      ((FixedArrowEntity)p_32325_).setBaseDamage(this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
      super.shootCrossbowProjectile(p_32323_, p_32324_, p_32325_, p_32326_, p_32327_);
   }

   protected void dropFromLootTable(DamageSource source, boolean attackedRecently) {
   }

   public void tick() {
      if (!this.level.isClientSide) {
         ServerVaults.get(this.level).ifPresent(vault -> {
            if (this.type != null) {
               this.stats = ModConfigs.VAULT_GUARDIAN.get(vault.get(Vault.LEVEL).get(), this.type);
            }
         });
      }

      super.tick();
      if (!this.level.isClientSide) {
         this.stats.onTick(this);
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      if (!(source instanceof NovaDotAbility.PlayerDamageOverTimeSource)
         && !(source.getEntity() instanceof Player)
         && !(source.getEntity() instanceof EternalEntity)) {
         return false;
      } else if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         this.stats.onHurt(this, source, amount);
         return super.hurt(source, amount);
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return super.isInvulnerableTo(source) || source.isProjectile() || source == DamageSource.FALL;
   }

   public void knockback(double p_147241_, double p_147242_, double p_147243_) {
   }

   public boolean doHurtTarget(Entity pEntity) {
      float f = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
      float f1 = (float)this.getAttribute(Attributes.ATTACK_KNOCKBACK).getBaseValue();
      int i = EnchantmentHelper.getFireAspect(this);
      if (i > 0) {
         pEntity.setSecondsOnFire(i * 4);
      }

      boolean flag = pEntity.hurt(DamageSource.mobAttack(this), f);
      if (flag) {
         if (f1 > 0.0F && pEntity instanceof LivingEntity) {
            ((LivingEntity)pEntity)
               .knockback(f1 * 0.5F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
         }

         this.doEnchantDamageEffects(this, pEntity);
         this.setLastHurtMob(pEntity);
      }

      return flag;
   }

   public void readAdditionalSaveData(CompoundTag nbt) {
      super.readAdditionalSaveData(nbt);
      this.type = GuardianType.values()[nbt.getInt("Type")];
   }

   public void addAdditionalSaveData(CompoundTag nbt) {
      super.addAdditionalSaveData(nbt);
      nbt.putInt("Type", this.type.ordinal());
   }
}
