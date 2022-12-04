package iskallia.vault.dynamodel.model.item;

import iskallia.vault.VaultMod;
import iskallia.vault.util.calc.BlockChanceHelper;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoconutShieldModel extends ShieldModel {
   protected ResourceLocation coconutOrangeModelId;
   protected ResourceLocation coconutOrangeBlockingModelId;

   public CoconutShieldModel(ResourceLocation id, ResourceLocation coconutOrangeId, String displayName) {
      super(id, displayName);
      this.coconutOrangeModelId = coconutOrangeId;
      this.coconutOrangeBlockingModelId = appendToId(coconutOrangeId, "_blocking");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Set<ModelResourceLocation> getAssociatedModelLocations() {
      Set<ModelResourceLocation> associatedModelLocations = super.getAssociatedModelLocations();
      associatedModelLocations.add(new ModelResourceLocation(this.coconutOrangeModelId, "inventory"));
      associatedModelLocations.add(new ModelResourceLocation(this.coconutOrangeBlockingModelId, "inventory"));
      return associatedModelLocations;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Map<String, ResourceLocation> resolveTextures(ResourceManager resourceManager, ResourceLocation resourceLocation) {
      Map<String, ResourceLocation> resolveTextures = super.resolveTextures(resourceManager, resourceLocation);
      resolveTextures.put("coconut_orange", VaultMod.id("item/gear/shield/coconut_orange"));
      resolveTextures.put("coconut_orange_leaf", VaultMod.id("item/gear/shield/coconut_orange_leaf"));
      return resolveTextures;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ResourceLocation resolveBakedIcon(@NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
      if (stack.getDisplayName().getString().toLowerCase().contains("coconutorange")) {
         return entity instanceof LocalPlayer player && BlockChanceHelper.isPlayerBlocking(player)
            ? this.coconutOrangeBlockingModelId
            : this.coconutOrangeModelId;
      } else {
         return super.resolveBakedIcon(stack, world, entity, seed);
      }
   }
}
