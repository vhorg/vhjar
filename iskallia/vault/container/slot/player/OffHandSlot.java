package iskallia.vault.container.slot.player;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.container.slot.ReadOnlySlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OffHandSlot extends ReadOnlySlot {
   public OffHandSlot(PlayerEntity player, int xPosition, int yPosition) {
      super(player.field_71071_by, 40, xPosition, yPosition);
   }

   @OnlyIn(Dist.CLIENT)
   public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
      return Pair.of(PlayerContainer.field_226615_c_, PlayerContainer.field_226620_h_);
   }
}
