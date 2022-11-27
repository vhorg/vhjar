package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.init.ModTextureAtlases;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2FloatMap.Entry;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class CombatStatsContainerElement extends VerticalScrollClipContainer<CombatStatsContainerElement> {
   public CombatStatsContainerElement(ISpatial spatial, StatCollector statCollector) {
      super(spatial, Padding.of(2, 0));
      this.addElement(
         new CombatStatsContainerElement.VaultCombatStatsBoxElement(Spatials.positionY(3), statCollector).postLayout((screen, gui, parent, world) -> {
            world.translateX((this.innerWidth() - world.width()) / 2);
            return true;
         })
      );
   }

   private static final class ChestStringElement extends DynamicLabelElement<Component, CombatStatsContainerElement.ChestStringElement> {
      private ChestStringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }

   private static final class ComponentElement extends DynamicLabelElement<Component, CombatStatsContainerElement.ComponentElement> {
      private ComponentElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }

   private static final class VaultCombatStatsBoxElement extends ElasticContainerElement<CombatStatsContainerElement.VaultCombatStatsBoxElement> {
      private VaultCombatStatsBoxElement(IPosition position, StatCollector statCollector) {
         super(Spatials.positionXYZ(position));
         Map<ResourceLocation, Float> list = new HashMap<>();
         Object2FloatMap<ResourceLocation> group = statCollector.getDamageReceived();
         float damageReceived = 0.0F;
         ObjectIterator list2 = group.object2FloatEntrySet().iterator();

         while (list2.hasNext()) {
            Entry<ResourceLocation> entry = (Entry<ResourceLocation>)list2.next();
            ResourceLocation loc = (ResourceLocation)entry.getKey();
            float amount = entry.getFloatValue();
            damageReceived += amount;
            list.put(loc, amount);
         }

         Map<ResourceLocation, Float> list2x = new HashMap<>();
         Object2FloatMap<ResourceLocation> group2 = statCollector.getDamageDealt();
         float damageDealt = 0.0F;
         ObjectIterator var21 = group2.object2FloatEntrySet().iterator();

         while (var21.hasNext()) {
            Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var21.next();
            ResourceLocation loc = (ResourceLocation)entry.getKey();
            float amount = entry.getFloatValue();
            damageDealt += amount;
            list2x.put(loc, amount);
         }

         Component component = new TextComponent("Damage Dealt: ")
            .withStyle(ChatFormatting.WHITE)
            .append(new TextComponent(String.format("%.1f", damageDealt)).withStyle(ChatFormatting.RED));
         Component component2 = new TextComponent("Damage Taken: ")
            .withStyle(ChatFormatting.WHITE)
            .append(new TextComponent(String.format("%.1f", damageReceived)).withStyle(ChatFormatting.DARK_RED));
         this.addElements(
            new CombatStatsContainerElement.ComponentElement(
               Spatials.positionXYZ(101, 4, 1), Spatials.size(16, 7), (Supplier<Component>)(() -> component), LabelTextStyle.shadow().center()
            ),
            new IElement[0]
         );
         this.addElements(
            new CombatStatsContainerElement.ComponentElement(
               Spatials.positionXYZ(101, 16, 1), Spatials.size(16, 7), (Supplier<Component>)(() -> component2), LabelTextStyle.shadow().center()
            ),
            new IElement[0]
         );
         Map<ResourceLocation, Integer> mobsKilled = new HashMap<>();
         Object2IntMap<ResourceLocation> mobsKilledGroup = statCollector.getEntitiesKilled();
         int mobsKilledNum = 0;
         ObjectIterator var14 = mobsKilledGroup.object2IntEntrySet().iterator();

         while (var14.hasNext()) {
            it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<ResourceLocation> entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<ResourceLocation>)var14.next();
            ResourceLocation loc = (ResourceLocation)entry.getKey();
            int amount = entry.getIntValue();
            mobsKilledNum += amount;
            mobsKilled.put(loc, amount);
         }

         this.addElements(
            new VaultCombatStatsElement(
               Spatials.positionY(28).positionX(0),
               TextureAtlasRegion.of(ModTextureAtlases.SCAVENGER, VaultMod.id("gui/scavenger/mob")),
               222,
               27,
               new TextComponent(mobsKilledNum + "x Mobs Unalived"),
               mobsKilled,
               statCollector
            ),
            new IElement[0]
         );
      }
   }
}
