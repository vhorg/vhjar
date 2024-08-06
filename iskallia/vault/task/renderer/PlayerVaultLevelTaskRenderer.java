package iskallia.vault.task.renderer;

import iskallia.vault.VaultMod;
import iskallia.vault.task.PlayerVaultLevelTask;
import iskallia.vault.task.renderer.context.AchievementRendererContext;
import iskallia.vault.task.util.ITaskModifier;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerVaultLevelTaskRenderer {
   public static class Achievement extends TaskRenderer<PlayerVaultLevelTask, AchievementRendererContext> implements ITaskModifier {
      private int minimumLevel;

      @OnlyIn(Dist.CLIENT)
      public void onRender(PlayerVaultLevelTask task, AchievementRendererContext context) {
         this.minimumLevel = task.getMinimumLevel();
         context.addModifier(this);
         super.onRender(task, context);
      }

      @Override
      public ResourceLocation getRenderIcon() {
         return VaultMod.id("textures/gui/screen/tab_icon_history.png");
      }

      @Override
      public List<Component> getTooltips() {
         return List.of(new TextComponent("Minimum Level: " + this.minimumLevel));
      }
   }
}
