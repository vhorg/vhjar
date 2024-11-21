package iskallia.vault.gear.item;

import com.google.gson.JsonObject;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.gear.GearRollHelper;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.VaultGearType;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.tooltip.VaultGearTooltipItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.IAnvilPreventCombination;
import iskallia.vault.item.IConditionalDamageable;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.RecyclableItem;
import iskallia.vault.item.gear.VaultLevelItem;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.util.VHSmpUtil;
import iskallia.vault.world.data.ServerVaults;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import org.apache.commons.lang3.ObjectUtils;

public interface VaultGearItem
   extends IForgeItem,
   VaultGearTooltipItem,
   DataTransferItem,
   VaultLevelItem,
   RecyclableItem,
   DynamicModelItem,
   IConditionalDamageable,
   IAnvilPreventCombination,
   IdentifiableItem {
   JavaRandom random = JavaRandom.ofNanoTime();
   int BROKEN_DAMAGE_VALUE = -1;

   default Item getItem() {
      return (Item)this;
   }

   static boolean matches(ItemStack stack) {
      return stack.getItem() instanceof VaultGearItem;
   }

   static <T extends VaultGearItem> T of(ItemStack stack) {
      if (!matches(stack)) {
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
   default boolean shouldPreventAnvilCombination(ItemStack other) {
      return other.getItem() instanceof VaultGearItem;
   }

   @Override
   default boolean isImmuneToDamage(ItemStack stack, @Nullable Player player) {
      return player == null ? false : ServerVaults.get(player.getLevel()).isEmpty() && !VHSmpUtil.isArenaWorld(player);
   }

   @Nullable
   @Override
   default Optional<ResourceLocation> getDynamicModelId(ItemStack itemStack) {
      GearDataCache cache = GearDataCache.of(itemStack);
      VaultGearState state = (VaultGearState)ObjectUtils.firstNonNull(new VaultGearState[]{cache.getState(), VaultGearState.UNIDENTIFIED});
      return state == VaultGearState.UNIDENTIFIED ? Optional.empty() : cache.getGearModel();
   }

   default Optional<? extends DynamicModel<?>> resolveDynamicModel(ItemStack stack, ResourceLocation key) {
      return Optional.empty();
   }

   @Override
   default void initializeVaultLoot(int vaultLevel, ItemStack stack, @Nullable BlockPos pos, @Nullable Vault vault) {
      VaultGearData data = VaultGearData.read(stack);
      data.setItemLevel(vaultLevel);
      data.createOrReplaceAttributeValue(ModGearAttributes.IS_LOOT, Boolean.valueOf(vault != null));
      data.write(stack);
   }

   @Override
   default ItemStack convertStack(ItemStack stack, RandomSource random) {
      EquipmentSlot slot = this.getGearType(stack).getEquipmentSlot();
      if (stack.hasTag() && slot != null) {
         CompoundTag tag = stack.getOrCreateTag();
         String modelAttrKey = ModGearAttributes.GEAR_MODEL.getRegistryName().toString();
         if (tag.contains(modelAttrKey, 8)) {
            String modelStr = tag.getString(modelAttrKey);
            if (modelStr.equalsIgnoreCase("random")) {
               Set<String> models = new HashSet<>();
               ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRolls(stack).forEach((rarity, modelList) -> {
                  if (!rarity.equals(VaultGearRarity.UNIQUE.name())) {
                     models.addAll(modelList);
                  }
               });
               List<? extends DynamicModel<?>> armorModelSet = models.stream()
                  .<ResourceLocation>map(ResourceLocation::new)
                  .map(key -> this.resolveDynamicModel(stack, key))
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .toList();
               DynamicModel<?> randomModel = (DynamicModel<?>)(armorModelSet.isEmpty() ? null : armorModelSet.get(random.nextInt(armorModelSet.size())));
               if (randomModel != null) {
                  VaultGearData data = VaultGearData.read(stack);
                  data.createOrReplaceAttributeValue(ModGearAttributes.GEAR_MODEL, randomModel.getId());
                  data.write(stack);
               }

               tag.remove(modelAttrKey);
            }
         }
      }

      ItemStack result = DataTransferItem.super.convertStack(stack, random);
      VaultGearHelper.initializeGearRollType(result, VaultGearData.read(result).getItemLevel(), random);
      return result;
   }

   @Override
   default Optional<UUID> getUuid(ItemStack stack) {
      return AttributeGearData.readUUID(stack);
   }

   default boolean shouldCauseEquipmentCooldown(ServerPlayer sPlayer, ItemStack stack, EquipmentSlot slot) {
      return !sPlayer.isCreative();
   }

   @Override
   default boolean isValidInput(ItemStack input) {
      return !input.isEmpty() && AttributeGearData.hasData(input);
   }

   @Override
   default VaultRecyclerConfig.RecyclerOutput getOutput(ItemStack input) {
      return ModConfigs.VAULT_RECYCLER.getGearRecyclingOutput();
   }

   @Override
   default float getResultPercentage(ItemStack input) {
      if (input.isEmpty()) {
         return 0.0F;
      } else if (VaultGearData.read(input).getState() != VaultGearState.IDENTIFIED) {
         return 1.0F;
      } else {
         return !input.isDamageableItem() ? 1.0F : 1.0F - (float)input.getDamageValue() / input.getMaxDamage();
      }
   }

   default void setItemLevel(ItemStack stack, Player player) {
      this.setItemLevel(stack, SidedHelper.getVaultLevel(player));
   }

   default void setItemLevel(ItemStack stack, int level) {
      VaultGearData data = VaultGearData.read(stack);
      data.setItemLevel(level);
      data.write(stack);
   }

   @Nonnull
   VaultGearClassification getClassification(ItemStack var1);

   @Deprecated(
      forRemoval = true
   )
   @Nonnull
   ProficiencyType getCraftingProficiencyType(ItemStack var1);

   @Nonnull
   VaultGearType getGearType(ItemStack var1);

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
      return this.getGearType(stack).getEquipmentSlot() == slotType;
   }

   default void setDamage(ItemStack stack, int newDamage) {
      int maxDamage = stack.getMaxDamage();
      if (stack.getDamageValue() != newDamage) {
         if (stack.getDamageValue() == -1 && newDamage < maxDamage) {
            stack.getOrCreateTag().putInt("Damage", newDamage);
            VaultGearData.read(stack).write(stack);
         } else if (newDamage != -1 && newDamage < maxDamage) {
            super.setDamage(stack, newDamage);
         } else {
            stack.getOrCreateTag().putInt("Damage", -1);
            VaultGearData.read(stack).write(stack);
         }
      }
   }

   default boolean isBroken(ItemStack stack) {
      return stack.getDamageValue() == -1;
   }

   default void vaultGearTick(ItemStack stack, ServerPlayer player) {
      VaultGearHelper.initializeGearRollType(stack, player);
      this.inventoryIdentificationTick(player, stack);
   }

   @Override
   default void tickRoll(ItemStack stack, @Nullable Player player) {
      GearRollHelper.tickGearRoll(stack);
   }

   @Override
   default void tickFinishRoll(ItemStack stack, @Nullable Player player) {
      GearRollHelper.initializeAndDiscoverGear(stack, player);
   }
}
