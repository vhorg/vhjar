package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.EffectGrantingTalent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class EffectTalent extends PlayerTalent implements EffectGrantingTalent {
   @Expose
   private final String effect;
   @Expose
   private final int amplifier;

   @Override
   public String toString() {
      return "EffectTalent{effect='" + this.effect + "', amplifier=" + this.amplifier + "}";
   }

   public EffectTalent(int cost, MobEffect effect, int amplifier) {
      this(cost, Registry.MOB_EFFECT.getKey(effect).toString(), amplifier);
   }

   public EffectTalent(int cost, String effect, int amplifier) {
      super(cost);
      this.effect = effect;
      this.amplifier = amplifier;
   }

   @Override
   public MobEffect getEffect() {
      return (MobEffect)Registry.MOB_EFFECT.get(new ResourceLocation(this.effect));
   }

   @Override
   public int getAmplifier() {
      return this.amplifier;
   }

   @Override
   public void onRemoved(Player player) {
      player.removeEffect(this.getEffect());
   }
}
