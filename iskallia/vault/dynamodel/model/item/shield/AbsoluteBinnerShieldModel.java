package iskallia.vault.dynamodel.model.item.shield;

import iskallia.vault.VaultMod;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AbsoluteBinnerShieldModel extends ShieldModel {
   public AbsoluteBinnerShieldModel(ResourceLocation id, String displayName) {
      super(id, displayName);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Map<String, ResourceLocation> resolveTextures(ResourceManager resourceManager, ResourceLocation resourceLocation) {
      Map<String, ResourceLocation> resolveTextures = super.resolveTextures(resourceManager, resourceLocation);
      resolveTextures.put("texture1", VaultMod.id("item/gear/shield/absolutebinner/texture1"));
      resolveTextures.put("texture2", VaultMod.id("item/gear/shield/absolutebinner/texture2"));
      resolveTextures.put("texture3", VaultMod.id("item/gear/shield/absolutebinner/texture3"));
      resolveTextures.put("texture4", VaultMod.id("item/gear/shield/absolutebinner/texture4"));
      resolveTextures.put("douwskydood", VaultMod.id("item/gear/shield/absolutebinner/douwskydood"));
      return resolveTextures;
   }
}
