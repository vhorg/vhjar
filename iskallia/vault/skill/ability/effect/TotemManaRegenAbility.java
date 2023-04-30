package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.TotemManaRegenTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.effect.spi.AbstractTotemAbility;
import iskallia.vault.util.calc.TotemDurationHelper;
import iskallia.vault.util.calc.TotemEffectRadiusHelper;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.level.block.state.BlockState;

public class TotemManaRegenAbility extends AbstractTotemAbility<TotemManaRegenTileEntity> {
   private float totemManaRegenPercent;

   public TotemManaRegenAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      int totemDurationTicks,
      float totemEffectRadius,
      float totemManaRegenPercent
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, totemDurationTicks, totemEffectRadius);
      this.totemManaRegenPercent = totemManaRegenPercent;
   }

   public TotemManaRegenAbility() {
   }

   public float getTotemManaRegenPercent() {
      return this.totemManaRegenPercent;
   }

   @Nonnull
   @Override
   protected BlockState getTotemForPlacement() {
      return ModBlocks.TOTEM_MANA_REGEN.defaultBlockState();
   }

   @Override
   protected Class<TotemManaRegenTileEntity> getTotemTileEntityClass() {
      return TotemManaRegenTileEntity.class;
   }

   protected void initializeTotem(TotemManaRegenTileEntity totem, ServerPlayer player) {
      totem.initialize(
         player.getUUID(),
         TotemDurationHelper.adjustTotemDurationTicks(player, this.getTotemDurationTicks()),
         TotemEffectRadiusHelper.adjustTotemEffectRadiusHelper(player, this.getTotemEffectRadius()),
         this.getTotemManaRegenPercent()
      );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.totemManaRegenPercent), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.totemManaRegenPercent = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.totemManaRegenPercent)).ifPresent(tag -> nbt.put("totemManaRegenPercent", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.totemManaRegenPercent = Adapters.FLOAT.readNbt(nbt.get("totemManaRegenPercent")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.totemManaRegenPercent)).ifPresent(element -> json.add("totemManaRegenPercent", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.totemManaRegenPercent = Adapters.FLOAT.readJson(json.get("totemManaRegenPercent")).orElse(0.0F);
   }

   public static class TotemManaRegenEffect extends MobEffect {
      private static final UUID UUID = UUID.fromString("9fac344f-fb1a-4a30-bb79-a2895e09a4a0");

      public TotemManaRegenEffect(int color, ResourceLocation id) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(id);
      }

      public static void addTo(LivingEntity livingEntity, float amount) {
         livingEntity.addEffect(instance(amount));
      }

      private static MobEffectInstance instance(float amount) {
         return new TotemManaRegenAbility.TotemManaRegenMobEffectInstance(ModEffects.TOTEM_MANA_REGEN, 5, 0, false, false, true, amount);
      }

      @ParametersAreNonnullByDefault
      public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            AttributeInstance attributeInstance = player.getAttribute(ModAttributes.MANA_REGEN);
            if (attributeInstance != null) {
               if (player.getEffect(this) instanceof TotemManaRegenAbility.TotemManaRegenMobEffectInstance effectInstance) {
                  AttributeModifier var8 = new AttributeModifier(UUID, "totem_mana_regen", effectInstance.manaRegen, Operation.MULTIPLY_BASE);
                  attributeInstance.addTransientModifier(var8);
               } else {
                  attributeInstance.removeModifier(UUID);
               }
            }
         }
      }

      @ParametersAreNonnullByDefault
      public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            AttributeInstance attributeInstance = player.getAttribute(ModAttributes.MANA_REGEN);
            if (attributeInstance != null) {
               attributeInstance.removeModifier(UUID);
            }
         }
      }
   }

   public static class TotemManaRegenMobEffectInstance extends MobEffectInstance {
      private final float manaRegen;

      public TotemManaRegenMobEffectInstance(
         MobEffect pEffect, int pDuration, int pAmplifier, boolean pAmbient, boolean pVisible, boolean pShowIcon, float manaRegen
      ) {
         super(pEffect, pDuration, pAmplifier, pAmbient, pVisible, pShowIcon);
         this.manaRegen = manaRegen;
      }
   }
}
