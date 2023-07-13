package iskallia.vault.network.message;

import iskallia.vault.block.entity.AlchemyTableTileEntity;
import iskallia.vault.config.gear.VaultAlchemyTableConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.AlchemyTableHelper;
import iskallia.vault.gear.crafting.ModifierWorkbenchHelper;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
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

public class ModifierAlchemyCraftMessage {
   private final BlockPos pos;
   @Nullable
   private final ResourceLocation craftModifierIdentifier;

   public ModifierAlchemyCraftMessage(BlockPos pos, @Nullable ResourceLocation craftModifierIdentifier) {
      this.pos = pos;
      this.craftModifierIdentifier = craftModifierIdentifier;
   }

   public static void encode(ModifierAlchemyCraftMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
      buffer.writeOptional(Optional.ofNullable(message.craftModifierIdentifier), FriendlyByteBuf::writeResourceLocation);
   }

   public static ModifierAlchemyCraftMessage decode(FriendlyByteBuf buffer) {
      BlockPos pos = buffer.readBlockPos();
      ResourceLocation key = (ResourceLocation)buffer.readOptional(FriendlyByteBuf::readResourceLocation).orElse(null);
      return new ModifierAlchemyCraftMessage(pos, key);
   }

   public static void handle(ModifierAlchemyCraftMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         BlockPos pos = message.pos;
         BlockEntity tile = player.getLevel().getBlockEntity(pos);
         if (tile instanceof AlchemyTableTileEntity workbenchTile) {
            ItemStack input = workbenchTile.getInventory().getItem(0);
            if (!input.isEmpty() && input.getItem() instanceof VaultGearItem && AttributeGearData.hasData(input)) {
               if (VaultGearData.read(input).isModifiable()) {
                  VaultAlchemyTableConfig cfg = ModConfigs.VAULT_ALCHEMY_TABLE;
                  ItemStack inputCopy = input.copy();
                  VaultGearModifier.AffixType targetAffix = null;
                  VaultGearModifier<?> createdModifier = null;
                  List<ItemStack> cost = new ArrayList<>();
                  if (message.craftModifierIdentifier == null) {
                     if (!AlchemyTableHelper.hasCraftedModifier(inputCopy)) {
                        return;
                     }

                     cost.addAll(cfg.getCostRemoveCraftedModifiers());
                  } else {
                     VaultAlchemyTableConfig.CraftableModifierConfig modifierConfig = cfg.getConfig(message.craftModifierIdentifier);
                     if (modifierConfig == null) {
                        return;
                     }

                     if (!modifierConfig.hasPrerequisites(player)) {
                        return;
                     }

                     VaultGearData data = VaultGearData.read(inputCopy);
                     if (data.getItemLevel() < modifierConfig.getMinLevel()) {
                        return;
                     }

                     targetAffix = modifierConfig.getAffixGroup().getTargetAffixType();
                     createdModifier = modifierConfig.createModifier().orElse(null);
                     if (createdModifier == null) {
                        return;
                     }

                     Set<String> existingModGroups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
                     if (existingModGroups.contains(createdModifier.getModifierGroup())) {
                        return;
                     }

                     cost.addAll(modifierConfig.createCraftingCost(inputCopy));
                  }

                  List<ItemStack> missing = InventoryUtil.getMissingInputs(cost, player.getInventory());
                  if (missing.isEmpty()) {
                     if (InventoryUtil.consumeInputs(cost, player.getInventory(), true)) {
                        if (InventoryUtil.consumeInputs(cost, player.getInventory(), false)) {
                           if (createdModifier == null) {
                              ModifierWorkbenchHelper.removeCraftedModifiers(input);
                           } else {
                              createdModifier.setCategory(VaultGearModifier.AffixCategory.CRAFTED);
                              createdModifier.setGameTimeAdded(player.getLevel().getGameTime());
                              VaultGearData datax = VaultGearData.read(input);
                              datax.addModifier(targetAffix, createdModifier);
                              datax.write(input);
                           }

                           player.getLevel().levelEvent(1030, tile.getBlockPos(), 0);
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
