package iskallia.vault.skill.talent.type.onhit;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.event.PlayerActiveFlags;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.talent.type.EntityFilterTalent;
import iskallia.vault.skill.talent.type.JavelinConductTalent;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class SweepingOnHitTalent extends EntityFilterTalent {
   private float damagePercentage;
   private float damageRange;
   private float knockbackStrength;
   private float probability;

   public SweepingOnHitTalent(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      EntityPredicate[] filter,
      float damagePercentage,
      float damageRange,
      float knockbackStrength,
      float probability
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, filter);
      this.damagePercentage = damagePercentage;
      this.damageRange = damageRange;
      this.knockbackStrength = knockbackStrength;
      this.probability = probability;
   }

   public SweepingOnHitTalent() {
   }

   @SubscribeEvent
   public static void onAttack(LivingHurtEvent event) {
      if (!ActiveFlags.IS_AOE_ATTACKING.isSet()) {
         if (!ActiveFlags.IS_TOTEM_ATTACKING.isSet()) {
            if (!ActiveFlags.IS_CHARMED_ATTACKING.isSet()) {
               if (!ActiveFlags.IS_DOT_ATTACKING.isSet()) {
                  if (!ActiveFlags.IS_REFLECT_ATTACKING.isSet()) {
                     if (!ActiveFlags.IS_EFFECT_ATTACKING.isSet()) {
                        boolean hasConduct = false;
                        if (ActiveFlags.IS_JAVELIN_ATTACKING.isSet()) {
                           if (event.getSource().getEntity() instanceof ServerPlayer sPlayer) {
                              TalentTree talents = PlayerTalentsData.get(sPlayer.getLevel()).getTalents(sPlayer);

                              for (JavelinConductTalent talent : talents.getAll(JavelinConductTalent.class, Skill::isUnlocked)) {
                                 hasConduct = true;
                              }
                           }

                           if (!hasConduct) {
                              return;
                           }
                        }

                        if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
                           TalentTree var12 = PlayerTalentsData.get(attacker.getLevel()).getTalents(attacker);
                           Entity var13 = event.getEntity();
                           ServerLevel world = attacker.getLevel();

                           for (SweepingOnHitTalent talent : var12.getAll(SweepingOnHitTalent.class, Skill::isUnlocked)) {
                              if (!(world.getRandom().nextFloat() >= talent.probability) && talent.isValid(event.getEntity())) {
                                 ActiveFlags.IS_AOE_ATTACKING
                                    .runIfNotSet(
                                       () -> {
                                          List<Mob> nearby = EntityHelper.getNearby(world, var13.blockPosition(), talent.damageRange, Mob.class);
                                          nearby.remove(var13);
                                          nearby.remove(attacker);
                                          nearby.removeIf(mob -> mob instanceof EternalEntity);
                                          nearby.forEach(
                                             mob -> {
                                                Vec3 movement = mob.getDeltaMovement();
                                                mob.hurt(event.getSource(), event.getAmount() * talent.damagePercentage);
                                                mob.setDeltaMovement(movement);
                                                mob.knockback(
                                                   talent.knockbackStrength,
                                                   Mth.sin(attacker.getYRot() * (float) (Math.PI / 180.0)),
                                                   -Mth.cos(attacker.getYRot() * (float) (Math.PI / 180.0))
                                                );
                                             }
                                          );
                                          PlayerActiveFlags.set(attacker, PlayerActiveFlags.Flag.ATTACK_AOE, 2);
                                       }
                                    );
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damagePercentage), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageRange), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.knockbackStrength), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.probability), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.damagePercentage = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.damageRange = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.knockbackStrength = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.probability = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damagePercentage)).ifPresent(tag -> nbt.put("damagePercentage", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageRange)).ifPresent(tag -> nbt.put("damageRange", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.knockbackStrength)).ifPresent(tag -> nbt.put("knockbackStrength", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.probability)).ifPresent(tag -> nbt.put("probability", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.damagePercentage = Adapters.FLOAT.readNbt(nbt.get("damagePercentage")).orElseThrow();
      this.damageRange = Adapters.FLOAT.readNbt(nbt.get("damageRange")).orElseThrow();
      this.knockbackStrength = Adapters.FLOAT.readNbt(nbt.get("knockbackStrength")).orElseThrow();
      this.probability = Adapters.FLOAT.readNbt(nbt.get("probability")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.damagePercentage)).ifPresent(element -> json.add("damagePercentage", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageRange)).ifPresent(element -> json.add("damageRange", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.knockbackStrength)).ifPresent(element -> json.add("knockbackStrength", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.probability)).ifPresent(element -> json.add("probability", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.damagePercentage = Adapters.FLOAT.readJson(json.get("damagePercentage")).orElseThrow();
      this.damageRange = Adapters.FLOAT.readJson(json.get("damageRange")).orElseThrow();
      this.knockbackStrength = Adapters.FLOAT.readJson(json.get("knockbackStrength")).orElseThrow();
      this.probability = Adapters.FLOAT.readJson(json.get("probability")).orElseThrow();
   }
}
