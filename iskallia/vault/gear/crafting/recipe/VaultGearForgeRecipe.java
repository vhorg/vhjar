package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class VaultGearForgeRecipe extends VaultForgeRecipe {
   private ProficiencyType proficiencyType;

   protected VaultGearForgeRecipe(ResourceLocation id, ItemStack output) {
      super(id, output);
   }

   public VaultGearForgeRecipe(ResourceLocation id, ItemStack output, List<ItemStack> inputs, ProficiencyType proficiencyType) {
      super(id, output, inputs);
      this.proficiencyType = proficiencyType;
   }

   @Override
   protected int getClassId() {
      return 1;
   }

   @Override
   protected void readAdditional(FriendlyByteBuf buf) {
      super.readAdditional(buf);
      this.proficiencyType = (ProficiencyType)buf.readEnum(ProficiencyType.class);
   }

   @Override
   protected void writeAdditional(FriendlyByteBuf buf) {
      super.writeAdditional(buf);
      buf.writeEnum(this.proficiencyType);
   }

   @Override
   public ItemStack getDisplayOutput() {
      ItemStack out = super.getDisplayOutput();
      VaultGearData data = VaultGearData.read(out);
      data.setState(VaultGearState.IDENTIFIED);
      data.write(out);
      return out;
   }

   @Override
   public ItemStack createOutput(ServerPlayer crafter) {
      ItemStack stack = super.createOutput(crafter);
      Item item = stack.getItem();
      if (item instanceof IdolItem) {
         item = MiscUtils.getRandomEntry(ModItems.IDOL_BENEVOLENT, ModItems.IDOL_MALEVOLENCE, ModItems.IDOL_OMNISCIENT, ModItems.IDOL_TIMEKEEPER);
      }

      return item instanceof VaultGearItem gearItem ? VaultGearCraftingHelper.doCraftGear(gearItem, crafter, false) : stack;
   }
}
