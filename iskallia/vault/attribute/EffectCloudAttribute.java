package iskallia.vault.attribute;

import com.google.gson.annotations.Expose;
import iskallia.vault.entity.entity.EffectCloudEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class EffectCloudAttribute extends PooledAttribute<List<EffectCloudEntity.Config>> {
   public EffectCloudAttribute() {
   }

   public EffectCloudAttribute(VAttribute.Modifier<List<EffectCloudEntity.Config>> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundTag nbt) {
      if (this.getBaseValue() != null) {
         CompoundTag tag = new CompoundTag();
         ListTag effectsList = new ListTag();
         this.getBaseValue().forEach(effect -> effectsList.add(effect.serializeNBT()));
         tag.put("EffectClouds", effectsList);
         nbt.put("BaseValue", tag);
      }
   }

   @Override
   public void read(CompoundTag nbt) {
      if (!nbt.contains("BaseValue", 10)) {
         this.setBaseValue(new ArrayList<>());
      } else {
         CompoundTag tag = nbt.getCompound("BaseValue");
         ListTag effectsList = tag.getList("EffectClouds", 10);
         this.setBaseValue(effectsList.stream().map(inbt -> EffectCloudEntity.Config.fromNBT((CompoundTag)inbt)).collect(Collectors.toList()));
      }
   }

   public static EffectCloudAttribute.Generator generator() {
      return new EffectCloudAttribute.Generator();
   }

   public static EffectCloudAttribute.Generator.Operator of(EffectCloudAttribute.Type type) {
      return new EffectCloudAttribute.Generator.Operator(type);
   }

   public static class Generator extends PooledAttribute.Generator<List<EffectCloudEntity.Config>, EffectCloudAttribute.Generator.Operator> {
      public List<EffectCloudEntity.Config> getDefaultValue(Random random) {
         return new ArrayList<>();
      }

      public static class Operator extends PooledAttribute.Generator.Operator<List<EffectCloudEntity.Config>> {
         @Expose
         protected String type;

         public Operator(EffectCloudAttribute.Type type) {
            this.type = type.name();
         }

         public EffectCloudAttribute.Type getType() {
            return EffectCloudAttribute.Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
         }

         public List<EffectCloudEntity.Config> apply(List<EffectCloudEntity.Config> value, List<EffectCloudEntity.Config> modifier) {
            if (this.getType() == EffectCloudAttribute.Type.SET) {
               return modifier;
            } else if (this.getType() == EffectCloudAttribute.Type.MERGE) {
               List<EffectCloudEntity.Config> res = new ArrayList<>(value);
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

      public static Optional<EffectCloudAttribute.Type> getByName(String name) {
         for (EffectCloudAttribute.Type value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
               return Optional.of(value);
            }
         }

         return Optional.empty();
      }
   }
}
