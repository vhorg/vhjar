package iskallia.vault.network.message;

import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.event.event.ForgeGearEvent;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.util.SidedHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultForgeRequestCraftMessage {
   private final ResourceLocation recipe;
   private final int level;
   private final boolean shift;

   public VaultForgeRequestCraftMessage(ResourceLocation recipe, int level, boolean shift) {
      this.recipe = recipe;
      this.level = level;
      this.shift = shift;
   }

   public static void encode(VaultForgeRequestCraftMessage message, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(message.recipe);
      buffer.writeInt(message.level);
      buffer.writeBoolean(message.shift);
   }

   public static VaultForgeRequestCraftMessage decode(FriendlyByteBuf buffer) {
      return new VaultForgeRequestCraftMessage(buffer.readResourceLocation(), buffer.readInt(), buffer.readBoolean());
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
                        if (InventoryUtil.consumeInputs(recipe.getInputs(), playerInventory, tileInventory, true)
                           && InventoryUtil.consumeInputs(recipe.getInputs(), playerInventory, tileInventory, false, consumed)) {
                           int level = Mth.clamp(message.level, 0, Math.min(ModConfigs.LEVELS_META.getMaxLevel(), SidedHelper.getVaultLevel(requester)));
                           ItemStack output = recipe.createOutput(consumed, requester, level);
                           if (message.shift) {
                              if (!requester.getInventory().add(output)) {
                                 container.getResultSlot().set(output);
                              }
                           } else {
                              container.getResultSlot().set(output);
                           }

                           requester.level.levelEvent(1030, tile.getBlockPos(), 0);
                           container.broadcastChanges();
                           ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ForgeParticleMessage(tile.getBlockPos()));
                           MinecraftForge.EVENT_BUS.post(new ForgeGearEvent(requester, recipe));
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
