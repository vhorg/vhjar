package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.world.data.EternalsData;
import java.util.UUID;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.UUIDArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;

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
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(Commands.func_197057_a("remove").then(Commands.func_197056_a("uuid", UUIDArgument.func_239194_a_()).executes(this::removeEternal)));
      builder.then(Commands.func_197057_a("list").then(Commands.func_197056_a("playerId", UUIDArgument.func_239194_a_()).executes(this::listEternals)));
      builder.then(Commands.func_197057_a("set").then(Commands.func_197056_a("uuid", UUIDArgument.func_239194_a_()).executes(this::setEternal)));
   }

   private int setEternal(CommandContext<CommandSource> context) throws CommandSyntaxException {
      ServerPlayerEntity sPlayer = ((CommandSource)context.getSource()).func_197035_h();
      ItemStack held = sPlayer.func_184586_b(Hand.MAIN_HAND);
      if (!held.func_190926_b() && held.func_77973_b() instanceof BlockItem && ((BlockItem)held.func_77973_b()).func_179223_d() instanceof CryoChamberBlock) {
         UUID eternalUUID = UUIDArgument.func_239195_a_(context, "uuid");
         EternalsData data = EternalsData.get(sPlayer.func_71121_q());
         EternalData eternal = data.getEternal(eternalUUID);
         if (eternal == null) {
            sPlayer.func_145747_a(new StringTextComponent("Specified eternal does not exist!").func_240699_a_(TextFormatting.RED), Util.field_240973_b_);
            return 0;
         } else {
            CompoundNBT tag = held.func_190925_c("BlockEntityTag");
            tag.func_186854_a("EternalId", eternalUUID);
            sPlayer.func_145747_a(new StringTextComponent("Eternal set!").func_240699_a_(TextFormatting.GREEN), Util.field_240973_b_);
            return 0;
         }
      } else {
         sPlayer.func_145747_a(new StringTextComponent("Not holding cryochamber!").func_240699_a_(TextFormatting.RED), Util.field_240973_b_);
         return 0;
      }
   }

   private int listEternals(CommandContext<CommandSource> context) throws CommandSyntaxException {
      ServerPlayerEntity sPlayer = ((CommandSource)context.getSource()).func_197035_h();
      UUID playerId = UUIDArgument.func_239195_a_(context, "playerId");
      EternalsData data = EternalsData.get(sPlayer.func_71121_q());
      EternalsData.EternalGroup group = data.getEternals(playerId);
      sPlayer.func_145747_a(new StringTextComponent("Eternals:").func_240699_a_(TextFormatting.GREEN), Util.field_240973_b_);

      for (EternalData eternal : group.getEternals()) {
         IFormattableTextComponent txt = new StringTextComponent(eternal.getId().toString() + " / " + eternal.getName());
         txt.func_240700_a_(style -> style.func_240716_a_(new HoverEvent(Action.field_230550_a_, new StringTextComponent("Copy UUID"))));
         txt.func_240700_a_(
            style -> style.func_240715_a_(new ClickEvent(net.minecraft.util.text.event.ClickEvent.Action.COPY_TO_CLIPBOARD, eternal.getId().toString()))
         );
         sPlayer.func_145747_a(txt, Util.field_240973_b_);
      }

      return 0;
   }

   private int removeEternal(CommandContext<CommandSource> context) throws CommandSyntaxException {
      ServerPlayerEntity sPlayer = ((CommandSource)context.getSource()).func_197035_h();
      UUID eternalUUID = UUIDArgument.func_239195_a_(context, "uuid");
      EternalsData data = EternalsData.get(sPlayer.func_71121_q());
      if (data.removeEternal(eternalUUID)) {
         sPlayer.func_145747_a(new StringTextComponent("Eternal removed!").func_240699_a_(TextFormatting.GREEN), Util.field_240973_b_);
         return 0;
      } else {
         sPlayer.func_145747_a(new StringTextComponent("Specified eternal does not exist!").func_240699_a_(TextFormatting.RED), Util.field_240973_b_);
         return 0;
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
