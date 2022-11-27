package iskallia.vault.entity.renderer;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultGummySoldier;
import iskallia.vault.entity.model.VaultGummySoldierModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class VaultGummySoldierRenderer extends HumanoidMobRenderer<VaultGummySoldier, VaultGummySoldierModel<VaultGummySoldier>> {
   private static final ResourceLocation TEXTURE_LOCATION_BLUE = VaultMod.id("textures/entity/bluegummibear.png");
   private static final ResourceLocation TEXTURE_LOCATION_GREEN = VaultMod.id("textures/entity/greengummibear.png");
   private static final ResourceLocation TEXTURE_LOCATION_RED = VaultMod.id("textures/entity/redgummibear.png");
   private static final ResourceLocation TEXTURE_LOCATION_YELLOW = VaultMod.id("textures/entity/yellowgummibear.png");
   public VaultGummySoldierRenderer.Color color;

   public VaultGummySoldierRenderer(Context context, VaultGummySoldierRenderer.Color color) {
      super(context, new VaultGummySoldierModel(context.bakeLayer(ModelLayers.HUSK)), 0.5F);
      this.color = color;
   }

   @Nullable
   protected RenderType getRenderType(VaultGummySoldier p_115322_, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
      return RenderType.itemEntityTranslucentCull(this.getTextureLocation(p_115322_));
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull VaultGummySoldier entity) {
      return switch (this.color) {
         case BLUE -> TEXTURE_LOCATION_BLUE;
         case RED -> TEXTURE_LOCATION_RED;
         case GREEN -> TEXTURE_LOCATION_GREEN;
         case YELLOW -> TEXTURE_LOCATION_YELLOW;
      };
   }

   public static enum Color {
      BLUE,
      RED,
      GREEN,
      YELLOW;
   }
}
