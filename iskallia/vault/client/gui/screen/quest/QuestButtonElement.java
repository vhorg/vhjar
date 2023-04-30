package iskallia.vault.client.gui.screen.quest;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.NineSliceButtonElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.quest.QuestState;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.quest.client.ClientQuestState;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class QuestButtonElement extends NineSliceButtonElement<QuestButtonElement> {
   private final Quest quest;
   private final TextureAtlasRegion icon;

   public QuestButtonElement(ISpatial spatial, Quest quest, Runnable onClick) {
      super(spatial, ScreenTextures.BUTTON_EMPTY_TEXTURES, onClick);
      this.quest = quest;
      this.icon = TextureAtlasRegion.of(ModTextureAtlases.QUESTS, quest.getIcon());
      this.label(() -> new TextComponent(this.quest.getName()), LabelTextStyle.defaultStyle().shadow());
   }

   public Quest getQuest() {
      return this.quest;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      renderer.render(this.icon, poseStack, this.getWorldSpatial().x() + 2, this.getWorldSpatial().y() + 2, 1);
      QuestState state = ClientQuestState.INSTANCE.getState();
      if (state.getCompleted().contains(this.quest.getId())) {
         renderer.render(
            TextureAtlasRegion.of(ModTextureAtlases.QUESTS, VaultMod.id("gui/quests/check")),
            poseStack,
            this.getWorldSpatial().right() - 18,
            this.getWorldSpatial().y() + 2,
            1
         );
      }
   }

   @Override
   protected void renderLabel(IElementRenderer renderer, PoseStack poseStack, int x, int y, int z, int width) {
      super.renderLabel(renderer, poseStack, x + 20, y, z, width);
   }
}
