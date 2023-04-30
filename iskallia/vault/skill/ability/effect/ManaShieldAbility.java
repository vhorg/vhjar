package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.ManaShieldAbsorptionModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.ability.effect.spi.core.ToggleManaAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ManaShieldAbility extends ToggleManaAbility {
   private float percentageDamageAbsorbed;
   private float manaPerDamageScalar;

   public ManaShieldAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      float percentageDamageAbsorbed,
      float manaPerDamageScalar
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond);
      this.percentageDamageAbsorbed = percentageDamageAbsorbed;
      this.manaPerDamageScalar = manaPerDamageScalar;
   }

   public ManaShieldAbility() {
   }

   public float getPercentageDamageAbsorbed() {
      return this.percentageDamageAbsorbed;
   }

   public float getManaPerDamageScalar() {
      return this.manaPerDamageScalar;
   }

   @Override
   public String getAbilityGroupName() {
      return "Mana Shield";
   }

   protected ToggleAbilityEffect getEffect() {
      return ModEffects.MANA_SHIELD;
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            this.getEffect().addTo(player, 0);
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            player.removeEffect(this.getEffect());
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doToggleSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         if (this.isActive()) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANA_SHIELD, SoundSource.MASTER, 0.4F, 1.0F);
            player.playNotifySound(ModSounds.MANA_SHIELD, SoundSource.MASTER, 0.4F, 1.0F);
         }
      });
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(this.getEffect())) {
            player.removeEffect(this.getEffect());
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(this.getEffect()));
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(this.getEffect()));
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void on(LivingDamageEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);

         for (ManaShieldAbility ability : abilities.getAll(ManaShieldAbility.class, Skill::isUnlocked)) {
            if (player.hasEffect(ability.getEffect())) {
               float percentageDamageAbsorbed = Mth.clamp(ability.getPercentageDamageAbsorbed(), 0.0F, 1.0F);
               float manaCostPerDamage = ability.getManaPerDamageScalar();

               for (ConfiguredModification<FloatValueConfig, ManaShieldAbsorptionModification> mod : SpecialAbilityModification.getModifications(
                  player, ManaShieldAbsorptionModification.class
               )) {
                  manaCostPerDamage = mod.modification().adjustAbsorptionDamageCost(mod.config(), manaCostPerDamage);
               }

               manaCostPerDamage = Math.max(manaCostPerDamage, 1.0E-5F);
               float manaUsed = Math.min(event.getAmount() * percentageDamageAbsorbed * manaCostPerDamage, Mana.get(player));
               float damageAbsorbed = manaUsed / manaCostPerDamage;
               if (Mth.equal(damageAbsorbed, 0.0F)) {
                  return;
               }

               if (Mth.equal(damageAbsorbed, event.getAmount())) {
                  event.setCanceled(true);
               } else {
                  event.setAmount(event.getAmount() - damageAbsorbed);
               }

               float mana = Mana.decrease(player, manaUsed);
               ability.onDamageAbsorbed(player, damageAbsorbed);
               float pitch = 1.25F + -0.5F * (mana / Mana.getMax(player));
               player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANA_SHIELD_HIT, SoundSource.MASTER, 0.2F, pitch);
               player.playNotifySound(ModSounds.MANA_SHIELD_HIT, SoundSource.MASTER, 0.2F, pitch);
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
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentageDamageAbsorbed), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.manaPerDamageScalar), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.percentageDamageAbsorbed = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.manaPerDamageScalar = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentageDamageAbsorbed)).ifPresent(tag -> nbt.put("percentageDamageAbsorbed", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.manaPerDamageScalar)).ifPresent(tag -> nbt.put("manaPerDamageScalar", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.percentageDamageAbsorbed = Adapters.FLOAT.readNbt(nbt.get("percentageDamageAbsorbed")).orElse(0.0F);
      this.manaPerDamageScalar = Adapters.FLOAT.readNbt(nbt.get("manaPerDamageScalar")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentageDamageAbsorbed)).ifPresent(element -> json.add("percentageDamageAbsorbed", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.manaPerDamageScalar)).ifPresent(element -> json.add("manaPerDamageScalar", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.percentageDamageAbsorbed = Adapters.FLOAT.readJson(json.get("percentageDamageAbsorbed")).orElse(0.0F);
      this.manaPerDamageScalar = Adapters.FLOAT.readJson(json.get("manaPerDamageScalar")).orElse(0.0F);
   }

   public static class ManaShieldEffect extends ToggleAbilityEffect {
      public ManaShieldEffect(int color, ResourceLocation resourceLocation) {
         super(ManaShieldAbility.class, color, resourceLocation);
      }

      @Override
      protected void removeAttributeModifiers(ServerPlayer player, AttributeMap attributeMap, int amplifier) {
         super.removeAttributeModifiers(player, attributeMap, amplifier);
         if (!player.hasEffect(this)) {
            AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);

            for (ManaShieldAbility ability : abilities.getAll(ManaShieldAbility.class, Skill::isUnlocked)) {
               ability.onEffectRemoved(player);
            }
         }
      }
   }
}
