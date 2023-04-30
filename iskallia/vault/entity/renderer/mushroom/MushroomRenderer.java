package iskallia.vault.entity.renderer.mushroom;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.mushroom.MushroomEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public abstract class MushroomRenderer<M extends MushroomEntity> extends MobRenderer<M, EntityModel<M>> {
   private static final Map<String, ResourceLocation> LOCATION_BY_PATH = new HashMap<>();
   private static final Map<Integer, String> TYPE_BY_VARIANT = (Map<Integer, String>)Util.make(new HashMap(), textures -> {
      textures.put(0, "purple");
      textures.put(1, "red");
   });

   public MushroomRenderer(Context ctx, Supplier<EntityModel<M>> modelSupplier) {
      super(ctx, modelSupplier.get(), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(M entity) {
      String type = TYPE_BY_VARIANT.getOrDefault(entity.getTypeVariant(), TYPE_BY_VARIANT.get(0));
      String path = "textures/entity/mushroom/t%d/%s.png".formatted(entity.getTier(), type);
      return LOCATION_BY_PATH.computeIfAbsent(path, VaultMod::id);
   }
}
