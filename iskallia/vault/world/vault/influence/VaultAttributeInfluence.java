package iskallia.vault.world.vault.influence;

import iskallia.vault.VaultMod;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;

public class VaultAttributeInfluence extends VaultInfluence {
   private final VaultAttributeInfluence.Type type;
   private float value;
   private boolean isMultiplicative;

   public VaultAttributeInfluence(VaultAttributeInfluence.Type type) {
      super(VaultMod.id("type_" + type.name().toLowerCase()));
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
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putFloat("value", this.value);
      tag.putBoolean("isMultiplicative", this.isMultiplicative);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.value = tag.getFloat("value");
      this.isMultiplicative = tag.getBoolean("isMultiplicative");
   }

   public static enum Type {
      RESISTANCE,
      PARRY,
      DURABILITY_DAMAGE,
      COOLDOWN_REDUCTION,
      CHEST_RARITY,
      HEALING_EFFECTIVENESS,
      SOUL_SHARD_DROPS,
      FATAL_STRIKE_CHANCE,
      FATAL_STRIKE_DAMAGE;
   }
}
