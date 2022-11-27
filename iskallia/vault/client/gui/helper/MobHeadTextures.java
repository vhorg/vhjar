package iskallia.vault.client.gui.helper;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModEntities;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class MobHeadTextures {
   private static final Map<String, ResourceLocation> REGISTRY = new HashMap<>();

   private static void register(EntityType<?> entityType) {
      ResourceLocation registryName = entityType.getRegistryName();
      if (registryName == null) {
         throw new InternalError();
      } else {
         register(registryName);
      }
   }

   private static void register(ResourceLocation id) {
      String namespace = id.getNamespace();
      String path = id.getPath();
      REGISTRY.put(id.toString(), VaultMod.id("textures/gui/mob_heads/" + namespace + "/" + path + ".png"));
   }

   public static Optional<ResourceLocation> get(@Nullable ResourceLocation mobId) {
      return mobId == null ? Optional.empty() : get(mobId.toString());
   }

   public static Optional<ResourceLocation> get(String mobId) {
      return Optional.ofNullable(REGISTRY.get(mobId));
   }

   static {
      register(EntityType.CAVE_SPIDER);
      register(EntityType.CREEPER);
      register(EntityType.DROWNED);
      register(EntityType.HUSK);
      register(EntityType.PIGLIN);
      register(EntityType.RAVAGER);
      register(EntityType.SILVERFISH);
      register(EntityType.SKELETON);
      register(EntityType.SPIDER);
      register(EntityType.STRAY);
      register(EntityType.VEX);
      register(EntityType.VINDICATOR);
      register(EntityType.WITCH);
      register(EntityType.WITHER_SKELETON);
      register(EntityType.ZOMBIE);
      register(ModEntities.BOOGIEMAN);
      register(ModEntities.BLUE_BLAZE);
      register(ModEntities.TREASURE_GOBLIN);
      register(ModEntities.ROBOT);
   }
}
