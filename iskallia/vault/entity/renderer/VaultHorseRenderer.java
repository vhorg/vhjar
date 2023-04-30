package iskallia.vault.entity.renderer;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultHorseEntity;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class VaultHorseRenderer extends AbstractHorseRenderer<VaultHorseEntity, HorseModel<VaultHorseEntity>> {
   private static final Map<Integer, ResourceLocation> LOCATION_BY_VARIANT = (Map<Integer, ResourceLocation>)Util.make(new HashMap(), textures -> {
      textures.put(0, VaultMod.id("textures/entity/vault_horse/diorite.png"));
      textures.put(1, VaultMod.id("textures/entity/vault_horse/glorple.png"));
   });

   public VaultHorseRenderer(Context p_174167_) {
      super(p_174167_, new HorseModel(p_174167_.bakeLayer(ModelLayers.HORSE)), 1.1F);
   }

   public ResourceLocation getTextureLocation(VaultHorseEntity pEntity) {
      return LOCATION_BY_VARIANT.getOrDefault(pEntity.getTypeVariant(), LOCATION_BY_VARIANT.get(0));
   }
}
