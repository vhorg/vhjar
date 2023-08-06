package iskallia.vault.skill.talent.type.onhit;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.ability.effect.JavelinPiercingAbility;
import iskallia.vault.skill.ability.effect.JavelinScatterAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractJavelinAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.talent.type.EntityFilterTalent;
import iskallia.vault.skill.talent.type.JavelinConductTalent;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.damage.AttackScaleHelper;
import iskallia.vault.util.damage.CritHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class EffectOnHitTalent extends EntityFilterTalent {
   private MobEffect effect;
   private int amplifier;
   private int duration;
   private float probability;

   public EffectOnHitTalent(
      int unlockLevel, int learnPointCost, int regretPointCost, EntityPredicate[] filter, MobEffect effect, int amplifier, int duration, float probability
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, filter);
      this.effect = effect;
      this.amplifier = amplifier;
      this.duration = duration;
      this.probability = probability;
   }

   public EffectOnHitTalent() {
   }

   public MobEffectInstance toEffect() {
      return new MobEffectInstance(this.effect, this.duration, this.amplifier, false, false, true);
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

                        if (event.getSource().getEntity() instanceof ServerPlayer player) {
                           if (hasConduct || !CritHelper.getCrit(player)) {
                              TalentTree talents = PlayerTalentsData.get(player.getLevel()).getTalents(player);
                              AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
                              if (hasConduct || !(AttackScaleHelper.getLastAttackScale(player) < 1.0F)) {
                                 for (EffectOnHitTalent talent : talents.getAll(EffectOnHitTalent.class, Skill::isUnlocked)) {
                                    if (talent.isValid(event.getEntity())) {
                                       int chances = 1;
                                       if (hasConduct && talent.toEffect().getEffect() == ModEffects.GLACIAL_SHATTER) {
                                          for (AbstractJavelinAbility javelinAbility : abilities.getAll(AbstractJavelinAbility.class, Skill::isUnlocked)) {
                                             if (javelinAbility instanceof JavelinScatterAbility scatterAbility) {
                                                chances = scatterAbility.getPiercing() * scatterAbility.getNumberOfJavelins();
                                             }

                                             if (javelinAbility instanceof JavelinPiercingAbility piercingAbility) {
                                                chances = piercingAbility.getPiercing();
                                             }
                                          }
                                       }

                                       if (ActiveFlags.IS_CHAINING_ATTACKING.isSet()) {
                                          AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
                                          chances = snapshot.getAttributeValue(ModGearAttributes.ON_HIT_CHAIN, VaultGearAttributeTypeMerger.intSum());
                                          if (player.getLevel().getRandom().nextFloat() >= talent.probability / Math.max(1.0F, chances / 2.0F)) {
                                             return;
                                          }

                                          event.getEntityLiving().addEffect(talent.toEffect());
                                       }

                                       if (player.getLevel().getRandom().nextFloat() >= talent.probability / Math.max(1, chances / 2)) {
                                          return;
                                       }

                                       event.getEntityLiving().addEffect(talent.toEffect());
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
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.EFFECT.writeBits((IForgeRegistryEntry)this.effect, buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.amplifier), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.duration), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.probability), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.effect = (MobEffect)Adapters.EFFECT.readBits(buffer).orElseThrow();
      this.amplifier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.duration = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.probability = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.EFFECT.writeNbt((IForgeRegistryEntry)this.effect).ifPresent(tag -> nbt.put("effect", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.amplifier)).ifPresent(tag -> nbt.put("amplifier", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.duration)).ifPresent(tag -> nbt.put("duration", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.probability)).ifPresent(tag -> nbt.put("probability", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.effect = (MobEffect)Adapters.EFFECT.readNbt(nbt.get("effect")).orElseThrow(() -> new IllegalStateException("Unknown effect in " + nbt));
      this.amplifier = Adapters.INT.readNbt(nbt.get("amplifier")).orElse(0);
      this.duration = Adapters.INT.readNbt(nbt.get("duration")).orElse(0);
      this.probability = Adapters.FLOAT.readNbt(nbt.get("probability")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.EFFECT.writeJson((IForgeRegistryEntry)this.effect).ifPresent(element -> json.add("effect", element));
         Adapters.INT.writeJson(Integer.valueOf(this.amplifier)).ifPresent(element -> json.add("amplifier", element));
         Adapters.INT.writeJson(Integer.valueOf(this.duration)).ifPresent(element -> json.add("duration", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.probability)).ifPresent(element -> json.add("probability", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.effect = (MobEffect)Adapters.EFFECT.readJson(json.get("effect")).orElseThrow(() -> new IllegalStateException("Unknown effect in " + json));
      this.amplifier = Adapters.INT.readJson(json.get("amplifier")).orElse(0);
      this.duration = Adapters.INT.readJson(json.get("duration")).orElse(0);
      this.probability = Adapters.FLOAT.readJson(json.get("probability")).orElseThrow();
   }
}
