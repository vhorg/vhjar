package iskallia.vault.item.crystal.recipe;

import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

public abstract class VanillaAnvilRecipe extends AnvilRecipe {
   public abstract boolean onSimpleCraft(AnvilContext var1);

   @Override
   public boolean onCraft(AnvilContext context) {
      boolean result = this.onSimpleCraft(context);
      if (!result) {
         return false;
      } else {
         if (this.processNameChange(context)) {
            context.setLevelCost(context.getLevelCost() + 1);
         }

         context.onTake(context.getTake().append(() -> {
            this.consumeExperience(context);
            this.tryBreak(JavaRandom.ofNanoTime(), 0.12, context);
         }));
         return true;
      }
   }

   public boolean processNameChange(AnvilContext context) {
      String name = context.getName();
      ItemStack output = context.getOutput();
      if (StringUtils.isBlank(name)) {
         if (output.hasCustomHoverName()) {
            output.resetHoverName();
            return true;
         }
      } else if (!name.equals(output.getHoverName().getString())) {
         output.setHoverName(new TextComponent(name));
         return true;
      }

      return false;
   }

   public void consumeExperience(AnvilContext context) {
      context.getPlayer().ifPresent(player -> {
         if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-context.getLevelCost());
         }
      });
   }

   public void tryBreak(RandomSource random, double chance, AnvilContext context) {
      context.getBlockState().ifPresent(state -> {
         Level world = context.getWorld().orElseThrow();
         BlockPos pos = context.getPos().orElseThrow();
         boolean creative = context.getPlayer().map(player -> player.getAbilities().instabuild).orElse(false);
         if (state.is(BlockTags.ANVIL) && !creative && random.nextDouble() >= chance) {
            BlockState damaged = AnvilBlock.damage(state);
            if (damaged == null) {
               world.removeBlock(pos, false);
               world.levelEvent(1029, pos, 0);
            } else {
               world.setBlock(pos, damaged, 2);
               world.levelEvent(1030, pos, 0);
            }
         } else {
            world.levelEvent(1030, pos, 0);
         }
      });
   }
}
