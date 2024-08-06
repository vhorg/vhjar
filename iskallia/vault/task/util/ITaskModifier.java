package iskallia.vault.task.util;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ITaskModifier {
   @OnlyIn(Dist.CLIENT)
   ResourceLocation getRenderIcon();

   @OnlyIn(Dist.CLIENT)
   List<Component> getTooltips();
}
