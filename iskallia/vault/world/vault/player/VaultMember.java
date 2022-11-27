package iskallia.vault.world.vault.player;

import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class VaultMember extends VaultPlayer {
   public static final ResourceLocation ID = VaultMod.id("member");

   public VaultMember() {
   }

   public VaultMember(UUID playerId) {
      this(ID, playerId);
   }

   public VaultMember(ResourceLocation id, UUID playerId) {
      super(id, playerId);
   }

   @Override
   public void tickTimer(VaultRaid vault, ServerLevel world, VaultTimer timer) {
   }

   @Override
   public void tickObjectiveUpdates(VaultRaid vault, ServerLevel world) {
   }
}
