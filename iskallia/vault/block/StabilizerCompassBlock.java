package iskallia.vault.block;

import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectSummonAndKillBossesObjective;
import iskallia.vault.world.vault.logic.objective.architect.SummonAndKillBossesVotingSession;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class StabilizerCompassBlock extends Block {
   public static final EnumProperty<Direction> DIRECTION = BlockStateProperties.field_208157_J;

   public StabilizerCompassBlock() {
      super(Properties.func_200945_a(Material.field_151576_e).func_200947_a(SoundType.field_185851_d).func_200948_a(-1.0F, 3.6E8F).func_222380_e());
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(DIRECTION, Direction.NORTH));
   }

   public void func_149666_a(ItemGroup group, NonNullList<ItemStack> items) {
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{DIRECTION});
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (hand != Hand.MAIN_HAND) {
         return ActionResultType.PASS;
      } else {
         if (world instanceof ServerWorld) {
            ServerWorld sWorld = (ServerWorld)world;
            VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, pos);
            if (vault != null) {
               vault.getActiveObjective(ArchitectSummonAndKillBossesObjective.class).ifPresent(objective -> {
                  if (objective.getActiveSession() instanceof SummonAndKillBossesVotingSession) {
                     SummonAndKillBossesVotingSession session = (SummonAndKillBossesVotingSession)objective.getActiveSession();
                     Direction direction = (Direction)state.func_177229_b(DIRECTION);
                     BlockPos stabilizerPos = pos.func_177984_a().func_177972_a(direction.func_176734_d());
                     if (stabilizerPos.equals(session.getStabilizerPos()) && session.hasDirectionChoice(direction)) {
                        session.setVotedDirection(direction);
                     }
                  }
               });
            }
         }

         return ActionResultType.SUCCESS;
      }
   }
}
