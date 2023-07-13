package iskallia.vault.entity.renderer.dungeon;

import com.google.common.collect.ImmutableMap;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModEntities;
import java.util.Map;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class DungeonPiglinRenderer extends PiglinRenderer {
   private static final Map<EntityType<?>, ResourceLocation> TEXTURE_LOCATION = ImmutableMap.of(
      ModEntities.DUNGEON_PIGLIN,
      VaultMod.id("textures/entity/dungeon/piglin.png"),
      ModEntities.DUNGEON_PIGLIN_BRUTE,
      VaultMod.id("textures/entity/dungeon/piglin_brute.png")
   );

   public DungeonPiglinRenderer(Context p_174344_, ModelLayerLocation p_174345_, ModelLayerLocation p_174346_, ModelLayerLocation p_174347_, boolean p_174348_) {
      super(p_174344_, p_174345_, p_174346_, p_174347_, p_174348_);
   }

   public ResourceLocation getTextureLocation(Mob pEntity) {
      ResourceLocation resourcelocation = TEXTURE_LOCATION.get(pEntity.getType());
      if (resourcelocation == null) {
         throw new IllegalArgumentException("I don't know what texture to use for " + pEntity.getType());
      } else {
         return resourcelocation;
      }
   }
}
