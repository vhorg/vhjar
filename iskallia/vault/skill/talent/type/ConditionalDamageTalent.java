package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ConditionalDamageTalent extends LearnableSkill {
   private MobEffect targetEffect;
   private double damageIncrease;

   public ConditionalDamageTalent(int unlockLevel, int learnPointCost, int regretPointCost, MobEffect targetEffect, double damageIncrease) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.targetEffect = targetEffect;
      this.damageIncrease = damageIncrease;
   }

   public ConditionalDamageTalent() {
   }

   public MobEffect getTargetEffect() {
      return this.targetEffect;
   }

   public double getDamageIncrease() {
      return this.damageIncrease;
   }

   @SubscribeEvent
   public static void onAttack(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer sPlayer) {
         LivingEntity attacked = event.getEntityLiving();
         float addedMultiplier = 0.0F;
         TalentTree talents = PlayerTalentsData.get(sPlayer.getLevel()).getTalents(sPlayer);

         for (ConditionalDamageTalent talent : talents.getAll(ConditionalDamageTalent.class, Skill::isUnlocked)) {
            if (talent.getTargetEffect() != null && attacked.hasEffect(talent.getTargetEffect())) {
               addedMultiplier = (float)(addedMultiplier + talent.getDamageIncrease());
            }
         }

         event.setAmount(event.getAmount() * (1.0F + addedMultiplier));
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.EFFECT.writeBits((IForgeRegistryEntry)this.targetEffect, buffer);
      Adapters.DOUBLE.writeBits(Double.valueOf(this.damageIncrease), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.targetEffect = (MobEffect)Adapters.EFFECT.readBits(buffer).orElseThrow();
      this.damageIncrease = Adapters.DOUBLE.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.EFFECT.writeNbt((IForgeRegistryEntry)this.targetEffect).ifPresent(tag -> nbt.put("targetEffect", tag));
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.damageIncrease)).ifPresent(tag -> nbt.put("damageIncrease", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.targetEffect = (MobEffect)Adapters.EFFECT.readNbt(nbt.get("targetEffect")).orElseThrow();
      this.damageIncrease = Adapters.DOUBLE.readNbt(nbt.get("damageIncrease")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.EFFECT.writeJson((IForgeRegistryEntry)this.targetEffect).ifPresent(element -> json.add("targetEffect", element));
         Adapters.DOUBLE.writeJson(Double.valueOf(this.damageIncrease)).ifPresent(element -> json.add("damageIncrease", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.targetEffect = (MobEffect)Adapters.EFFECT.readJson(json.get("targetEffect")).orElseThrow();
      this.damageIncrease = Adapters.DOUBLE.readJson(json.get("damageIncrease")).orElseThrow();
   }
}
