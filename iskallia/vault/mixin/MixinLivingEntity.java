package iskallia.vault.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import iskallia.vault.client.render.IVaultOptions;
import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.core.vault.modifier.modifier.EntityEffectModifier;
import iskallia.vault.entity.champion.ChampionLogic;
import iskallia.vault.entity.entity.FighterEntity;
import iskallia.vault.entity.entity.guardian.AbstractGuardianEntity;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.WingsTrinket;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.util.calc.ResistanceHelper;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
public abstract class MixinLivingEntity extends Entity implements ChampionLogic.IChampionLogicHolder, EntityEffectModifier.ILivingEntityAccessor {
   private float prevSize = -1.0F;
   @Nullable
   private ChampionLogic championLogic = new ChampionLogic();
   private Map<MobEffectInstance, Double> scheduledEffects = new HashMap<>();
   @Shadow
   protected ItemStack useItem;
   @Shadow
   public long lastDamageStamp;
   @Shadow
   protected int fallFlyTicks;
   @Shadow
   @Final
   private AttributeMap attributes;
   private float rawHealth;
   private boolean useRawHealth;

   @Override
   public ChampionLogic getChampionLogic() {
      return this.championLogic;
   }

   public MixinLivingEntity(EntityType<?> entityType, Level world) {
      super(entityType, world);
   }

   @Override
   public Map<MobEffectInstance, Double> getEffects() {
      return this.scheduledEffects;
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
         .add(ModAttributes.DURABILITY_WEAR_REDUCTION_CAP)
         .add(ModAttributes.POTION_RESISTANCE)
         .add(ModAttributes.SIZE_SCALE)
         .add(ModAttributes.BREAK_ARMOR_CHANCE)
         .add(ModAttributes.MANA_MAX)
         .add(ModAttributes.MANA_REGEN)
         .add(ModAttributes.REACH)
         .add(ModAttributes.CROSSBOW_CHARGE_TIME)
         .add(ModAttributes.BOW_CHARGE_TIME)
         .add(ModAttributes.THORNS_CHANCE)
         .add(ModAttributes.THORNS_DAMAGE)
         .add(ModAttributes.MANA_SHIELD);
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
      } else if (this.useItem.getItem() == Items.BOW && this.isUsingItem()) {
         AttributeInstance attribute = this.getAttribute(ModAttributes.BOW_CHARGE_TIME);
         if (attribute != null && attribute.getValue() > 0.0) {
            int value = this.useItem.getUseDuration() - this.getUseItemRemainingTicks();
            ci.setReturnValue((int)(20.0 / attribute.getValue() * value));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Inject(
      method = {"handleEntityEvent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void exitIfDamageAlreadyHandled(byte pId, CallbackInfo ci) {
      if (pId == 2 && this.lastDamageStamp == this.level.getGameTime() && !((IVaultOptions)Minecraft.getInstance().options).doVanillaPotionDamageEffects()) {
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
         float resistance = ResistanceHelper.getResistance(entity, pSource.getEntity() instanceof LivingEntity living ? living : null);
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
      if (!(src.getEntity() instanceof AbstractGuardianEntity)) {
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

         this.championLogic.tick(this);
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
         target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
      )
   )
   private ItemStack getBySlot(LivingEntity entity, EquipmentSlot slot) {
      ItemStack stack = entity.getItemBySlot(slot);
      if (entity instanceof Player player && stack.getItem() instanceof VaultGearItem) {
         ItemStack resultCopy = stack.copy();
         VaultGearData data = VaultGearData.read(resultCopy);
         boolean preventModifiers = false;
         int playerLevel = SidedHelper.getVaultLevel(player);
         if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            preventModifiers = true;
         }

         if (VaultGearData.read(stack).getItemLevel() > playerLevel) {
            preventModifiers = true;
         }

         if (preventModifiers) {
            for (VaultGearModifier.AffixType type : VaultGearModifier.AffixType.values()) {
               List<VaultGearModifier<?>> modifiers = new ArrayList<>(data.getModifiers(type));
               modifiers.forEach(data::removeModifier);
            }

            data.writeUnchanged(resultCopy);
            return resultCopy;
         }
      }

      return stack;
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
      method = {"Lnet/minecraft/world/entity/LivingEntity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"},
      at = {@At("HEAD")}
   )
   private void addAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
      this.championLogic.serialize().ifPresent(championNbt -> nbt.put("championLogic", championNbt));
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

      if (nbt.contains("championLogic", 10)) {
         this.championLogic = ChampionLogic.deserialize(nbt.getCompound("championLogic"));
      }

      if (nbt.contains("Modifiers", 10)) {
         CompoundTag modifiers = nbt.getCompound("Modifiers");

         for (String key : modifiers.getAllKeys()) {
            Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(key)).ifPresent(attribute -> {
               AttributeInstance instance = this.getAttribute(attribute);
               if (instance != null) {
                  CompoundTag entry = modifiers.getCompound(key);
                  double amount = entry.getDouble("Value");
                  Operation operation = Operation.fromValue(entry.getInt("Operation"));
                  instance.addPermanentModifier(new AttributeModifier(UUID.randomUUID(), "Unspecified", amount, operation));
               }
            });
         }
      }
   }

   @Inject(
      method = {"updateFallFlying"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getFlag(CallbackInfo ci) {
      LivingEntity livingEntity = (LivingEntity)this;
      ItemStack chestStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
      if (livingEntity instanceof Player player && player.getCooldowns().isOnCooldown(chestStack.getItem())) {
         if (!this.level.isClientSide) {
            this.setSharedFlag(7, false);
         }

         ci.cancel();
      } else {
         AtomicBoolean flag = new AtomicBoolean(this.getSharedFlag(7));
         if (flag.get() && !this.onGround && !this.isPassenger() && !this.hasEffect(MobEffects.LEVITATION)) {
            flag.set(chestStack.canElytraFly(livingEntity) && chestStack.elytraFlightTick(livingEntity, this.fallFlyTicks));
            if (livingEntity instanceof Player player) {
               TrinketHelper.getTrinkets(player, WingsTrinket.class).forEach(wings -> {
                  if (wings.isUsable(player)) {
                     flag.set(true);
                  }
               });
            }
         }

         if (!this.level.isClientSide) {
            this.setSharedFlag(7, flag.get());
         }

         ci.cancel();
      }
   }
}
