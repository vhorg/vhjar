package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.VaultMod;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public interface JigsawPoolProvider {
   Random rand = new Random();

   default StructureTemplatePool getStartRoomPool(Registry<StructureTemplatePool> jigsawRegistry) {
      return (StructureTemplatePool)jigsawRegistry.get(VaultMod.id("vault/starts"));
   }

   default StructureTemplatePool getRoomPool(Registry<StructureTemplatePool> jigsawRegistry) {
      return (StructureTemplatePool)jigsawRegistry.get(VaultMod.id("vault/rooms"));
   }

   default StructureTemplatePool getTunnelPool(Registry<StructureTemplatePool> jigsawRegistry) {
      return (StructureTemplatePool)jigsawRegistry.get(VaultMod.id("vault/tunnels"));
   }
}
