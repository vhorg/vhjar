package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.Entropy;
import iskallia.vault.util.calc.ThornsHelper;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ShellQuillAbility extends ShellPorcupineAbility {
   private int quillCount;

   public ShellQuillAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      float additionalDurabilityWearReduction,
      float additionalThornsDamagePercent,
      float additionalManaPerHit,
      int quillCount
   ) {
      super(
         unlockLevel,
         learnPointCost,
         regretPointCost,
         cooldownTicks,
         manaCostPerSecond,
         additionalDurabilityWearReduction,
         additionalThornsDamagePercent,
         additionalManaPerHit
      );
      this.quillCount = quillCount;
   }

   public ShellQuillAbility() {
   }

   public int getQuillCount() {
      return this.quillCount;
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            ModEffects.SHELL_QUILL.addTo(player, 0);
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            player.removeEffect(ModEffects.SHELL_QUILL);
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doToggleSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         if (this.isActive()) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SHELL_QUILL, SoundSource.MASTER, 0.7F, 1.0F);
            player.playNotifySound(ModSounds.SHELL_QUILL, SoundSource.MASTER, 0.7F, 1.0F);
         }
      });
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(ModEffects.SHELL_QUILL)) {
            player.removeEffect(ModEffects.SHELL_QUILL);
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.SHELL_QUILL));
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.SHELL_QUILL));
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void thornsReflectDamage(LivingAttackEvent event) {
      if (!(event.getSource() instanceof ThornsReflectDamageSource) && !ActiveFlags.IS_THORNS_REFLECTING.isSet()) {
         Entity source = event.getSource().getEntity();
         if (source instanceof LivingEntity && event.getEntity() instanceof ServerPlayer player) {
            PlayerAbilitiesData.get(player.getLevel())
               .getAbilities(player)
               .getAll(ShellQuillAbility.class, Ability::isActive)
               .stream()
               .findFirst()
               .ifPresent(ability -> {
                  float thornsChance = ThornsHelper.getThornsChance(player);
                  if (Entropy.canExecute(player, Entropy.Stat.THORNS, thornsChance)) {
                     ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
                        ActiveFlags.IS_THORNS_REFLECTING.push();
                        float reflectedDamage = 0.0F;
                        float dmg = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        float thornsMultiplier = ThornsHelper.getThornsDamageMultiplier(player);
                        if (thornsMultiplier > 0.0F) {
                           reflectedDamage += dmg * thornsMultiplier;
                        }

                        float additionalThornsDamage = ThornsHelper.getAdditionalThornsFlatDamage(player);
                        reflectedDamage += additionalThornsDamage;
                        doQuill(player, reflectedDamage, ability.getQuillCount());
                        if (Mana.decrease(player, ability.getAdditionalManaPerHit()) <= 0.0F) {
                           player.removeEffect(ModEffects.SHELL_QUILL);
                           ability.putOnCooldown(SkillContext.of(player));
                           ability.setActive(false);
                        }
                     });
                  }
               });
         }
      }
   }

   private static void doQuill(Player player, float reflectedDamage, int quillCount) {
      List<Mob> nearby = EntityHelper.getNearby(player.getLevel(), player.blockPosition(), 5.0F, Mob.class);
      nearby.removeIf(mobx -> mobx instanceof EternalEntity);
      if (!nearby.isEmpty()) {
         nearby.sort(Comparator.comparing(e -> e.distanceTo(player)));
         nearby = nearby.subList(0, Math.min(quillCount, nearby.size()));
         float multiplier = 0.5F;

         for (Mob mob : nearby) {
            Vec3 movement = mob.getDeltaMovement();
            mob.hurt(ThornsReflectDamageSource.of(player), reflectedDamage * multiplier);
            mob.setDeltaMovement(movement);
            multiplier *= 0.5F;
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.quillCount), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.quillCount = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.quillCount)).ifPresent(tag -> nbt.put("quillCount", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.quillCount = Adapters.INT.readNbt(nbt.get("quillCount")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.quillCount)).ifPresent(element -> json.add("quillCount", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.quillCount = Adapters.INT.readJson(json.get("quillCount")).orElse(0);
   }

   public static class ShellQuillEffect extends ShellPorcupineAbility.ShellPorcupineEffect {
      public ShellQuillEffect(int color, ResourceLocation resourceLocation) {
         super(ShellAbility.class, color, resourceLocation);
         this.addAttributeModifier(
            Attributes.KNOCKBACK_RESISTANCE, Mth.createInsecureUUID(new Random(resourceLocation.hashCode())).toString(), 0.01, Operation.ADDITION
         );
      }
   }
}
