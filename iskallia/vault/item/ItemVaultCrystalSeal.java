package iskallia.vault.item;

import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.objective.NullCrystalObjective;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemVaultCrystalSeal extends Item {
   public ItemVaultCrystalSeal(ResourceLocation id) {
      this(id, 1);
   }

   public ItemVaultCrystalSeal(ResourceLocation id, int maxStackSize) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(maxStackSize));
      this.setRegistryName(id);
   }

   public boolean configure(CrystalData crystal) {
      return ModConfigs.VAULT_CRYSTAL.applySeal(new ItemStack(ModItems.VAULT_CRYSTAL), new ItemStack(this), new ItemStack(ModItems.VAULT_CRYSTAL), crystal);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      if (ModConfigs.isInitialized()) {
         CrystalData crystal = CrystalData.empty();
         if (this.configure(crystal)) {
            if (crystal.getObjective() != NullCrystalObjective.INSTANCE) {
               crystal.getObjective().addText(tooltip, tooltip.size(), flag, (float)ClientScheduler.INSTANCE.getTickCount());
            }
         }
      }
   }
}
