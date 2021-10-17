package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;

public interface JigsawPoolProvider {
   default JigsawPattern getStartRoomPool(Registry<JigsawPattern> jigsawRegistry) {
      return (JigsawPattern)jigsawRegistry.func_82594_a(Vault.id("vault/starts"));
   }

   default JigsawPattern getRoomPool(Registry<JigsawPattern> jigsawRegistry) {
      return (JigsawPattern)jigsawRegistry.func_82594_a(Vault.id("vault/rooms"));
   }

   default JigsawPattern getTunnelPool(Registry<JigsawPattern> jigsawRegistry) {
      return (JigsawPattern)jigsawRegistry.func_82594_a(Vault.id("vault/tunnels"));
   }
}
