package iskallia.vault.event;

import iskallia.vault.Vault;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.util.EntityHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class AdvancementEvents {
   @SubscribeEvent
   public static void onVaultHunterAdvancement(AdvancementEvent event) {
      LivingEntity entityLiving = event.getEntityLiving();
      if (entityLiving instanceof PlayerEntity && event.getAdvancement().func_192067_g().equals(Vault.id("vault_hunter"))) {
         PlayerEntity player = (PlayerEntity)entityLiving;
         String nickname = player.func_145748_c_().getString();
         ItemStack statueItem = LootStatueBlockItem.forVaultBoss(nickname, 0, true);
         EntityHelper.giveItem(player, statueItem);
      }
   }
}
