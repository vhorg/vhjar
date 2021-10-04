package iskallia.vault.world.vault.influence;

import iskallia.vault.Vault;
import iskallia.vault.skill.talent.type.EffectTalent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectInfluence extends VaultInfluence {
   public static final ResourceLocation ID = Vault.id("effect");
   private Effect effect;
   private int amplifier;

   EffectInfluence() {
      super(ID);
   }

   public EffectInfluence(Effect effect, int amplifier) {
      this();
      this.effect = effect;
      this.amplifier = amplifier;
   }

   public Effect getEffect() {
      return this.effect;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public EffectTalent makeTalent() {
      return new EffectTalent(0, this.getEffect(), this.getAmplifier(), EffectTalent.Type.HIDDEN, EffectTalent.Operator.ADD);
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74778_a("effect", this.effect.getRegistryName().toString());
      tag.func_74768_a("amplifier", this.amplifier);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.effect = (Effect)ForgeRegistries.POTIONS.getValue(new ResourceLocation(tag.func_74779_i("effect")));
      this.amplifier = tag.func_74762_e("amplifier");
   }
}
