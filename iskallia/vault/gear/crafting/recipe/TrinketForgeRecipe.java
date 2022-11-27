package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.client.ClientDiscoveredEntriesData;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.world.data.DiscoveredTrinketsData;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrinketForgeRecipe extends VaultForgeRecipe {
   private TrinketEffect<?> effect;

   protected TrinketForgeRecipe(ResourceLocation id, ItemStack output) {
      super(id, output);
   }

   public TrinketForgeRecipe(ResourceLocation id, ItemStack output, List<ItemStack> inputs, TrinketEffect<?> effect) {
      super(id, output, inputs);
      this.effect = effect;
   }

   @Override
   protected int getClassId() {
      return 2;
   }

   @Override
   protected void readAdditional(FriendlyByteBuf buf) {
      super.readAdditional(buf);
      this.effect = (TrinketEffect<?>)buf.readRegistryId();
   }

   @Override
   protected void writeAdditional(FriendlyByteBuf buf) {
      super.writeAdditional(buf);
      buf.writeRegistryId(this.effect);
   }

   @Override
   public ItemStack getDisplayOutput() {
      return TrinketItem.createBaseTrinket(this.effect);
   }

   @Override
   public ItemStack createOutput(ServerPlayer crafter) {
      ItemStack trinket = TrinketItem.createRandomTrinket(this.effect);
      AttributeGearData data = AttributeGearData.read(trinket);
      data.updateAttribute(ModGearAttributes.CRAFTED_BY, crafter.getName().getContents());
      data.write(trinket);
      return trinket;
   }

   @Override
   public boolean canCraft(Player player) {
      if (player instanceof ServerPlayer sPlayer) {
         DiscoveredTrinketsData trinketsData = DiscoveredTrinketsData.get(sPlayer.getLevel());
         return trinketsData.hasDiscovered(sPlayer, this.effect);
      } else {
         return ClientDiscoveredEntriesData.Trinkets.getDiscoveredTrinkets().contains(this.effect.getRegistryName());
      }
   }
}
