package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.TotemPlayerDamageTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.effect.spi.AbstractTotemAbility;
import iskallia.vault.util.calc.TotemDurationHelper;
import iskallia.vault.util.calc.TotemEffectRadiusHelper;
import iskallia.vault.util.damage.PlayerDamageHelper;
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
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.block.state.BlockState;

public class TotemPlayerDamageAbility extends AbstractTotemAbility<TotemPlayerDamageTileEntity> {
   private float totemPlayerDamagePercent;

   public TotemPlayerDamageAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      int totemDurationTicks,
      float totemEffectRadius,
      float totemPlayerDamagePercent
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, totemDurationTicks, totemEffectRadius);
      this.totemPlayerDamagePercent = totemPlayerDamagePercent;
   }

   public TotemPlayerDamageAbility() {
   }

   @Nonnull
   @Override
   protected BlockState getTotemForPlacement() {
      return ModBlocks.TOTEM_PLAYER_DAMAGE.defaultBlockState();
   }

   public float getTotemPlayerDamagePercent() {
      return this.totemPlayerDamagePercent;
   }

   @Override
   protected Class<TotemPlayerDamageTileEntity> getTotemTileEntityClass() {
      return TotemPlayerDamageTileEntity.class;
   }

   protected void initializeTotem(TotemPlayerDamageTileEntity totem, ServerPlayer player) {
      totem.initialize(
         player.getUUID(),
         TotemDurationHelper.adjustTotemDurationTicks(player, this.getTotemDurationTicks(player)),
         TotemEffectRadiusHelper.adjustTotemEffectRadiusHelper(player, this.getTotemEffectRadius(player)),
         this.getTotemPlayerDamagePercent()
      );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.totemPlayerDamagePercent), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.totemPlayerDamagePercent = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.totemPlayerDamagePercent)).ifPresent(tag -> nbt.put("totemPlayerDamagePercent", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.totemPlayerDamagePercent = Adapters.FLOAT.readNbt(nbt.get("totemPlayerDamagePercent")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.totemPlayerDamagePercent)).ifPresent(element -> json.add("totemPlayerDamagePercent", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.totemPlayerDamagePercent = Adapters.FLOAT.readJson(json.get("totemPlayerDamagePercent")).orElse(0.0F);
   }

   public static class TotemPlayerDamageEffect extends MobEffect {
      private static final UUID DAMAGE_MULTIPLIER_ID = UUID.fromString("747a8006-1f17-448e-ba10-238a3912c416");

      public TotemPlayerDamageEffect(int color, ResourceLocation id) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(id);
      }

      public static void addTo(LivingEntity livingEntity, float amount) {
         livingEntity.addEffect(instance(amount));
      }

      private static MobEffectInstance instance(float amount) {
         return new TotemPlayerDamageAbility.TotemPlayerDamageMobEffectInstance(ModEffects.TOTEM_PLAYER_DAMAGE, 5, 0, false, false, true, amount);
      }

      @ParametersAreNonnullByDefault
      public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            this.removeExistingDamageBuff(player);
            if (player.getEffect(this) instanceof TotemPlayerDamageAbility.TotemPlayerDamageMobEffectInstance effectInstance) {
               PlayerDamageHelper.applyMultiplier(
                  DAMAGE_MULTIPLIER_ID, player, effectInstance.playerDamageIncrease, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY
               );
            }
         }
      }

      @ParametersAreNonnullByDefault
      public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            this.removeExistingDamageBuff(player);
         }
      }

      private void removeExistingDamageBuff(ServerPlayer player) {
         PlayerDamageHelper.DamageMultiplier existing = PlayerDamageHelper.getMultiplier(player, DAMAGE_MULTIPLIER_ID);
         if (existing != null) {
            PlayerDamageHelper.removeMultiplier(player, existing);
         }
      }
   }

   public static class TotemPlayerDamageMobEffectInstance extends MobEffectInstance {
      private final float playerDamageIncrease;

      public TotemPlayerDamageMobEffectInstance(
         MobEffect pEffect, int pDuration, int pAmplifier, boolean pAmbient, boolean pVisible, boolean pShowIcon, float playerDamageIncrease
      ) {
         super(pEffect, pDuration, pAmplifier, pAmbient, pVisible, pShowIcon);
         this.playerDamageIncrease = playerDamageIncrease;
      }
   }
}
