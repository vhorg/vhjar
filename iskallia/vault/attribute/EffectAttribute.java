package iskallia.vault.attribute;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EffectAttribute extends PooledAttribute<List<EffectAttribute.Instance>> {
   public EffectAttribute() {
   }

   public EffectAttribute(VAttribute.Modifier<List<EffectAttribute.Instance>> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundNBT nbt) {
      if (this.getBaseValue() != null) {
         CompoundNBT tag = new CompoundNBT();
         ListNBT effectsList = new ListNBT();
         this.getBaseValue().forEach(effect -> {
            CompoundNBT effectTag = new CompoundNBT();
            tag.func_74778_a("Id", effect.effect);
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
               .map(effect -> new EffectAttribute.Instance(tag.func_74779_i("Id")))
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

   public static class Generator extends PooledAttribute.Generator<List<EffectAttribute.Instance>, EffectAttribute.Generator.Operator> {
      public List<EffectAttribute.Instance> getDefaultValue(Random random) {
         return new ArrayList<>();
      }

      public static class Operator extends PooledAttribute.Generator.Operator<List<EffectAttribute.Instance>> {
         @Expose
         protected String type;

         public Operator(EffectAttribute.Type type) {
            this.type = type.name();
         }

         public EffectAttribute.Type getType() {
            return EffectAttribute.Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
         }

         public List<EffectAttribute.Instance> apply(List<EffectAttribute.Instance> value, List<EffectAttribute.Instance> modifier) {
            if (this.getType() == EffectAttribute.Type.SET) {
               return modifier;
            } else if (this.getType() == EffectAttribute.Type.MERGE) {
               List<EffectAttribute.Instance> res = new ArrayList<>(value);
               res.addAll(modifier);
               return res;
            } else {
               return value;
            }
         }
      }
   }

   public static class Instance {
      @Expose
      protected String effect;

      public Instance(String effect) {
         this.effect = effect;
      }

      public Instance(Effect effect) {
         this(effect.getRegistryName().toString());
      }

      public String getId() {
         return this.effect;
      }

      public Effect toEffect() {
         return (Effect)Registry.field_212631_t.func_241873_b(new ResourceLocation(this.effect)).orElse(null);
      }

      @Override
      public String toString() {
         return "Instance{effect='" + this.effect + '\'' + '}';
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
