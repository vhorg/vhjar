package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityStunnedEvent;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.StonefallFrostParticleMessage;
import iskallia.vault.skill.ability.effect.spi.AbstractStonefallAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class StonefallColdAbility extends AbstractStonefallAbility {
   private int amplifier;
   private int freezeTicks;

   public StonefallColdAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      int durationTicks,
      float knockbackMultiplier,
      float radius,
      float damageReduction,
      int amplifier,
      int freezeTicks
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, durationTicks, knockbackMultiplier, radius, damageReduction);
      this.amplifier = amplifier;
      this.freezeTicks = freezeTicks;
   }

   public StonefallColdAbility() {
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public int getFreezeTicks() {
      return this.freezeTicks;
   }

   @Override
   protected boolean canDoAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> !player.hasEffect(ModEffects.STONEFALL_COLD) && super.canDoAction(context)).orElse(false);
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         player.addEffect(new MobEffectInstance(ModEffects.STONEFALL_COLD, this.getDurationTicks(), 0, false, false, true));
         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @SubscribeEvent
   public static void on(LivingFallEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         MobEffectInstance effectInstance = player.getEffect(ModEffects.STONEFALL_COLD);
         if (effectInstance != null) {
            float dist = event.getDistance();
            Level level = player.level;
            if (!(dist < 3.0F)) {
               AbilityTree abilities = PlayerAbilitiesData.get((ServerLevel)player.level).getAbilities(player);

               for (StonefallColdAbility ability : abilities.getAll(StonefallColdAbility.class, Skill::isUnlocked)) {
                  float radius = ability.getRadius() + Mth.clamp(dist / 3.75F, 0.0F, 8.0F);
                  List<LivingEntity> nearby = EntityHelper.getNearby(level, player.blockPosition(), radius, LivingEntity.class);
                  nearby.removeIf(mob -> mob instanceof EternalEntity || mob instanceof ServerPlayer);
                  nearby.forEach(mob -> {
                     EntityHelper.knockbackWithStrength(mob, player, 0.2F);
                     mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ability.getFreezeTicks(), ability.getAmplifier()));
                     CommonEvents.ENTITY_STUNNED.invoke(new EntityStunnedEvent.Data(player, mob));
                  });
                  event.setDamageMultiplier(Mth.clamp(1.0F - ability.getDamageReduction(), 0.0F, 1.0F));
                  ModNetwork.CHANNEL
                     .send(
                        PacketDistributor.ALL.noArg(), new StonefallFrostParticleMessage(new Vec3(player.getX(), player.getY() + 0.15F, player.getZ()), radius)
                     );
                  player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.NOVA_SPEED, SoundSource.PLAYERS, 0.2F, 1.0F);
                  player.playNotifySound(ModSounds.NOVA_SPEED, SoundSource.PLAYERS, 0.2F, 1.0F);
               }
            }
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.amplifier), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.freezeTicks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.amplifier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.freezeTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.amplifier)).ifPresent(tag -> nbt.put("amplifier", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.freezeTicks)).ifPresent(tag -> nbt.put("freezeTicks", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.amplifier = Adapters.INT.readNbt(nbt.get("amplifier")).orElse(0);
      this.freezeTicks = Adapters.INT.readNbt(nbt.get("freezeTicks")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.amplifier)).ifPresent(element -> json.add("amplifier", element));
         Adapters.INT.writeJson(Integer.valueOf(this.freezeTicks)).ifPresent(element -> json.add("freezeTicks", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.amplifier = Adapters.INT.readJson(json.get("amplifier")).orElse(0);
      this.freezeTicks = Adapters.INT.readJson(json.get("freezeTicks")).orElse(0);
   }

   public static class StonefallColdEffect extends MobEffect {
      public StonefallColdEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(resourceLocation);
      }
   }
}
