package iskallia.vault.item;

import iskallia.vault.Vault;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModModels;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.AdvancementHelper;
import iskallia.vault.util.EntityHelper;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemModArmorCrate extends BasicItem {
   private final Supplier<List<ModModels.SpecialGearModel.SpecialGearModelSet>> modelSetSupplier;

   public ItemModArmorCrate(ResourceLocation id, Properties properties, Supplier<List<ModModels.SpecialGearModel.SpecialGearModelSet>> modelSetSupplier) {
      super(id, properties);
      this.modelSetSupplier = modelSetSupplier;
   }

   @Nonnull
   public ActionResult<ItemStack> func_77659_a(World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
      if (!world.field_72995_K) {
         List<ModModels.SpecialGearModel.SpecialGearModelSet> modelSets = this.modelSetSupplier.get();
         ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
         ItemStack heldStack = player.func_184614_ca();
         int modelSetIndex = world.func_201674_k().nextInt(modelSets.size());
         ModModels.SpecialGearModel.SpecialGearModelSet modelSet = modelSets.get(modelSetIndex);
         int slot = world.func_201674_k().nextInt(4);
         ItemStack itemStack;
         if (slot == 0) {
            itemStack = new ItemStack(ModItems.HELMET);
            this.configureItemStack(itemStack, modelSet.head.getId());
            AdvancementHelper.grantCriterion(serverPlayer, Vault.id("armors/" + this.getRegistryName().func_110623_a() + "/set"), "looted_helmet");
         } else if (slot == 1) {
            itemStack = new ItemStack(ModItems.CHESTPLATE);
            this.configureItemStack(itemStack, modelSet.chestplate.getId());
            AdvancementHelper.grantCriterion(serverPlayer, Vault.id("armors/" + this.getRegistryName().func_110623_a() + "/set"), "looted_chestplate");
         } else if (slot == 2) {
            itemStack = new ItemStack(ModItems.LEGGINGS);
            this.configureItemStack(itemStack, modelSet.leggings.getId());
            AdvancementHelper.grantCriterion(serverPlayer, Vault.id("armors/" + this.getRegistryName().func_110623_a() + "/set"), "looted_leggings");
         } else {
            itemStack = new ItemStack(ModItems.BOOTS);
            this.configureItemStack(itemStack, modelSet.boots.getId());
            AdvancementHelper.grantCriterion(serverPlayer, Vault.id("armors/" + this.getRegistryName().func_110623_a() + "/set"), "looted_boots");
         }

         EntityHelper.giveItem(player, itemStack);
         ItemRelicBoosterPack.successEffects(world, player.func_213303_ch());
         heldStack.func_190918_g(1);
      }

      return super.func_77659_a(world, player, hand);
   }

   private void configureItemStack(ItemStack gearStack, int model) {
      ModAttributes.GEAR_STATE.create(gearStack, VaultGear.State.IDENTIFIED);
      gearStack.func_196082_o().func_82580_o("RollTicks");
      gearStack.func_196082_o().func_82580_o("LastModelHit");
      ModAttributes.GEAR_RARITY.create(gearStack, VaultGear.Rarity.UNIQUE);
      ModAttributes.GEAR_SET.create(gearStack, VaultGear.Set.NONE);
      ModAttributes.GEAR_SPECIAL_MODEL.create(gearStack, model);
      ModAttributes.GEAR_COLOR.create(gearStack, -1);
   }
}
