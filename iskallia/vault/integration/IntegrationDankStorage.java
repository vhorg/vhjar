package iskallia.vault.integration;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.Utils;

public class IntegrationDankStorage {
   @SubscribeEvent
   public static void onStewFinish(Finish event) {
      ItemStack dank = event.getItem();
      if (dank.func_77973_b() instanceof DankItem && Utils.isConstruction(dank)) {
         ItemStack dankUsedStack = Utils.getItemStackInSelectedSlot(dank);
         if (dankUsedStack.func_77973_b() instanceof SuspiciousStewItem) {
            CompoundNBT tag = dankUsedStack.func_77978_p();
            if (tag != null && tag.func_150297_b("Effects", 9)) {
               ListNBT effectList = tag.func_150295_c("Effects", 10);

               for (int i = 0; i < effectList.size(); i++) {
                  int duration = 160;
                  CompoundNBT effectTag = effectList.func_150305_b(i);
                  if (effectTag.func_150297_b("EffectDuration", 3)) {
                     duration = effectTag.func_74762_e("EffectDuration");
                  }

                  Effect effect = Effect.func_188412_a(effectTag.func_74771_c("EffectId"));
                  if (effect != null) {
                     event.getEntityLiving().func_195064_c(new EffectInstance(effect, duration));
                  }
               }
            }
         }
      }
   }
}
