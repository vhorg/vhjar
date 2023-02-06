package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.item.tool.ToolMaterial;
import iskallia.vault.item.tool.ToolType;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ToolStationRecipe extends VaultForgeRecipe {
   protected ToolType toolType = ToolType.PICK;
   protected ToolMaterial toolMaterial = ToolMaterial.CHROMATIC_IRON_INGOT;

   protected ToolStationRecipe(ResourceLocation id, ItemStack output) {
      super(id, output);
   }

   public ToolStationRecipe(ResourceLocation id, ItemStack output, List<ItemStack> inputs, ToolType toolType, ToolMaterial toolMaterial) {
      super(id, output, inputs);
      this.toolType = toolType;
      this.toolMaterial = toolMaterial;
   }

   @Override
   protected int getClassId() {
      return 3;
   }

   @Override
   public Component getDisabledText() {
      return new TextComponent("Requires Level " + this.toolMaterial.getLevel()).withStyle(ChatFormatting.ITALIC);
   }

   @Override
   protected void readAdditional(FriendlyByteBuf buf) {
      super.readAdditional(buf);
      this.toolType = (ToolType)buf.readEnum(ToolType.class);
      this.toolMaterial = (ToolMaterial)buf.readEnum(ToolMaterial.class);
   }

   @Override
   protected void writeAdditional(FriendlyByteBuf buf) {
      super.writeAdditional(buf);
      buf.writeEnum(this.toolType);
      buf.writeEnum(this.toolMaterial);
   }

   @Override
   public ItemStack getDisplayOutput() {
      return ToolItem.create(this.toolMaterial, this.toolType);
   }

   @Override
   public ItemStack createOutput(List<OverSizedItemStack> consumed, ServerPlayer crafter) {
      return ToolItem.create(this.toolMaterial, this.toolType);
   }

   @Override
   public boolean canCraft(Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         PlayerVaultStatsData data = PlayerVaultStatsData.get(serverPlayer.server);
         int vaultLevel = data.getVaultStats(serverPlayer).getVaultLevel();
         return vaultLevel >= this.toolMaterial.getLevel();
      } else {
         return VaultBarOverlay.vaultLevel >= this.toolMaterial.getLevel();
      }
   }
}
