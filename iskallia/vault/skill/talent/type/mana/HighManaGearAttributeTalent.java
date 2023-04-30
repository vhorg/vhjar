package iskallia.vault.skill.talent.type.mana;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.talent.GearAttributeSkill;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class HighManaGearAttributeTalent extends HighManaTalent implements GearAttributeSkill {
   private VaultGearAttribute<?> attribute;
   private double value;
   private Boolean state;

   public HighManaGearAttributeTalent(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      MobEffect effect,
      float manaThreshold,
      VaultGearAttribute<?> attribute,
      double value,
      Boolean state
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, effect, manaThreshold);
      this.attribute = attribute;
      this.value = value;
      this.state = state;
   }

   public HighManaGearAttributeTalent() {
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
      super.onTick(context);
      ServerPlayer player = context.getSource().as(ServerPlayer.class).orElse(null);
      if (player != null) {
         boolean newState = this.shouldGetBenefits(player);
         if (this.isUnlocked() && newState) {
            this.onAddModifiers(context);
         } else {
            this.onRemoveModifiers(context);
         }

         if (!this.isUnlocked()) {
            this.state = null;
         } else {
            if (this.state == null || this.state != newState) {
               this.refreshSnapshot(player);
               this.state = newState;
            }
         }
      }
   }

   @Override
   public Stream<VaultGearAttributeInstance<?>> getGearAttributes(SkillContext context) {
      LivingEntity entity = context.getSource().as(LivingEntity.class).orElse(null);
      return entity != null && !this.shouldGetBenefits(entity) ? Stream.empty() : Stream.of(VaultGearAttributeInstance.cast(this.attribute, this.value));
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
