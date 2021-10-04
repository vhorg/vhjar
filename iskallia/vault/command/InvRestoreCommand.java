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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.UUIDArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;

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
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(Commands.func_197057_a("list").then(Commands.func_197056_a("playerUUID", UUIDArgument.func_239194_a_()).executes(this::listTimestamps)));
      builder.then(
         Commands.func_197057_a("restore")
            .then(
               Commands.func_197056_a("playerUUID", UUIDArgument.func_239194_a_())
                  .then(Commands.func_197056_a("target", new BackupListArgument.UUIDRef()).executes(this::restoreUUID))
            )
      );
   }

   private int listTimestamps(CommandContext<CommandSource> ctx) {
      CommandSource src = (CommandSource)ctx.getSource();
      UUID playerRef = UUIDArgument.func_239195_a_(ctx, "playerUUID");
      List<String> timestamps = BackupManager.getMostRecentBackupFileTimestamps(src.func_197028_i(), playerRef);
      src.func_197030_a(new StringTextComponent("Last 5 available backups:"), true);
      timestamps.forEach(
         timestamp -> {
            String restoreCmd = String.format("/%s %s restore %s %s", "the_vault", this.getName(), playerRef.toString(), timestamp);
            ClickEvent ce = new ClickEvent(Action.SUGGEST_COMMAND, restoreCmd);
            HoverEvent he = new HoverEvent(
               net.minecraft.util.text.event.HoverEvent.Action.field_230550_a_, new StringTextComponent("Click to get restore command!")
            );
            StringTextComponent feedback = new StringTextComponent(timestamp);
            feedback.func_230530_a_(Style.field_240709_b_.func_240715_a_(ce).func_240716_a_(he));
            src.func_197030_a(feedback, true);
         }
      );
      return 0;
   }

   private int restoreUUID(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      UUID playerRef = UUIDArgument.func_239195_a_(ctx, "playerUUID");
      return this.attemptRestore((CommandSource)ctx.getSource(), playerRef, (String)ctx.getArgument("target", String.class)) ? 1 : 0;
   }

   private boolean attemptRestore(CommandSource src, UUID playerRef, String target) throws CommandSyntaxException {
      ServerPlayerEntity playerSource = src.func_197035_h();
      MinecraftServer srv = src.func_197028_i();
      return BackupManager.getStoredItemStacks(srv, playerRef, target).map(stacks -> {
         if (stacks.isEmpty()) {
            src.func_197030_a(new StringTextComponent("Backup file did not contain any items!").func_240699_a_(TextFormatting.RED), true);
            return false;
         } else {
            ServerWorld world = playerSource.func_71121_q();
            BlockPos offset = playerSource.func_233580_cy_();
            int chestsRequired = MathHelper.func_76123_f(stacks.size() / 27.0F);

            for (int i = 0; i < 2 + chestsRequired; i++) {
               BlockPos chestPos = offset.func_177982_a(0, 2 + i, 0);
               if (!World.func_175701_a(chestPos) || !world.func_175623_d(chestPos)) {
                  src.func_197030_a(new StringTextComponent("Empty space above the player is required!").func_240699_a_(TextFormatting.RED), true);
                  return false;
               }
            }

            for (int ix = 0; ix < chestsRequired; ix++) {
               BlockPos chestPos = offset.func_177982_a(0, 2 + ix, 0);
               List<ItemStack> subStacks = stacks.subList(ix * 27, Math.min(stacks.size(), (ix + 1) * 27));
               if (world.func_180501_a(chestPos, ModBlocks.VAULT_CHEST.func_176223_P(), 3)) {
                  TileEntity te = world.func_175625_s(chestPos);
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
         src.func_197030_a(new StringTextComponent("No such backup file found!").func_240699_a_(TextFormatting.RED), true);
         return false;
      });
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
