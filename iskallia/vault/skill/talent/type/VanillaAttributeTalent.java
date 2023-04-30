package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.TickingSkill;
import iskallia.vault.skill.talent.GearAttributeSkill;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class VanillaAttributeTalent extends LearnableSkill implements GearAttributeSkill, TickingSkill {
   protected static final EnumAdapter<Operation> OPERATION_ORDINAL = Adapters.ofEnum(Operation.class, EnumAdapter.Mode.ORDINAL);
   protected static final EnumAdapter<Operation> OPERATION_NAME = Adapters.ofEnum(Operation.class, EnumAdapter.Mode.NAME);
   private Attribute attribute;
   private Operation operation;
   public double amount;

   public VanillaAttributeTalent(int unlockLevel, int learnPointCost, int regretPointCost, Attribute attribute, Operation operation, double amount) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.attribute = attribute;
      this.operation = operation;
      this.amount = amount;
   }

   public VanillaAttributeTalent() {
   }

   @Override
   public void onAdd(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(this::refreshSnapshot);
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(this::refreshSnapshot);
   }

   @Override
   public void onTick(SkillContext context) {
      if (!this.isUnlocked()) {
         this.onRemoveModifiers(context);
      } else {
         this.onAddModifiers(context);
      }
   }

   @Override
   public Stream<VaultGearAttributeInstance<?>> getGearAttributes(SkillContext context) {
      VaultGearAttribute<?> attribute = ModGearAttributes.getGearAttribute(this.attribute, this.operation);
      return attribute == null ? Stream.empty() : Stream.of(VaultGearAttributeInstance.cast(attribute, this.amount));
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.ATTRIBUTE.writeBits((IForgeRegistryEntry)this.attribute, buffer);
      OPERATION_ORDINAL.writeBits((Enum)this.operation, buffer);
      Adapters.DOUBLE.writeBits(Double.valueOf(this.amount), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.attribute = (Attribute)Adapters.ATTRIBUTE.readBits(buffer).orElseThrow();
      this.operation = (Operation)OPERATION_ORDINAL.readBits(buffer).orElseThrow();
      this.amount = Adapters.DOUBLE.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.ATTRIBUTE.writeNbt((IForgeRegistryEntry)this.attribute).ifPresent(tag -> nbt.put("attribute", tag));
         OPERATION_NAME.writeNbt((Enum)this.operation).ifPresent(tag -> nbt.put("operation", tag));
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.amount)).ifPresent(tag -> nbt.put("amount", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.attribute = (Attribute)Adapters.ATTRIBUTE.readNbt(nbt.get("attribute")).orElseThrow(() -> new IllegalStateException("Unknown attribute in " + nbt));
      this.operation = (Operation)OPERATION_NAME.readNbt(nbt.get("operation")).orElseThrow();
      this.amount = Adapters.DOUBLE.readNbt(nbt.get("amount")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.ATTRIBUTE.writeJson((IForgeRegistryEntry)this.attribute).ifPresent(element -> json.add("attribute", element));
         OPERATION_NAME.writeJson((Enum)this.operation).ifPresent(element -> json.add("operation", element));
         Adapters.DOUBLE.writeJson(Double.valueOf(this.amount)).ifPresent(element -> json.add("amount", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.attribute = (Attribute)Adapters.ATTRIBUTE
         .readJson(json.get("attribute"))
         .orElseThrow(() -> new IllegalStateException("Unknown attribute in " + json));
      this.operation = (Operation)OPERATION_NAME.readJson(json.get("operation")).orElseThrow();
      this.amount = Adapters.DOUBLE.readJson(json.get("amount")).orElseThrow();
   }
}
