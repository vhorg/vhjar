package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.client.gui.screen.summary.VaultEndScreen;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModTextureAtlases;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultCombatStatsElement<E extends VaultCombatStatsElement<E>> extends ContainerElement<E> {
   public VaultCombatStatsElement(
      IPosition position,
      TextureAtlasRegion icon,
      int width,
      int height,
      Component name,
      Map<ResourceLocation, Integer> mobsKilled,
      StatCollector statCollector
   ) {
      super(Spatials.positionXYZ(position).size(width, height + mobsKilled.size() * 20 + 4));
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(5, 0, 3).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON)
            .layout((screen, gui, parent, world) -> world.size(24, 24))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(0, 2, 2).size(width, 20), ScreenTextures.VAULT_EXIT_ELEMENT_TITLE)
            .layout((screen, gui, parent, world) -> world.size(width, 20))
      );
      if (mobsKilled.size() > 0) {
         this.addElement(
            (NineSliceElement)new NineSliceElement(
                  Spatials.positionXYZ(4, 20, 1).size(width - 8, height - 20 + mobsKilled.size() * 20), ScreenTextures.VAULT_EXIT_ELEMENT_BG
               )
               .layout((screen, gui, parent, world) -> world.size(width - 8, height - 20 + mobsKilled.size() * 20))
         );
      }

      this.addElement(new TextureAtlasElement(Spatials.positionXYZ(9, 4, 5), icon));
      this.addElement(
         new VaultCombatStatsElement.ChestStringElement(
            Spatials.positionXYZ(32, 8, 4), Spatials.size(16, 7), (Supplier<Component>)(() -> name), LabelTextStyle.shadow().left()
         )
      );
      AtomicReference<Float> totalXp = new AtomicReference<>(0.0F);
      AtomicInteger iterator = new AtomicInteger();
      mobsKilled.forEach(
         (resourceLocation, integer) -> {
            String texturePath = resourceLocation.toString().replace(':', '/');
            EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(resourceLocation);
            String entityName = type == null ? "Unknown" : type.toString();
            Map<ResourceLocation, Float> map = ModConfigs.VAULT_STATS.getMobsKilled();
            float xpMul = map.getOrDefault(resourceLocation, map.get(new ResourceLocation("default")));
            float xp = xpMul * integer.intValue();
            totalXp.updateAndGet(v -> v + xp);
            Component nameComponent = new TextComponent(integer + "x ").append(new TranslatableComponent(entityName));
            Component xpComponent = new TranslatableComponent(String.format("%.1f xp", xp)).withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR));
            int textWidth = TextBorder.DEFAULT_FONT.get().width(nameComponent);
            int xpWidth = TextBorder.DEFAULT_FONT.get().width(xpComponent);
            this.addElement(
               (VaultCombatStatsElement.ValueElement)new VaultCombatStatsElement.ValueElement(
                     Spatials.positionXYZ(29, 30 + iterator.get() * 20, 2), Spatials.size(textWidth, 9), nameComponent, LabelTextStyle.shadow().left()
                  )
                  .tooltip(() -> {
                     for (int i = 0; i < ModEntities.VAULT_FIGHTER_TYPES.size() && type != null; i++) {
                        if (type.equals(ModEntities.VAULT_FIGHTER_TYPES.get(i))) {
                           return new TranslatableComponent(entityName).append(" Tier " + (i + 1)).append(" ").append(xpComponent);
                        }
                     }

                     return new TranslatableComponent(entityName).append(" ").append(xpComponent);
                  })
            );
            this.addElement(
               (VaultCombatStatsElement.ValueElement)new VaultCombatStatsElement.ValueElement(
                     Spatials.positionXYZ(width - 10 - xpWidth, 30 + iterator.get() * 20, 2), Spatials.size(textWidth, 9), xpComponent, LabelTextStyle.shadow()
                  )
                  .tooltip(
                     () -> new TranslatableComponent(entityName)
                        .withStyle(ChatFormatting.WHITE)
                        .append(
                           new TextComponent(" " + String.format(Locale.US, "%.1f", xpMul) + " xp").withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR))
                        )
                        .append(new TextComponent(" Per Unalive").withStyle(ChatFormatting.WHITE))
                  )
            );
            TextureAtlasRegion textureAtlasRegion = TextureAtlasRegion.of(ModTextureAtlases.MOB_HEADS, VaultMod.id("gui/mob_heads/" + texturePath));
            TextureAtlasSprite textureAtlasSprite = textureAtlasRegion.getSprite();
            boolean missing = textureAtlasSprite.getName().toString().equals("minecraft:missingno");
            this.addElement(
               new TextureAtlasElement(Spatials.positionXYZ(9, 26 + iterator.get() * 20, 2).size(16, 16), missing ? ScreenTextures.BLANK : textureAtlasRegion)
            );
            iterator.getAndIncrement();
         }
      );
      this.addElement(
         new VaultCombatStatsElement.ChestStringElement(
            Spatials.positionXYZ(width - 21, 8, 4),
            Spatials.size(16, 7),
            (Supplier<Component>)(() -> new TextComponent("+ " + String.format("%.1f xp", totalXp.get()))
               .withStyle(Style.EMPTY.withColor(VaultEndScreen.XP_COLOR))),
            LabelTextStyle.shadow().right()
         )
      );
   }

   private static final class ChestStringElement extends DynamicLabelElement<Component, VaultCombatStatsElement.ChestStringElement> {
      private ChestStringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }

   private static final class ValueElement extends DynamicLabelElement<Component, VaultCombatStatsElement.ValueElement> {
      private ValueElement(IPosition position, ISize size, Component component, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, () -> component, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }
}
