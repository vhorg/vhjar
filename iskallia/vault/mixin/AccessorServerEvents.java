package iskallia.vault.mixin;

import com.github.alexthe666.alexsmobs.event.ServerEvents;
import com.github.alexthe666.alexsmobs.world.BeachedCachalotWhaleSpawner;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ServerEvents.class})
public interface AccessorServerEvents {
   @Accessor("BEACHED_CACHALOT_WHALE_SPAWNER_MAP")
   static Map<ServerLevel, BeachedCachalotWhaleSpawner> getBeachedWhaleMap() {
      throw new AssertionError();
   }
}
