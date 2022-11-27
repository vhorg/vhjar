package iskallia.vault.client.gui.screen.bounty;

import iskallia.vault.VaultMod;
import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.screen.bounty.element.BountyTableContainerElement;
import iskallia.vault.container.BountyContainer;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BountyScreen extends AbstractElementContainerScreen<BountyContainer> {
   public static final Map<ResourceLocation, TextureAtlasRegion> TASK_ICON_MAP = Map.of(
      TaskRegistry.KILL_ENTITY,
      ScreenTextures.BOUNTY_KILL_ICON,
      TaskRegistry.DAMAGE_ENTITY,
      ScreenTextures.BOUNTY_DAMAGE_ENTITY,
      TaskRegistry.COMPLETION,
      ScreenTextures.BOUNTY_COMPLETION,
      TaskRegistry.ITEM_DISCOVERY,
      ScreenTextures.BOUNTY_ITEM_DISCOVERY,
      TaskRegistry.ITEM_SUBMISSION,
      ScreenTextures.BOUNTY_ITEM_SUBMISSION,
      TaskRegistry.MINING,
      ScreenTextures.BOUNTY_MINING,
      VaultMod.id("unindentified"),
      ScreenTextures.BOUNTY_UNIDENTIFIED
   );
   public static final Map<ResourceLocation, TextComponent> OBJECTIVE_NAME = Map.of(
      VaultMod.id("obelisk"),
      new TextComponent("Obelisks"),
      VaultMod.id("kill_boss"),
      new TextComponent("Kill The Boss"),
      VaultMod.id("cake"),
      new TextComponent("Find The Cakes"),
      VaultMod.id("scavenger"),
      new TextComponent("Scavenger Hunt"),
      VaultMod.id("vault"),
      new TextComponent("Any Vault")
   );
   private final BountyTableContainerElement bountyTableContainerElement;
   private static final boolean debug = false;

   public BountyScreen(BountyContainer container, Inventory inv, Component title) {
      super(container, inv, title, ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      int width = 366;
      int height = 224;
      this.setGuiSize(Spatials.size(width, height));
      this.bountyTableContainerElement = this.addElement(
         new BountyTableContainerElement(Spatials.positionXY(0, 0).size(this.imageWidth, this.imageHeight), container)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (SlotsElement)new SlotsElement(Spatials.positionXY(0, 0), ((BountyContainer)this.getMenu()).slots, ScreenTextures.INSET_ITEM_SLOT_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.positionXY(gui))
      );
   }

   public BountyTableContainerElement getBountyTableElement() {
      return this.bountyTableContainerElement;
   }
}
