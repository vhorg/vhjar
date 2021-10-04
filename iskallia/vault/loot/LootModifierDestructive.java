package iskallia.vault.loot;

import com.google.gson.JsonObject;
import iskallia.vault.item.paxel.enhancement.PaxelEnhancements;
import iskallia.vault.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

public class LootModifierDestructive extends LootModifier {
   private LootModifierDestructive(ILootCondition[] conditionsIn) {
      super(conditionsIn);
   }

   @Nonnull
   protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
      if (!LootUtils.doesContextFulfillSet(context, LootParameterSets.field_216267_h)) {
         return generatedLoot;
      } else {
         ItemStack tool = (ItemStack)context.func_216031_c(LootParameters.field_216289_i);
         return (List<ItemStack>)(PaxelEnhancements.getEnhancement(tool) != PaxelEnhancements.DESTRUCTIVE ? generatedLoot : new ArrayList<>());
      }
   }

   public static class Serializer extends GlobalLootModifierSerializer<LootModifierDestructive> {
      public LootModifierDestructive read(ResourceLocation location, JsonObject object, ILootCondition[] lootConditions) {
         return new LootModifierDestructive(lootConditions);
      }

      public JsonObject write(LootModifierDestructive instance) {
         return this.makeConditions(instance.conditions);
      }
   }
}
