package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.gen.structure.VaultJigsawHelper;
import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public class PieceSelectionModifier extends VoteModifier {
   @Expose
   private final float filterChance;
   @Expose
   private final List<String> selectedRoomPrefixes;
   private WeightedList<StructurePoolElement> filteredPieces = null;

   public PieceSelectionModifier(String name, String description, int voteLockDurationChangeSeconds, float filterChance, List<String> selectedRoomPrefixes) {
      super(name, description, voteLockDurationChangeSeconds);
      this.filterChance = filterChance;
      this.selectedRoomPrefixes = selectedRoomPrefixes;
   }

   @Nullable
   @Override
   public StructurePoolElement getSpecialRoom(ArchitectObjective objective, VaultRaid vault) {
      if (rand.nextFloat() >= this.filterChance) {
         return super.getSpecialRoom(objective, vault);
      } else if (this.filteredPieces != null) {
         return this.filteredPieces.getRandom(rand);
      } else {
         int vaultLevel = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
         this.filteredPieces = VaultJigsawHelper.getVaultRoomList(vaultLevel).copyFiltered(this::isApplicable);
         return this.filteredPieces.getRandom(rand);
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
               String keyStr = key.toString();

               for (String prefix : this.selectedRoomPrefixes) {
                  if (keyStr.startsWith(prefix)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }
}
