package iskallia.vault.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.util.calc.ResistanceHelper;
import iskallia.vault.world.data.ServerVaults;
import javax.annotation.Nullable;
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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
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
      method = {"getTicksUsingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getTicksUsingItem(CallbackInfoReturnable<Integer> ci) {
      if (this.useItem.getItem() == Items.CROSSBOW && this.isUsingItem()) {
         AttributeInstance attribute = this.getAttribute(ModAttributes.CROSSBOW_CHARGE_TIME);
         if (attribute != null) {
            int value = this.useItem.getUseDuration() - this.getUseItemRemainingTicks();
            ci.setReturnValue((int)(this.useItem.getUseDuration() / attribute.getValue() * value));
         }
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

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   public void tick(CallbackInfo ci) {
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
}
