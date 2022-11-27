package iskallia.vault.bounty;

import com.google.gson.annotations.Expose;
import com.mojang.datafixers.util.Pair;
import iskallia.vault.block.VaultCrateBlock;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

public class TaskReward implements INBTSerializable<CompoundTag> {
   @Expose
   private int vaultExp;
   @Expose
   private List<ItemStack> rewardItems;
   @Expose
   private List<ResourceLocation> discoverModels;

   public TaskReward(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public TaskReward(int vaultExp, List<ItemStack> rewardItems) {
      this.vaultExp = vaultExp;
      this.rewardItems = rewardItems;
      this.discoverModels = new ArrayList<>();
   }

   public TaskReward(int vaultExp, List<ItemStack> items, List<ResourceLocation> discoverModels) {
      this.vaultExp = vaultExp;
      this.rewardItems = items;
      this.discoverModels = discoverModels;
   }

   public void apply(ServerPlayer player) {
      PlayerVaultStatsData.get(player.getLevel()).addVaultExp(player, this.vaultExp);
      ItemHandlerHelper.giveItemToPlayer(player, this.createRewardCrate(player));
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

   private ItemStack createRewardCrate(Player player) {
      NonNullList<ItemStack> rewardItemStacks = this.createRewardItems(player);
      return VaultCrateBlock.getCrateWithLoot(VaultCrateBlock.Type.BOUNTY, rewardItemStacks);
   }

   @Nonnull
   public NonNullList<ItemStack> createRewardItems(Player player) {
      NonNullList<ItemStack> rewardItemStacks = NonNullList.create();
      this.rewardItems.forEach(stack -> rewardItemStacks.add(stack.copy()));
      return rewardItemStacks;
   }

   public int getVaultExp() {
      return this.vaultExp;
   }

   public List<ResourceLocation> getDiscoverModels() {
      return this.discoverModels;
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("vaultExp", this.vaultExp);
      NBTHelper.writeCollection(tag, "items", this.rewardItems, CompoundTag.class, IForgeItemStack::serializeNBT);
      return (R)tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.vaultExp = tag.getInt("vaultExp");
      this.rewardItems = NBTHelper.readList(tag, "items", CompoundTag.class, ItemStack::of);
   }
}
