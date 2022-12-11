package iskallia.vault.gear.item;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.gear.GearRollHelper;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.IConditionalDamageable;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLootItem;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.util.VHSmpUtil;
import iskallia.vault.world.data.ServerVaults;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

public interface VaultGearItem extends IForgeItem, DataTransferItem, VaultLootItem, DynamicModelItem, IConditionalDamageable {
   default Item getItem() {
      return (Item)this;
   }

   static <T extends VaultGearItem> T of(ItemStack stack) {
      if (!(stack.getItem() instanceof VaultGearItem)) {
         throw new IllegalArgumentException("Item is not VaultGear: %s".formatted(stack));
      } else {
         return (T)stack.getItem();
      }
   }

   static JsonObject serializeGearData(ItemStack stack) {
      return AttributeGearData.<AttributeGearData>read(stack).serialize();
   }

   default ItemStack defaultItem() {
      ItemStack stack = new ItemStack(this.getItem());
      VaultGearData.read(stack).write(stack);
      return stack;
   }

   @Override
   default boolean isImmuneToDamage(ItemStack stack, @Nullable Player player) {
      return player == null ? false : !ServerVaults.isVaultWorld(player.getLevel()) && !VHSmpUtil.isArenaWorld(player);
   }

   @Nullable
   @Override
   default Optional<ResourceLocation> getDynamicModelId(ItemStack itemStack) {
      VaultGearData gearData = VaultGearData.read(itemStack);
      return gearData.getState() == VaultGearState.UNIDENTIFIED ? Optional.empty() : gearData.getFirstValue(ModGearAttributes.GEAR_MODEL);
   }

   @Override
   default void initializeLoot(Vault vault, ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      data.setItemLevel(vault.get(Vault.LEVEL).get());
      data.updateAttribute(ModGearAttributes.IS_LOOT, Boolean.valueOf(true));
      data.write(stack);
   }

   @Override
   default ItemStack convertStack(ItemStack stack, RandomSource random) {
      EquipmentSlot slot = this.getIntendedSlot(stack);
      if (stack.hasTag() && slot != null) {
         CompoundTag tag = stack.getOrCreateTag();
         String modelAttrKey = ModGearAttributes.GEAR_MODEL.getRegistryName().toString();
         if (tag.contains(modelAttrKey, 8)) {
            String modelStr = tag.getString(modelAttrKey);
            if (modelStr.equalsIgnoreCase("random")) {
               Set<String> models = new HashSet<>();
               ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRolls(this).forEach((rarity, modelList) -> {
                  if (rarity != VaultGearRarity.UNIQUE) {
                     models.addAll(modelList);
                  }
               });
               List<? extends ArmorPieceModel> armorModelSet = models.stream()
                  .<ResourceLocation>map(ResourceLocation::new)
                  .map(ModDynamicModels.Armor.MODEL_REGISTRY::get)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .map(model -> model.getPiece(slot))
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .toList();
               ArmorPieceModel randomModel = armorModelSet.isEmpty() ? null : armorModelSet.get(random.nextInt(armorModelSet.size()));
               if (randomModel != null) {
                  VaultGearData data = VaultGearData.read(stack);
                  data.updateAttribute(ModGearAttributes.GEAR_MODEL, randomModel.getId());
                  data.write(stack);
               }
            }

            tag.remove(modelAttrKey);
         }
      }

      ItemStack result = DataTransferItem.super.convertStack(stack, random);
      VaultGearHelper.initializeGearRollType(result, VaultGearData.read(result).getItemLevel(), random);
      return result;
   }

   default void setPlayerLevel(ItemStack stack, Player player) {
      VaultGearData data = VaultGearData.read(stack);
      data.setItemLevel(SidedHelper.getVaultLevel(player));
      data.write(stack);
   }

   default void setLevel(ItemStack stack, int level) {
      VaultGearData data = VaultGearData.read(stack);
      data.setItemLevel(level);
      data.write(stack);
   }

   @Nonnull
   VaultGearClassification getClassification(ItemStack var1);

   @Nonnull
   ProficiencyType getCraftingProficiencyType(ItemStack var1);

   @Nullable
   EquipmentSlot getIntendedSlot(ItemStack var1);

   @Nullable
   ResourceLocation getRandomModel(ItemStack var1, Random var2);

   default int getRandomColor(ItemStack stack, Random random) {
      List<Integer> colors = Arrays.stream(DyeColor.values())
         .filter(color -> color != DyeColor.BLACK && color != DyeColor.GRAY && color != DyeColor.LIGHT_GRAY)
         .<Integer>map(DyeColor::getTextColor)
         .toList();
      return colors.get(random.nextInt(colors.size()));
   }

   default boolean isIntendedForSlot(ItemStack stack, EquipmentSlot slotType) {
      return this.getIntendedSlot(stack) == slotType;
   }

   default void vaultGearTick(ItemStack stack, ServerPlayer player) {
      VaultGearHelper.initializeGearRollType(stack, player);
      GearRollHelper.tickRollVaultGear(stack, player);
   }
}
