package iskallia.vault.init;

import iskallia.vault.loot.conditions.HasBlockEntityTagCondition;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModLootConditions {
   public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(Registry.LOOT_CONDITION_TYPE.key(), "the_vault");
   public static final RegistryObject<LootItemConditionType> HAS_BLOCK_ENTITY_TAG = LOOT_CONDITION_TYPES.register(
      "has_block_entity_tag", () -> new LootItemConditionType(new HasBlockEntityTagCondition.ConditionSerializer())
   );

   public static void register(IEventBus eventBus) {
      LOOT_CONDITION_TYPES.register(eventBus);
   }
}
