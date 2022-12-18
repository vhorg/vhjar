package iskallia.vault.config.bounty;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.init.ModEntities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class BountyEntitiesConfig extends Config {
   @Expose
   private HashMap<ResourceLocation, List<ResourceLocation>> entities = new HashMap<>();

   @Override
   public String getName() {
      return "bounty/entities";
   }

   @Override
   protected void reset() {
      List<ResourceLocation> skeletons = List.of(
         ForgeRegistries.ENTITIES.getKey(ModEntities.ELITE_SKELETON),
         ForgeRegistries.ENTITIES.getKey(ModEntities.ELITE_WITHER_SKELETON),
         ForgeRegistries.ENTITIES.getKey(ModEntities.T1_SKELETON),
         ForgeRegistries.ENTITIES.getKey(ModEntities.T1_WITHER_SKELETON)
      );
      List<ResourceLocation> strays = List.of(ForgeRegistries.ENTITIES.getKey(ModEntities.ELITE_STRAY), ForgeRegistries.ENTITIES.getKey(ModEntities.T1_STRAY));
      List<ResourceLocation> zombies = List.of(
         ForgeRegistries.ENTITIES.getKey(ModEntities.ELITE_ZOMBIE), ForgeRegistries.ENTITIES.getKey(ModEntities.T1_ZOMBIE)
      );
      List<ResourceLocation> husks = List.of(ForgeRegistries.ENTITIES.getKey(ModEntities.ELITE_HUSK), ForgeRegistries.ENTITIES.getKey(ModEntities.T1_HUSK));
      List<ResourceLocation> drowned = List.of(
         ForgeRegistries.ENTITIES.getKey(ModEntities.ELITE_DROWNED), ForgeRegistries.ENTITIES.getKey(ModEntities.T1_DROWNED)
      );
      List<ResourceLocation> vaultFighters = new ArrayList<>(
         ModEntities.VAULT_FIGHTER_TYPES.stream().map(ForgeRegistries.ENTITIES::getKey).filter(Objects::nonNull).toList()
      );
      List<ResourceLocation> spiders = List.of(
         ForgeRegistries.ENTITIES.getKey(ModEntities.ELITE_SPIDER), ForgeRegistries.ENTITIES.getKey(ModEntities.VAULT_SPIDER_BABY)
      );
      List<ResourceLocation> piglin = List.of(ForgeRegistries.ENTITIES.getKey(ModEntities.T1_PIGLIN));
      this.entities.put(ForgeRegistries.ENTITIES.getKey(EntityType.SKELETON), skeletons);
      this.entities.put(ForgeRegistries.ENTITIES.getKey(EntityType.STRAY), strays);
      this.entities.put(ForgeRegistries.ENTITIES.getKey(EntityType.ZOMBIE), zombies);
      this.entities.put(ForgeRegistries.ENTITIES.getKey(EntityType.HUSK), husks);
      this.entities.put(ForgeRegistries.ENTITIES.getKey(EntityType.DROWNED), drowned);
      this.entities.put(VaultMod.id("vault_fighter"), vaultFighters);
      this.entities.put(ForgeRegistries.ENTITIES.getKey(ModEntities.VAULT_SPIDER), spiders);
      this.entities.put(ForgeRegistries.ENTITIES.getKey(EntityType.PIGLIN), piglin);
   }

   public List<ResourceLocation> getValidEntities(ResourceLocation id) {
      return this.entities.computeIfAbsent(id, location -> new ArrayList<>());
   }
}
