package iskallia.vault.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.entity.entity.FighterEntity;
import iskallia.vault.entity.entity.VaultGuardianEntity;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.util.calc.ResistanceHelper;
import iskallia.vault.world.data.ServerVaults;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class MixinLivingEntity extends Entity {
   private float prevSize = -1.0F;
   @Shadow
   protected ItemStack useItem;
   @Shadow
   public long lastDamageStamp;
   @Shadow
   @Final
   private AttributeMap attributes;
   private float rawHealth;
   private boolean useRawHealth;

   public MixinLivingEntity(EntityType<?> entityType, Level world) {
      super(entityType, world);
   }

   @Shadow
   public abstract EntityDimensions getDimensions(Pose var1);

   @Shadow
   public abstract boolean hasEffect(MobEffect var1);

   @Shadow
   @Nullable
   public abstract MobEffectInstance getEffect(MobEffect var1);

   @Shadow
   @Nullable
   public abstract AttributeInstance getAttribute(Attribute var1);

   @Shadow
   public abstract ItemStack getItemInHand(InteractionHand var1);

   @Shadow
   public abstract boolean isUsingItem();

   @Shadow
   public abstract int getUseItemRemainingTicks();

   @Shadow
   public abstract float getMaxHealth();

   @Shadow
   public abstract float getHealth();

   @Shadow
   public abstract void setHealth(float var1);

   @Shadow
   protected abstract void hurtArmor(DamageSource var1, float var2);

   @Shadow
   public abstract double getAttributeValue(Attribute var1);

   @Shadow
   public abstract AttributeMap getAttributes();

   @Redirect(
      method = {"createLivingAttributes"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier;builder()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"
      )
   )
   private static Builder registerAttributes() {
      return AttributeSupplier.builder()
         .add(Attributes.MAX_HEALTH)
         .add(Attributes.KNOCKBACK_RESISTANCE)
         .add(Attributes.MOVEMENT_SPEED)
         .add(Attributes.ARMOR)
         .add(Attributes.ARMOR_TOUGHNESS)
         .add((Attribute)ForgeMod.SWIM_SPEED.get())
         .add((Attribute)ForgeMod.NAMETAG_DISTANCE.get())
         .add((Attribute)ForgeMod.ENTITY_GRAVITY.get())
         .add(ModAttributes.CRIT_CHANCE)
         .add(ModAttributes.CRIT_MULTIPLIER)
         .add(ModAttributes.TP_CHANCE)
         .add(ModAttributes.TP_INDIRECT_CHANCE)
         .add(ModAttributes.TP_RANGE)
         .add(ModAttributes.POTION_RESISTANCE)
         .add(ModAttributes.SIZE_SCALE)
         .add(ModAttributes.BREAK_ARMOR_CHANCE)
         .add(ModAttributes.MANA_MAX)
         .add(ModAttributes.MANA_REGEN)
         .add(ModAttributes.REACH)
         .add(ModAttributes.CROSSBOW_CHARGE_TIME)
         .add(ModAttributes.THORNS_CHANCE)
         .add(ModAttributes.THORNS_DAMAGE);
   }

   @Inject(
      method = {"getAttributeValue"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getAttributeValue(Attribute attribute, CallbackInfoReturnable<Double> cir) {
      if (attribute == ForgeMod.REACH_DISTANCE.get()) {
         if (this.level.isClientSide() && ClientVaults.getActive().isPresent()) {
            cir.setReturnValue(Math.min(this.getAttributes().getValue(attribute), 7.0));
         } else if (ServerVaults.get(this.level).isPresent()) {
            cir.setReturnValue(Math.min(this.getAttributes().getValue(attribute), 7.0));
         }
      }
   }

   @Redirect(
      method = {"die"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;hasCustomName()Z"
      )
   )
   public boolean removeFighterKilledLogMessage(LivingEntity entity) {
      return entity instanceof FighterEntity ? false : entity.hasCustomName();
   }

   @Inject(
      method = {"getTicksUsingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getTicksUsingItem(CallbackInfoReturnable<Integer> ci) {
      if (this.useItem.getItem() == Items.CROSSBOW && this.isUsingItem()) {
         AttributeInstance attribute = this.getAttribute(ModAttributes.CROSSBOW_CHARGE_TIME);
         if (attribute != null && attribute.getValue() > 0.0) {
            int value = this.useItem.getUseDuration() - this.getUseItemRemainingTicks();
            ci.setReturnValue((int)(this.useItem.getUseDuration() / attribute.getValue() * value));
         }
      }
   }

   @Inject(
      method = {"handleEntityEvent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void exitIfDamageAlreadyHandled(byte pId, CallbackInfo ci) {
      if (pId == 2 && this.lastDamageStamp == this.level.getGameTime()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"getDamageAfterMagicAbsorb"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void applyResistance(DamageSource pSource, float pDamage, CallbackInfoReturnable<Float> cir) {
      LivingEntity entity = (LivingEntity)this;
      if (AttributeSnapshotHelper.canHaveSnapshot(entity)) {
         float resistance = ResistanceHelper.getResistance(entity);
         if (resistance > 1.0E-4) {
            float damage = (Float)cir.getReturnValue();
            cir.setReturnValue(Math.max(damage - damage * resistance, 0.0F));
         }
      }
   }

   @Redirect(
      method = {"getDamageAfterArmorAbsorb"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;hurtArmor(Lnet/minecraft/world/damagesource/DamageSource;F)V"
      )
   )
   public void preventHurtArmor(LivingEntity entity, DamageSource src, float damage) {
      if (!(src.getEntity() instanceof VaultGuardianEntity)) {
         this.hurtArmor(src, damage);
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   public void tick(CallbackInfo ci) {
      if (this.useRawHealth && this.rawHealth > 0.0F && this.rawHealth > this.getHealth()) {
         this.setHealth(this.rawHealth);
         this.useRawHealth = false;
      }

      AttributeInstance scale = this.getAttribute(ModAttributes.SIZE_SCALE);
      if (scale != null) {
         if (this.prevSize != scale.getValue()) {
            this.prevSize = (float)scale.getValue();
            this.dimensions = this.getDimensions(Pose.STANDING).scale(this.prevSize);
            this.refreshDimensions();
         }
      }
   }

   @Inject(
      method = {"addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void addPotionEffect(MobEffectInstance effect, CallbackInfoReturnable<Boolean> ci) {
      AttributeInstance attribute = this.getAttribute(ModAttributes.POTION_RESISTANCE);
      if (attribute != null) {
         if (!(this.random.nextDouble() >= attribute.getValue())) {
            ci.setReturnValue(false);
         }
      }
   }

   @Redirect(
      method = {"collectEquipmentChanges"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"
      )
   )
   private Multimap<Attribute, AttributeModifier> preventVanillaAttributes(ItemStack stack, EquipmentSlot slot) {
      LivingEntity entity = (LivingEntity)this;
      if (stack.getItem() instanceof VaultGearItem && entity instanceof Player player) {
         int playerLevel = SidedHelper.getVaultLevel(player);
         if (VaultGearData.read(stack).getItemLevel() > playerLevel) {
            return HashMultimap.create();
         }
      }

      return stack.getAttributeModifiers(slot);
   }

   @Inject(
      method = {"checkTotemDeathProtection"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void checkTotemDeathProtection(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
      if (ServerVaults.get(this.level).isPresent()) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"},
      at = {@At("HEAD")}
   )
   private void readAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
      if (nbt.contains("Health", 99)) {
         float health = nbt.getFloat("Health");
         if (health > this.getMaxHealth() && health > 0.0F) {
            this.rawHealth = health;
            this.useRawHealth = true;
         }
      }
   }
}
