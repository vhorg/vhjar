package iskallia.vault.util;

import iskallia.vault.VaultMod;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.data.entity.PartialEntityGroup;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber({Dist.CLIENT})
public class EntityGroupsUtils {
   public static final HashMap<EntityPredicate, Set<EntityType<?>>> GROUPED_TYPES = new HashMap<>();
   private static boolean isSetup = false;

   @SubscribeEvent
   public static void setup(ClientTickEvent event) {
      if (Minecraft.getInstance().level != null) {
         if (!isSetup) {
            loadDescriptions();
            isSetup = true;
         }
      }
   }

   private static void loadDescriptions() {
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

            GROUPED_TYPES.put(PartialEntityGroup.of(group, PartialCompoundNbt.empty()), types);
         }
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

   public static Set<String> getDescriptions(EntityPredicate filter) {
      if (filter instanceof PartialEntity partialEntity) {
         Optional<CompoundTag> whole = partialEntity.getNbt().asWhole();
         if (whole.isPresent()) {
            String id = whole.get().getString("id");
            EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
            return type == null ? Set.of() : Set.of(type.getDescription().getString());
         }
      }

      return GROUPED_TYPES.getOrDefault(filter, Set.of())
         .stream()
         .map(entityType -> entityType.getDescription().getString())
         .sorted()
         .collect(Collectors.toCollection(LinkedHashSet::new));
   }

   public static Component getName(EntityPredicate filter) {
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

   public static List<Component> getGroupNames() {
      return GROUPED_TYPES.keySet().stream().map(EntityGroupsUtils::getName).sorted(TextComponentUtils.componentComparator()).toList();
   }

   public static Optional<EntityPredicate> getByName(String groupName) {
      groupName = groupName.toLowerCase().replace(' ', '_');
      ResourceLocation groupId = VaultMod.id(groupName.toLowerCase());
      if (groupName.equalsIgnoreCase("dweller")) {
         groupId = VaultMod.id("fighter");
      }

      for (EntityPredicate predicate : GROUPED_TYPES.keySet()) {
         if (predicate instanceof PartialEntityGroup group && groupId.equals(group.getId())) {
            return Optional.of(predicate);
         }
      }

      return Optional.empty();
   }

   public static Optional<EntityPredicate> getByName(ResourceLocation groupId) {
      for (EntityPredicate predicate : GROUPED_TYPES.keySet()) {
         if (predicate instanceof PartialEntityGroup group && groupId.equals(group.getId())) {
            return Optional.of(predicate);
         }
      }

      return Optional.empty();
   }

   public static List<EntityType<?>> getTypes(ResourceLocation groupId) {
      Set<EntityType<?>> types = GROUPED_TYPES.getOrDefault(PartialEntityGroup.of(groupId, PartialCompoundNbt.empty()), Set.of());
      Comparator<EntityType<?>> entityTypeComparator = (o1, o2) -> {
         Comparator<String> stringComparator = Comparator.naturalOrder();
         return stringComparator.compare(o1.getDescription().getString(), o2.getDescription().getString());
      };
      return types.stream().sorted(entityTypeComparator).toList();
   }
}
