package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.TotemPlayerHealthTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.effect.spi.AbstractTotemAbility;
import iskallia.vault.util.calc.TotemDurationHelper;
import iskallia.vault.util.calc.TotemEffectRadiusHelper;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TotemAbility extends AbstractTotemAbility<TotemPlayerHealthTileEntity> {
   private float totemHealthPerSecond;

   public TotemAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      int totemDurationTicks,
      float totemEffectRadius,
      float totemHealthPerSecond
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, totemDurationTicks, totemEffectRadius);
      this.totemHealthPerSecond = totemHealthPerSecond;
   }

   public TotemAbility() {
   }

   @Nonnull
   @Override
   protected BlockState getTotemForPlacement() {
      return ModBlocks.TOTEM_PLAYER_HEALTH.defaultBlockState();
   }

   @Override
   protected Class<TotemPlayerHealthTileEntity> getTotemTileEntityClass() {
      return TotemPlayerHealthTileEntity.class;
   }

   protected void initializeTotem(TotemPlayerHealthTileEntity totem, ServerPlayer player) {
      totem.initialize(
         player.getUUID(),
         TotemDurationHelper.adjustTotemDurationTicks(player, this.getTotemDurationTicks(player)),
         TotemEffectRadiusHelper.adjustTotemEffectRadiusHelper(player, this.getTotemEffectRadius(player)),
         this.totemHealthPerSecond
      );
   }

   public float getTotemHealthPerSecond() {
      return this.totemHealthPerSecond;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.totemHealthPerSecond), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.totemHealthPerSecond = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.totemHealthPerSecond)).ifPresent(tag -> nbt.put("totemHealthPerSecond", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.totemHealthPerSecond = Adapters.FLOAT.readNbt(nbt.get("totemHealthPerSecond")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.totemHealthPerSecond)).ifPresent(element -> json.add("totemHealthPerSecond", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.totemHealthPerSecond = Adapters.FLOAT.readJson(json.get("totemHealthPerSecond")).orElse(0.0F);
   }

   public static class TotemPlayerHealthEffect extends MobEffect {
      public TotemPlayerHealthEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(resourceLocation);
      }

      public static void addTo(LivingEntity livingEntity, float healthPerTick) {
         livingEntity.addEffect(instance(healthPerTick));
      }

      private static MobEffectInstance instance(float healthPerTick) {
         return new TotemAbility.TotemPlayerHealthMobEffectInstance(ModEffects.TOTEM_PLAYER_HEALTH, 5, 0, false, false, true, healthPerTick);
      }

      public boolean isDurationEffectTick(int duration, int amplifier) {
         return true;
      }

      public void applyEffectTick(@Nonnull LivingEntity livingEntity, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            if (!(player.getHealth() >= player.getMaxHealth())) {
               if (player.getEffect(this) instanceof TotemAbility.TotemPlayerHealthMobEffectInstance effectInstance) {
                  player.heal(effectInstance.healthPerTick);
               }
            }
         }
      }
   }

   public static class TotemPlayerHealthMobEffectInstance extends MobEffectInstance {
      private final float healthPerTick;

      public TotemPlayerHealthMobEffectInstance(
         MobEffect pEffect, int pDuration, int pAmplifier, boolean pAmbient, boolean pVisible, boolean pShowIcon, float healthPerTick
      ) {
         super(pEffect, pDuration, pAmplifier, pAmbient, pVisible, pShowIcon);
         this.healthPerTick = healthPerTick;
      }
   }
}
