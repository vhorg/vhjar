package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ManaShieldAbility extends InstantManaAbility {
   private int durationTicks;
   private int healthPoints;

   public ManaShieldAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, int manaCost, int duration, int healthPoints) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.durationTicks = duration;
      this.healthPoints = healthPoints;
   }

   public ManaShieldAbility() {
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }

   public int getHealthPoints() {
      return this.healthPoints;
   }

   protected MobEffect getEffect() {
      return ModEffects.MANA_SHIELD;
   }

   @Override
   protected boolean canDoAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> !player.hasEffect(this.getEffect())
               || player.getAttribute(ModAttributes.MANA_SHIELD).getValue() < this.healthPoints && super.canDoAction(context)
         )
         .orElse(false);
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(this.getEffect())) {
            player.removeEffect(this.getEffect());
         }

         player.addEffect(new MobEffectInstance(this.getEffect(), this.getDurationTicks(), 0, false, false, true));
         player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANA_SHIELD, SoundSource.PLAYERS, 0.2F, 0.2F);
         return Ability.ActionResult.successCooldownDeferred();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(this.getEffect()));
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void on(LivingDamageEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && !(event.getAmount() <= 0.0F)) {
         AttributeInstance manaShieldAttribute = player.getAttribute(ModAttributes.MANA_SHIELD);
         AttributeModifier manaShieldAttributeModifier = manaShieldAttribute.getModifier(ManaShieldAbility.ManaShieldEffect.MANA_SHIELD_MODIFIER_ID);
         if (manaShieldAttributeModifier != null) {
            double manaShieldHealthPoints = manaShieldAttributeModifier.getAmount();
            if (!(manaShieldHealthPoints <= 0.0)) {
               double manaShieldLost = Math.min(manaShieldHealthPoints, (double)event.getAmount());
               event.setAmount(event.getAmount() - (float)manaShieldLost);
               manaShieldAttribute.removeModifier(ManaShieldAbility.ManaShieldEffect.MANA_SHIELD_MODIFIER_ID);
               double remainingManaShieldHealthPoints = manaShieldHealthPoints - manaShieldLost;
               float pitch = 0.7F + player.getLevel().getRandom().nextFloat(0.6F);
               player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANA_SHIELD_HIT, SoundSource.PLAYERS, 0.1F, pitch);
               if (remainingManaShieldHealthPoints <= 0.0) {
                  player.removeEffect(ModEffects.MANA_SHIELD);
               } else {
                  manaShieldAttribute.addTransientModifier(
                     new AttributeModifier(
                        ManaShieldAbility.ManaShieldEffect.MANA_SHIELD_MODIFIER_ID, "Mana Shield", remainingManaShieldHealthPoints, Operation.ADDITION
                     )
                  );
               }
            }
         }
      }
   }

   protected void onDamageAbsorbed(ServerPlayer player, float amount) {
   }

   protected void onEffectRemoved(ServerPlayer player) {
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.durationTicks), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.healthPoints), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.durationTicks = Adapters.INT.readBits(buffer).orElseThrow();
      this.healthPoints = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.durationTicks)).ifPresent(tag -> nbt.put("durationTicks", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.healthPoints)).ifPresent(tag -> nbt.put("healthPoints", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.durationTicks = Adapters.INT.readNbt(nbt.get("durationTicks")).orElse(0);
      this.healthPoints = Adapters.INT.readNbt(nbt.get("healthPoints")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.durationTicks)).ifPresent(element -> json.add("durationTicks", element));
         Adapters.INT.writeJson(Integer.valueOf(this.healthPoints)).ifPresent(element -> json.add("healthPoints", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.durationTicks = Adapters.INT.readJson(json.get("durationTicks")).orElse(0);
      this.healthPoints = Adapters.INT.readJson(json.get("healthPoints")).orElse(0);
   }

   public static class ManaShieldEffect extends MobEffect {
      private static final UUID MANA_SHIELD_MODIFIER_ID = UUID.fromString("f0761ea3-2f69-4da3-a1b4-2d7d5ea21f8d");

      public ManaShieldEffect(int color, ResourceLocation registryName) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(registryName);
      }

      public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
         super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
         if (pLivingEntity instanceof ServerPlayer player) {
            AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
            List<ManaShieldAbility> manaShieldAbilities = abilities.getAll(ManaShieldAbility.class, Skill::isUnlocked);
            Iterator var7 = manaShieldAbilities.iterator();
            if (var7.hasNext()) {
               ManaShieldAbility manaShieldAbility = (ManaShieldAbility)var7.next();
               player.getAttribute(ModAttributes.MANA_SHIELD)
                  .addTransientModifier(new AttributeModifier(MANA_SHIELD_MODIFIER_ID, "Mana Shield", manaShieldAbility.getHealthPoints(), Operation.ADDITION));
            }
         }
      }

      public MobEffect addAttributeModifier(Attribute pAttribute, String pUuid, double pAmount, Operation pOperation) {
         return super.addAttributeModifier(pAttribute, pUuid, pAmount, pOperation);
      }

      public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
         AttributeInstance attribute = livingEntity.getAttribute(ModAttributes.MANA_SHIELD);
         if (attribute != null) {
            attribute.removeModifier(MANA_SHIELD_MODIFIER_ID);
            if (livingEntity instanceof ServerPlayer player) {
               PlayerAbilitiesData.setAbilityOnCooldown(player, ManaShieldAbility.class);
            }
         }
      }
   }
}
