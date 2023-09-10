package iskallia.vault.mixin;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.GameRules.Value;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({GameRuleCommand.class})
public class MixinGameRuleCommand {
   @Inject(
      method = {"Lnet/minecraft/server/commands/GameRuleCommand;setRule(Lcom/mojang/brigadier/context/CommandContext;Lnet/minecraft/world/level/GameRules$Key;)I"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static <T extends Value<T>> void preventKeepInventory(CommandContext<CommandSourceStack> context, Key<T> key, CallbackInfoReturnable<Integer> cir) {
      if (key == GameRules.RULE_KEEPINVENTORY && (Boolean)context.getArgument("value", Boolean.class)) {
         ((CommandSourceStack)context.getSource()).sendFailure(new TextComponent("You can't set keep inventory as it conflicts with the Vault mod features!"));
         cir.setReturnValue(0);
      }
   }
}
