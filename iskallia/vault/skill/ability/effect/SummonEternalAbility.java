package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.eternal.ActiveEternalData;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.entity.eternal.EternalHelper;
import iskallia.vault.gear.attribute.ability.special.EternalsSpeedModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.archetype.archetype.CommanderArchetype;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.PlayerArchetypeData;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class SummonEternalAbility extends InstantManaAbility {
   private static final UUID SPEED_INCREASE_ID = UUID.fromString("849085f9-1195-45fd-b219-243e7aec29e3");
   private int numberOfEternals;
   private int despawnTime;
   private float ancientChance;
   private boolean vaultOnly;

   public SummonEternalAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      int numberOfEternals,
      int despawnTime,
      float ancientChance,
      boolean vaultOnly
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.numberOfEternals = numberOfEternals;
      this.despawnTime = despawnTime;
      this.ancientChance = ancientChance;
      this.vaultOnly = vaultOnly;
   }

   public SummonEternalAbility() {
   }

   @Override
   public String getAbilityGroupName() {
      return "Summon Eternal";
   }

   public int getNumberOfEternals() {
      return this.numberOfEternals;
   }

   public int getDespawnTime() {
      return this.despawnTime;
   }

   public float getAncientChance() {
      return this.ancientChance;
   }

   public boolean isVaultOnly() {
      return this.vaultOnly;
   }

   @Override
   protected boolean canDoAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (!player.getCommandSenderWorld().isClientSide() && player.getCommandSenderWorld() instanceof ServerLevel serverLevel) {
            EternalsData.EternalGroup playerEternals = EternalsData.get(serverLevel).getEternals(player);
            if (playerEternals.getEternals().isEmpty()) {
               player.sendMessage(new TextComponent("You have no eternals to summon.").withStyle(ChatFormatting.RED), Util.NIL_UUID);
               return false;
            } else if (ServerVaults.get(player.level).isEmpty()) {
               player.sendMessage(new TextComponent("You can only summon eternals in the Vault!").withStyle(ChatFormatting.RED), Util.NIL_UUID);
               return false;
            } else {
               return super.canDoAction(context);
            }
         } else {
            return false;
         }
      }).orElse(false);
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               if (!(player.getCommandSenderWorld() instanceof ServerLevel world)) {
                  return Ability.ActionResult.fail();
               } else {
                  EternalsData.EternalGroup var10 = EternalsData.get(world).getEternals(player);
                  ArrayList eternals = new ArrayList();
                  int count = this.getNumberOfEternals();

                  for (int speedIncrease = 0; speedIncrease < count; speedIncrease++) {
                     EternalData eternal = null;
                     if (world.getRandom().nextFloat() < this.getAncientChance()) {
                        eternal = var10.getRandomAliveAncient(
                           world.getRandom(),
                           eternalDatax -> !eternals.contains(eternalDatax) && !ActiveEternalData.getInstance().isEternalActive(eternalDatax.getId())
                        );
                     }

                     if (eternal == null) {
                        eternal = var10.getRandomAlive(
                           world.getRandom(),
                           eternalDatax -> !eternals.contains(eternalDatax) && !ActiveEternalData.getInstance().isEternalActive(eternalDatax.getId())
                        );
                     }

                     if (eternal != null) {
                        eternals.add(eternal);
                     }
                  }

                  if (eternals.isEmpty()) {
                     player.sendMessage(new TextComponent("You have no (alive) eternals to summon.").withStyle(ChatFormatting.RED), Util.NIL_UUID);
                     return Ability.ActionResult.fail();
                  } else {
                     float speedIncrease = 0.0F;

                     for (ConfiguredModification<FloatValueConfig, EternalsSpeedModification> mod : SpecialAbilityModification.getModifications(
                        player, EternalsSpeedModification.class
                     )) {
                        speedIncrease = mod.modification().adjustEternalSpeed(mod.config(), speedIncrease);
                     }

                     for (EternalData eternalData : eternals) {
                        EternalEntity eternalx = EternalHelper.spawnEternal(world, eternalData);
                        eternalx.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                        eternalx.setDespawnTime(world.getServer().getTickCount() + this.getDespawnTime());
                        eternalx.setOwner(player.getUUID());
                        eternalx.setEternalId(eternalData.getId());
                        eternalx.addEffect(new MobEffectInstance(MobEffects.GLOWING, Integer.MAX_VALUE, 0, true, false));
                        if (speedIncrease > 0.0F && eternalx.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                           eternalx.getAttribute(Attributes.MOVEMENT_SPEED)
                              .addPermanentModifier(new AttributeModifier(SPEED_INCREASE_ID, "Gear Speed Increase", speedIncrease, Operation.MULTIPLY_BASE));
                        }

                        PlayerArchetypeData.get(world)
                           .getArchetypeContainer(player)
                           .ifCurrentArchetype(CommanderArchetype.class, archetype -> archetype.applyToEternal(eternal));
                        if (eternalData.getAura() != null) {
                           eternalx.setProvidedAura(eternalData.getAura().getAuraName());
                        }

                        world.addFreshEntity(eternalx);
                     }

                     return Ability.ActionResult.successCooldownImmediate();
                  }
               }
            }
         )
         .orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @Override
   protected void doSound(SkillContext context) {
   }

   @SubscribeEvent
   public static void onDamage(LivingAttackEvent event) {
      LivingEntity damagedEntity = event.getEntityLiving();
      if (damagedEntity instanceof EternalEntity && event.getSource().getEntity() instanceof Player player && !player.isCreative()) {
         event.setCanceled(true);
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.numberOfEternals), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.despawnTime), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.ancientChance), buffer);
      Adapters.BOOLEAN.writeBits(this.vaultOnly, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.numberOfEternals = Adapters.INT.readBits(buffer).orElseThrow();
      this.despawnTime = Adapters.INT.readBits(buffer).orElseThrow();
      this.ancientChance = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.vaultOnly = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.numberOfEternals)).ifPresent(tag -> nbt.put("numberOfEternals", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.despawnTime)).ifPresent(tag -> nbt.put("despawnTime", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.ancientChance)).ifPresent(tag -> nbt.put("ancientChance", tag));
         Adapters.BOOLEAN.writeNbt(this.vaultOnly).ifPresent(tag -> nbt.put("vaultOnly", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.numberOfEternals = Adapters.INT.readNbt(nbt.get("numberOfEternals")).orElse(0);
      this.despawnTime = Adapters.INT.readNbt(nbt.get("despawnTime")).orElse(0);
      this.ancientChance = Adapters.FLOAT.readNbt(nbt.get("ancientChance")).orElse(0.0F);
      this.vaultOnly = Adapters.BOOLEAN.readNbt(nbt.get("vaultOnly")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.numberOfEternals)).ifPresent(element -> json.add("numberOfEternals", element));
         Adapters.INT.writeJson(Integer.valueOf(this.despawnTime)).ifPresent(element -> json.add("despawnTime", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.ancientChance)).ifPresent(element -> json.add("ancientChance", element));
         Adapters.BOOLEAN.writeJson(this.vaultOnly).ifPresent(element -> json.add("vaultOnly", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.numberOfEternals = Adapters.INT.readJson(json.get("numberOfEternals")).orElse(0);
      this.despawnTime = Adapters.INT.readJson(json.get("despawnTime")).orElse(0);
      this.ancientChance = Adapters.FLOAT.readJson(json.get("ancientChance")).orElse(0.0F);
      this.vaultOnly = Adapters.BOOLEAN.readJson(json.get("vaultOnly")).orElse(false);
   }
}
