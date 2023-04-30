package iskallia.vault.client.gui.screen.player;

import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.screen.player.legacy.LegacySkillTreeElementContainerScreen;
import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.skill.tree.SkillTree;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class SkillsElementContainerScreen<T extends SkillTree> extends LegacySkillTreeElementContainerScreen<T> {
   public SkillsElementContainerScreen(NBTElementContainer<T> container, Inventory inventory, Component title, IElementRenderer elementRenderer) {
      super(container, inventory, title, elementRenderer);
   }

   public T getSkillTree() {
      return (T)((NBTElementContainer)this.menu).getData();
   }
}
