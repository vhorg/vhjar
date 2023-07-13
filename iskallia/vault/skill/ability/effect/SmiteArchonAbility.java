package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.effect.spi.AbstractSmiteAbility;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class SmiteArchonAbility extends AbstractSmiteAbility {
   private float additionalDurabilityWearReduction;

   public SmiteArchonAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      float radius,
      int intervalTicks,
      float playerDamagePercent,
      int color,
      float additionalManaPerBolt,
      float additionalDurabilityWearReduction
   ) {
      super(
         unlockLevel,
         learnPointCost,
         regretPointCost,
         cooldownTicks,
         manaCostPerSecond,
         radius,
         intervalTicks,
         playerDamagePercent,
         color,
         additionalManaPerBolt
      );
      this.additionalDurabilityWearReduction = additionalDurabilityWearReduction;
   }

   public float getAdditionalDurabilityWearReduction() {
      return this.additionalDurabilityWearReduction;
   }

   private static List<SmiteArchonAbility> getAll(LivingEntity livingEntity) {
      List<SmiteArchonAbility> result = new ArrayList<>();
      if (livingEntity instanceof ServerPlayer player && !player.getActiveEffects().stream().noneMatch(e -> e.getEffect() == ModEffects.SMITE_ARCHON)) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         return abilities.getAll(SmiteArchonAbility.class, Skill::isUnlocked);
      } else {
         return result;
      }
   }

   public SmiteArchonAbility() {
   }

   @Override
   public ToggleAbilityEffect getEffect() {
      return ModEffects.SMITE_ARCHON;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.additionalDurabilityWearReduction), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalDurabilityWearReduction = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.additionalDurabilityWearReduction)).ifPresent(tag -> nbt.put("additionalDurabilityWearReduction", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalDurabilityWearReduction = Adapters.FLOAT.readNbt(nbt.get("additionalDurabilityWearReduction")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson()
         .map(
            json -> {
               Adapters.FLOAT
                  .writeJson(Float.valueOf(this.additionalDurabilityWearReduction))
                  .ifPresent(element -> json.add("additionalDurabilityWearReduction", element));
               return (JsonObject)json;
            }
         );
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalDurabilityWearReduction = Adapters.FLOAT.readJson(json.get("additionalDurabilityWearReduction")).orElse(0.0F);
   }

   static {
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.DURABILITY_WEAR_REDUCTION)
         .register(
            SmiteArchonAbility.class,
            data -> getAll(data.getEntity()).forEach(skill -> data.setValue(data.getValue() + skill.getAdditionalDurabilityWearReduction()))
         );
   }
}
