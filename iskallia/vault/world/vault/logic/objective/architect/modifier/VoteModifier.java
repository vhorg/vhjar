package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.logic.objective.architect.processor.VaultPieceProcessor;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.server.ServerWorld;

public class VoteModifier {
   protected static final Random rand = new Random();
   @Expose
   private final String name;
   @Expose
   private final String description;
   @Expose
   private final String color = String.valueOf(65535);
   @Expose
   private final int voteLockDurationChangeSeconds;

   public VoteModifier(String name, String description, int voteLockDurationChangeSeconds) {
      this.name = name;
      this.description = description;
      this.voteLockDurationChangeSeconds = voteLockDurationChangeSeconds;
   }

   public String getName() {
      return this.name;
   }

   public String getDescriptionText() {
      return this.description;
   }

   public int getVoteLockDurationChangeSeconds() {
      return this.voteLockDurationChangeSeconds;
   }

   public ITextComponent getDescription() {
      return new StringTextComponent(this.getDescriptionText())
         .func_240703_c_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(Integer.parseInt(this.color))));
   }

   @Nullable
   public JigsawPiece getSpecialRoom(ArchitectObjective objective, VaultRaid vault) {
      return null;
   }

   @Nullable
   public VaultPieceProcessor getPostProcessor(ArchitectObjective objective, VaultRaid vault) {
      return null;
   }

   public void onApply(ArchitectObjective objective, VaultRaid vault, ServerWorld world) {
   }
}
