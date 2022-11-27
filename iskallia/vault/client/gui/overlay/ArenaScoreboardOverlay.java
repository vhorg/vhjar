package iskallia.vault.client.gui.overlay;

import iskallia.vault.client.gui.helper.ArenaScoreboardContainer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public class ArenaScoreboardOverlay {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/arena_scoreboard.png");
   public static ArenaScoreboardContainer scoreboard = new ArenaScoreboardContainer();
   public static int hornsPlayedCount;
   private static SimpleSoundInstance arenaHorns;
}
