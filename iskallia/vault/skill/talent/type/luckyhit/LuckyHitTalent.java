package iskallia.vault.skill.talent.type.luckyhit;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.BonkParticleMessage;
import iskallia.vault.network.message.LuckyHitParticleMessage;
import iskallia.vault.skill.ability.effect.BonkLuckyStrikeAbility;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.util.calc.LuckyHitHelper;
import iskallia.vault.util.damage.AttackScaleHelper;
import iskallia.vault.util.damage.CritHelper;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.List;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public abstract class LuckyHitTalent extends LearnableSkill {
   public LuckyHitTalent(int unlockLevel, int learnPointCost, int regretPointCost) {
      super(unlockLevel, learnPointCost, regretPointCost);
   }

   protected LuckyHitTalent() {
   }

   public abstract void onLuckyHit(LivingHurtEvent var1);

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void doLuckyHit(LivingHurtEvent event) {
      if (!ActiveFlags.IS_CHAINING_ATTACKING.isSet()) {
         if (!ActiveFlags.IS_AOE_ATTACKING.isSet()) {
            if (!ActiveFlags.IS_TOTEM_ATTACKING.isSet()) {
               if (!ActiveFlags.IS_CHARMED_ATTACKING.isSet()) {
                  if (!ActiveFlags.IS_DOT_ATTACKING.isSet()) {
                     if (!ActiveFlags.IS_REFLECT_ATTACKING.isSet()) {
                        if (!ActiveFlags.IS_EFFECT_ATTACKING.isSet()) {
                           if (!ActiveFlags.IS_JAVELIN_ATTACKING.isSet()) {
                              if (!ActiveFlags.IS_SMITE_ATTACKING.isSet() && !ActiveFlags.IS_SMITE_BASE_ATTACKING.isSet()) {
                                 if (!(event.getSource() instanceof ThornsReflectDamageSource)) {
                                    if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
                                       if (!CritHelper.getCrit(attacker)) {
                                          if (!(AttackScaleHelper.getLastAttackScale(attacker) < 1.0F)) {
                                             float probability = LuckyHitHelper.getLuckyHitChance(attacker);
                                             MobEffectInstance battleCry = attacker.getEffect(ModEffects.BATTLE_CRY_LUCKY_STRIKE);
                                             if (battleCry != null) {
                                                AbilityTree abilities = PlayerAbilitiesData.get((ServerLevel)attacker.level).getAbilities(attacker);

                                                for (BonkLuckyStrikeAbility ability : abilities.getAll(BonkLuckyStrikeAbility.class, Skill::isUnlocked)) {
                                                   int stacksUsed = ability.getMaxStacksUsedPerHit();
                                                   MobEffectInstance newBattleCry = null;
                                                   if (battleCry.getAmplifier() - stacksUsed >= 0) {
                                                      newBattleCry = new MobEffectInstance(
                                                         battleCry.getEffect(),
                                                         battleCry.getDuration(),
                                                         battleCry.getAmplifier() - stacksUsed,
                                                         false,
                                                         false,
                                                         true
                                                      );
                                                   } else {
                                                      stacksUsed = battleCry.getAmplifier() + 1;
                                                   }

                                                   probability += ability.getLuckyHitChancePerStack() * stacksUsed;
                                                   attacker.level
                                                      .playSound(
                                                         attacker,
                                                         attacker.position().x,
                                                         attacker.position().y,
                                                         attacker.position().z,
                                                         ModSounds.BONK,
                                                         SoundSource.PLAYERS,
                                                         1.0F,
                                                         0.7F
                                                      );
                                                   attacker.playNotifySound(ModSounds.BONK, SoundSource.PLAYERS, 1.0F, 0.7F);
                                                   ModNetwork.CHANNEL
                                                      .send(
                                                         PacketDistributor.ALL.noArg(),
                                                         new BonkParticleMessage(
                                                            new Vec3(attacker.getX(), attacker.getY() + attacker.getBbHeight() / 3.0F, attacker.getZ()),
                                                            event.getEntity().getId(),
                                                            7206307,
                                                            5 * stacksUsed,
                                                            5 + (int)(new Random().nextFloat() * 10.0F)
                                                         )
                                                      );
                                                   attacker.removeEffect(ModEffects.BATTLE_CRY_LUCKY_STRIKE);
                                                   if (newBattleCry != null) {
                                                      attacker.addEffect(newBattleCry);
                                                   }
                                                }
                                             }

                                             if (!(attacker.getLevel().getRandom().nextFloat() >= probability)) {
                                                TalentTree tree = PlayerTalentsData.get((ServerLevel)attacker.level).getTalents(attacker);
                                                boolean hasLuckyHit = false;
                                                List<LuckyHitTalent> luckyHitTalents = tree.getAll(LuckyHitTalent.class, Skill::isUnlocked);

                                                for (LuckyHitTalent talent : luckyHitTalents) {
                                                   if (talent instanceof SweepingLuckyHitTalent cleave) {
                                                      talent.onLuckyHit(event);
                                                      hasLuckyHit = true;
                                                   }
                                                }

                                                for (LuckyHitTalent talentx : luckyHitTalents) {
                                                   if (!(talentx instanceof SweepingLuckyHitTalent cleave)) {
                                                      talentx.onLuckyHit(event);
                                                      hasLuckyHit = true;
                                                   }
                                                }

                                                if (!hasLuckyHit) {
                                                   event.setAmount(event.getAmount() * 1.1F);
                                                }

                                                event.getEntity()
                                                   .getLevel()
                                                   .playSound(null, event.getEntity(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.BLOCKS, 1.0F, 1.75F);
                                                ModNetwork.CHANNEL
                                                   .send(
                                                      PacketDistributor.ALL.noArg(),
                                                      new LuckyHitParticleMessage(
                                                         new Vec3(
                                                            event.getEntity().getX(),
                                                            event.getEntity().getY() + event.getEntity().getBbHeight(),
                                                            event.getEntity().getZ()
                                                         )
                                                      )
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
               }
            }
         }
      }
   }
}
