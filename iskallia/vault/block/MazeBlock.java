package iskallia.vault.block;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreCriteria.RenderType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class MazeBlock extends Block {
   public static final EnumProperty<MazeBlock.MazeColor> COLOR = EnumProperty.func_177709_a("color", MazeBlock.MazeColor.class);

   public MazeBlock() {
      super(
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151668_h)
            .func_200948_a(-1.0F, 3600000.0F)
            .func_200947_a(SoundType.field_185852_e)
      );
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(COLOR, MazeBlock.MazeColor.RED));
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{COLOR});
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      return super.func_196258_a(context);
   }

   public void func_176199_a(World worldIn, BlockPos pos, Entity entityIn) {
      if (!worldIn.field_72995_K) {
         if (entityIn instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entityIn;
            Scoreboard scoreboard = worldIn.func_96441_U();
            if (scoreboard.func_96518_b("Color") == null) {
               scoreboard.func_199868_a("Color", ScoreCriteria.field_96641_b, new StringTextComponent("Color"), RenderType.INTEGER);
            }

            ScoreObjective colorObjective = scoreboard.func_96518_b("Color");

            assert colorObjective != null;

            Score colorScore = worldIn.func_96441_U().func_96529_a(player.func_145748_c_().getString(), colorObjective);
            MazeBlock.MazeColor playerColor = MazeBlock.MazeColor.values()[colorScore.func_96652_c()];
            BlockPos nextPosition = player.func_233580_cy_();
            if (playerColor == worldIn.func_180495_p(pos).func_177229_b(COLOR)) {
               nextPosition = nextPosition.func_177967_a(player.func_174811_aO().func_176734_d(), 1);
            } else {
               nextPosition = nextPosition.func_177967_a(player.func_174811_aO(), 1);
               colorScore.func_96647_c(playerColor == MazeBlock.MazeColor.RED ? MazeBlock.MazeColor.BLUE.ordinal() : MazeBlock.MazeColor.RED.ordinal());
            }

            player.func_70634_a(nextPosition.func_177958_n() + 0.5, nextPosition.func_177956_o(), nextPosition.func_177952_p() + 0.5);
            super.func_176199_a(worldIn, pos, entityIn);
         }
      }
   }

   public static enum MazeColor implements IStringSerializable {
      RED("red"),
      BLUE("blue");

      private String name;

      private MazeColor(String name) {
         this.name = name;
      }

      public String func_176610_l() {
         return this.name;
      }
   }
}
