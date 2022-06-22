package iskallia.vault.client.gui.helper;

import iskallia.vault.Vault;
import iskallia.vault.init.ModEntities;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

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
      String namespace = id.func_110624_b();
      String path = id.func_110623_a();
      REGISTRY.put(id.toString(), Vault.id("textures/gui/mob_heads/" + namespace + "/" + path + ".png"));
   }

   public static Optional<ResourceLocation> get(@Nullable ResourceLocation mobId) {
      return mobId == null ? Optional.empty() : get(mobId.toString());
   }

   public static Optional<ResourceLocation> get(String mobId) {
      return Optional.ofNullable(REGISTRY.get(mobId));
   }

   static {
      register(EntityType.field_200794_h);
      register(EntityType.field_200797_k);
      register(EntityType.field_204724_o);
      register(EntityType.field_200763_C);
      register(EntityType.field_233591_ai_);
      register(EntityType.field_220352_aU);
      register(EntityType.field_200740_af);
      register(EntityType.field_200741_ag);
      register(EntityType.field_200748_an);
      register(EntityType.field_200750_ap);
      register(EntityType.field_200755_au);
      register(EntityType.field_200758_ax);
      register(EntityType.field_200759_ay);
      register(EntityType.field_200722_aA);
      register(EntityType.field_200725_aD);
      register(ModEntities.BOOGIEMAN);
      register(ModEntities.BLUE_BLAZE);
      register(ModEntities.TREASURE_GOBLIN);
      register(ModEntities.ROBOT);
   }
}
