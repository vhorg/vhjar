package iskallia.vault.dynamodel.model.item.shield;

import iskallia.vault.VaultMod;
import iskallia.vault.util.calc.BlockChanceHelper;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
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

public class FlowahShieldModel extends ShieldModel {
   protected ResourceLocation eggeModelId;
   protected ResourceLocation eggBlockingModelId;

   public FlowahShieldModel(ResourceLocation id, ResourceLocation eggModelId, String displayName) {
      super(id, displayName);
      this.eggeModelId = eggModelId;
      this.eggBlockingModelId = appendToId(eggModelId, "_blocking");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Set<ModelResourceLocation> getAssociatedModelLocations() {
      Set<ModelResourceLocation> associatedModelLocations = super.getAssociatedModelLocations();
      associatedModelLocations.add(new ModelResourceLocation(this.eggeModelId, "inventory"));
      associatedModelLocations.add(new ModelResourceLocation(this.eggBlockingModelId, "inventory"));
      return associatedModelLocations;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Map<String, ResourceLocation> resolveTextures(ResourceManager resourceManager, ResourceLocation resourceLocation) {
      Map<String, ResourceLocation> resolveTextures = super.resolveTextures(resourceManager, resourceLocation);
      resolveTextures.put("egg", VaultMod.id("item/gear/shield/fried_egg"));
      return resolveTextures;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ResourceLocation resolveBakedIcon(@NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
      String[] words = stack.getDisplayName().getString().toLowerCase().split(" +");
      if (Stream.of(words).anyMatch(word -> word.contains("egg"))) {
         return entity instanceof LocalPlayer player && BlockChanceHelper.isPlayerBlocking(player) ? this.eggBlockingModelId : this.eggeModelId;
      } else {
         return super.resolveBakedIcon(stack, world, entity, seed);
      }
   }
}
