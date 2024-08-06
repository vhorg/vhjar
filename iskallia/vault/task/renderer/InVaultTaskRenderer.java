package iskallia.vault.task.renderer;

import iskallia.vault.VaultMod;
import iskallia.vault.task.InVaultTask;
import iskallia.vault.task.renderer.context.AchievementRendererContext;
import iskallia.vault.task.util.ITaskModifier;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class InVaultTaskRenderer {
   public static class Achievement extends TaskRenderer<InVaultTask, AchievementRendererContext> implements ITaskModifier {
      @OnlyIn(Dist.CLIENT)
      public void onRender(InVaultTask task, AchievementRendererContext context) {
         context.addModifier(this);
         super.onRender(task, context);
      }

      @Override
      public ResourceLocation getRenderIcon() {
         return VaultMod.id("textures/gui/screen/tab_icon_portal_vault.png");
      }

      @Override
      public List<Component> getTooltips() {
         return List.of(new TextComponent("Vault Only"));
      }
   }
}
