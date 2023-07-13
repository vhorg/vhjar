package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.ClientTalentData;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.TabElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.legacy.widget.AbilityNodeTextures;
import iskallia.vault.client.gui.screen.player.legacy.widget.NodeState;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.container.SkillAltarContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.world.data.SkillAltarData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillAltarScreen extends AbstractElementContainerScreen<SkillAltarContainer> {
   private static final int MAX_TABS = 5;
   private final ButtonElement<?> loadButton;
   private final ButtonElement<?> saveButton;
   private final SkillAltarScreen.SkillView skillView;
   private boolean isSaveLocked = true;
   @Nullable
   private List<Component> loadTooltip = null;
   @Nullable
   private List<Component> saveTooltip = null;
   private final boolean abilitiesTalentsEqual = this.areAbilitiesAndTalentsEqual();
   private final int numberOfOrbsRequired = ((SkillAltarContainer)this.getMenu()).getNumberOfRegretOrbsRequired();

   public SkillAltarScreen(SkillAltarContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.setGuiSize(Spatials.size(176, 216));
      this.addTabs();
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXY(0, 0).size(0, 10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(gui))
      );
      this.addElement(
         (LabelElement)new LabelElement(Spatials.positionXY(8, 6), title.copy().withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.skillView = this.createSkillView();
      this.addElement(this.skillView);
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, ((SkillAltarContainer)this.getMenu()).getSlot(0).y - 12), inventoryName, LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      ButtonElement.ButtonTextures buttonTextures = ScreenTextures.BUTTON_SKILL_ALTAR_SAVE_TEXTURES;
      this.saveButton = this.addElement(
         new ButtonElement(
               Spatials.positionXY(this.imageWidth / 4 - buttonTextures.button().width() / 2, 99),
               buttonTextures,
               ((SkillAltarContainer)this.getMenu())::saveTemplate
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.saveButton
         .setDisabled(
            () -> this.playerHasNoAbilitiesAndTalents()
               || !((SkillAltarContainer)this.getMenu()).isEmptyTemplate() && this.isSaveLocked
               || this.abilitiesTalentsEqual
               || ((SkillAltarContainer)this.getMenu()).isOpenedByNonOwner()
         );
      this.saveButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
         tooltipRenderer.renderComponentTooltip(poseStack, this.getSaveButtonTooltip(), mouseX, mouseY, TooltipDirection.RIGHT);
         return true;
      });
      if (!((SkillAltarContainer)this.getMenu()).isEmptyTemplate()) {
         this.addElement(
            new SkillAltarScreen.LockButton(Spatials.positionX(-10).translateXY(this.saveButton), () -> this.isSaveLocked = !this.isSaveLocked)
               .layout((screen, gui, parent, world) -> world.translateXY(gui))
         );
      }

      buttonTextures = ScreenTextures.BUTTON_SKILL_ALTAR_LOAD_TEXTURES;
      this.loadButton = this.addElement(
         new ButtonElement(
               Spatials.positionXY(3 * (this.imageWidth / 4) - buttonTextures.button().width() / 2, 99),
               buttonTextures,
               () -> ((SkillAltarContainer)this.getMenu()).setPlayerAbilitiesAndTalentsFromTemplate(VaultBarOverlay.unspentRegretPoints)
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.loadButton
         .setDisabled(
            () -> ((SkillAltarContainer)this.getMenu()).isEmptyTemplate()
               || this.abilitiesTalentsEqual
               || ((SkillAltarContainer)this.getMenu()).getNumberOfMissingRegretOrbs(VaultBarOverlay.unspentRegretPoints) > 0
               || ((SkillAltarContainer)this.getMenu()).isOpenedByNonOwner()
         );
      this.loadButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
         tooltipRenderer.renderComponentTooltip(poseStack, this.getLoadButtonTooltip(), mouseX, mouseY, TooltipDirection.RIGHT);
         return true;
      });
   }

   public static TextureAtlasRegion getSkillIcon(SkillAltarData.SkillIcon icon) {
      if (icon.isTalent()) {
         SkillStyle skillStyle = ModConfigs.TALENTS_GUI.getStyles().get(icon.key());
         if (skillStyle != null) {
            return TextureAtlasRegion.of(ModTextureAtlases.SKILLS, skillStyle.icon);
         }
      } else {
         ResourceLocation i = ModConfigs.ABILITIES_GUI.getIcon(icon.key());
         if (i != null) {
            return TextureAtlasRegion.of(ModTextureAtlases.ABILITIES, i);
         }
      }

      return ScreenTextures.TAB_ICON_ABILITIES;
   }

   private boolean playerHasNoAbilitiesAndTalents() {
      return this.getPlayerAbilities().isEmpty() && this.getPlayerTalents().isEmpty();
   }

   private List<Component> getSaveButtonTooltip() {
      if (this.saveTooltip != null) {
         return this.saveTooltip;
      } else {
         this.saveTooltip = new ArrayList<>();
         if (((SkillAltarContainer)this.getMenu()).isOpenedByNonOwner()) {
            this.saveTooltip.add(new TranslatableComponent("screen.the_vault.skill_altar.tooltip.save.not_owner").withStyle(ChatFormatting.RED));
         } else {
            if (this.playerHasNoAbilitiesAndTalents()) {
               this.saveTooltip = List.of(
                  new TranslatableComponent("screen.the_vault.skill_altar.tooltip.save.no_abilities_talents").withStyle(ChatFormatting.RED)
               );
               return this.saveTooltip;
            }

            if (this.abilitiesTalentsEqual) {
               this.saveTooltip = List.of(
                  new TranslatableComponent("screen.the_vault.skill_altar.tooltip.save.same_abilities_talents").withStyle(ChatFormatting.YELLOW)
               );
               return this.saveTooltip;
            }

            if (!((SkillAltarContainer)this.getMenu()).isEmptyTemplate() && this.isSaveLocked) {
               this.saveTooltip = List.of(new TranslatableComponent("screen.the_vault.skill_altar.tooltip.save.unlock_required").withStyle(ChatFormatting.RED));
               return this.saveTooltip;
            }

            this.saveTooltip.add(new TranslatableComponent("screen.the_vault.skill_altar.tooltip.save"));
         }

         List<TieredSkill> templateAbilities = ((SkillAltarContainer)this.getMenu()).getTemplate() != null
            ? this.getTemplateAbilities()
            : Collections.emptyList();
         List<TieredSkill> templateTalents = ((SkillAltarContainer)this.getMenu()).getTemplate() != null ? this.getTemplateTalents() : Collections.emptyList();
         this.saveTooltip.addAll(this.getSkillDifferencesTooltip(templateAbilities, this.getPlayerAbilities(), templateTalents, this.getPlayerTalents()));
         return this.saveTooltip;
      }
   }

   private List<TieredSkill> getTemplateTalents() {
      return ((SkillAltarContainer)this.getMenu()).getTemplate().getTalents().getAll(TieredSkill.class, TieredSkill::isUnlocked);
   }

   private List<TieredSkill> getTemplateAbilities() {
      return ((SkillAltarContainer)this.getMenu())
         .getTemplate()
         .getAbilities()
         .getAll(SpecializedSkill.class, SpecializedSkill::isUnlocked)
         .stream()
         .map(SpecializedSkill::getSpecialization)
         .map(TieredSkill.class::cast)
         .toList();
   }

   private List<TieredSkill> getPlayerTalents() {
      return ClientTalentData.getLearnedTalentNodes();
   }

   private List<TieredSkill> getPlayerAbilities() {
      return ClientAbilityData.getLearnedAbilities();
   }

   private List<Component> getLoadButtonTooltip() {
      if (this.loadTooltip != null) {
         return this.loadTooltip;
      } else {
         this.loadTooltip = new ArrayList<>();
         if (((SkillAltarContainer)this.getMenu()).isOpenedByNonOwner()) {
            this.loadTooltip.add(new TranslatableComponent("screen.the_vault.skill_altar.tooltip.load.not_owner").withStyle(ChatFormatting.RED));
         } else {
            if (((SkillAltarContainer)this.getMenu()).isEmptyTemplate()) {
               this.loadTooltip = List.of(
                  new TranslatableComponent("screen.the_vault.skill_altar.tooltip.load.no_abilities_talents").withStyle(ChatFormatting.RED)
               );
               return this.loadTooltip;
            }

            if (this.abilitiesTalentsEqual) {
               this.loadTooltip = List.of(
                  new TranslatableComponent("screen.the_vault.skill_altar.tooltip.load.same_abilities_talents").withStyle(ChatFormatting.YELLOW)
               );
               return this.loadTooltip;
            }

            if (((SkillAltarContainer)this.getMenu()).getNumberOfMissingRegretOrbs(VaultBarOverlay.unspentRegretPoints) > 0) {
               this.loadTooltip
                  .add(
                     new TranslatableComponent(
                           "screen.the_vault.skill_altar.tooltip.load.missing_orbs",
                           new Object[]{((SkillAltarContainer)this.getMenu()).getNumberOfMissingRegretOrbs(VaultBarOverlay.unspentRegretPoints)}
                        )
                        .withStyle(ChatFormatting.RED)
                  );
            } else {
               this.loadTooltip.add(new TranslatableComponent("screen.the_vault.skill_altar.tooltip.load"));
               this.loadTooltip
                  .add(
                     new TranslatableComponent("screen.the_vault.skill_altar.tooltip.load.cost", new Object[]{this.numberOfOrbsRequired})
                        .withStyle(ChatFormatting.DARK_GRAY)
                  );
            }
         }

         if (((SkillAltarContainer)this.getMenu()).isEmptyTemplate()) {
            return this.loadTooltip;
         } else {
            this.loadTooltip
               .addAll(
                  this.getSkillDifferencesTooltip(this.getPlayerAbilities(), this.getTemplateAbilities(), this.getPlayerTalents(), this.getTemplateTalents())
               );
            return this.loadTooltip;
         }
      }
   }

   private List<Component> getSkillDifferencesTooltip(
      List<TieredSkill> abilitiesA, List<TieredSkill> abilitiesB, List<TieredSkill> talentsA, List<TieredSkill> talentsB
   ) {
      List<Component> tooltip = new ArrayList<>();
      List<Component> abilityDifferencesTooltip = this.getSkillDifferencesTooltip(
         abilitiesA, abilitiesB, Skill::getName, TieredSkill::getUnmodifiedTier, Style.EMPTY.withColor(-9916953)
      );
      if (!abilityDifferencesTooltip.isEmpty()) {
         tooltip.add(TextComponent.EMPTY);
         tooltip.add(new TranslatableComponent("screen.the_vault.skill_altar.tooltip.abilities").withStyle(ChatFormatting.GRAY));
         tooltip.addAll(abilityDifferencesTooltip);
      }

      List<Component> talentDifferencesTooltip = this.getSkillDifferencesTooltip(
         talentsA, talentsB, Skill::getName, TieredSkill::getUnmodifiedTier, Style.EMPTY.withColor(-6981964)
      );
      if (!talentDifferencesTooltip.isEmpty()) {
         tooltip.add(TextComponent.EMPTY);
         tooltip.add(new TranslatableComponent("screen.the_vault.skill_altar.tooltip.talents").withStyle(ChatFormatting.GRAY));
         tooltip.addAll(talentDifferencesTooltip);
      }

      return tooltip;
   }

   private <T> List<Component> getSkillDifferencesTooltip(
      List<T> nodesA, List<T> nodesB, Function<T, String> nameFunction, ToIntFunction<T> levelFunction, Style nameStyle
   ) {
      Map<String, Component> abilityDifferences = new TreeMap<>();
      Map<String, T> nodesBMap = nodesB.stream().collect(Collectors.toMap(nameFunction, a -> (T)a));
      Set<String> nodesANames = nodesA.stream().map(nameFunction).collect(Collectors.toSet());
      nodesA.forEach(
         nodeA -> {
            String parentName = nameFunction.apply((T)nodeA);
            int levelDifference = (nodesBMap.containsKey(parentName) ? levelFunction.applyAsInt(nodesBMap.get(parentName)) : 0)
               - levelFunction.applyAsInt((T)nodeA);
            if (levelDifference > 0) {
               abilityDifferences.put(parentName, this.getPositiveLevelDifference(parentName, levelDifference, nameStyle));
            } else if (levelDifference < 0) {
               abilityDifferences.put(parentName, this.getNegativeLevelDifference(parentName, levelDifference, nameStyle));
            }
         }
      );
      nodesBMap.values().forEach(nodeB -> {
         String parentName = nameFunction.apply((T)nodeB);
         if (!nodesANames.contains(parentName)) {
            abilityDifferences.put(parentName, this.getPositiveLevelDifference(parentName, levelFunction.applyAsInt((T)nodeB), nameStyle));
         }
      });
      return new ArrayList<>(abilityDifferences.values());
   }

   private MutableComponent getNegativeLevelDifference(String name, int levelDifference, Style nameStyle) {
      return new TextComponent("-" + Math.abs(levelDifference) + " ").withStyle(ChatFormatting.RED).append(new TextComponent(name).withStyle(nameStyle));
   }

   private MutableComponent getPositiveLevelDifference(String name, int levelDifference, Style nameStyle) {
      return new TextComponent("+" + levelDifference + " ").withStyle(ChatFormatting.GREEN).append(new TextComponent(name).withStyle(nameStyle));
   }

   private void addTabs() {
      for (AtomicInteger i = new AtomicInteger(0); i.get() < Math.min(5, ((SkillAltarContainer)this.getMenu()).getSkillIcons().size() + 1); i.incrementAndGet()) {
         int templateIndex = i.get();
         if (templateIndex == ((SkillAltarContainer)this.getMenu()).getTemplateIndex()) {
            this.addSelectedTab();
         } else if (templateIndex < ((SkillAltarContainer)this.getMenu()).getSkillIcons().size()) {
            SkillAltarData.SkillIcon icon = ((SkillAltarContainer)this.getMenu()).getSkillIcons().get(templateIndex);
            this.addElement(createTab(false, getSkillIcon(icon), 4 + templateIndex * 31, () -> ((SkillAltarContainer)this.getMenu()).openTab(templateIndex)));
         } else {
            this.addElement(
               createTab(false, ScreenTextures.TAB_ICON_ABILITIES, 4 + templateIndex * 31, () -> ((SkillAltarContainer)this.getMenu()).openTab(templateIndex))
            );
         }
      }
   }

   private SkillAltarScreen.SkillView createSkillView() {
      return new SkillAltarScreen.SkillView(
            Spatials.positionXY(7, 16).size(177, 81),
            ((SkillAltarContainer)this.getMenu()).getTemplate(),
            icon -> ((SkillAltarContainer)this.getMenu()).updateTemplateIcon(icon)
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
   }

   private void addSelectedTab() {
      SkillAltarData.SkillTemplate template = ((SkillAltarContainer)this.getMenu()).getTemplate();
      TextureAtlasRegion icon = ScreenTextures.TAB_ICON_ABILITIES;
      if (template != null && !template.getIcon().key().isEmpty()) {
         icon = getSkillIcon(template.getIcon());
      }

      this.addElement(createTab(true, icon, 4 + ((SkillAltarContainer)this.getMenu()).getTemplateIndex() * 31, () -> {}));
   }

   private static TabElement<?> createTab(boolean selected, TextureAtlasRegion icon, int x, Runnable onClick) {
      return new TabElement(
            Spatials.positionXYZ(x, selected ? -28 : -24, 1),
            new TextureAtlasElement(selected ? ScreenTextures.TAB_BACKGROUND_TOP_SELECTED : ScreenTextures.TAB_BACKGROUND_TOP),
            new TextureAtlasElement(Spatials.positionXYZ(6, selected ? 9 : 5, 1), icon),
            onClick
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
   }

   @Override
   public void mouseMoved(double mouseX, double mouseY) {
      super.mouseMoved(mouseX, mouseY);
      if (this.loadTooltip != null && !this.loadButton.isMouseOver(mouseX, mouseY)) {
         this.loadTooltip = null;
      } else if (this.saveTooltip != null && !this.saveButton.isMouseOver(mouseX, mouseY)) {
         this.saveTooltip = null;
      }
   }

   public boolean areAbilitiesAndTalentsEqual() {
      SkillAltarData.SkillTemplate template = ((SkillAltarContainer)this.getMenu()).getTemplate();
      return template == null
         ? false
         : this.areSkillsEqual(this.getPlayerAbilities(), this.getTemplateAbilities())
            && this.areSkillsEqual(this.getPlayerTalents(), this.getTemplateTalents());
   }

   private boolean areSkillsEqual(List<TieredSkill> skillsA, List<TieredSkill> skillsB) {
      if (skillsA.size() != skillsB.size()) {
         return false;
      } else {
         List<TieredSkill> sortedByNameA = skillsA.stream().sorted(Comparator.comparing(Skill::getId)).toList();
         List<TieredSkill> sortedByNameB = skillsB.stream().sorted(Comparator.comparing(Skill::getId)).toList();

         for (int i = 0; i < sortedByNameA.size(); i++) {
            TieredSkill skillA = sortedByNameA.get(i);
            TieredSkill skillB = sortedByNameB.get(i);
            if (!skillA.getId().equals(skillB.getId()) || skillA.getUnmodifiedTier() != skillB.getUnmodifiedTier()) {
               return false;
            }
         }

         return true;
      }
   }

   protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType clickType) {
      super.slotClicked(slot, slotId, mouseButton, clickType);
      this.loadTooltip = null;
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      this.skillView.stopDragging();
      return super.mouseReleased(pMouseX, pMouseY, pButton);
   }

   private static class AbilityElement extends AbstractSpatialElement<SkillAltarScreen.AbilityElement> implements IRenderedElement, IGuiEventElement {
      private final int tier;
      private final Map<NodeState, TextureAtlasRegion> background;
      private final TextureAtlasRegion icon;
      private final TextureAtlasRegion levelBackground;
      private boolean visible = true;

      public AbilityElement(ISpatial spatial, int tier, Map<NodeState, TextureAtlasRegion> background, TextureAtlasRegion icon) {
         super(spatial);
         this.tier = tier;
         this.background = background;
         this.icon = icon;
         this.levelBackground = AbilityNodeTextures.NODE_BACKGROUND_LEVEL;
      }

      @Override
      public void setVisible(boolean visible) {
         this.visible = visible;
      }

      @Override
      public boolean isVisible() {
         return this.visible;
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         if (this.visible) {
            this.renderLevel(poseStack);
            this.background.get(NodeState.DEFAULT).blit(poseStack, this.worldSpatial.x() + this.levelBackground.width() - 4, this.worldSpatial.y());
            this.icon.blit(poseStack, this.worldSpatial.x() + this.levelBackground.width() - 4 + 3, this.worldSpatial.y() + 3);
         }
      }

      private void renderLevel(PoseStack poseStack) {
         if (this.tier != 0) {
            poseStack.pushPose();
            this.levelBackground.blit(poseStack, this.worldSpatial.x(), this.worldSpatial.y() + this.height() / 2 - this.levelBackground.height() / 2);
            Font font = Minecraft.getInstance().font;
            String text = String.valueOf(this.tier);
            font.draw(
               poseStack,
               text,
               this.left() + this.levelBackground.width() / 2.0F - font.width(text) / 2.0F,
               this.top() + this.height() / 2.0F - 9.0F / 2.0F + 1.0F,
               -1
            );
            RenderSystem.enableDepthTest();
         }
      }
   }

   private class LockButton extends ButtonElement<SkillAltarScreen.LockButton> {
      public LockButton(IPosition position, Runnable onClick) {
         super(position, ScreenTextures.BUTTON_TOGGLE_OFF_TEXTURES, onClick);
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         ButtonElement.ButtonTextures textures = SkillAltarScreen.this.isSaveLocked
            ? ScreenTextures.BUTTON_TOGGLE_OFF_TEXTURES
            : ScreenTextures.BUTTON_TOGGLE_ON_TEXTURES;
         TextureAtlasRegion texture = textures.selectTexture(this.isDisabled(), this.containsMouse(mouseX, mouseY), false);
         renderer.render(texture, poseStack, this.worldSpatial);
      }
   }

   private static class SkillView extends VerticalScrollClipContainer<SkillAltarScreen.SkillView> {
      private static final NineSlice.TextureRegion SKILL_ALTAR_SCROLL_BACKGROUND = NineSlice.region(
         ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/scroll_background"), NineSlice.slice(1, 1, 1, 1), NineSlice.DrawMode.Tiled
      );
      private static final double SCROLL_SENSITIVITY = 0.5;
      private double mouseDragY = 0.0;
      private boolean dragging = false;

      public SkillView(ISpatial spatial, @Nullable SkillAltarData.SkillTemplate template, final Consumer<SkillAltarData.SkillIcon> onIconClicked) {
         super(spatial, Padding.ZERO, SKILL_ALTAR_SCROLL_BACKGROUND);
         this.verticalScrollBarElement.setVisible(false);
         this.verticalScrollBarElement.setEnabled(false);
         if (template != null) {
            int i = 0;

            for (final SpecializedSkill ability : template.getAbilities()
               .getAll(SpecializedSkill.class, SpecializedSkill::isUnlocked)
               .stream()
               .sorted(SkillAltarData.SPECIALIZED_SKILL_HIGHEST_LEVEL_COMPARATOR)
               .toList()) {
               if (ModConfigs.ABILITIES_GUI.getStyles().containsKey(ability.getId())) {
                  int abilityY = i / 2 * 24;
                  int abilityX = i % 2 * 44;
                  this.addElement(
                     new SkillAltarScreen.AbilityElement(
                        Spatials.positionXY(abilityX, abilityY).size(32, 22),
                        ((TieredSkill)ability.getSpecialization()).getUnmodifiedTier(),
                        AbilityNodeTextures.SECONDARY_NODE,
                        TextureAtlasRegion.of(ModTextureAtlases.ABILITIES, ModConfigs.ABILITIES_GUI.getIcon(ability.getSpecialization().getId()))
                     ) {
                        @Override
                        public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
                           onIconClicked.accept(new SkillAltarData.SkillIcon(ability.getSpecialization().getId(), false));
                           return true;
                        }
                     }
                  );
                  i++;
               }
            }

            List<TieredSkill> templateTalents = template.getTalents()
               .getAll(TieredSkill.class, TieredSkill::isUnlocked)
               .stream()
               .sorted(SkillAltarData.TIERED_SKILL_HIGHEST_LEVEL_COMPARATOR)
               .toList();
            i = 0;

            for (final TieredSkill talent : templateTalents) {
               if (ModConfigs.TALENTS_GUI.getStyles().containsKey(talent.getId())) {
                  this.addElement(
                     new SkillAltarScreen.TalentElement(
                        Spatials.positionXY(79 + i % 2 * 36, i / 2 * 24).size(45, 22), talent.getId(), talent.getUnmodifiedTier()
                     ) {
                        @Override
                        public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
                           onIconClicked.accept(new SkillAltarData.SkillIcon(talent.getId(), true));
                           return true;
                        }
                     }
                  );
                  i++;
               }
            }
         }
      }

      @Override
      public boolean onMouseScrolled(double mouseX, double mouseY, double delta) {
         if (this.verticalScrollBarElement.isMouseOver(mouseX, mouseY)) {
            return false;
         } else {
            this.verticalScrollBarElement.setValue((float)Mth.clamp(this.verticalScrollBarElement.getValue() - delta * 0.5, 0.0, 1.0));
            this.onScrollbarValueChanged(this.verticalScrollBarElement.getValue());
            return true;
         }
      }

      @Override
      public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
         if (this.verticalScrollBarElement.isMouseOver(mouseX, mouseY)) {
            return false;
         } else {
            this.mouseDragY = mouseY;
            this.dragging = true;
            super.onMouseClicked(mouseX, mouseY, buttonIndex);
            return true;
         }
      }

      @Override
      public void onMouseMoved(double mouseX, double mouseY) {
         if (this.dragging) {
            int diffHeight = this.innerContainerElement.height() - this.clipContainerElement.height();
            double change = (this.mouseDragY - mouseY) / diffHeight;
            this.mouseDragY = mouseY;
            this.verticalScrollBarElement.setValue((float)Mth.clamp(change + this.verticalScrollBarElement.getValue(), 0.0, 1.0));
            this.onScrollbarValueChanged(this.verticalScrollBarElement.getValue());
         }

         super.onMouseMoved(mouseX, mouseY);
      }

      @Override
      public boolean onMouseReleased(double mouseX, double mouseY, int buttonIndex) {
         this.dragging = false;
         return super.onMouseReleased(mouseX, mouseY, buttonIndex);
      }

      public void stopDragging() {
         this.dragging = false;
      }
   }

   private static class TalentElement extends AbstractSpatialElement<SkillAltarScreen.TalentElement> implements IRenderedElement, IGuiEventElement {
      private static final int MAX_PIPS_IN_COLUMN = 3;
      private static final TextureAtlasRegion PIP = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/talent_pip"));
      private final Map<NodeState, TextureAtlasRegion> background;
      private boolean visible = true;
      private final TextureAtlasRegion icon;
      private final int tier;

      public TalentElement(ISpatial spatial, String skillId, int tier) {
         super(spatial);
         SkillStyle skillStyle = ModConfigs.TALENTS_GUI.getStyles().get(skillId);
         this.icon = TextureAtlasRegion.of(ModTextureAtlases.SKILLS, skillStyle.icon);
         this.background = AbilityNodeTextures.SECONDARY_NODE;
         this.tier = tier;
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         this.renderIcon(renderer, poseStack);
      }

      public void renderIcon(IElementRenderer renderer, PoseStack poseStack) {
         this.background.get(NodeState.DEFAULT).blit(poseStack, this.worldSpatial.x() + 24, this.worldSpatial.y());
         this.icon.blit(poseStack, this.worldSpatial.x() + 24 + 3, this.worldSpatial.y() + 3);
         this.renderPips(renderer, poseStack);
      }

      public void renderPips(IElementRenderer renderer, PoseStack poseStack) {
         int columnCount = (int)Math.ceil(this.tier / 3.0);
         int remainingPips = this.tier;

         for (int column = 0; column < columnCount && remainingPips > 0; column++) {
            for (int row = 0; row < 3 && remainingPips > 0; row++) {
               renderer.render(PIP, poseStack, Spatials.positionXY(18 - column * 6, 14 - row * 6).translateXY(this.worldSpatial), Spatials.size(6, 6));
               remainingPips--;
            }
         }
      }

      @Override
      public void setVisible(boolean visible) {
         this.visible = visible;
      }

      @Override
      public boolean isVisible() {
         return this.visible;
      }
   }
}
