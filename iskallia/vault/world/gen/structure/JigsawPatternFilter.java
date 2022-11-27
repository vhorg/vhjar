package iskallia.vault.world.gen.structure;

import iskallia.vault.mixin.AccessorStructureTemplatePool;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class JigsawPatternFilter {
   private Predicate<ResourceLocation> roomPieceFilter = key -> true;
   private WeightedList<StructurePoolElement> filteredPieceCache = null;

   public JigsawPatternFilter andMatches(Predicate<ResourceLocation> filter) {
      this.roomPieceFilter = this.roomPieceFilter.and(filter);
      return this;
   }

   public StructurePoolElement getRandomPiece(StructureTemplatePool pattern, Random random) {
      if (this.filteredPieceCache != null) {
         return this.filteredPieceCache.getRandom(random);
      } else {
         this.filteredPieceCache = new WeightedList<>();
         ((AccessorStructureTemplatePool)pattern).getRawTemplates().forEach(weightedPiece -> {
            if (this.isApplicable((StructurePoolElement)weightedPiece.getFirst())) {
               this.filteredPieceCache.add((StructurePoolElement)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond());
            }
         });
         return this.getRandomPiece(pattern, random);
      }
   }

   private boolean isApplicable(StructurePoolElement piece) {
      if (piece instanceof PalettedListPoolElement) {
         List<StructurePoolElement> elements = ((PalettedListPoolElement)piece).getElements();

         for (StructurePoolElement elementPiece : elements) {
            if (!this.isApplicable(elementPiece)) {
               return false;
            }
         }

         return !elements.isEmpty();
      } else {
         if (piece instanceof PalettedSinglePoolElement) {
            ResourceLocation key = (ResourceLocation)((PalettedSinglePoolElement)piece).getTemplate().left().orElse(null);
            if (key != null) {
               return this.roomPieceFilter.test(key);
            }
         }

         return false;
      }
   }
}
