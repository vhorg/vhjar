package iskallia.vault.item.paxel.enhancement;

import iskallia.vault.Vault;
import iskallia.vault.init.ModItems;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;

public class PaxelEnhancements {
   public static Map<ResourceLocation, PaxelEnhancement> REGISTRY = new HashMap<>();
   public static DurabilityEnhancement FRAGILE = register("fragile", new DurabilityEnhancement(-3000));
   public static DurabilityEnhancement STURDY = register("sturdy", new DurabilityEnhancement(2000));
   public static PaxelEnhancement DESTRUCTIVE = register("destructive", new DestructiveEnhancement());
   public static PaxelEnhancement HAMMER = register("hammer", new HammerEnhancement());
   public static PaxelEnhancement AUTO_SMELT = register("auto_smelt", new AutoSmeltEnhancement());
   public static PaxelEnhancement FORTUNATE = register("fortunate", new FortuneEnhancement(1));
   public static PaxelEnhancement RUSH = register("rush", new EffectEnhancement(Effects.field_76422_e, 1));
   public static PaxelEnhancement RUSH_II = register("rush_2", new EffectEnhancement(Effects.field_76422_e, 2));
   public static PaxelEnhancement SPEEDY = register("speedy", new EffectEnhancement(Effects.field_76424_c, 1));
   public static final String ID_TAG = "Id";
   public static final String ENHANCEMENT_TAG = "Enhancement";
   public static final String SHOULD_ENHANCE_TAG = "ShouldEnhance";

   private static <T extends PaxelEnhancement> T register(String name, T enhancement) {
      return register(Vault.id(name), enhancement);
   }

   private static <T extends PaxelEnhancement> T register(ResourceLocation resourceLocation, T enhancement) {
      enhancement.setResourceLocation(resourceLocation);
      REGISTRY.put(resourceLocation, enhancement);
      return enhancement;
   }

   public static void enhance(ItemStack itemStack, PaxelEnhancement enhancement) {
      CompoundNBT nbt = itemStack.func_196082_o();
      nbt.func_218657_a("Enhancement", enhancement.serializeNBT());
      nbt.func_74757_a("ShouldEnhance", false);
   }

   @Nullable
   public static PaxelEnhancement getEnhancement(ItemStack itemStack) {
      if (itemStack.func_77973_b() != ModItems.VAULT_PAXEL) {
         return null;
      } else {
         CompoundNBT nbt = itemStack.func_196082_o();
         if (!nbt.func_150297_b("Enhancement", 10)) {
            return null;
         } else {
            CompoundNBT enhancementNBT = nbt.func_74775_l("Enhancement");
            String sId = enhancementNBT.func_74779_i("Id");
            return sId.isEmpty() ? null : REGISTRY.get(new ResourceLocation(sId));
         }
      }
   }

   public static void markShouldEnhance(ItemStack itemStack) {
      CompoundNBT nbt = itemStack.func_196082_o();
      nbt.func_74757_a("ShouldEnhance", true);
   }

   public static boolean shouldEnhance(ItemStack itemStack) {
      CompoundNBT nbt = itemStack.func_196082_o();
      return nbt.func_74767_n("ShouldEnhance") && !nbt.func_150297_b("Enhancement", 10);
   }
}
