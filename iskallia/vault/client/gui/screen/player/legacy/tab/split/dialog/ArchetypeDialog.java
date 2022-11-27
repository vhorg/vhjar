package iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.component.ScrollableContainer;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.ArchetypesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractDialog;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ServerboundSelectArchetypeMessage;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.AbstractArchetypeConfig;
import iskallia.vault.skill.archetype.ArchetypeContainer;
import iskallia.vault.skill.archetype.ArchetypeRegistry;
import iskallia.vault.world.data.ServerVaults;
import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ArchetypeDialog extends AbstractDialog<ArchetypesElementContainerScreen> {
   private final ArchetypeContainer archetypeContainer;
   private ResourceLocation selectedArchetypeId = null;
   private Component archetypeNameComponent = null;

   public ArchetypeDialog(ArchetypeContainer archetypeContainer, ArchetypesElementContainerScreen screen) {
      super(screen);
      this.archetypeContainer = archetypeContainer;
   }

   @Override
   public void update() {
      if (this.selectedArchetypeId != null) {
         AbstractArchetype<?> archetype = ArchetypeRegistry.getArchetype(this.selectedArchetypeId);
         this.archetypeNameComponent = new TranslatableComponent(archetype.getName());
         AbstractArchetypeConfig config = archetype.getConfig();
         int learningCost = config.getLearningCost();
         boolean isEquipped = this.archetypeContainer.getCurrentArchetype() == archetype;
         this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
         this.learnButton = new Button(
            10,
            this.bounds.height - 40,
            this.bounds.width - 30,
            20,
            new TextComponent(isEquipped ? "Selected" : "Select (" + learningCost + ")"),
            button -> this.selectArchetype(),
            Button.NO_TOOLTIP
         );
         this.learnButton.active = !isEquipped
            && learningCost <= VaultBarOverlay.unspentArchetypePoints
            && VaultBarOverlay.vaultLevel >= config.getLevelRequirement()
            && !ServerVaults.isInVault(Minecraft.getInstance().player);
      }
   }

   public void setWidget(ResourceLocation archetypeId) {
      this.selectedArchetypeId = archetypeId;
      this.update();
   }

   private void selectArchetype() {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         player.playSound(ModSounds.SKILL_TREE_UPGRADE_SFX, 1.0F, 1.0F);
      }

      this.archetypeContainer.setCurrentArchetype(null, this.selectedArchetypeId);
      this.update();
      ModNetwork.CHANNEL.sendToServer(new ServerboundSelectArchetypeMessage(this.selectedArchetypeId));
   }

   @Override
   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.selectedArchetypeId != null && this.archetypeNameComponent != null) {
         matrixStack.pushPose();
         matrixStack.translate(this.bounds.x + 5, this.bounds.y + 5, 0.0);
         this.renderHeader(matrixStack, mouseX, mouseY, partialTicks);
         this.descriptionComponent.setBounds(this.getDescriptionsBounds());
         this.descriptionComponent.render(matrixStack, mouseX, mouseY, partialTicks);
         this.renderFooter(matrixStack, mouseX, mouseY, partialTicks);
         matrixStack.popPose();
      }
   }

   private void renderHeader(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, ScreenTextures.UI_RESOURCE);
      AbstractArchetype<?> archetype = ArchetypeRegistry.getArchetype(this.selectedArchetypeId);
      boolean isEquipped = this.archetypeContainer.getCurrentArchetype() == archetype;
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      matrixStack.pushPose();
      matrixStack.translate(10.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(matrixStack, this.archetypeNameComponent, 0.0F, 13.0F, !isEquipped ? -1 : -1849, !isEquipped ? -16777216 : -12897536);
      FontHelper.drawStringWithBorder(
         matrixStack, isEquipped ? "Selected" : "Not Selected", 0.0F, 23.0F, !isEquipped ? -1 : -1849, !isEquipped ? -16777216 : -12897536
      );
      matrixStack.popPose();
   }

   private void renderDescriptions(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle bounds = this.descriptionComponent.getRenderableBounds();
      TextComponent text = new TextComponent("");
      text.append(ModConfigs.ARCHETYPE_DESCRIPTIONS.getDescriptionFor(this.selectedArchetypeId));
      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, text, bounds.width, 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private void renderFooter(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int containerX = mouseX - this.bounds.x - 5;
      int containerY = mouseY - this.bounds.y - 5;
      this.learnButton.render(matrixStack, containerX, containerY, partialTicks);
   }
}
