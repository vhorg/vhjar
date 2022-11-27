package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class AbilityCommand extends Command {
   @Override
   public String getName() {
      return "ability";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("reset_specializations").executes(this::resetSpecializations));
   }

   private int resetSpecializations(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      PlayerAbilitiesData data = PlayerAbilitiesData.get(player.getLevel());
      AbilityTree abilities = data.getAbilities(player);

      for (AbilityNode<?, ?> node : abilities.getLearnedNodes()) {
         data.selectSpecialization(player, node, null);
      }

      player.sendMessage(new TextComponent("Success").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
