package iskallia.vault.network.message;

import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.container.VaultForgeContainer;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.crafting.VaultForgeHelper;
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

public class VaultForgeRequestCraftMessage {
   private final ResourceLocation recipe;

   public VaultForgeRequestCraftMessage(ResourceLocation recipe) {
      this.recipe = recipe;
   }

   public static void encode(VaultForgeRequestCraftMessage message, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(message.recipe);
   }

   public static VaultForgeRequestCraftMessage decode(FriendlyByteBuf buffer) {
      return new VaultForgeRequestCraftMessage(buffer.readResourceLocation());
   }

   public static void handle(VaultForgeRequestCraftMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer requester = context.getSender();
            if (requester != null && requester.containerMenu instanceof VaultForgeContainer container) {
               if (container.getResultSlot().getItem().isEmpty()) {
                  VaultForgeRecipe recipe = ModConfigs.VAULT_GEAR_RECIPES_CONFIG.getRecipe(message.recipe);
                  if (recipe != null && recipe.canCraft(requester)) {
                     Inventory playerInventory = requester.getInventory();
                     VaultForgeTileEntity tile = container.getTileEntity();
                     List<OverSizedItemStack> consumed = new ArrayList<>();
                     if (VaultForgeHelper.consumeInputs(recipe.getInputs(), playerInventory, tile, true)
                        && VaultForgeHelper.consumeInputs(recipe.getInputs(), playerInventory, tile, false, consumed)) {
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
