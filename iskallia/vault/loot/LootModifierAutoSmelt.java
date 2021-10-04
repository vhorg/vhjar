package iskallia.vault.loot;

import com.google.gson.JsonObject;
import iskallia.vault.item.paxel.enhancement.PaxelEnhancements;
import iskallia.vault.util.LootUtils;
import iskallia.vault.util.RecipeUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.hooks.BasicEventHooks;

public class LootModifierAutoSmelt extends LootModifier {
   private LootModifierAutoSmelt(ILootCondition[] conditionsIn) {
      super(conditionsIn);
   }

   @Nonnull
   protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
      if (LootUtils.doesContextFulfillSet(context, LootParameterSets.field_216267_h) && context.func_216033_a(LootParameters.field_216281_a)) {
         Entity e = (Entity)context.func_216031_c(LootParameters.field_216281_a);
         if (!(e instanceof ServerPlayerEntity)) {
            return generatedLoot;
         } else {
            ServerPlayerEntity player = (ServerPlayerEntity)e;
            ItemStack tool = (ItemStack)context.func_216031_c(LootParameters.field_216289_i);
            if (PaxelEnhancements.getEnhancement(tool) != PaxelEnhancements.AUTO_SMELT) {
               return generatedLoot;
            } else {
               ServerWorld world = context.func_202879_g();
               Vector3d pos = (Vector3d)context.func_216031_c(LootParameters.field_237457_g_);
               return generatedLoot.stream()
                  .filter(stack -> !stack.func_190926_b())
                  .map(
                     stack -> {
                        Optional<Tuple<ItemStack, Float>> furnaceResult = RecipeUtil.findSmeltingResult(context.func_202879_g(), stack);
                        furnaceResult.ifPresent(
                           result -> {
                              BasicEventHooks.firePlayerSmeltedEvent(player, (ItemStack)result.func_76341_a());
                              float exp = (Float)result.func_76340_b();
                              if (exp > 0.0F) {
                                 int iExp = (int)exp;
                                 float partialExp = exp - iExp;
                                 if (partialExp > 0.0F && partialExp > context.func_216032_b().nextFloat()) {
                                    iExp++;
                                 }

                                 while (iExp > 0) {
                                    int expPart = ExperienceOrbEntity.func_70527_a(iExp);
                                    iExp -= expPart;
                                    world.func_217376_c(
                                       new ExperienceOrbEntity(world, pos.func_82615_a() + 0.5, pos.func_82617_b() + 0.5, pos.func_82616_c() + 0.5, expPart)
                                    );
                                 }
                              }
                           }
                        );
                        return furnaceResult.map(Tuple::func_76341_a).orElse((ItemStack)stack);
                     }
                  )
                  .collect(Collectors.toList());
            }
         }
      } else {
         return generatedLoot;
      }
   }

   public static class Serializer extends GlobalLootModifierSerializer<LootModifierAutoSmelt> {
      public LootModifierAutoSmelt read(ResourceLocation location, JsonObject object, ILootCondition[] lootConditions) {
         return new LootModifierAutoSmelt(lootConditions);
      }

      public JsonObject write(LootModifierAutoSmelt instance) {
         return this.makeConditions(instance.conditions);
      }
   }
}
