package iskallia.vault.world.gen.structure;

import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

public class JigsawPatternFilter {
   private Predicate<ResourceLocation> roomPieceFilter = key -> true;
   private WeightedList<JigsawPiece> filteredPieceCache = null;

   public JigsawPatternFilter andMatches(Predicate<ResourceLocation> filter) {
      this.roomPieceFilter = this.roomPieceFilter.and(filter);
      return this;
   }

   public JigsawPiece getRandomPiece(JigsawPattern pattern, Random random) {
      if (this.filteredPieceCache != null) {
         return this.filteredPieceCache.getRandom(random);
      } else {
         this.filteredPieceCache = new WeightedList<>();
         pattern.field_214952_d.forEach(weightedPiece -> {
            if (this.isApplicable((JigsawPiece)weightedPiece.getFirst())) {
               this.filteredPieceCache.add((JigsawPiece)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond());
            }
         });
         return this.getRandomPiece(pattern, random);
      }
   }

   private boolean isApplicable(JigsawPiece piece) {
      if (piece instanceof PalettedListPoolElement) {
         List<JigsawPiece> elements = ((PalettedListPoolElement)piece).getElements();

         for (JigsawPiece elementPiece : elements) {
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
