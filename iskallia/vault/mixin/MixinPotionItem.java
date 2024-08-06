package iskallia.vault.mixin;

import iskallia.vault.VaultMod;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PotionItem.class})
public class MixinPotionItem {
   @Inject(
      method = {"finishUsingItem"},
      at = {@At("HEAD")}
   )
   public void addCurseInVault(ItemStack stack, Level level, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
      if (!level.isClientSide()) {
         ServerVaults.get(level)
            .ifPresent(
               vault -> {
                  JavaRandom random = JavaRandom.ofNanoTime();
                  ModConfigs.VAULT_MODIFIER_POOLS
                     .getRandom(VaultMod.id("potion_curse"), vault.get(Vault.LEVEL).get(), random)
                     .stream()
                     .findFirst()
                     .ifPresent(
                        modifier -> {
                           vault.get(Vault.MODIFIERS).addModifier((VaultModifier<?>)modifier, 1, true, random);
                           if (entityLiving instanceof Player potionPlayer) {
                              level.getServer()
                                 .getPlayerList()
                                 .getPlayers()
                                 .forEach(
                                    player -> {
                                       if (player.getLevel().dimension().equals(level.dimension())) {
                                          player.displayClientMessage(
                                             new TextComponent("")
                                                .withStyle(ChatFormatting.GRAY)
                                                .append(potionPlayer.getDisplayName().copy().withStyle(ChatFormatting.WHITE))
                                                .append(" added ")
                                                .append(new TextComponent("1x ").withStyle(ChatFormatting.WHITE))
                                                .append(modifier.getDisplayName())
                                                .append(" to the vault!"),
                                             false
                                          );
                                       }
                                    }
                                 );
                           }
                        }
                     );
               }
            );
      }
   }
}
