package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.TickingSkill;
import iskallia.vault.skill.talent.GearAttributeSkill;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class GearAttributeTalent extends LearnableSkill implements GearAttributeSkill, TickingSkill {
   private VaultGearAttribute<?> attribute;
   private double value;

   public GearAttributeTalent(int unlockLevel, int learnPointCost, int regretPointCost, VaultGearAttribute<?> attribute, double value) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.attribute = attribute;
      this.value = value;
   }

   public GearAttributeTalent() {
   }

   public VaultGearAttribute<?> getAttribute() {
      return this.attribute;
   }

   public double getValue() {
      return this.value;
   }

   public boolean canApply(SkillContext context) {
      return this.isUnlocked();
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
      } else if (this.canApply(context)) {
         this.onAddModifiers(context);
      }
   }

   @Override
   public Stream<VaultGearAttributeInstance<?>> getGearAttributes(SkillContext context) {
      return this.canApply(context) ? Stream.of(VaultGearAttributeInstance.cast(this.getAttribute(), this.getValue())) : Stream.empty();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.GEAR_ATTRIBUTE.writeBits((IForgeRegistryEntry)this.attribute, buffer);
      Adapters.DOUBLE.writeBits(Double.valueOf(this.value), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.attribute = (VaultGearAttribute<?>)Adapters.GEAR_ATTRIBUTE.readBits(buffer).orElseThrow();
      this.value = Adapters.DOUBLE.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.GEAR_ATTRIBUTE.writeNbt((IForgeRegistryEntry)this.attribute).ifPresent(tag -> nbt.put("attribute", tag));
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.value)).ifPresent(tag -> nbt.put("value", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.attribute = (VaultGearAttribute<?>)Adapters.GEAR_ATTRIBUTE
         .readNbt(nbt.get("attribute"))
         .orElseThrow(() -> new IllegalStateException("Unknown attribute in " + nbt));
      this.value = Adapters.DOUBLE.readNbt(nbt.get("value")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.GEAR_ATTRIBUTE.writeJson((IForgeRegistryEntry)this.attribute).ifPresent(element -> json.add("attribute", element));
         Adapters.DOUBLE.writeJson(Double.valueOf(this.value)).ifPresent(element -> json.add("value", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.attribute = (VaultGearAttribute<?>)Adapters.GEAR_ATTRIBUTE
         .readJson(json.get("attribute"))
         .orElseThrow(() -> new IllegalStateException("Unknown attribute in " + json));
      this.value = Adapters.DOUBLE.readJson(json.get("value")).orElseThrow();
   }
}
