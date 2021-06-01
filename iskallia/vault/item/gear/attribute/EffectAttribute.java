package iskallia.vault.item.gear.attribute;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.type.EffectTalent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class EffectAttribute extends PooledAttribute<List<EffectTalent>> {
   public EffectAttribute() {
   }

   public EffectAttribute(ItemAttribute.Modifier<List<EffectTalent>> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundNBT nbt) {
      if (this.getBaseValue() != null) {
         CompoundNBT tag = new CompoundNBT();
         ListNBT effectsList = new ListNBT();
         this.getBaseValue().forEach(effect -> {
            CompoundNBT effectTag = new CompoundNBT();
            tag.func_74778_a("Id", effect.getEffect().getRegistryName().toString());
            tag.func_74768_a("Amplifier", effect.getAmplifier());
            tag.func_74778_a("Type", effect.getType().name);
            tag.func_74778_a("Operator", effect.getOperator().name);
            effectsList.add(effectTag);
         });
         tag.func_218657_a("Effects", effectsList);
         nbt.func_218657_a("BaseValue", tag);
      }
   }

   @Override
   public void read(CompoundNBT nbt) {
      if (!nbt.func_150297_b("BaseValue", 10)) {
         this.setBaseValue(new ArrayList<>());
      } else {
         CompoundNBT tag = nbt.func_74775_l("BaseValue");
         ListNBT effectsList = tag.func_150295_c("Effects", 10);
         this.setBaseValue(
            effectsList.stream()
               .map(inbt -> (CompoundNBT)inbt)
               .map(
                  compoundNBT -> new EffectTalent(
                     0, tag.func_74779_i("Id"), tag.func_74762_e("Amplifier"), tag.func_74779_i("Type"), tag.func_74779_i("Operator")
                  )
               )
               .collect(Collectors.toList())
         );
      }
   }

   public static EffectAttribute.Generator generator() {
      return new EffectAttribute.Generator();
   }

   public static EffectAttribute.Generator.Operator of(EffectAttribute.Type type) {
      return new EffectAttribute.Generator.Operator(type);
   }

   public static class Generator extends PooledAttribute.Generator<List<EffectTalent>, EffectAttribute.Generator.Operator> {
      public List<EffectTalent> getDefaultValue(Random random) {
         return new ArrayList<>();
      }

      public static class Operator extends PooledAttribute.Generator.Operator<List<EffectTalent>> {
         @Expose
         protected String type;

         public Operator(EffectAttribute.Type type) {
            this.type = type.name();
         }

         public EffectAttribute.Type getType() {
            return EffectAttribute.Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
         }

         public List<EffectTalent> apply(List<EffectTalent> value, List<EffectTalent> modifier) {
            if (this.getType() == EffectAttribute.Type.SET) {
               return modifier;
            } else if (this.getType() == EffectAttribute.Type.MERGE) {
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

      public static Optional<EffectAttribute.Type> getByName(String name) {
         for (EffectAttribute.Type value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
               return Optional.of(value);
            }
         }

         return Optional.empty();
      }
   }
}
