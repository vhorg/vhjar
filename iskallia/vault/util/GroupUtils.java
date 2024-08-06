package iskallia.vault.util;

import iskallia.vault.VaultMod;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.data.entity.PartialEntityGroup;
import iskallia.vault.core.world.data.tile.PartialBlockGroup;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber({Dist.CLIENT})
public class GroupUtils {
   public static final HashMap<EntityPredicate, Set<EntityType<?>>> ENTITY_GROUPS = new HashMap<>();
   public static final HashMap<ResourceLocation, Set<ResourceLocation>> BLOCK_GROUPS = new HashMap<>();
   private static boolean isSetup = false;

   @SubscribeEvent
   public static void setup(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         if (Minecraft.getInstance().level != null) {
            if (!isSetup) {
               loadBlockGroups();
               loadEntityGroups();
               isSetup = true;
            }
         }
      }
   }

   private static void loadEntityGroups() {
      VaultMod.LOGGER.info("Loading Entity Groups Started");
      long start = System.currentTimeMillis();
      Minecraft mc = Minecraft.getInstance();
      ClientLevel level = mc.level;
      if (level != null) {
         List<Entity> entities = createAllEntities(level);
         Map<ResourceLocation, Set<EntityPredicate>> groups = ModConfigs.ENTITY_GROUPS.getGroups();

         for (ResourceLocation group : groups.keySet()) {
            Set<EntityType<?>> types = new HashSet<>();

            for (Entity entity : entities) {
               if (ModConfigs.ENTITY_GROUPS.isInGroup(group, entity)) {
                  types.add(entity.getType());
               }
            }

            ENTITY_GROUPS.put(PartialEntityGroup.of(group, PartialCompoundNbt.empty()), types);
         }

         VaultMod.LOGGER.info("Loading Entity Groups Completed in {}ms", System.currentTimeMillis() - start);
      }
   }

   private static void loadBlockGroups() {
      VaultMod.LOGGER.info("Loading Block Groups Started");
      long start = System.currentTimeMillis();
      Map<ResourceLocation, Set<TilePredicate>> groups = ModConfigs.TILE_GROUPS.getGroups();
      Collection<Block> allBlocks = ForgeRegistries.BLOCKS.getValues();

      for (ResourceLocation group : groups.keySet()) {
         Set<ResourceLocation> blockIds = new HashSet<>();

         for (Block block : allBlocks) {
            if (ModConfigs.TILE_GROUPS.isInGroup(group, PartialTile.of(PartialBlockState.of(block), PartialCompoundNbt.empty()))) {
               ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
               if (key != null) {
                  blockIds.add(key);
               }
            }
         }

         BLOCK_GROUPS.put(group, blockIds);
      }

      VaultMod.LOGGER.info("Loading Block Groups Completed in {}ms", System.currentTimeMillis() - start);
   }

   public static Set<ResourceLocation> getBlockIdsFor(TilePredicate predicate) {
      if (predicate instanceof PartialBlockGroup tilePredicate) {
         ResourceLocation id = tilePredicate.getId();
         return BLOCK_GROUPS.getOrDefault(id, new HashSet<>());
      } else {
         return new HashSet<>();
      }
   }

   private static List<Entity> createAllEntities(ClientLevel level) {
      List<Entity> entities = new ArrayList<>();

      for (EntityType<?> entityType : ForgeRegistries.ENTITIES.getValues()) {
         if (entityType.create(level) instanceof LivingEntity living) {
            entities.add(living);
         }
      }

      return entities;
   }

   public static Set<String> getEntityNamesAsString(EntityPredicate filter) {
      if (filter instanceof PartialEntity partialEntity) {
         Optional<CompoundTag> whole = partialEntity.getNbt().asWhole();
         if (whole.isPresent()) {
            String id = whole.get().getString("id");
            EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
            return type == null ? Set.of() : Set.of(type.getDescription().getString());
         }
      }

      return ENTITY_GROUPS.getOrDefault(filter, Set.of())
         .stream()
         .map(entityType -> entityType.getDescription().getString())
         .sorted()
         .collect(Collectors.toCollection(LinkedHashSet::new));
   }

   public static Component getEntityName(EntityPredicate filter) {
      if (filter instanceof PartialEntityGroup group) {
         return group.getId().getPath().equalsIgnoreCase("fighter") ? new TextComponent("Dweller") : TextUtil.formatLocationPathAsProperNoun(group.getId());
      } else {
         if (filter instanceof PartialEntity entity) {
            Optional<CompoundTag> whole = entity.getNbt().asWhole();
            if (whole.isPresent()) {
               String id = whole.get().getString("id");
               EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
               return type == null ? Component.nullToEmpty("Entities") : type.getDescription();
            }
         }

         return Component.nullToEmpty("");
      }
   }

   public static List<String> getEntityGroupNames() {
      return ENTITY_GROUPS.keySet().stream().map(GroupUtils::getEntityName).<String>map(Component::getString).sorted().toList();
   }

   public static Optional<EntityPredicate> getFilterByName(String groupName) {
      groupName = groupName.toLowerCase().replace(' ', '_');
      ResourceLocation groupId = VaultMod.id(groupName.toLowerCase());
      if (groupName.equalsIgnoreCase("dweller")) {
         groupId = VaultMod.id("fighter");
      }

      for (EntityPredicate predicate : ENTITY_GROUPS.keySet()) {
         if (predicate instanceof PartialEntityGroup group && groupId.equals(group.getId())) {
            return Optional.of(predicate);
         }
      }

      return Optional.empty();
   }

   public static Optional<EntityPredicate> getFilterById(ResourceLocation groupId) {
      for (EntityPredicate predicate : ENTITY_GROUPS.keySet()) {
         if (predicate instanceof PartialEntityGroup group && groupId.equals(group.getId())) {
            return Optional.of(predicate);
         }
      }

      return Optional.empty();
   }

   public static List<EntityType<?>> getEntityTypes(ResourceLocation groupId) {
      Set<EntityType<?>> types = ENTITY_GROUPS.getOrDefault(PartialEntityGroup.of(groupId, PartialCompoundNbt.empty()), Set.of());
      Comparator<EntityType<?>> entityTypeComparator = (o1, o2) -> {
         Comparator<String> stringComparator = Comparator.naturalOrder();
         return stringComparator.compare(o1.getDescription().getString(), o2.getDescription().getString());
      };
      return types.stream().sorted(entityTypeComparator).toList();
   }
}
