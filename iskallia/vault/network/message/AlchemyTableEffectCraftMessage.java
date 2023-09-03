package iskallia.vault.network.message;

import iskallia.vault.block.entity.AlchemyTableTileEntity;
import iskallia.vault.config.AlchemyTableConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.bottle.BottleEffect;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.util.InventoryUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class AlchemyTableEffectCraftMessage {
   private final BlockPos pos;
   private final String effectId;

   public AlchemyTableEffectCraftMessage(BlockPos pos, @Nullable String effectId) {
      this.pos = pos;
      this.effectId = effectId;
   }

   public static void encode(AlchemyTableEffectCraftMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
      buffer.writeOptional(Optional.ofNullable(message.effectId), FriendlyByteBuf::writeUtf);
   }

   public static AlchemyTableEffectCraftMessage decode(FriendlyByteBuf buffer) {
      BlockPos pos = buffer.readBlockPos();
      String effectId = (String)buffer.readOptional(FriendlyByteBuf::readUtf).orElse(null);
      return new AlchemyTableEffectCraftMessage(pos, effectId);
   }

   public static void handle(AlchemyTableEffectCraftMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         BlockPos pos = message.pos;
         if (player.getLevel().getBlockEntity(pos) instanceof AlchemyTableTileEntity alchemyTableTile) {
            ItemStack input = alchemyTableTile.getInventory().getItem(0);
            if (!input.isEmpty()) {
               AlchemyTableConfig cfg = ModConfigs.VAULT_ALCHEMY_TABLE;
               ItemStack inputCopy = input.copy();
               List<ItemStack> cost = new ArrayList<>();
               AlchemyTableConfig.CraftableEffectConfig effectConfig = cfg.getConfig(message.effectId);
               if (effectConfig != null) {
                  if (effectConfig.hasPrerequisites(player)) {
                     BottleEffect createdEffect = BottleItem.getType(input).flatMap(effectConfig::createEffect).orElse(null);
                     if (createdEffect != null) {
                        cost.addAll(effectConfig.createCraftingCost(inputCopy));
                        List<ItemStack> missing = InventoryUtil.getMissingInputs(cost, player.getInventory());
                        if (missing.isEmpty()) {
                           if (InventoryUtil.consumeInputs(cost, player.getInventory(), true)) {
                              if (InventoryUtil.consumeInputs(cost, player.getInventory(), false)) {
                                 alchemyTableTile.startCrafting();
                                 BottleItem.setEffect(input, createdEffect);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
