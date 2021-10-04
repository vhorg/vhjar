package iskallia.vault.item.paxel.enhancement;

import iskallia.vault.skill.talent.type.EffectTalent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class EffectEnhancement extends PaxelEnhancement {
   public Map<UUID, EffectInstance> EFFECT_INSTANCE_CAPTURES = new HashMap<>();
   protected String effectName;
   protected int extraAmplifier;
   protected Effect effect;

   public EffectEnhancement(Effect effect, int extraAmplifier) {
      this.effectName = Objects.requireNonNull(Registry.field_212631_t.func_177774_c(effect)).toString();
      this.extraAmplifier = extraAmplifier;
   }

   @Override
   public Color getColor() {
      return Color.func_240743_a_(-10047745);
   }

   @Override
   public IFormattableTextComponent getDescription() {
      return new TranslationTextComponent(
         "paxel_enhancement.the_vault.effects.desc", new Object[]{this.extraAmplifier, this.getEffect().func_199286_c().getString()}
      );
   }

   public Effect getEffect() {
      if (this.effect == null) {
         this.effect = (Effect)Registry.field_212631_t.func_82594_a(new ResourceLocation(this.effectName));
      }

      return this.effect;
   }

   public int getExtraAmplifier() {
      return this.extraAmplifier;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      nbt.func_74778_a("EffectName", this.effectName);
      nbt.func_74768_a("ExtraAmplifier", this.extraAmplifier);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.effectName = nbt.func_74779_i("EffectName");
      this.extraAmplifier = nbt.func_74762_e("ExtraAmplifier");
   }

   public void captureEffect(ServerPlayerEntity player, EffectInstance instance) {
      UUID playerUUID = player.func_110124_au();
      if (instance == null) {
         this.EFFECT_INSTANCE_CAPTURES.put(playerUUID, null);
      } else {
         EffectInstance copiedInstance = new EffectInstance(instance);
         this.EFFECT_INSTANCE_CAPTURES.put(playerUUID, copiedInstance);
      }
   }

   public void revertCapturedEffect(ServerPlayerEntity player) {
      EffectInstance capturedInstance = this.EFFECT_INSTANCE_CAPTURES.remove(player.func_110124_au());
      if (capturedInstance != null) {
         player.func_195064_c(capturedInstance);
      }
   }

   @Override
   public void onEnhancementActivated(ServerPlayerEntity player, ItemStack paxelStack) {
      Effect enhancementEffect = this.getEffect();
      EffectInstance activeEffect = player.func_70660_b(enhancementEffect);
      if (activeEffect != null) {
         ServerWorld world = player.func_71121_q();
         EffectTalent.CombinedEffects effects = EffectTalent.getEffectData(player, world, enhancementEffect);
         if (effects.getAmplifier() >= 0) {
            return;
         }
      }

      this.captureEffect(player, activeEffect);
   }

   @Override
   public void onEnhancementDeactivated(ServerPlayerEntity player, ItemStack paxelStack) {
      player.func_195063_d(this.getEffect());
      this.revertCapturedEffect(player);
   }

   @Override
   public void heldTick(ServerPlayerEntity player, ItemStack paxelStack, int slotIndex) {
      Effect enhancementEffect = this.getEffect();
      EffectInstance capturedEffect = this.EFFECT_INSTANCE_CAPTURES.get(player.func_110124_au());
      if (capturedEffect != null) {
         player.func_195064_c(this.createEnhancedEffect(capturedEffect));
      } else {
         ServerWorld world = player.func_71121_q();
         EffectTalent.CombinedEffects effects = EffectTalent.getEffectData(player, world, enhancementEffect);
         if (effects.getAmplifier() >= 0) {
            EffectTalent display = effects.getDisplayEffect();
            EffectTalent.Type type = display.getType();
            player.func_195064_c(this.createEnhancedEffect(display.getAmplifier(), type.showParticles, type.showIcon));
         } else {
            player.func_195064_c(this.createEnhancedEffect(-1, false, true));
         }
      }
   }

   public EffectInstance createEnhancedEffect(EffectInstance instance) {
      return this.createEnhancedEffect(instance.func_76458_c(), instance.func_188418_e(), instance.func_205348_f());
   }

   public EffectInstance createEnhancedEffect(int baseAmplifier, boolean doesShowParticles, boolean doesShowIcon) {
      return new EffectInstance(this.getEffect(), 310, baseAmplifier + this.extraAmplifier, false, doesShowParticles, doesShowIcon);
   }
}
