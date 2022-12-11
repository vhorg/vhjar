package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.world.data.PlayerVaultAltarData;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public class VaultAltarCommand extends Command {
   @Override
   public String getName() {
      return "altar";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("reset").executes(this::reset));
   }

   private int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      PlayerVaultAltarData data = PlayerVaultAltarData.get(player.getLevel());
      List<BlockPos> altars = data.getAltars(player.getUUID());
      altars.stream()
         .filter(pos -> player.getLevel().isLoaded(pos))
         .map(pos -> player.getLevel().getBlockEntity(pos))
         .filter(te -> te instanceof VaultAltarTileEntity)
         .map(te -> (VaultAltarTileEntity)te)
         .filter(altar -> altar.getAltarState() == VaultAltarTileEntity.AltarState.ACCEPTING)
         .forEach(VaultAltarTileEntity::onRemoveVaultRock);
      data.removeRecipe(player.getUUID());
      data.setDirty();
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
