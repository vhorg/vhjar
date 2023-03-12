package iskallia.vault.client.gui.screen.bounty;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
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
import net.minecraft.client.Minecraft;
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
   public static final Map<ResourceLocation, TextureAtlasRegion> TASK_ICON_MAP_LEGENDARY = Map.of(
      TaskRegistry.KILL_ENTITY,
      ScreenTextures.BOUNTY_KILL_ICON_32,
      TaskRegistry.DAMAGE_ENTITY,
      ScreenTextures.BOUNTY_DAMAGE_ENTITY_32,
      TaskRegistry.COMPLETION,
      ScreenTextures.BOUNTY_COMPLETION_32,
      TaskRegistry.ITEM_DISCOVERY,
      ScreenTextures.BOUNTY_ITEM_DISCOVERY_32,
      TaskRegistry.ITEM_SUBMISSION,
      ScreenTextures.BOUNTY_ITEM_SUBMISSION_32,
      TaskRegistry.MINING,
      ScreenTextures.BOUNTY_MINING_32,
      VaultMod.id("unindentified"),
      ScreenTextures.BOUNTY_UNIDENTIFIED
   );
   public static final Map<String, TextComponent> OBJECTIVE_NAME = Map.of(
      "obelisk",
      new TextComponent("Obelisks"),
      "boss",
      new TextComponent("Hunt The Guardians"),
      "cake",
      new TextComponent("Find The Cakes"),
      "scavenger",
      new TextComponent("Scavenger Hunt"),
      "vault",
      new TextComponent("Any Vault"),
      "monolith",
      new TextComponent("Light the Monoliths"),
      "elixir",
      new TextComponent("Gather Elixir")
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

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      Key key = InputConstants.getKey(pKeyCode, pScanCode);
      if (pKeyCode != 256 && !Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
         return super.keyPressed(pKeyCode, pScanCode, pModifiers);
      } else {
         this.onClose();
         return true;
      }
   }
}
