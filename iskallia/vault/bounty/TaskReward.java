package iskallia.vault.bounty;

import com.google.gson.annotations.Expose;
import com.mojang.datafixers.util.Pair;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.DiscoveredModelsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

public class TaskReward implements INBTSerializable<CompoundTag> {
   @Expose
   private int vaultExp;
   @Expose
   private List<OverSizedItemStack> rewardItems;
   @Expose
   private List<ResourceLocation> discoverModels;

   public TaskReward(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public TaskReward(int vaultExp, List<OverSizedItemStack> rewardItems) {
      this.vaultExp = vaultExp;
      this.rewardItems = rewardItems;
      this.discoverModels = new ArrayList<>();
   }

   public TaskReward(int vaultExp, List<OverSizedItemStack> items, List<ResourceLocation> discoverModels) {
      this.vaultExp = vaultExp;
      this.rewardItems = items;
      this.discoverModels = discoverModels;
   }

   public void apply(ServerPlayer player) {
      PlayerVaultStatsData.get(player.getLevel()).addVaultExp(player, this.vaultExp);
      ItemHandlerHelper.giveItemToPlayer(player, this.createRewardCrate());
      player.inventoryMenu.broadcastChanges();
      if (this.discoverModels != null) {
         this.discoverModels
            .stream()
            .map(ModDynamicModels.REGISTRIES::getModelAndAssociatedItem)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(itemPair -> this.discoverModel((Pair<? extends DynamicModel<?>, Item>)itemPair, player));
      }
   }

   private void discoverModel(Pair<? extends DynamicModel<?>, Item> itemPair, ServerPlayer player) {
      DiscoveredModelsData.get(player.getLevel()).discoverModelAndBroadcast((Item)itemPair.getSecond(), ((DynamicModel)itemPair.getFirst()).getId(), player);
   }

   public ItemStack createRewardCrate() {
      NonNullList<OverSizedItemStack> rewardItemStacks = this.createRewardItems();
      return VaultCrateBlock.getCrateWithLootOversized(VaultCrateBlock.Type.BOUNTY, rewardItemStacks);
   }

   @Nonnull
   public NonNullList<OverSizedItemStack> createRewardItems() {
      NonNullList<OverSizedItemStack> rewardItemStacks = NonNullList.create();
      this.rewardItems.stream().map(overSizedItemStack -> overSizedItemStack.addCopy(0)).forEach(rewardItemStacks::add);
      return rewardItemStacks;
   }

   public int getVaultExp() {
      return this.vaultExp;
   }

   public List<OverSizedItemStack> getRewardItems() {
      return this.rewardItems;
   }

   public List<ResourceLocation> getDiscoverModels() {
      return this.discoverModels;
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("vaultExp", this.vaultExp);
      NBTHelper.writeCollection(tag, "items", this.rewardItems, CompoundTag.class, OverSizedItemStack::serialize);
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.vaultExp = tag.getInt("vaultExp");
      this.rewardItems = NBTHelper.readList(tag, "items", CompoundTag.class, compoundTag -> {
         if (compoundTag.contains("stack")) {
            return OverSizedItemStack.deserialize(compoundTag);
         } else {
            ItemStack stack = ItemStack.of(compoundTag);
            return OverSizedItemStack.of(stack);
         }
      });
   }
}
