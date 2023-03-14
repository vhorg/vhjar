package iskallia.vault.network.message;

import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.enhancement.EnhancementData;
import iskallia.vault.core.vault.enhancement.EnhancementTask;
import iskallia.vault.gear.GearRollHelper;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.world.data.ServerVaults;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultEnhancementRequestMessage {
   private final BlockPos altarPos;

   public VaultEnhancementRequestMessage(BlockPos altarPos) {
      this.altarPos = altarPos;
   }

   public static void encode(VaultEnhancementRequestMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.altarPos);
   }

   public static VaultEnhancementRequestMessage decode(FriendlyByteBuf buffer) {
      return new VaultEnhancementRequestMessage(buffer.readBlockPos());
   }

   public static void handle(VaultEnhancementRequestMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer player = context.getSender();
            BlockPos pos = message.altarPos;
            if (player.getLevel().getBlockEntity(pos) instanceof VaultEnhancementAltarTileEntity altarTile) {
               if (altarTile.canBeUsed(player)) {
                  ItemStack gearItem = altarTile.getInventory().getItem(0);
                  if (AttributeGearData.hasData(gearItem)) {
                     Vault vault = ServerVaults.get(player.getLevel()).orElse(null);
                     if (vault != null) {
                        EnhancementTask<?> task = EnhancementData.getForAltar(altarTile.getUUID()).get(player.getUUID());
                        if (task == null || !task.isFinished()) {
                           return;
                        }
                     }

                     if (VaultGearModifierHelper.createOrReplaceAbilityEnhancementModifier(gearItem, GearRollHelper.rand)) {
                        altarTile.setUsedByPlayer(player);
                        player.getLevel()
                           .playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, player.getRandom().nextFloat() * 0.1F + 0.9F);
                     }
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
