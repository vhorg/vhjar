package iskallia.vault.world.vault.influence;

import iskallia.vault.VaultMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectInfluence extends VaultInfluence {
   public static final ResourceLocation ID = VaultMod.id("effect");
   private MobEffect effect;
   private int amplifier;

   EffectInfluence() {
      super(ID);
   }

   public EffectInfluence(MobEffect effect, int amplifier) {
      this();
      this.effect = effect;
      this.amplifier = amplifier;
   }

   public MobEffect getEffect() {
      return this.effect;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putString("effect", this.effect.getRegistryName().toString());
      tag.putInt("amplifier", this.amplifier);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(tag.getString("effect")));
      this.amplifier = tag.getInt("amplifier");
   }
}
