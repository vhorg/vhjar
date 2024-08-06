package iskallia.vault.task.renderer;

import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.task.MineBlockTask;
import iskallia.vault.task.renderer.context.AchievementRendererContext;
import iskallia.vault.util.GroupUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class MineBlockTaskRenderer {
   public static class Achievement extends AchievementRenderer.Base<MineBlockTask, AchievementRendererContext> {
      private List<ItemStack> cache = null;

      @OnlyIn(Dist.CLIENT)
      public void onRenderDetails(MineBlockTask task, AchievementRendererContext context) {
         super.onRenderDetails(task, context);
         float scale = 4.0F;
         double center = context.getSize().getX() / 2.0 - 2.0;
         float iconWidth = 18.0F * scale;
         List<ItemStack> items = this.getItems(task);
         int index = (int)(context.getTick() / 30L % items.size());
         context.renderStack(items.get(index), (int)(center - iconWidth / 2.0F), 0, scale, true, true);
         context.translate(0.0, 18.0F * scale + 2.0F, 0.0);
      }

      private List<ItemStack> getItems(MineBlockTask task) {
         if (this.cache == null) {
            this.cache = new ArrayList<>();

            for (ResourceLocation blockId : GroupUtils.getBlockIdsFor(task.getConfig().filter)) {
               if (ForgeRegistries.BLOCKS.containsKey(blockId)) {
                  Block block = (Block)ForgeRegistries.BLOCKS.getValue(blockId);
                  if (block != null) {
                     ItemStack stack = new ItemStack(block);
                     if (block instanceof VaultOreBlock) {
                        CompoundTag tag = stack.getOrCreateTag();
                        tag.putString("type", "vault_stone");
                        stack.setTag(tag);
                     }

                     this.cache.add(stack);
                  }
               }
            }
         }

         return this.cache;
      }
   }
}
