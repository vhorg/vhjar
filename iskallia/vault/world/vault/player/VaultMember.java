package iskallia.vault.world.vault.player;

import iskallia.vault.Vault;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.UUID;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class VaultMember extends VaultPlayer {
   public static final ResourceLocation ID = Vault.id("member");

   public VaultMember() {
   }

   public VaultMember(UUID playerId) {
      this(ID, playerId);
   }

   public VaultMember(ResourceLocation id, UUID playerId) {
      super(id, playerId);
   }

   @Override
   public void tickTimer(VaultRaid vault, ServerWorld world, VaultTimer timer) {
   }

   @Override
   public void tickObjectiveUpdates(VaultRaid vault, ServerWorld world) {
   }
}
