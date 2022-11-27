package iskallia.vault.dynamodel.model.item;

import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.util.calc.BlockChanceHelper;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShieldModel extends DynamicModel<ShieldModel> {
   protected ResourceLocation blockingModelId = appendToId(this.getId(), "_blocking");

   public ShieldModel(ResourceLocation id, String displayName) {
      super(id, displayName);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Set<ModelResourceLocation> getAssociatedModelLocations() {
      HashSet<ModelResourceLocation> locations = new HashSet<>(super.getAssociatedModelLocations());
      locations.add(new ModelResourceLocation(this.blockingModelId, "inventory"));
      return locations;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ResourceLocation resolveBakedIcon(@NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
      return entity instanceof LocalPlayer player && BlockChanceHelper.isPlayerBlocking(player)
         ? this.blockingModelId
         : super.resolveBakedIcon(stack, world, entity, seed);
   }
}
