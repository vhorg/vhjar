package iskallia.vault.item.tool;

import java.util.function.Consumer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IManualModelLoading {
   @OnlyIn(Dist.CLIENT)
   void loadModels(Consumer<ModelResourceLocation> var1);
}
