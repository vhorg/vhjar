package iskallia.vault.mixin;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.skill.set.GolemSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.CarapaceTalent;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class MixinLivingEntity extends Entity {
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

   @Redirect(
      method = {"registerAttributes"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/ai/attributes/AttributeModifierMap;createMutableAttribute()Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;"
      )
   )
   private static MutableAttribute registerAttributes() {
      return AttributeModifierMap.func_233803_a_()
         .func_233814_a_(ModAttributes.CRIT_CHANCE)
         .func_233814_a_(ModAttributes.CRIT_MULTIPLIER)
         .func_233814_a_(ModAttributes.TP_CHANCE)
         .func_233814_a_(ModAttributes.TP_INDIRECT_CHANCE)
         .func_233814_a_(ModAttributes.TP_RANGE)
         .func_233814_a_(ModAttributes.POTION_RESISTANCE);
   }

   @Redirect(
      method = {"applyPotionDamageCalculations"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/lang/Math;max(FF)F"
      )
   )
   protected float applyPotionDamageCalculations(float a, float b) {
      if (!this.field_70170_p.field_72995_K && (LivingEntity)this instanceof PlayerEntity) {
         int resistance = this.func_70644_a(Effects.field_76429_m) ? 0 : this.func_70660_b(Effects.field_76429_m).func_76458_c() + 1;
         float damageCancel = resistance * 5 / 25.0F;
         float damage = a * 25.0F / (25 - resistance * 5);
         ServerPlayerEntity player = (ServerPlayerEntity)this;
         TalentTree abilities = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

         for (TalentNode<?> node : abilities.getNodes()) {
            if (node.getTalent() instanceof CarapaceTalent) {
               CarapaceTalent talent = (CarapaceTalent)node.getTalent();
               damageCancel += talent.getResistanceBonus();
            }
         }

         SetTree sets = PlayerSetsData.get(player.func_71121_q()).getSets(player);

         for (SetNode<?> nodex : sets.getNodes()) {
            if (nodex.getSet() instanceof GolemSet) {
               GolemSet set = (GolemSet)nodex.getSet();
               damageCancel += set.getResistanceBonus();
            }
         }

         return Math.max(damage - damage * damageCancel, 0.0F);
      } else {
         return Math.max(a, b);
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
}
