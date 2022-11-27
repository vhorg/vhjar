package iskallia.vault.mixin;

import java.util.List;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.Stitcher.Holder;
import net.minecraft.client.renderer.texture.Stitcher.Region;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Stitcher.class})
public class MixinStitcher {
   @Shadow
   private int storageX;
   @Shadow
   private int storageY;
   @Shadow
   @Final
   private int maxWidth;
   @Shadow
   @Final
   private int maxHeight;
   @Shadow
   @Final
   private List<Region> storage;

   @Redirect(
      method = {"addToStorage"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/texture/Stitcher;expand(Lnet/minecraft/client/renderer/texture/Stitcher$Holder;)Z"
      )
   )
   public boolean expand(Stitcher stitcher, Holder pHolder) {
      int storageWidth = Mth.smallestEncompassingPowerOfTwo(this.storageX);
      int storageHeight = Mth.smallestEncompassingPowerOfTwo(this.storageY);
      int storageWidthExpanded = Mth.smallestEncompassingPowerOfTwo(this.storageX + pHolder.width);
      int storageHeightExpanded = Mth.smallestEncompassingPowerOfTwo(this.storageY + pHolder.height);
      boolean canExpandWidth = storageWidthExpanded <= this.maxWidth;
      boolean canExpandHeight = storageHeightExpanded <= this.maxHeight;
      if (!canExpandWidth && !canExpandHeight) {
         return false;
      } else {
         boolean needsWidthExpansion = canExpandWidth && storageWidth != storageWidthExpanded;
         boolean needsHeightExpansion = canExpandHeight && storageHeight != storageHeightExpanded;
         boolean expandHorizontal;
         if (needsWidthExpansion ^ needsHeightExpansion) {
            expandHorizontal = !needsWidthExpansion && canExpandWidth;
         } else {
            expandHorizontal = canExpandWidth && storageWidth <= storageHeight;
         }

         Region region;
         if (expandHorizontal) {
            this.storageY = Math.max(this.storageY, pHolder.height);
            region = new Region(this.storageX, 0, pHolder.width, this.storageY);
            this.storageX = this.storageX + pHolder.width;
         } else {
            this.storageX = Math.max(this.storageX, pHolder.width);
            region = new Region(0, this.storageY, this.storageX, pHolder.height);
            this.storageY = this.storageY + pHolder.height;
         }

         region.add(pHolder);
         this.storage.add(region);
         return true;
      }
   }
}
