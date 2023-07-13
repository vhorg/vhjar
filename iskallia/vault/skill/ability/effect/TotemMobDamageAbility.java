package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.TotemMobDamageTileEntity;
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

public class TotemMobDamageAbility extends AbstractTotemAbility<TotemMobDamageTileEntity> {
   private float totemPercentDamagePerInterval;
   private int totemDamageIntervalTicks;

   public TotemMobDamageAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      int totemDurationTicks,
      float totemEffectRadius,
      float totemPercentDamagePerInterval,
      int totemDamageIntervalTicks
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, totemDurationTicks, totemEffectRadius);
      this.totemPercentDamagePerInterval = totemPercentDamagePerInterval;
      this.totemDamageIntervalTicks = totemDamageIntervalTicks;
   }

   public TotemMobDamageAbility() {
   }

   public float getTotemPercentDamagePerInterval() {
      return this.totemPercentDamagePerInterval;
   }

   public int getTotemDamageIntervalTicks() {
      return this.totemDamageIntervalTicks;
   }

   @Nonnull
   @Override
   protected BlockState getTotemForPlacement() {
      return ModBlocks.TOTEM_MOB_DAMAGE.defaultBlockState();
   }

   @Override
   protected Class<TotemMobDamageTileEntity> getTotemTileEntityClass() {
      return TotemMobDamageTileEntity.class;
   }

   protected void initializeTotem(TotemMobDamageTileEntity totem, ServerPlayer player) {
      totem.initialize(
         player.getUUID(),
         TotemDurationHelper.adjustTotemDurationTicks(player, this.getTotemDurationTicks()),
         TotemEffectRadiusHelper.adjustTotemEffectRadiusHelper(player, this.getTotemEffectRadius(player)),
         this.getTotemPercentDamagePerInterval(),
         this.getTotemDamageIntervalTicks()
      );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.totemPercentDamagePerInterval), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.totemDamageIntervalTicks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.totemPercentDamagePerInterval = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.totemDamageIntervalTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.totemPercentDamagePerInterval)).ifPresent(tag -> nbt.put("totemPercentDamagePerInterval", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.totemDamageIntervalTicks)).ifPresent(tag -> nbt.put("totemDamageIntervalTicks", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.totemPercentDamagePerInterval = Adapters.FLOAT.readNbt(nbt.get("totemPercentDamagePerInterval")).orElse(0.0F);
      this.totemDamageIntervalTicks = Adapters.INT.readNbt(nbt.get("totemDamageIntervalTicks")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.totemPercentDamagePerInterval)).ifPresent(element -> json.add("totemPercentDamagePerInterval", element));
         Adapters.INT.writeJson(Integer.valueOf(this.totemDamageIntervalTicks)).ifPresent(element -> json.add("totemDamageIntervalTicks", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.totemPercentDamagePerInterval = Adapters.FLOAT.readJson(json.get("totemPercentDamagePerInterval")).orElse(0.0F);
      this.totemDamageIntervalTicks = Adapters.INT.readJson(json.get("totemDamageIntervalTicks")).orElse(0);
   }

   public static class TotemMobDamageEffect extends MobEffect {
      public TotemMobDamageEffect(int color, ResourceLocation id) {
         super(MobEffectCategory.HARMFUL, color);
         this.setRegistryName(id);
      }

      public static void addTo(LivingEntity livingEntity, int durationTicks) {
         livingEntity.addEffect(instance(durationTicks));
      }

      private static MobEffectInstance instance(int durationTicks) {
         return new MobEffectInstance(ModEffects.TOTEM_MOB_DAMAGE, durationTicks, 0, false, false, true);
      }

      public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
         return false;
      }
   }
}
