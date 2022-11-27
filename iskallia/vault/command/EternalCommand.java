package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.world.data.EternalsData;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class EternalCommand extends Command {
   @Override
   public String getName() {
      return "eternal";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("remove").then(Commands.argument("uuid", UuidArgument.uuid()).executes(this::removeEternal)));
      builder.then(Commands.literal("list").then(Commands.argument("playerId", UuidArgument.uuid()).executes(this::listEternals)));
      builder.then(Commands.literal("set").then(Commands.argument("uuid", UuidArgument.uuid()).executes(this::setEternal)));
   }

   private int setEternal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer sPlayer = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ItemStack held = sPlayer.getItemInHand(InteractionHand.MAIN_HAND);
      if (!held.isEmpty() && held.getItem() instanceof BlockItem && ((BlockItem)held.getItem()).getBlock() instanceof CryoChamberBlock) {
         UUID eternalUUID = UuidArgument.getUuid(context, "uuid");
         EternalsData data = EternalsData.get(sPlayer.getLevel());
         EternalData eternal = data.getEternal(eternalUUID);
         if (eternal == null) {
            sPlayer.sendMessage(new TextComponent("Specified eternal does not exist!").withStyle(ChatFormatting.RED), Util.NIL_UUID);
            return 0;
         } else {
            CompoundTag tag = held.getOrCreateTagElement("BlockEntityTag");
            tag.putUUID("EternalId", eternalUUID);
            sPlayer.sendMessage(new TextComponent("Eternal set!").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
            return 0;
         }
      } else {
         sPlayer.sendMessage(new TextComponent("Not holding cryochamber!").withStyle(ChatFormatting.RED), Util.NIL_UUID);
         return 0;
      }
   }

   private int listEternals(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer sPlayer = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      UUID playerId = UuidArgument.getUuid(context, "playerId");
      EternalsData data = EternalsData.get(sPlayer.getLevel());
      EternalsData.EternalGroup group = data.getEternals(playerId);
      sPlayer.sendMessage(new TextComponent("Eternals:").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);

      for (EternalData eternal : group.getEternals()) {
         MutableComponent txt = new TextComponent(eternal.getId().toString() + " / " + eternal.getName());
         txt.withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent("Copy UUID"))));
         txt.withStyle(
            style -> style.withClickEvent(new ClickEvent(net.minecraft.network.chat.ClickEvent.Action.COPY_TO_CLIPBOARD, eternal.getId().toString()))
         );
         sPlayer.sendMessage(txt, Util.NIL_UUID);
      }

      return 0;
   }

   private int removeEternal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer sPlayer = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      UUID eternalUUID = UuidArgument.getUuid(context, "uuid");
      EternalsData data = EternalsData.get(sPlayer.getLevel());
      if (data.removeEternal(eternalUUID)) {
         sPlayer.sendMessage(new TextComponent("Eternal removed!").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
         return 0;
      } else {
         sPlayer.sendMessage(new TextComponent("Specified eternal does not exist!").withStyle(ChatFormatting.RED), Util.NIL_UUID);
         return 0;
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
