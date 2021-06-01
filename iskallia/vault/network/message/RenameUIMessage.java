package iskallia.vault.network.message;

import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.block.entity.PlayerStatueTileEntity;
import iskallia.vault.util.RenameType;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class RenameUIMessage {
   public RenameType renameType;
   public CompoundNBT payload;

   public static void encode(RenameUIMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.renameType.ordinal());
      buffer.func_150786_a(message.payload);
   }

   public static RenameUIMessage decode(PacketBuffer buffer) {
      RenameUIMessage message = new RenameUIMessage();
      message.renameType = RenameType.values()[buffer.readInt()];
      message.payload = buffer.func_150793_b();
      return message;
   }

   public static void handle(RenameUIMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         CompoundNBT data = message.payload.func_74775_l("Data");
         ServerPlayerEntity sender = context.getSender();
         if (message.renameType == RenameType.PLAYER_STATUE) {
            BlockPos statuePos = new BlockPos(data.func_74762_e("x"), data.func_74762_e("y"), data.func_74762_e("z"));
            TileEntity te = sender.func_130014_f_().func_175625_s(statuePos);
            if (te instanceof PlayerStatueTileEntity) {
               PlayerStatueTileEntity statue = (PlayerStatueTileEntity)te;
               statue.getSkin().updateSkin(data.func_74779_i("PlayerNickname"));
               statue.sendUpdates();
            } else if (te instanceof LootStatueTileEntity) {
               LootStatueTileEntity statue = (LootStatueTileEntity)te;
               statue.getSkin().updateSkin(data.func_74779_i("PlayerNickname"));
               statue.sendUpdates();
            }
         } else if (message.renameType == RenameType.TRADER_CORE) {
            sender.field_71071_by.field_70462_a.set(sender.field_71071_by.field_70461_c, ItemStack.func_199557_a(data));
         } else if (message.renameType == RenameType.CRYO_CHAMBER) {
            BlockPos pos = NBTUtil.func_186861_c(data.func_74775_l("BlockPos"));
            String name = data.func_74779_i("EternalName");
            TileEntity te = sender.func_130014_f_().func_175625_s(pos);
            if (te instanceof CryoChamberTileEntity) {
               CryoChamberTileEntity chamber = (CryoChamberTileEntity)te;
               chamber.renameEternal(name);
               chamber.getSkin().updateSkin(name);
               chamber.sendUpdates();
            }
         }
      });
      context.setPacketHandled(true);
   }

   public static RenameUIMessage updateName(RenameType type, CompoundNBT nbt) {
      RenameUIMessage message = new RenameUIMessage();
      message.renameType = type;
      message.payload = nbt;
      return message;
   }
}
