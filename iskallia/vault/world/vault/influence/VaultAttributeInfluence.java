package iskallia.vault.world.vault.influence;

import iskallia.vault.Vault;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundNBT;

public class VaultAttributeInfluence extends VaultInfluence {
   private final VaultAttributeInfluence.Type type;
   private float value;
   private boolean isMultiplicative;

   public VaultAttributeInfluence(VaultAttributeInfluence.Type type) {
      super(Vault.id("type_" + type.name().toLowerCase()));
      this.type = type;
   }

   public VaultAttributeInfluence(VaultAttributeInfluence.Type type, float value, boolean isMultiplicative) {
      this(type);
      this.value = value;
      this.isMultiplicative = isMultiplicative;
   }

   public static Supplier<VaultInfluence> newInstance(VaultAttributeInfluence.Type type) {
      return () -> new VaultAttributeInfluence(type);
   }

   public VaultAttributeInfluence.Type getType() {
      return this.type;
   }

   public float getValue() {
      return this.value;
   }

   public boolean isMultiplicative() {
      return this.isMultiplicative;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74776_a("value", this.value);
      tag.func_74757_a("isMultiplicative", this.isMultiplicative);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.value = tag.func_74760_g("value");
      this.isMultiplicative = tag.func_74767_n("isMultiplicative");
   }

   public static enum Type {
      RESISTANCE,
      PARRY,
      DURABILITY_DAMAGE,
      COOLDOWN_REDUCTION,
      HEALING_EFFECTIVENESS,
      FATAL_STRIKE_CHANCE,
      FATAL_STRIKE_DAMAGE;
   }
}
