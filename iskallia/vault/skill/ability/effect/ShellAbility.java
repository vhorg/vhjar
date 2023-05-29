package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityStunnedEvent;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.mana.Mana;
import iskallia.vault.network.message.StunnedParticleMessage;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber
public class ShellAbility extends AbstractShellAbility {
   private float additionalManaPerHit;
   private float stunChance;
   private int stunDurationTicks;
   private int stunAmplifier;

   public ShellAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCostPerSecond) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond);
   }

   public ShellAbility() {
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            ModEffects.SHELL.addTo(player, 0);
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            player.removeEffect(ModEffects.SHELL);
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doToggleSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         if (this.isActive()) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SHELL, SoundSource.MASTER, 0.7F, 1.0F);
            player.playNotifySound(ModSounds.SHELL, SoundSource.MASTER, 0.7F, 1.0F);
         }
      });
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(ModEffects.SHELL)) {
            player.removeEffect(ModEffects.SHELL);
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.SHELL));
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.SHELL));
   }

   @SubscribeEvent
   public static void onDamage(LivingAttackEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         abilities.getAll(ShellAbility.class, Ability::isActive)
            .stream()
            .findFirst()
            .ifPresent(
               ability -> {
                  if (!(player.getLevel().random.nextDouble() > ability.stunChance)) {
                     if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                        attacker.addEffect(
                           new MobEffectInstance(ModEffects.NO_AI, 30, 1, false, false) {
                              public boolean tick(LivingEntity livingEntity, Runnable p_19554_) {
                                 if (!livingEntity.isDeadOrDying()) {
                                    ModNetwork.CHANNEL
                                       .send(
                                          PacketDistributor.ALL.noArg(),
                                          new StunnedParticleMessage(
                                             new Vec3(livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight(), livingEntity.getZ()),
                                             livingEntity.getBbWidth()
                                          )
                                       );
                                 }

                                 return super.tick(livingEntity, p_19554_);
                              }
                           }
                        );
                        CommonEvents.ENTITY_STUNNED.invoke(new EntityStunnedEvent.Data(player, attacker));
                        if (Mana.decrease(player, ability.additionalManaPerHit) <= 0.0F) {
                           player.removeEffect(ModEffects.SHELL);
                           ability.putOnCooldown(SkillContext.of(player));
                           ability.setActive(false);
                        }
                     }
                  }
               }
            );
      }
   }

   public float getStunChance() {
      return this.stunChance;
   }

   public int getStunDurationSeconds() {
      return this.stunDurationTicks / 20;
   }

   public int getStunAmplifier() {
      return this.stunAmplifier;
   }

   public float getAdditionalManaPerHit() {
      return this.additionalManaPerHit;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.additionalManaPerHit), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.stunChance), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.stunDurationTicks), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.stunAmplifier), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalManaPerHit = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.stunChance = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.stunDurationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.stunAmplifier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.additionalManaPerHit)).ifPresent(tag -> nbt.put("additionalManaPerHit", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.stunChance)).ifPresent(tag -> nbt.put("stunChance", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.stunDurationTicks)).ifPresent(tag -> nbt.put("stunDurationTicks", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.stunAmplifier)).ifPresent(tag -> nbt.put("stunAmplifier", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalManaPerHit = Adapters.FLOAT.readNbt(nbt.get("additionalManaPerHit")).orElse(0.0F);
      this.stunChance = Adapters.FLOAT.readNbt(nbt.get("stunChance")).orElse(0.0F);
      this.stunDurationTicks = Adapters.INT.readNbt(nbt.get("stunDurationTicks")).orElse(0);
      this.stunAmplifier = Adapters.INT.readNbt(nbt.get("stunAmplifier")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.additionalManaPerHit)).ifPresent(element -> json.add("additionalManaPerHit", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.stunChance)).ifPresent(element -> json.add("stunChance", element));
         Adapters.INT.writeJson(Integer.valueOf(this.stunDurationTicks)).ifPresent(element -> json.add("stunDurationTicks", element));
         Adapters.INT.writeJson(Integer.valueOf(this.stunAmplifier)).ifPresent(element -> json.add("stunAmplifier", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalManaPerHit = Adapters.FLOAT.readJson(json.get("additionalManaPerHit")).orElse(0.0F);
      this.stunChance = Adapters.FLOAT.readJson(json.get("stunChance")).orElse(0.0F);
      this.stunDurationTicks = Adapters.INT.readJson(json.get("stunDurationTicks")).orElse(0);
      this.stunAmplifier = Adapters.INT.readJson(json.get("stunAmplifier")).orElse(0);
   }

   public static class ShellEffect extends EmpowerAbility.EmpowerEffect {
      public ShellEffect(int color, ResourceLocation resourceLocation) {
         super(ShellPorcupineAbility.class, color, resourceLocation);
      }
   }
}
