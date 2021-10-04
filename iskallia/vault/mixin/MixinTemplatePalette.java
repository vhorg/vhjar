package iskallia.vault.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import net.minecraft.world.gen.feature.template.Template.Palette;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Palette.class})
public class MixinTemplatePalette {
   @Shadow
   @Final
   private Map<Block, List<BlockInfo>> field_237156_b_;
   @Shadow
   @Final
   private List<BlockInfo> field_237155_a_;

   @Overwrite
   public List<BlockInfo> func_237158_a_(Block block) {
      return this.field_237156_b_
         .computeIfAbsent(
            block,
            filterBlock -> {
               if (block == Blocks.field_226904_lY_) {
                  List<BlockInfo> prioritizedJigsawPieces = new ArrayList<>();
                  List<BlockInfo> jigsawBlocks = this.field_237155_a_
                     .stream()
                     .filter(blockInfo -> blockInfo.field_186243_b.func_203425_a(filterBlock))
                     .filter(blockInfo -> {
                        String registryKey = blockInfo.field_186244_c.func_74779_i("pool");
                        if (registryKey.contains("vault") && registryKey.contains("omega")) {
                           prioritizedJigsawPieces.add(blockInfo);
                           return false;
                        } else {
                           return true;
                        }
                     })
                     .collect(Collectors.toList());
                  prioritizedJigsawPieces.addAll(jigsawBlocks);
                  return prioritizedJigsawPieces;
               } else {
                  return this.field_237155_a_.stream().filter(blockInfo -> blockInfo.field_186243_b.func_203425_a(filterBlock)).collect(Collectors.toList());
               }
            }
         );
   }
}
