package iskallia.vault.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;

public class MazeBlock extends Block {
   public static final EnumProperty<MazeBlock.MazeColor> COLOR = EnumProperty.create("color", MazeBlock.MazeColor.class);

   public MazeBlock() {
      super(Properties.of(Material.METAL, MaterialColor.METAL).strength(-1.0F, 3600000.0F).sound(SoundType.METAL));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(COLOR, MazeBlock.MazeColor.RED));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{COLOR});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return super.getStateForPlacement(context);
   }

   public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
      if (!level.isClientSide) {
         if (entity instanceof Player player) {
            Scoreboard scoreboard = level.getScoreboard();
            if (scoreboard.getObjective("Color") == null) {
               scoreboard.addObjective("Color", ObjectiveCriteria.DUMMY, new TextComponent("Color"), RenderType.INTEGER);
            }

            Objective colorObjective = scoreboard.getObjective("Color");

            assert colorObjective != null;

            Score colorScore = level.getScoreboard().getOrCreatePlayerScore(player.getDisplayName().getString(), colorObjective);
            MazeBlock.MazeColor playerColor = MazeBlock.MazeColor.values()[colorScore.getScore()];
            MazeBlock.MazeColor blockColor = (MazeBlock.MazeColor)level.getBlockState(pos).getValue(COLOR);
            if (playerColor != blockColor) {
               BlockPos nextPosition = player.blockPosition().relative(player.getDirection(), 1);
               colorScore.setScore(playerColor == MazeBlock.MazeColor.RED ? MazeBlock.MazeColor.BLUE.ordinal() : MazeBlock.MazeColor.RED.ordinal());
               player.teleportTo(nextPosition.getX() + 0.5, nextPosition.getY(), nextPosition.getZ() + 0.5);
            }

            super.stepOn(level, pos, state, entity);
         }
      }
   }

   public static enum MazeColor implements StringRepresentable {
      RED("red"),
      BLUE("blue");

      private final String name;

      private MazeColor(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
