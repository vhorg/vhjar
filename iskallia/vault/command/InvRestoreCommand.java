package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.backup.BackupListArgument;
import iskallia.vault.backup.BackupManager;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.init.ModBlocks;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;

@Deprecated
public class InvRestoreCommand extends Command {
   @Override
   public String getName() {
      return "inv_restore";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("list").then(Commands.argument("playerUUID", UuidArgument.uuid()).executes(this::listTimestamps)));
      builder.then(
         Commands.literal("restore")
            .then(
               Commands.argument("playerUUID", UuidArgument.uuid())
                  .then(Commands.argument("target", new BackupListArgument.UUIDRef()).executes(this::restoreUUID))
            )
      );
   }

   private int listTimestamps(CommandContext<CommandSourceStack> ctx) {
      CommandSourceStack src = (CommandSourceStack)ctx.getSource();
      UUID playerRef = UuidArgument.getUuid(ctx, "playerUUID");
      List<String> timestamps = BackupManager.getMostRecentBackupFileTimestamps(src.getServer(), playerRef);
      src.sendSuccess(new TextComponent("Last 5 available backups:"), true);
      timestamps.forEach(timestamp -> {
         String restoreCmd = String.format("/%s %s restore %s %s", "the_vault", this.getName(), playerRef.toString(), timestamp);
         ClickEvent ce = new ClickEvent(Action.SUGGEST_COMMAND, restoreCmd);
         HoverEvent he = new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to get restore command!"));
         TextComponent feedback = new TextComponent(timestamp);
         feedback.setStyle(Style.EMPTY.withClickEvent(ce).withHoverEvent(he));
         src.sendSuccess(feedback, true);
      });
      return 0;
   }

   private int restoreUUID(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      UUID playerRef = UuidArgument.getUuid(ctx, "playerUUID");
      return this.attemptRestore((CommandSourceStack)ctx.getSource(), playerRef, (String)ctx.getArgument("target", String.class)) ? 1 : 0;
   }

   private boolean attemptRestore(CommandSourceStack src, UUID playerRef, String target) throws CommandSyntaxException {
      ServerPlayer playerSource = src.getPlayerOrException();
      MinecraftServer srv = src.getServer();
      return BackupManager.getStoredItemStacks(srv, playerRef, target).map(stacks -> {
         if (stacks.isEmpty()) {
            src.sendSuccess(new TextComponent("Backup file did not contain any items!").withStyle(ChatFormatting.RED), true);
            return false;
         } else {
            ServerLevel world = playerSource.getLevel();
            BlockPos offset = playerSource.blockPosition();
            int chestsRequired = Mth.ceil(stacks.size() / 27.0F);

            for (int i = 0; i < 2 + chestsRequired; i++) {
               BlockPos chestPos = offset.offset(0, 2 + i, 0);
               if (!world.isInWorldBounds(chestPos) || !world.isEmptyBlock(chestPos)) {
                  src.sendSuccess(new TextComponent("Empty space above the player is required!").withStyle(ChatFormatting.RED), true);
                  return false;
               }
            }

            for (int ix = 0; ix < chestsRequired; ix++) {
               BlockPos chestPos = offset.offset(0, 2 + ix, 0);
               List<ItemStack> subStacks = stacks.subList(ix * 27, Math.min(stacks.size(), (ix + 1) * 27));
               if (world.setBlock(chestPos, ModBlocks.WOODEN_CHEST.defaultBlockState(), 3)) {
                  BlockEntity te = world.getBlockEntity(chestPos);
                  if (te instanceof VaultChestTileEntity) {
                     te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                        for (int index = 0; index < subStacks.size(); index++) {
                           inv.insertItem(index, subStacks.get(index), false);
                        }
                     });
                  }
               }
            }

            return true;
         }
      }).orElseGet(() -> {
         src.sendSuccess(new TextComponent("No such backup file found!").withStyle(ChatFormatting.RED), true);
         return false;
      });
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
