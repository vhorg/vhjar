package iskallia.vault.network.message;

import iskallia.vault.block.entity.ToolStationTileEntity;
import iskallia.vault.container.ToolStationContainer;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.crafting.ToolStationHelper;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;

public class ToolStationRequestCraftMessage {
   private final ResourceLocation recipe;

   public ToolStationRequestCraftMessage(ResourceLocation recipe) {
      this.recipe = recipe;
   }

   public static void encode(ToolStationRequestCraftMessage message, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(message.recipe);
   }

   public static ToolStationRequestCraftMessage decode(FriendlyByteBuf buffer) {
      return new ToolStationRequestCraftMessage(buffer.readResourceLocation());
   }

   public static void handle(ToolStationRequestCraftMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer requester = context.getSender();
            if (requester != null && requester.containerMenu instanceof ToolStationContainer container) {
               if (container.getResultSlot().getItem().isEmpty()) {
                  VaultForgeRecipe recipe = ModConfigs.TOOL_RECIPES.getRecipe(message.recipe);
                  if (recipe != null && recipe.canCraft(requester)) {
                     Inventory playerInventory = requester.getInventory();
                     ToolStationTileEntity tile = container.getTileEntity();
                     List<OverSizedItemStack> consumed = new ArrayList<>();
                     if (ToolStationHelper.consumeInputs(recipe.getInputs(), playerInventory, tile, true)
                        && ToolStationHelper.consumeInputs(recipe.getInputs(), playerInventory, tile, false, consumed)) {
                        container.getResultSlot().set(recipe.createOutput(consumed, requester));
                        requester.level.levelEvent(1030, tile.getBlockPos(), 0);
                        container.broadcastChanges();
                        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ForgeParticleMessage(tile.getBlockPos()));
                     }
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
