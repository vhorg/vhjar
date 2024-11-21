package iskallia.vault.network.message;

import iskallia.vault.block.entity.ModifierWorkbenchTileEntity;
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ModifierWorkbenchHelper;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.util.InventoryUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class ModifierWorkbenchCraftMessage {
   private final BlockPos pos;
   @Nullable
   private final ResourceLocation craftModifierIdentifier;

   public ModifierWorkbenchCraftMessage(BlockPos pos, @Nullable ResourceLocation craftModifierIdentifier) {
      this.pos = pos;
      this.craftModifierIdentifier = craftModifierIdentifier;
   }

   public static void encode(ModifierWorkbenchCraftMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
      buffer.writeOptional(Optional.ofNullable(message.craftModifierIdentifier), FriendlyByteBuf::writeResourceLocation);
   }

   public static ModifierWorkbenchCraftMessage decode(FriendlyByteBuf buffer) {
      BlockPos pos = buffer.readBlockPos();
      ResourceLocation key = (ResourceLocation)buffer.readOptional(FriendlyByteBuf::readResourceLocation).orElse(null);
      return new ModifierWorkbenchCraftMessage(pos, key);
   }

   public static void handle(ModifierWorkbenchCraftMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         BlockPos pos = message.pos;
         BlockEntity tile = player.getLevel().getBlockEntity(pos);
         if (tile instanceof ModifierWorkbenchTileEntity workbenchTile) {
            ItemStack input = workbenchTile.getInventory().getItem(0);
            if (!input.isEmpty() && input.getItem() instanceof VaultGearItem && AttributeGearData.hasData(input)) {
               if (VaultGearData.read(input).isModifiable()) {
                  VaultGearWorkbenchConfig.getConfig(input.getItem()).ifPresent(cfg -> {
                     ItemStack inputCopy = input.copy();
                     VaultGearModifier.AffixType targetAffix = null;
                     VaultGearModifier<?> createdModifier = null;
                     List<ItemStack> cost = new ArrayList<>();
                     if (message.craftModifierIdentifier == null) {
                        if (!ModifierWorkbenchHelper.hasCraftedModifier(inputCopy)) {
                           return;
                        }

                        if (!ModifierWorkbenchHelper.removeCraftedModifiers(inputCopy)) {
                           return;
                        }

                        cost.addAll(cfg.getCostRemoveCraftedModifiers());
                     } else {
                        VaultGearWorkbenchConfig.CraftableModifierConfig modifierConfig = cfg.getConfig(message.craftModifierIdentifier);
                        if (modifierConfig == null) {
                           return;
                        }

                        if (!modifierConfig.hasPrerequisites(player)) {
                           return;
                        }

                        boolean hadCraftedModifiers = ModifierWorkbenchHelper.hasCraftedModifier(inputCopy);
                        if (hadCraftedModifiers && !ModifierWorkbenchHelper.removeCraftedModifiers(inputCopy)) {
                           return;
                        }

                        VaultGearData data = VaultGearData.read(inputCopy);
                        if (data.getItemLevel() < modifierConfig.getMinLevel()) {
                           return;
                        }

                        targetAffix = modifierConfig.getAffixGroup().getTargetAffixType();
                        if (targetAffix == null) {
                           return;
                        }

                        switch (targetAffix) {
                           case PREFIX:
                              if (!VaultGearModifierHelper.hasOpenPrefix(inputCopy)) {
                                 return;
                              }
                              break;
                           case SUFFIX:
                              if (!VaultGearModifierHelper.hasOpenSuffix(inputCopy)) {
                                 return;
                              }
                              break;
                           default:
                              return;
                        }

                        createdModifier = modifierConfig.createModifier().orElse(null);
                        if (createdModifier == null) {
                           return;
                        }

                        Set<String> existingModGroups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
                        if (existingModGroups.contains(createdModifier.getModifierGroup())) {
                           return;
                        }

                        cost.addAll(modifierConfig.createCraftingCost(inputCopy));
                        if (hadCraftedModifiers) {
                           cost.addAll(cfg.getCostRemoveCraftedModifiers());
                        }
                     }

                     List<ItemStack> missing = InventoryUtil.getMissingInputs(cost, player.getInventory());
                     if (missing.isEmpty()) {
                        if (InventoryUtil.consumeInputs(cost, player.getInventory(), true)) {
                           if (InventoryUtil.consumeInputs(cost, player.getInventory(), false)) {
                              if (createdModifier == null) {
                                 ModifierWorkbenchHelper.removeCraftedModifiers(input);
                              } else {
                                 createdModifier.addCategory(VaultGearModifier.AffixCategory.CRAFTED);
                                 createdModifier.setGameTimeAdded(player.getLevel().getGameTime());
                                 ModifierWorkbenchHelper.removeCraftedModifiers(input);
                                 VaultGearData datax = VaultGearData.read(input);
                                 datax.addModifier(targetAffix, createdModifier);
                                 datax.write(input);
                              }

                              player.getLevel().levelEvent(1030, tile.getBlockPos(), 0);
                           }
                        }
                     }
                  });
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
