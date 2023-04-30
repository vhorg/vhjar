package iskallia.vault.network.message;

import iskallia.vault.block.entity.VaultEnchanterTileEntity;
import iskallia.vault.container.VaultEnchanterContainer;
import iskallia.vault.util.EnchantmentEntry;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultEnchanterEnchantMessage {
   private final BlockPos pos;
   private final EnchantmentEntry entry;

   public VaultEnchanterEnchantMessage(BlockPos pos, EnchantmentEntry entry) {
      this.pos = pos;
      this.entry = entry;
   }

   public static void encode(VaultEnchanterEnchantMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
      message.entry.writeBytes(buffer);
   }

   public static VaultEnchanterEnchantMessage decode(FriendlyByteBuf buffer) {
      return new VaultEnchanterEnchantMessage(buffer.readBlockPos(), new EnchantmentEntry(buffer));
   }

   public static void handle(VaultEnchanterEnchantMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer player = context.getSender();
            BlockPos pos = message.pos;
            BlockEntity tile = player.getLevel().getBlockEntity(pos);
            if (player.containerMenu instanceof VaultEnchanterContainer container) {
               if (tile instanceof VaultEnchanterTileEntity enchanterTile) {
                  ItemStack input = enchanterTile.getInventory().getItem(0);
                  if (!input.isEmpty() && message.entry.isValid()) {
                     if (message.entry.getCost().tryConsume(player)) {
                        message.entry.apply(input);
                        container.broadcastChanges();
                        player.getLevel()
                           .playSound(
                              null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, player.getLevel().random.nextFloat() * 0.1F + 0.9F
                           );
                     }
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
