package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.TotemTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.util.calc.TotemDurationHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTotemAbility<T extends TotemTileEntity> extends InstantManaAbility {
   private int totemDurationTicks;
   private float totemEffectRadius;

   public AbstractTotemAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, int totemDurationTicks, float totemEffectRadius
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.totemDurationTicks = totemDurationTicks;
      this.totemEffectRadius = totemEffectRadius;
   }

   protected AbstractTotemAbility() {
   }

   public int getTotemDurationTicks() {
      return this.totemDurationTicks;
   }

   public float getUnmodifiedTotemEffectRadius() {
      return this.totemEffectRadius;
   }

   public float getTotemEffectRadius(Player player) {
      float realRadius = this.getUnmodifiedTotemEffectRadius();
      return AreaOfEffectHelper.adjustAreaOfEffect(player, realRadius);
   }

   @Override
   public String getAbilityGroupName() {
      return "Totem";
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         int cooldownDelayTicks = TotemDurationHelper.adjustTotemDurationTicks(player, this.getTotemDurationTicks());
         return this.placeTotem(player) ? Ability.ActionResult.successCooldownDelayed(cooldownDelayTicks) : Ability.ActionResult.fail();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.TOTEM, SoundSource.PLAYERS, 1.0F, 1.0F);
         player.playNotifySound(ModSounds.TOTEM, SoundSource.PLAYERS, 1.0F, 1.0F);
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.5F, 2.0F);
         player.playNotifySound(SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.5F, 2.0F);
      });
   }

   @Nonnull
   protected abstract BlockState getTotemForPlacement();

   protected abstract Class<T> getTotemTileEntityClass();

   protected boolean placeTotem(ServerPlayer player) {
      ServerLevel level = player.getLevel();
      BlockPos spawnPosition = this.getSpawnPosition(player);
      if (spawnPosition == null) {
         return false;
      } else {
         level.setBlockAndUpdate(spawnPosition, this.getTotemForPlacement());
         BlockEntity blockEntity = level.getBlockEntity(spawnPosition);
         if (this.getTotemTileEntityClass().isInstance(blockEntity)) {
            this.initializeTotem(this.getTotemTileEntityClass().cast(blockEntity), player);
         }

         return true;
      }
   }

   protected abstract void initializeTotem(T var1, ServerPlayer var2);

   @Nullable
   private BlockPos getSpawnPosition(ServerPlayer player) {
      BlockPos playerBlockPos = player.getOnPos().above();
      List<BlockPos> candidateList = new ArrayList<>();

      for (int x = -2; x <= 2; x++) {
         for (int z = -2; z <= 2; z++) {
            for (int y = -2; y <= 2; y++) {
               candidateList.add(playerBlockPos.offset(x, y, z));
            }
         }
      }

      Direction direction = player.getDirection();
      BlockPos preferredBlockPos = playerBlockPos.offset(direction.getStepX(), direction.getStepY(), direction.getStepZ());
      List<BlockPos> spawnPositionList = candidateList.stream()
         .filter(blockPosx -> this.canSpawnAt(player.level, blockPosx))
         .sorted(Comparator.comparingDouble(o -> MathUtilities.getDistanceSqr(o, preferredBlockPos)))
         .toList();
      if (!spawnPositionList.isEmpty()) {
         for (BlockPos blockPos : spawnPositionList) {
            BlockPos blockPosBelow = blockPos.below();
            BlockState blockStateBelow = player.level.getBlockState(blockPosBelow);
            if (blockStateBelow.isFaceSturdy(player.level, blockPosBelow, Direction.UP)) {
               return blockPos;
            }
         }

         return spawnPositionList.get(0);
      } else {
         return null;
      }
   }

   private boolean canSpawnAt(Level level, BlockPos blockPos) {
      BlockState blockState = level.getBlockState(blockPos);
      return blockState.isAir() || blockState.getMaterial().isReplaceable();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.totemDurationTicks), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.totemEffectRadius), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.totemDurationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.totemEffectRadius = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.totemDurationTicks)).ifPresent(tag -> nbt.put("totemDurationTicks", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.totemEffectRadius)).ifPresent(tag -> nbt.put("totemEffectRadius", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.totemDurationTicks = Adapters.INT.readNbt(nbt.get("totemDurationTicks")).orElse(0);
      this.totemEffectRadius = Adapters.FLOAT.readNbt(nbt.get("totemEffectRadius")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.totemDurationTicks)).ifPresent(element -> json.add("totemDurationTicks", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.totemEffectRadius)).ifPresent(element -> json.add("totemEffectRadius", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.totemDurationTicks = Adapters.INT.readJson(json.get("totemDurationTicks")).orElse(0);
      this.totemEffectRadius = Adapters.FLOAT.readJson(json.get("totemEffectRadius")).orElse(0.0F);
   }
}
