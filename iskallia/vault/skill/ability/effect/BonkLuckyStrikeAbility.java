package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.champion.ChampionLogic;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.BonkParticleMessage;
import iskallia.vault.skill.ability.effect.spi.AbstractBonkAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class BonkLuckyStrikeAbility extends AbstractBonkAbility {
   private float luckyHitChancePerStack;

   public BonkLuckyStrikeAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float radius,
      float luckyHitChancePerStack,
      int maxStacksUsedPerHit,
      int maxStacksTotal,
      int stackDuration
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, radius, maxStacksUsedPerHit, maxStacksTotal, stackDuration);
      this.luckyHitChancePerStack = luckyHitChancePerStack;
   }

   public BonkLuckyStrikeAbility() {
   }

   public float getLuckyHitChancePerStack() {
      return this.luckyHitChancePerStack;
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
                           7206307,
                           5,
                           20 + (int)(new Random().nextFloat() * 20.0F)
                        )
                     );
                  if (ChampionLogic.isChampion(livingEntity)) {
                     count += CHAMPION_COUNT;
                  } else if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("dungeon"), livingEntity)) {
                     count += DUNGEON_COUNT;
                  } else if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("tank"), livingEntity)) {
                     count += TANK_COUNT;
                  } else if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("assassin"), livingEntity)) {
                     count += ASSASSIN_COUNT;
                  } else if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("horde"), livingEntity)) {
                     count += HORDE_COUNT;
                  } else {
                     count++;
                  }
               }

               if (count > 0) {
                  MobEffectInstance battleCryEffect = new MobEffectInstance(
                     ModEffects.BATTLE_CRY_LUCKY_STRIKE, this.getStackDuration(), Math.min(count, this.getMaxStacksTotal()) - 1, false, false, true
                  );
                  if (player.hasEffect(ModEffects.BATTLE_CRY_LUCKY_STRIKE)) {
                     MobEffectInstance effectInstance = player.getEffect(ModEffects.BATTLE_CRY_LUCKY_STRIKE);
                     if (effectInstance != null) {
                        battleCryEffect = new MobEffectInstance(
                           ModEffects.BATTLE_CRY_LUCKY_STRIKE,
                           this.getStackDuration(),
                           Math.min(effectInstance.getAmplifier() + 1 + count, this.getMaxStacksTotal()) - 1,
                           false,
                           false,
                           true
                        );
                     }
                  }

                  player.removeEffect(ModEffects.BATTLE_CRY_LUCKY_STRIKE);
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

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.luckyHitChancePerStack), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.luckyHitChancePerStack = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.luckyHitChancePerStack)).ifPresent(tag -> nbt.put("luckyHitChancePerStack", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.luckyHitChancePerStack = Adapters.FLOAT.readNbt(nbt.get("luckyHitChancePerStack")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.luckyHitChancePerStack)).ifPresent(element -> json.add("luckyHitChancePerStack", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.luckyHitChancePerStack = Adapters.FLOAT.readJson(json.get("luckyHitChancePerStack")).orElse(0.0F);
   }
}
