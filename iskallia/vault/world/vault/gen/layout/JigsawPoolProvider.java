package iskallia.vault.world.vault.gen.layout;

import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;

public interface JigsawPoolProvider {
   Random rand = new Random();

   ResourceLocation getStartRoomId();

   ResourceLocation getRoomId();

   ResourceLocation getTunnelId();

   default JigsawPattern getStartRoomPool(Registry<JigsawPattern> jigsawRegistry) {
      return (JigsawPattern)jigsawRegistry.func_82594_a(this.getStartRoomId());
   }

   default JigsawPattern getRoomPool(Registry<JigsawPattern> jigsawRegistry) {
      return (JigsawPattern)jigsawRegistry.func_82594_a(this.getRoomId());
   }

   default JigsawPattern getTunnelPool(Registry<JigsawPattern> jigsawRegistry) {
      return (JigsawPattern)jigsawRegistry.func_82594_a(this.getTunnelId());
   }
}
