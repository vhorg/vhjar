package iskallia.vault.attribute;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.type.EffectTalent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

@Deprecated(
   forRemoval = true
)
public class EffectTalentAttribute extends PooledAttribute<List<EffectTalent>> {
   public EffectTalentAttribute() {
   }

   public EffectTalentAttribute(VAttribute.Modifier<List<EffectTalent>> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundTag nbt) {
      if (this.getBaseValue() != null) {
         CompoundTag tag = new CompoundTag();
         ListTag effectsList = new ListTag();
         this.getBaseValue().forEach(effect -> {
            CompoundTag effectTag = new CompoundTag();
            tag.putString("Id", effect.getEffect().getRegistryName().toString());
            tag.putInt("Amplifier", effect.getAmplifier());
            effectsList.add(effectTag);
         });
         tag.put("EffectTalents", effectsList);
         nbt.put("BaseValue", tag);
      }
   }

   @Override
   public void read(CompoundTag nbt) {
      if (!nbt.contains("BaseValue", 10)) {
         this.setBaseValue(new ArrayList<>());
      } else {
         CompoundTag tag = nbt.getCompound("BaseValue");
         ListTag effectsList = tag.getList("EffectTalents", 10);
         this.setBaseValue(
            effectsList.stream()
               .map(inbt -> (CompoundTag)inbt)
               .map(compoundNBT -> new EffectTalent(0, tag.getString("Id"), tag.getInt("Amplifier")))
               .collect(Collectors.toList())
         );
      }
   }

   public static EffectTalentAttribute.Generator generator() {
      return new EffectTalentAttribute.Generator();
   }

   public static EffectTalentAttribute.Generator.Operator of(EffectTalentAttribute.Type type) {
      return new EffectTalentAttribute.Generator.Operator(type);
   }

   public static class Generator extends PooledAttribute.Generator<List<EffectTalent>, EffectTalentAttribute.Generator.Operator> {
      public List<EffectTalent> getDefaultValue(Random random) {
         return new ArrayList<>();
      }

      public static class Operator extends PooledAttribute.Generator.Operator<List<EffectTalent>> {
         @Expose
         protected String type;

         public Operator(EffectTalentAttribute.Type type) {
            this.type = type.name();
         }

         public EffectTalentAttribute.Type getType() {
            return EffectTalentAttribute.Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
         }

         public List<EffectTalent> apply(List<EffectTalent> value, List<EffectTalent> modifier) {
            if (this.getType() == EffectTalentAttribute.Type.SET) {
               return modifier;
            } else if (this.getType() == EffectTalentAttribute.Type.MERGE) {
               List<EffectTalent> res = new ArrayList<>(value);
               res.addAll(modifier);
               return res;
            } else {
               return value;
            }
         }
      }
   }

   public static enum Type {
      SET,
      MERGE;

      public static Optional<EffectTalentAttribute.Type> getByName(String name) {
         for (EffectTalentAttribute.Type value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
               return Optional.of(value);
            }
         }

         return Optional.empty();
      }
   }
}
