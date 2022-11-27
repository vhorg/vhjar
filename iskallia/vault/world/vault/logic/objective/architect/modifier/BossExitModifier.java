package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.gen.structure.VaultJigsawHelper;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.logic.objective.architect.processor.BossSpawnPieceProcessor;
import iskallia.vault.world.vault.logic.objective.architect.processor.ExitPortalPieceProcessor;
import iskallia.vault.world.vault.logic.objective.architect.processor.VaultPieceProcessor;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public class BossExitModifier extends VoteModifier {
   @Expose
   private final float portalChance;

   public BossExitModifier(String name, String description, int voteLockDurationChangeSeconds, float portalChance) {
      super(name, description, voteLockDurationChangeSeconds);
      this.portalChance = portalChance;
   }

   public boolean generatePortal(VaultRaid vault) {
      return vault.getProperties().getBase(VaultRaid.IDENTIFIER).map(id -> {
         Random r = new Random(id.getMostSignificantBits() ^ id.getLeastSignificantBits());
         return r.nextFloat() < this.portalChance;
      }).orElse(false);
   }

   @Nullable
   @Override
   public StructurePoolElement getSpecialRoom(ArchitectObjective objective, VaultRaid vault) {
      return this.generatePortal(vault) ? VaultJigsawHelper.getArchitectRoom() : super.getSpecialRoom(objective, vault);
   }

   @Nullable
   @Override
   public VaultPieceProcessor getPostProcessor(ArchitectObjective objective, VaultRaid vault) {
      return (VaultPieceProcessor)(this.generatePortal(vault) ? new ExitPortalPieceProcessor(objective) : new BossSpawnPieceProcessor(objective));
   }

   @Override
   public void onApply(ArchitectObjective objective, VaultRaid vault, ServerLevel world) {
      super.onApply(objective, vault, world);
      objective.setVotingLocked();
   }
}
