package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.skill.ability.config.FarmerConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbilityTickResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractHoldManaAbility;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractFarmerAbility<C extends FarmerConfig> extends AbstractHoldManaAbility<C> {
   protected final Object2IntMap<UUID> tickCounterMap = new Object2IntOpenHashMap();

   protected AbstractFarmerAbility() {
      this.tickCounterMap.defaultReturnValue(0);
   }

   @Override
   public String getAbilityGroupName() {
      return "Farmer";
   }

   protected AbilityTickResult doActiveTick(C config, ServerPlayer player) {
      int tickDelay = this.tickCounterMap.computeIfAbsent(player.getUUID(), uuid -> config.getTickDelay());
      if (tickDelay > 0) {
         this.tickCounterMap.put(player.getUUID(), tickDelay - 1);
         return super.doActiveTick(config, player);
      } else {
         this.tickCounterMap.removeInt(player.getUUID());
         this.doGrow(config, player, (ServerLevel)player.getCommandSenderWorld());
         return super.doActiveTick(config, player);
      }
   }

   protected void doGrow(C config, ServerPlayer player, ServerLevel world) {
      BlockPos playerPos = player.blockPosition();
      int horizontalRange = config.getHorizontalRange();
      int verticalRange = config.getVerticalRange();
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
         BlockPos pos = candidateList.get(RANDOM.nextInt(candidateList.size()));
         BlockState state = world.getBlockState(pos);
         Block block = world.getBlockState(pos).getBlock();
         this.doGrowBlock(config, player, world, pos, block, state);
      }
   }

   protected abstract boolean canGrowBlock(ServerLevel var1, BlockPos var2, Block var3, BlockState var4);

   protected abstract void doGrowBlock(C var1, ServerPlayer var2, ServerLevel var3, BlockPos var4, Block var5, BlockState var6);
}
