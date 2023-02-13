package iskallia.vault.network.message;

import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.gear.crafting.VaultForgeHelper;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
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
            if (requester != null && requester.containerMenu instanceof ForgeRecipeContainer container) {
               if (container.getResultSlot().getItem().isEmpty()) {
                  ForgeRecipeTileEntity tile = container.getTile();
                  if (tile != null) {
                     VaultForgeRecipe recipe = null;

                     for (ForgeRecipeType type : tile.getSupportedRecipeTypes()) {
                        VaultForgeRecipe found = type.getRecipe(message.recipe);
                        if (found != null && found.canCraft(requester)) {
                           recipe = found;
                           break;
                        }
                     }

                     if (recipe != null) {
                        Inventory playerInventory = requester.getInventory();
                        OverSizedInventory tileInventory = tile.getInventory();
                        List<OverSizedItemStack> consumed = new ArrayList<>();
                        if (VaultForgeHelper.consumeInputs(recipe.getInputs(), playerInventory, tileInventory, true)
                           && VaultForgeHelper.consumeInputs(recipe.getInputs(), playerInventory, tileInventory, false, consumed)) {
                           container.getResultSlot().set(recipe.createOutput(consumed, requester));
                           requester.level.levelEvent(1030, tile.getBlockPos(), 0);
                           container.broadcastChanges();
                           ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ForgeParticleMessage(tile.getBlockPos()));
                        }
                     }
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
