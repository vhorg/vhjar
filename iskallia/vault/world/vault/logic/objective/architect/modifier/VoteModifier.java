package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.logic.objective.architect.processor.VaultPieceProcessor;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

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

   public Component getDescription() {
      return new TextComponent(this.getDescriptionText()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(Integer.parseInt(this.color))));
   }

   @Nullable
   public StructurePoolElement getSpecialRoom(ArchitectObjective objective, VaultRaid vault) {
      return null;
   }

   @Nullable
   public VaultPieceProcessor getPostProcessor(ArchitectObjective objective, VaultRaid vault) {
      return null;
   }

   public void onApply(ArchitectObjective objective, VaultRaid vault, ServerLevel world) {
   }
}
