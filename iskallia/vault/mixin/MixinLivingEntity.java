package iskallia.vault.mixin;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.util.calc.ResistanceHelper;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
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
   @Final
   protected static EntitySize field_213377_as;

   public MixinLivingEntity(EntityType<?> entityType, World world) {
      super(entityType, world);
   }

   @Shadow
   public abstract EffectInstance func_70660_b(Effect var1);

   @Shadow
   @Nullable
   public abstract ModifiableAttributeInstance func_110148_a(Attribute var1);

   @Shadow
   public abstract boolean func_70644_a(Effect var1);

   @Shadow
   public abstract float func_213355_cm();

   @Shadow
   public abstract boolean func_225503_b_(float var1, float var2);

   @Shadow
   public abstract ItemStack func_184586_b(Hand var1);

   @Shadow
   public abstract EntitySize func_213305_a(Pose var1);

   @Redirect(
      method = {"registerAttributes"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/ai/attributes/AttributeModifierMap;createMutableAttribute()Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;"
      )
   )
   private static MutableAttribute registerAttributes() {
      return AttributeModifierMap.func_233803_a_()
         .func_233814_a_(Attributes.field_233818_a_)
         .func_233814_a_(Attributes.field_233820_c_)
         .func_233814_a_(Attributes.field_233821_d_)
         .func_233814_a_(Attributes.field_233826_i_)
         .func_233814_a_(Attributes.field_233827_j_)
         .func_233814_a_((Attribute)ForgeMod.SWIM_SPEED.get())
         .func_233814_a_((Attribute)ForgeMod.NAMETAG_DISTANCE.get())
         .func_233814_a_((Attribute)ForgeMod.ENTITY_GRAVITY.get())
         .func_233814_a_(ModAttributes.CRIT_CHANCE)
         .func_233814_a_(ModAttributes.CRIT_MULTIPLIER)
         .func_233814_a_(ModAttributes.TP_CHANCE)
         .func_233814_a_(ModAttributes.TP_INDIRECT_CHANCE)
         .func_233814_a_(ModAttributes.TP_RANGE)
         .func_233814_a_(ModAttributes.POTION_RESISTANCE)
         .func_233814_a_(ModAttributes.SIZE_SCALE)
         .func_233814_a_(ModAttributes.BREAK_ARMOR_CHANCE);
   }

   @Redirect(
      method = {"applyPotionDamageCalculations"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/lang/Math;max(FF)F"
      )
   )
   protected float applyPotionDamageCalculations(float a, float b) {
      if (!this.field_70170_p.field_72995_K) {
         int resistance = this.func_70644_a(Effects.field_76429_m) ? 0 : this.func_70660_b(Effects.field_76429_m).func_76458_c() + 1;
         float damageCancel = resistance * 5 / 25.0F;
         float damage = a * 25.0F / (25 - resistance * 5);
         if (this instanceof ServerPlayerEntity) {
            damageCancel += ResistanceHelper.getPlayerResistancePercent((ServerPlayerEntity)this);
         } else {
            damageCancel += ResistanceHelper.getResistancePercent((LivingEntity)this);
         }

         return Math.max(damage - damage * damageCancel, 0.0F);
      } else {
         return Math.max(a, b);
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   public void tick(CallbackInfo ci) {
      ModifiableAttributeInstance scale = this.func_110148_a(ModAttributes.SIZE_SCALE);
      if (scale != null) {
         if (this.prevSize != scale.func_111126_e()) {
            this.prevSize = (float)scale.func_111126_e();
            this.field_213325_aI = this.func_213305_a(Pose.STANDING).func_220313_a(this.prevSize);
            this.func_213323_x_();
         }
      }
   }

   @Inject(
      method = {"addPotionEffect"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void addPotionEffect(EffectInstance effect, CallbackInfoReturnable<Boolean> ci) {
      ModifiableAttributeInstance attribute = this.func_110148_a(ModAttributes.POTION_RESISTANCE);
      if (attribute != null) {
         if (!(this.field_70146_Z.nextDouble() >= attribute.func_111126_e())) {
            ci.setReturnValue(false);
         }
      }
   }

   @Inject(
      method = {"checkTotemDeathProtection"},
      at = {@At(
         value = "RETURN",
         ordinal = 1
      )},
      cancellable = true
   )
   private void checkTotemDeathProtection(DamageSource damageSourceIn, CallbackInfoReturnable<Boolean> cir) {
      if (!(Boolean)cir.getReturnValue() && !damageSourceIn.func_76357_e()) {
         ItemStack idol = ItemStack.field_190927_a;

         for (Hand hand : Hand.values()) {
            ItemStack it = this.func_184586_b(hand);
            if (it.func_77973_b() instanceof IdolItem) {
               idol = it.func_77946_l();
               it.func_190918_g(1);
               break;
            }
         }

         if (!idol.func_190926_b()) {
            if (this instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)this;
               serverplayerentity.func_71029_a(Stats.field_75929_E.func_199076_b(Items.field_190929_cY));
               CriteriaTriggers.field_193130_A.func_193187_a(serverplayerentity, idol);
            }

            ((LivingEntity)this).func_70606_j(1.0F);
            ((LivingEntity)this).func_195061_cb();
            ((LivingEntity)this).func_195064_c(new EffectInstance(Effects.field_76428_l, 900, 1));
            ((LivingEntity)this).func_195064_c(new EffectInstance(Effects.field_76444_x, 100, 1));
            ((LivingEntity)this).func_195064_c(new EffectInstance(Effects.field_76426_n, 800, 0));
            this.field_70170_p.func_72960_a(this, (byte)35);
            cir.setReturnValue(true);
         }
      }
   }
}
