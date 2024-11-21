package iskallia.vault.block.entity.challenge.raid.action;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.roll.DoubleRoll;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class VanillaAttributeChallengeAction extends ChallengeAction<VanillaAttributeChallengeAction.Config> {
   protected UUID uuid;
   protected double amount;

   public VanillaAttributeChallengeAction() {
      super(new VanillaAttributeChallengeAction.Config());
   }

   public VanillaAttributeChallengeAction(VanillaAttributeChallengeAction.Config config) {
      super(config);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public double getAmount() {
      return this.amount;
   }

   public Attribute getAttribute() {
      return this.getConfig().attribute;
   }

   public Operation getOperation() {
      return this.getConfig().operation;
   }

   @Override
   public boolean onPopulate(RandomSource random) {
      if (!super.onPopulate(random)) {
         this.uuid = Mth.createInsecureUUID();
         this.amount = 0.0;

         for (DoubleRoll roll : this.getConfig().amount) {
            double value = roll.get(random);
            switch (this.getOperation()) {
               case ADDITION:
               case MULTIPLY_BASE:
                  this.amount += value;
                  break;
               case MULTIPLY_TOTAL:
                  this.amount = 1.0 - (1.0 + this.amount) * (1.0 + value);
            }
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public boolean onMerge(ChallengeAction<?> action) {
      if (action instanceof VanillaAttributeChallengeAction other && this.getAttribute() == other.getAttribute() && this.getOperation() == other.getOperation()
         )
       {
         this.getConfig().amount.addAll(other.getConfig().amount);
         switch (this.getOperation()) {
            case ADDITION:
            case MULTIPLY_BASE:
               this.amount = this.amount + other.getAmount();
               break;
            case MULTIPLY_TOTAL:
               this.amount = 1.0 - (1.0 + this.amount) * (1.0 + other.getAmount());
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public Component getText() {
      Component prefix = new TextComponent(switch (this.getOperation()) {
         case ADDITION -> String.format("%+.0f", this.amount);
         case MULTIPLY_BASE -> String.format("%+.0f%%", this.amount * 100.0);
         case MULTIPLY_TOTAL -> "×" + (1.0 + this.amount);
         default -> throw new IncompatibleClassChangeError();
      });
      MutableComponent text = new TextComponent("").append(prefix);
      if (this.getConfig().name == null) {
         Component suffix = new TranslatableComponent(this.getAttribute().getDescriptionId());
         text = text.append(this instanceof MobVanillaAttributeChallengeAction ? " Mob " : " Player ").append(suffix);
      } else {
         text = text.append(" ").append(new TextComponent(this.getConfig().name));
      }

      return text.setStyle(Style.EMPTY.withColor(this.getConfig().textColor));
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      if (this.isPopulated()) {
         Adapters.UUID.writeBits(this.uuid, buffer);
         Adapters.DOUBLE.writeBits(Double.valueOf(this.amount), buffer);
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      if (this.isPopulated()) {
         this.uuid = Adapters.UUID.readBits(buffer).orElseThrow();
         this.amount = Adapters.DOUBLE.readBits(buffer).orElseThrow();
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         if (!this.isPopulated()) {
            return (CompoundTag)nbt;
         } else {
            Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));
            Adapters.DOUBLE.writeNbt(Double.valueOf(this.amount)).ifPresent(tag -> nbt.put("amount", tag));
            return (CompoundTag)nbt;
         }
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      if (this.isPopulated()) {
         this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElseThrow();
         this.amount = Adapters.DOUBLE.readNbt(nbt.get("amount")).orElseThrow();
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         if (!this.isPopulated()) {
            return (JsonObject)json;
         } else {
            Adapters.UUID.writeJson(this.uuid).ifPresent(tag -> json.add("uuid", tag));
            Adapters.DOUBLE.writeJson(Double.valueOf(this.amount)).ifPresent(tag -> json.add("amount", tag));
            return (JsonObject)json;
         }
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      if (this.isPopulated()) {
         this.uuid = Adapters.UUID.readJson(json.get("uuid")).orElseThrow();
         this.amount = Adapters.DOUBLE.readJson(json.get("amount")).orElseThrow();
      }
   }

   public static class Config extends ChallengeAction.Config {
      private String name;
      protected EntityPredicate filter;
      protected Attribute attribute;
      protected Operation operation;
      protected List<DoubleRoll> amount;
      protected static final EnumAdapter<Operation> OPERATION_ORDINAL = Adapters.ofEnum(Operation.class, EnumAdapter.Mode.ORDINAL);
      protected static final EnumAdapter<Operation> OPERATION_NAME = Adapters.ofEnum(Operation.class, EnumAdapter.Mode.NAME);
      protected static final ArrayAdapter<DoubleRoll> AMOUNT = Adapters.ofArray(DoubleRoll[]::new, Adapters.DOUBLE_ROLL);

      public Config() {
         this.amount = new ArrayList<>();
      }

      public Config(String name, EntityPredicate filter, Attribute attribute, Operation operation, DoubleRoll... amount) {
         this.name = name;
         this.filter = filter;
         this.attribute = attribute;
         this.operation = operation;
         this.amount = new ArrayList<>(Arrays.asList(amount));
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.ENTITY_PREDICATE.writeBits(this.filter, buffer);
         Adapters.ATTRIBUTE.writeBits((IForgeRegistryEntry)this.attribute, buffer);
         OPERATION_ORDINAL.writeBits((Enum)this.operation, buffer);
         AMOUNT.writeBits(this.amount.toArray(DoubleRoll[]::new), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.filter = Adapters.ENTITY_PREDICATE.readBits(buffer).orElse(null);
         this.attribute = (Attribute)Adapters.ATTRIBUTE.readBits(buffer).orElseThrow();
         this.operation = (Operation)OPERATION_ORDINAL.readBits(buffer).orElseThrow();
         this.amount = Arrays.stream(AMOUNT.readBits(buffer).orElseThrow()).collect(Collectors.toList());
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.ENTITY_PREDICATE.writeNbt(this.filter).ifPresent(tag -> nbt.put("filter", tag));
            Adapters.ATTRIBUTE.writeNbt((IForgeRegistryEntry)this.attribute).ifPresent(tag -> nbt.put("attribute", tag));
            OPERATION_NAME.writeNbt((Enum)this.operation).ifPresent(tag -> nbt.put("operation", tag));
            AMOUNT.writeNbt(this.amount.toArray(DoubleRoll[]::new)).ifPresent(tag -> nbt.put("amount", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.filter = Adapters.ENTITY_PREDICATE.readNbt(nbt.get("filter")).orElse(null);
         this.attribute = (Attribute)Adapters.ATTRIBUTE
            .readNbt(nbt.get("attribute"))
            .orElseThrow(() -> new IllegalStateException("Unknown attribute in " + nbt));
         this.operation = (Operation)OPERATION_NAME.readNbt(nbt.get("operation")).orElseThrow();
         this.amount = Arrays.stream(AMOUNT.readNbt(nbt.get("amount")).orElseThrow()).collect(Collectors.toList());
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.ENTITY_PREDICATE.writeJson(this.filter).ifPresent(tag -> json.add("filter", tag));
            Adapters.ATTRIBUTE.writeJson((IForgeRegistryEntry)this.attribute).ifPresent(element -> json.add("attribute", element));
            OPERATION_NAME.writeJson((Enum)this.operation).ifPresent(element -> json.add("operation", element));
            AMOUNT.writeJson(this.amount.toArray(DoubleRoll[]::new)).ifPresent(tag -> json.add("amount", tag));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.filter = Adapters.ENTITY_PREDICATE.readJson(json.get("filter")).orElse(null);
         this.attribute = (Attribute)Adapters.ATTRIBUTE
            .readJson(json.get("attribute"))
            .orElseThrow(() -> new IllegalStateException("Unknown attribute in " + json));
         this.operation = (Operation)OPERATION_NAME.readJson(json.get("operation")).orElseThrow();
         this.amount = Arrays.stream(AMOUNT.readJson(json.get("amount")).orElseThrow()).collect(Collectors.toList());
      }
   }
}
