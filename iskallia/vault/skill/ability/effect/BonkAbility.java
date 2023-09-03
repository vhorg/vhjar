package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.event.ActiveFlagsCheck;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.BonkParticleMessage;
import iskallia.vault.skill.ability.effect.spi.AbstractBonkAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.damage.AttackScaleHelper;
import iskallia.vault.util.damage.CritHelper;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
public class BonkAbility extends AbstractBonkAbility {
   private float attackDamagePerStack;

   public BonkAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float radius,
      float attackDamagePerStack,
      int maxStacksUsedPerHit,
      int maxStacksTotal,
      int stackDuration
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, radius, maxStacksUsedPerHit, maxStacksTotal, stackDuration);
      this.attackDamagePerStack = attackDamagePerStack;
   }

   public BonkAbility() {
   }

   public float getAttackDamagePerStack() {
      return this.attackDamagePerStack;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               Vec3 pos = context.getSource().getPos().orElse(player.position());
               List<LivingEntity> targetEntities = this.getTargetEntities(player.level, player, pos);
               int count = 0;

               for (LivingEntity livingEntity : targetEntities) {
                  ModNetwork.CHANNEL
                     .send(
                        PacketDistributor.ALL.noArg(),
                        new BonkParticleMessage(
                           new Vec3(livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() / 2.0F, livingEntity.getZ()),
                           player.getId(),
                           9306112,
                           5,
                           20 + (int)(new Random().nextFloat() * 20.0F)
                        )
                     );
                  count += EntityHelper.getEntityValue(livingEntity);
               }

               if (count > 0) {
                  MobEffectInstance battleCryEffect = new MobEffectInstance(
                     ModEffects.BATTLE_CRY, this.getStackDuration(), Math.min(count, this.getMaxStacksTotal()) - 1, false, false, true
                  );
                  if (player.hasEffect(ModEffects.BATTLE_CRY)) {
                     MobEffectInstance effectInstance = player.getEffect(ModEffects.BATTLE_CRY);
                     if (effectInstance != null) {
                        battleCryEffect = new MobEffectInstance(
                           ModEffects.BATTLE_CRY,
                           this.getStackDuration(),
                           Math.min(effectInstance.getAmplifier() + 1 + count, this.getMaxStacksTotal()) - 1,
                           false,
                           false,
                           true
                        );
                     }
                  }

                  player.removeEffect(ModEffects.BATTLE_CRY);
                  player.addEffect(battleCryEffect);
               }

               return Ability.ActionResult.successCooldownImmediate();
            }
         )
         .orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         Vec3 pos = context.getSource().getPos().orElse(player.position());
         player.level.playSound(player, pos.x, pos.y, pos.z, ModSounds.BONK_CHARGE, SoundSource.PLAYERS, 1.0F, 0.7F);
         player.playNotifySound(ModSounds.BONK_CHARGE, SoundSource.PLAYERS, 1.0F, 0.7F);
      });
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void on(LivingHurtEvent event) {
      if (!ActiveFlagsCheck.isAnyFlagActiveLuckyHit()) {
         if (!(event.getSource() instanceof ThornsReflectDamageSource)) {
            if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
               if (attacker.hasEffect(ModEffects.BATTLE_CRY)) {
                  if (!CritHelper.getCrit(attacker)) {
                     if (!(AttackScaleHelper.getLastAttackScale(attacker) < 1.0F)) {
                        MobEffectInstance battleCry = attacker.getEffect(ModEffects.BATTLE_CRY);
                        if (battleCry != null) {
                           AbilityTree abilities = PlayerAbilitiesData.get((ServerLevel)attacker.level).getAbilities(attacker);

                           for (BonkAbility ability : abilities.getAll(BonkAbility.class, Skill::isUnlocked)) {
                              int stacksUsed = ability.getMaxStacksUsedPerHit();
                              MobEffectInstance newBattleCry = null;
                              if (battleCry.getAmplifier() - stacksUsed >= 0) {
                                 newBattleCry = new MobEffectInstance(
                                    battleCry.getEffect(), battleCry.getDuration(), battleCry.getAmplifier() - stacksUsed, false, false, true
                                 );
                              } else {
                                 stacksUsed = battleCry.getAmplifier() + 1;
                              }

                              float damage = event.getAmount();
                              event.setAmount(
                                 damage + (float)attacker.getAttributeValue(Attributes.ATTACK_DAMAGE) * ability.getAttackDamagePerStack() * stacksUsed
                              );
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
                                       9306112,
                                       5 * stacksUsed,
                                       5 + (int)(new Random().nextFloat() * 10.0F)
                                    )
                                 );
                              attacker.removeEffect(ModEffects.BATTLE_CRY);
                              if (newBattleCry != null) {
                                 attacker.addEffect(newBattleCry);
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
      Adapters.FLOAT.writeBits(Float.valueOf(this.attackDamagePerStack), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.attackDamagePerStack = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.attackDamagePerStack)).ifPresent(tag -> nbt.put("attackDamagePerStack", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.attackDamagePerStack = Adapters.FLOAT.readNbt(nbt.get("attackDamagePerStack")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.attackDamagePerStack)).ifPresent(element -> json.add("attackDamagePerStack", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.attackDamagePerStack = Adapters.FLOAT.readJson(json.get("attackDamagePerStack")).orElse(0.0F);
   }
}
