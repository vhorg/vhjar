package iskallia.vault.skill.talent.type.luckyhit;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.LuckyHitParticleMessage;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.util.calc.LuckyHitHelper;
import iskallia.vault.util.damage.AttackScaleHelper;
import iskallia.vault.util.damage.CritHelper;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
      if (!ActiveFlags.IS_AOE_ATTACKING.isSet()) {
         if (!ActiveFlags.IS_TOTEM_ATTACKING.isSet()) {
            if (!ActiveFlags.IS_CHARMED_ATTACKING.isSet()) {
               if (!ActiveFlags.IS_DOT_ATTACKING.isSet()) {
                  if (!ActiveFlags.IS_REFLECT_ATTACKING.isSet()) {
                     if (!ActiveFlags.IS_EFFECT_ATTACKING.isSet()) {
                        if (!ActiveFlags.IS_JAVELIN_ATTACKING.isSet()) {
                           if (!ActiveFlags.IS_SMITE_ATTACKING.isSet()) {
                              if (!(event.getSource() instanceof ThornsReflectDamageSource)) {
                                 if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
                                    if (!CritHelper.getCrit(attacker)) {
                                       if (!(AttackScaleHelper.getLastAttackScale(attacker) < 1.0F)) {
                                          float probability = LuckyHitHelper.getLuckyHitChance(attacker);
                                          if (!(attacker.getLevel().getRandom().nextFloat() >= probability)) {
                                             TalentTree tree = PlayerTalentsData.get((ServerLevel)attacker.level).getTalents(attacker);
                                             boolean hasLuckyHit = false;

                                             for (LuckyHitTalent talent : tree.getAll(LuckyHitTalent.class, Skill::isUnlocked)) {
                                                talent.onLuckyHit(event);
                                                hasLuckyHit = true;
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
