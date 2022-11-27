package iskallia.vault.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.Palette;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Palette.class})
public class MixinTemplatePalette {
   @Shadow
   @Final
   private Map<Block, List<StructureBlockInfo>> cache;
   @Shadow
   @Final
   private List<StructureBlockInfo> blocks;

   @Overwrite
   public List<StructureBlockInfo> blocks(Block block) {
      return this.cache.computeIfAbsent(block, filterBlock -> {
         if (block == Blocks.JIGSAW) {
            List<StructureBlockInfo> prioritizedJigsawPieces = new ArrayList<>();
            List<StructureBlockInfo> jigsawBlocks = this.blocks.stream().filter(blockInfo -> blockInfo.state.is(filterBlock)).filter(blockInfo -> {
               String registryKey = blockInfo.nbt.getString("pool");
               if (registryKey.contains("vault") && registryKey.contains("omega")) {
                  prioritizedJigsawPieces.add(blockInfo);
                  return false;
               } else {
                  return true;
               }
            }).toList();
            prioritizedJigsawPieces.addAll(jigsawBlocks);
            return prioritizedJigsawPieces;
         } else {
            return this.blocks.stream().filter(blockInfo -> blockInfo.state.is(filterBlock)).collect(Collectors.toList());
         }
      });
   }
}
