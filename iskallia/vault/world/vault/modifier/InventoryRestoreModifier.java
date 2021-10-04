package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.data.PhoenixModifierSnapshotData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class InventoryRestoreModifier extends TexturedVaultModifier {
   @Expose
   private final boolean preventsArtifact;

   public InventoryRestoreModifier(String name, ResourceLocation icon, boolean preventsArtifact) {
      super(name, icon);
      this.preventsArtifact = preventsArtifact;
   }

   public boolean preventsArtifact() {
      return this.preventsArtifact;
   }

   @Override
   public void apply(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
      player.runIfPresent(world.func_73046_m(), sPlayer -> {
         PhoenixModifierSnapshotData snapshotData = PhoenixModifierSnapshotData.get(world);
         if (snapshotData.hasSnapshot(sPlayer)) {
            snapshotData.removeSnapshot(sPlayer);
         }

         snapshotData.createSnapshot(sPlayer);
      });
   }

   @Override
   public void remove(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
      PhoenixModifierSnapshotData snapshotData = PhoenixModifierSnapshotData.get(world);
      if (snapshotData.hasSnapshot(player.getPlayerId())) {
         snapshotData.removeSnapshot(player.getPlayerId());
      }
   }
}
