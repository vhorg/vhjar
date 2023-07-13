package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.FarmerAdditionalRangeModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.IntValueConfig;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.HoldManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.util.calc.CooldownHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractFarmerAbility extends HoldManaAbility {
   private int tickDelay;
   private int horizontalRange;
   private int verticalRange;
   private int tick;

   public AbstractFarmerAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      int tickDelay,
      int horizontalRange,
      int verticalRange
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond);
      this.tickDelay = tickDelay;
      this.horizontalRange = horizontalRange;
      this.verticalRange = verticalRange;
      this.tick = 0;
   }

   protected AbstractFarmerAbility() {
   }

   public int getTickDelay() {
      return this.tickDelay;
   }

   public int getHorizontalRange() {
      return this.horizontalRange;
   }

   public int getVerticalRange() {
      return this.verticalRange;
   }

   @Override
   public String getAbilityGroupName() {
      return "Farmer";
   }

   @Override
   public Ability.TickResult doActiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.tick > 0) {
            this.tick--;
            return super.doActiveTick(context);
         } else {
            this.tick = CooldownHelper.adjustCooldown(player, "Farmer", this.getTickDelay());
            this.doGrow(player, (ServerLevel)player.level);
            return super.doActiveTick(context);
         }
      }).orElse(Ability.TickResult.PASS);
   }

   protected void doGrow(ServerPlayer player, ServerLevel world) {
      BlockPos playerPos = player.blockPosition();
      int originalHorizontalRange = this.getHorizontalRange();
      int originalVerticalRange = this.getVerticalRange();
      int horizontalRange = originalHorizontalRange;
      int verticalRange = originalVerticalRange;

      for (ConfiguredModification<IntValueConfig, FarmerAdditionalRangeModification> mod : SpecialAbilityModification.getModifications(
         player, FarmerAdditionalRangeModification.class
      )) {
         horizontalRange = mod.modification().addRange(mod.config(), horizontalRange);
         verticalRange = mod.modification().addRange(mod.config(), verticalRange);
      }

      horizontalRange = Math.round(AreaOfEffectHelper.adjustAreaOfEffect(player, horizontalRange));
      verticalRange = Math.round(AreaOfEffectHelper.adjustAreaOfEffect(player, verticalRange));
      MutableBlockPos mutableBlockPos = new MutableBlockPos();
      List<BlockPos> candidateList = new ArrayList<>();

      for (int x = -horizontalRange; x <= horizontalRange; x++) {
         for (int z = -horizontalRange; z <= horizontalRange; z++) {
            for (int y = -verticalRange; y <= verticalRange; y++) {
               mutableBlockPos.set(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
               BlockState blockState = world.getBlockState(mutableBlockPos);
               Block block = blockState.getBlock();
               if (this.canGrowBlock(world, mutableBlockPos, block, blockState)) {
                  candidateList.add(new BlockPos(mutableBlockPos));
               }
            }
         }
      }

      if (!candidateList.isEmpty()) {
         int executionAttempts = Math.max(1, originalHorizontalRange * originalVerticalRange / (horizontalRange * verticalRange));

         for (int i = 0; i < executionAttempts; i++) {
            BlockPos pos = candidateList.get(world.getRandom().nextInt(candidateList.size()));
            BlockState state = world.getBlockState(pos);
            Block block = world.getBlockState(pos).getBlock();
            this.doGrowBlock(player, world, pos, block, state);
         }
      }
   }

   protected abstract boolean canGrowBlock(ServerLevel var1, BlockPos var2, Block var3, BlockState var4);

   protected abstract void doGrowBlock(ServerPlayer var1, ServerLevel var2, BlockPos var3, Block var4, BlockState var5);

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.tickDelay), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.horizontalRange), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.verticalRange), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.tick), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.tickDelay = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.horizontalRange = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.verticalRange = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.tick = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.tickDelay)).ifPresent(tag -> nbt.put("tickDelay", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.horizontalRange)).ifPresent(tag -> nbt.put("horizontalRange", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.verticalRange)).ifPresent(tag -> nbt.put("verticalRange", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.tick)).ifPresent(tag -> nbt.put("tick", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.tickDelay = Adapters.INT.readNbt(nbt.get("tickDelay")).orElse(0);
      this.horizontalRange = Adapters.INT.readNbt(nbt.get("horizontalRange")).orElse(0);
      this.verticalRange = Adapters.INT.readNbt(nbt.get("verticalRange")).orElse(0);
      this.tick = Adapters.INT.readNbt(nbt.get("tick")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.tickDelay)).ifPresent(element -> json.add("tickDelay", element));
         Adapters.INT.writeJson(Integer.valueOf(this.horizontalRange)).ifPresent(element -> json.add("horizontalRange", element));
         Adapters.INT.writeJson(Integer.valueOf(this.verticalRange)).ifPresent(element -> json.add("verticalRange", element));
         Adapters.INT.writeJson(Integer.valueOf(this.tick)).ifPresent(element -> json.add("tick", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.tickDelay = Adapters.INT.readJson(json.get("tickDelay")).orElse(0);
      this.horizontalRange = Adapters.INT.readJson(json.get("horizontalRange")).orElse(0);
      this.verticalRange = Adapters.INT.readJson(json.get("verticalRange")).orElse(0);
      this.tick = Adapters.INT.readJson(json.get("tick")).orElse(0);
   }
}
