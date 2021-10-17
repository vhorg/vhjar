package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.skill.talent.type.EffectTalent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.registries.ForgeRegistries;

public class CurseOnHitModifier extends TexturedVaultModifier {
   @Expose
   protected String effectName;
   @Expose
   protected int effectAmplifier;
   @Expose
   protected int effectDuration;
   @Expose
   protected double onHitApplyChance;

   public CurseOnHitModifier(String name, ResourceLocation icon, Effect effect) {
      this(name, icon, effect.getRegistryName().toString(), 0, 100, 1.0);
   }

   public CurseOnHitModifier(String name, ResourceLocation icon, String effectName, int effectAmplifier, int effectDuration, double onHitApplyChance) {
      super(name, icon);
      this.effectName = effectName;
      this.effectAmplifier = effectAmplifier;
      this.effectDuration = effectDuration;
      this.onHitApplyChance = onHitApplyChance;
   }

   public void applyCurse(ServerPlayerEntity player) {
      if (!(rand.nextFloat() > this.onHitApplyChance)) {
         Effect effect;
         try {
            effect = (Effect)ForgeRegistries.POTIONS.getValue(new ResourceLocation(this.effectName));
         } catch (ResourceLocationException var5) {
            Vault.LOGGER.error("Invalid resource location: " + this.effectName, var5);
            return;
         }

         if (effect != null && !EffectTalent.getImmunities(player).contains(effect)) {
            EffectTalent.CombinedEffects effects = EffectTalent.getEffectData(player, player.func_71121_q(), effect);
            int amplifier = effects.getAmplifier() + this.effectAmplifier + 1;
            player.func_195064_c(new EffectInstance(effect, this.effectDuration, amplifier, true, false));
         }
      }
   }
}
