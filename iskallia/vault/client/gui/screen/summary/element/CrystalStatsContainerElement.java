package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.core.vault.Modifiers;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class CrystalStatsContainerElement extends VerticalScrollClipContainer<CrystalStatsContainerElement> {
   public CrystalStatsContainerElement(ISpatial spatial, Modifiers modifiers) {
      super(spatial, Padding.of(2, 0));
      this.addElement(new CrystalStatsContainerElement.VaultCrystalElement(Spatials.positionY(3), modifiers).postLayout((screen, gui, parent, world) -> {
         world.translateX((this.innerWidth() - world.width()) / 2);
         return true;
      }));
   }

   private static final class VaultCrystalElement extends ElasticContainerElement<CrystalStatsContainerElement.VaultCrystalElement> {
      private VaultCrystalElement(IPosition position, Modifiers modifiers) {
         super(Spatials.positionXYZ(position));
         Map<VaultModifier<?>, Integer> list = new HashMap<>();
         ObjectIterator var4 = modifiers.getDisplayGroup().object2IntEntrySet().iterator();

         while (var4.hasNext()) {
            Entry<VaultModifier<?>> entry = (Entry<VaultModifier<?>>)var4.next();
            VaultModifier<?> modifier = (VaultModifier<?>)entry.getKey();
            int amount = entry.getIntValue();
            Optional<ResourceLocation> icon = modifier.getIcon();
            if (!icon.isEmpty()) {
               list.put(modifier, amount);
            }
         }

         this.addElements(
            new VaultModifiersElement(Spatials.positionY(10).positionX(0), ScreenTextures.TAB_ICON_CRYSTAL, 160, 30, new TextComponent("Modifiers"), list),
            new IElement[0]
         );
      }
   }
}
