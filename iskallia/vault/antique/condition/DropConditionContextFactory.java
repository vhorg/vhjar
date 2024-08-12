package iskallia.vault.antique.condition;

import iskallia.vault.block.entity.base.TemplateTagContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DropConditionContextFactory {
   public static <T extends BlockEntity & TemplateTagContainer> DropConditionContext makeLootableContext(int level, T tile) {
      DropConditionContext context = new DropConditionContext(level, DropConditionType.BLOCK, tile.getBlockState().getBlock().getRegistryName());
      context.addTags(tile.getTemplateTags());
      return context;
   }

   public static DropConditionContext makeEntityContext(int level, LivingEntity entity) {
      DropConditionContext context = new DropEntityConditionContext(level, entity.getType().getRegistryName(), entity);
      CompoundTag forgeData = entity.getPersistentData();
      if (forgeData.contains("template_tags", 9)) {
         for (Tag tag : forgeData.getList("template_tags", 8)) {
            context.addTag(tag.getAsString());
         }
      }

      return context;
   }

   public static DropConditionContext makeRewardCrate(int level, Block block) {
      return new DropConditionContext(level, DropConditionType.REWARD_CRATE, block.getRegistryName());
   }
}
