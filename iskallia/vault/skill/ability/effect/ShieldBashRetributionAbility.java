package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.util.calc.ThornsHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.EventPriority;

public class ShieldBashRetributionAbility extends InstantManaAbility {
   private int chargeTime;
   private float damageRadius;
   private float damagePerStack;
   private final Map<EntityPredicate, Integer> stackage;
   private int timeLeft;
   private int stacks;

   public ShieldBashRetributionAbility() {
      this.stackage = new LinkedHashMap<>();
   }

   public ShieldBashRetributionAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      int manaCost,
      int chargeTime,
      float damageRadius,
      float damagePerStack,
      Map<EntityPredicate, Integer> stackage
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.chargeTime = chargeTime;
      this.damageRadius = damageRadius;
      this.damagePerStack = damagePerStack;
      this.stackage = new LinkedHashMap<>(stackage);
      this.timeLeft = 0;
      this.stacks = 0;
   }

   public int getChargeTime() {
      return this.chargeTime;
   }

   public float getUnmodifiedDamageRadius() {
      return this.damageRadius;
   }

   public float getRadius(Entity attacker) {
      float realRadius = this.getUnmodifiedDamageRadius();
      if (attacker instanceof LivingEntity livingEntity) {
         realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, this, realRadius);
      }

      return realRadius;
   }

   public float getDamagePerStack() {
      return this.damagePerStack;
   }

   public Map<EntityPredicate, Integer> getStackage() {
      return this.stackage;
   }

   @Override
   public void onTick(SkillContext context) {
      super.onTick(context);
      if (this.timeLeft <= 0) {
         this.stacks = 0;
      } else {
         this.timeLeft--;
         if (this.isUnlocked() && this.timeLeft > 0 && this.stacks > 0) {
            context.getSource().as(ServerPlayer.class).ifPresent(player -> {
               player.removeEffect(ModEffects.SHIELD_BASH_RETRIBUTION);
               player.addEffect(new MobEffectInstance(ModEffects.SHIELD_BASH_RETRIBUTION, this.timeLeft, this.stacks - 1, true, false, true));
            });
         } else if (this.timeLeft == 0) {
            context.getSource()
               .as(ServerPlayer.class)
               .ifPresent(
                  player -> {
                     Vec3 pos = context.getSource().getPos().orElse(player.position());
                     float thornsDamage = ThornsHelper.getAdditionalThornsFlatDamage(player);
                     float totalDamage = thornsDamage * this.getDamagePerStack() * this.stacks;
                     if (totalDamage > 0.0F) {
                        List<LivingEntity> targetEntities = this.getTargetEntities(player.level, player, pos);
                        DamageSource damageSource = DamageSource.playerAttack(player);

                        for (LivingEntity entity : targetEntities) {
                           if (!entity.isInvulnerableTo(damageSource)) {
                              ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> entity.hurt(damageSource, totalDamage));
                           }
                        }
                     }

                     player.getLevel()
                        .sendParticles(
                           new SphericalParticleOptions(
                              (ParticleType<SphericalParticleOptions>)ModParticles.IMPLODE.get(), this.getRadius(player), new Vector3f(0.8F, 0.0F, 0.0F)
                           ),
                           player.getX(),
                           player.getY(),
                           player.getZ(),
                           400,
                           0.0,
                           0.0,
                           0.0,
                           0.0
                        );
                     player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANA_SHIELD, SoundSource.PLAYERS, 0.2F, 0.2F);
                     player.playNotifySound(ModSounds.MANA_SHIELD, SoundSource.PLAYERS, 0.2F, 0.2F);
                  }
               );
            this.stacks = 0;
         }
      }
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         this.timeLeft = this.chargeTime;
         player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANA_SHIELD, SoundSource.PLAYERS, 1.0F, 1.0F);
         player.playNotifySound(ModSounds.MANA_SHIELD, SoundSource.PLAYERS, 1.0F, 1.0F);
         return Ability.ActionResult.successCooldownDelayed(this.chargeTime);
      }).orElse(Ability.ActionResult.fail());
   }

   private void onTakeDamage(Entity attacker, Entity attacked, float amount) {
      for (Entry<EntityPredicate, Integer> entry : this.getStackage().entrySet()) {
         if (entry.getKey() != null && entry.getKey().test(attacker)) {
            this.stacks = this.stacks + entry.getValue();
            break;
         }
      }
   }

   protected List<LivingEntity> getTargetEntities(Level world, LivingEntity attacker, Vec3 pos) {
      float radius = this.getRadius(attacker);
      return world.getNearbyEntities(
         LivingEntity.class,
         TargetingConditions.forCombat().range(radius).selector(entity -> !(entity instanceof Player) && !(entity instanceof EternalEntity)),
         attacker,
         AABBHelper.create(pos, radius)
      );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.chargeTime), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageRadius), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damagePerStack), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.stackage.size()), buffer);
      this.stackage.forEach((filter, stacks) -> {
         Adapters.ENTITY_PREDICATE.writeBits(filter, buffer);
         Adapters.INT_SEGMENTED_3.writeBits(stacks, buffer);
      });
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.timeLeft), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.stacks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.chargeTime = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.damageRadius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.damagePerStack = Adapters.FLOAT.readBits(buffer).orElseThrow();
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.stackage.clear();

      for (int i = 0; i < size; i++) {
         this.stackage.put(Adapters.ENTITY_PREDICATE.readBits(buffer).orElse(null), Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow());
      }

      this.timeLeft = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.stacks = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt()
         .map(
            nbt -> {
               Adapters.INT.writeNbt(Integer.valueOf(this.chargeTime)).ifPresent(tag -> nbt.put("chargeTime", tag));
               Adapters.FLOAT.writeNbt(Float.valueOf(this.damageRadius)).ifPresent(tag -> nbt.put("damageRadius", tag));
               Adapters.FLOAT.writeNbt(Float.valueOf(this.damagePerStack)).ifPresent(tag -> nbt.put("damagePerStack", tag));
               Adapters.INT.writeNbt(Integer.valueOf(this.timeLeft)).ifPresent(tag -> nbt.put("timeLeft", tag));
               Adapters.INT.writeNbt(Integer.valueOf(this.stacks)).ifPresent(tag -> nbt.put("stacks", tag));
               ListTag stackage = new ListTag();
               this.stackage
                  .forEach((filter, stacks) -> Adapters.ENTITY_PREDICATE.writeNbt(filter).ifPresent(tag1 -> Adapters.INT.writeNbt(stacks).ifPresent(tag2 -> {
                     CompoundTag entry = new CompoundTag();
                     entry.put("filter", tag1);
                     entry.put("stacks", tag2);
                     stackage.add(entry);
                  })));
               nbt.put("stackage", stackage);
               return (CompoundTag)nbt;
            }
         );
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.chargeTime = Adapters.INT.readNbt(nbt.get("chargeTime")).orElse(0);
      this.damageRadius = Adapters.FLOAT.readNbt(nbt.get("damageRadius")).orElse(0.0F);
      this.damagePerStack = Adapters.FLOAT.readNbt(nbt.get("damagePerStack")).orElse(0.0F);
      this.timeLeft = Adapters.INT.readNbt(nbt.get("timeLeft")).orElse(0);
      this.stacks = Adapters.INT.readNbt(nbt.get("stacks")).orElse(0);
      this.stackage.clear();
      if (nbt.get("stackage") instanceof CompoundTag compound) {
         for (String key : compound.getAllKeys()) {
            Adapters.ENTITY_PREDICATE
               .readNbt(StringTag.valueOf(key))
               .ifPresent(predicate -> Adapters.INT.readNbt(compound.get(key)).ifPresent(stacks -> this.stackage.put(predicate, stacks)));
         }
      } else {
         Tag var8 = nbt.get("stackage");
         if (var8 instanceof ListTag) {
            for (Tag tag : (ListTag)var8) {
               if (tag instanceof CompoundTag entry) {
                  Adapters.ENTITY_PREDICATE
                     .readNbt(entry.get("filter"))
                     .ifPresent(predicate -> Adapters.INT.readNbt(entry.get("stacks")).ifPresent(stacks -> this.stackage.put(predicate, stacks)));
               }
            }
         }
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson()
         .map(
            json -> {
               Adapters.INT.writeJson(Integer.valueOf(this.chargeTime)).ifPresent(tag -> json.add("chargeTime", tag));
               Adapters.FLOAT.writeJson(Float.valueOf(this.damageRadius)).ifPresent(tag -> json.add("damageRadius", tag));
               Adapters.FLOAT.writeJson(Float.valueOf(this.damagePerStack)).ifPresent(tag -> json.add("damagePerStack", tag));
               Adapters.INT.writeJson(Integer.valueOf(this.timeLeft)).ifPresent(tag -> json.add("timeLeft", tag));
               Adapters.INT.writeJson(Integer.valueOf(this.stacks)).ifPresent(tag -> json.add("stacks", tag));
               JsonArray stackage = new JsonArray();
               this.stackage
                  .forEach((filter, stacks) -> Adapters.ENTITY_PREDICATE.writeJson(filter).ifPresent(tag1 -> Adapters.INT.writeJson(stacks).ifPresent(tag2 -> {
                     JsonObject entry = new JsonObject();
                     entry.add("filter", tag1);
                     entry.add("stacks", tag2);
                     stackage.add(entry);
                  })));
               json.add("stackage", stackage);
               return (JsonObject)json;
            }
         );
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.chargeTime = Adapters.INT.readJson(json.get("chargeTime")).orElse(0);
      this.damageRadius = Adapters.FLOAT.readJson(json.get("damageRadius")).orElse(0.0F);
      this.damagePerStack = Adapters.FLOAT.readJson(json.get("damagePerStack")).orElse(0.0F);
      this.timeLeft = Adapters.INT.readJson(json.get("timeLeft")).orElse(0);
      this.stacks = Adapters.INT.readJson(json.get("stacks")).orElse(0);
      this.stackage.clear();
      if (json.get("stackage") instanceof JsonObject object) {
         for (String key : object.keySet()) {
            Adapters.ENTITY_PREDICATE
               .readJson(new JsonPrimitive(key))
               .ifPresent(predicate -> Adapters.INT.readJson(object.get(key)).ifPresent(stacks -> this.stackage.put(predicate, stacks)));
         }
      } else if (json.get("stackage") instanceof JsonArray array) {
         for (int i = 0; i < array.size(); i++) {
            if (array.get(i) instanceof JsonObject entry) {
               Adapters.ENTITY_PREDICATE
                  .readJson(entry.get("filter"))
                  .ifPresent(predicate -> Adapters.INT.readJson(entry.get("stacks")).ifPresent(stacks -> this.stackage.put(predicate, stacks)));
            }
         }
      }
   }

   static {
      CommonEvents.ENTITY_DAMAGE
         .register(
            ShieldBashRetributionAbility.class,
            EventPriority.HIGHEST,
            event -> {
               if (!event.getEntity().level.isClientSide()
                  && event.getEntity().getServer() != null
                  && event.getEntity() instanceof Player
                  && event.getSource().getEntity() instanceof LivingEntity attacker) {
                  PlayerAbilitiesData data = PlayerAbilitiesData.get(event.getEntity().getServer());

                  for (ShieldBashRetributionAbility ability : data.getAbilities(event.getEntity().getUUID())
                     .getAll(ShieldBashRetributionAbility.class, abilityx -> true)) {
                     ability.onTakeDamage(attacker, event.getEntity(), event.getAmount());
                  }
               }
            }
         );
   }
}
