package iskallia.vault.attribute;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class EffectAttribute extends PooledAttribute<List<EffectAttribute.Instance>> {
   public EffectAttribute() {
   }

   public EffectAttribute(VAttribute.Modifier<List<EffectAttribute.Instance>> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundTag nbt) {
      if (this.getBaseValue() != null) {
         CompoundTag tag = new CompoundTag();
         ListTag effectsList = new ListTag();
         this.getBaseValue().forEach(effect -> {
            CompoundTag effectTag = new CompoundTag();
            tag.putString("Id", effect.effect);
            effectsList.add(effectTag);
         });
         tag.put("Effects", effectsList);
         nbt.put("BaseValue", tag);
      }
   }

   @Override
   public void read(CompoundTag nbt) {
      if (!nbt.contains("BaseValue", 10)) {
         this.setBaseValue(new ArrayList<>());
      } else {
         CompoundTag tag = nbt.getCompound("BaseValue");
         ListTag effectsList = tag.getList("Effects", 10);
         this.setBaseValue(
            effectsList.stream().map(inbt -> (CompoundTag)inbt).map(effect -> new EffectAttribute.Instance(tag.getString("Id"))).collect(Collectors.toList())
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

      public Instance(MobEffect effect) {
         this(effect.getRegistryName().toString());
      }

      public String getId() {
         return this.effect;
      }

      public MobEffect toEffect() {
         return (MobEffect)Registry.MOB_EFFECT.getOptional(new ResourceLocation(this.effect)).orElse(null);
      }

      @Override
      public String toString() {
         return "Instance{effect='" + this.effect + "'}";
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
