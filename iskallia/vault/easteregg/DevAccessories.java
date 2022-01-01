package iskallia.vault.easteregg;

import iskallia.vault.entity.FighterEntity;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModModels;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.GearItemStackBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class DevAccessories {
   @SubscribeEvent
   public static void onVaultFighterBossKilled(LivingDeathEvent event) {
      LivingEntity entityLiving = event.getEntityLiving();
      if (entityLiving.func_70613_aW()) {
         if (entityLiving instanceof FighterEntity) {
            FighterEntity fighter = (FighterEntity)entityLiving;
            Entity trueSource = event.getSource().func_76346_g();
            if (fighter.func_184216_O().contains("vault_boss") && trueSource instanceof PlayerEntity) {
               onDevBossKill(fighter, (ServerPlayerEntity)trueSource);
            }
         }
      }
   }

   public static void onDevBossKill(FighterEntity boss, ServerPlayerEntity player) {
      ServerBossInfo bossInfo = boss.bossInfo;
      if (bossInfo != null) {
         ServerWorld world = (ServerWorld)boss.func_130014_f_();
         if (!(world.func_201674_k().nextDouble() > 0.4)) {
            String bossName = bossInfo.func_186744_e().getString();
            if (bossName.equalsIgnoreCase("iskall85")) {
               ItemStack itemStack = new GearItemStackBuilder(ModItems.HELMET)
                  .setGearRarity(VaultGear.Rarity.UNIQUE)
                  .setColor(-5384139)
                  .setSpecialModelId(ModModels.SpecialGearModel.ISKALL_HOLOLENS.getId())
                  .build();
               EntityHelper.giveItem(player, itemStack);
            } else if (!bossName.equalsIgnoreCase("iGoodie") && bossName.equalsIgnoreCase("Douwsky")) {
               ItemStack itemStack = new GearItemStackBuilder(ModItems.SWORD)
                  .setGearRarity(VaultGear.Rarity.UNIQUE)
                  .setSpecialModelId(ModModels.SpecialSwordModel.JANITORS_BROOM.getId())
                  .build();
               EntityHelper.giveItem(player, itemStack);
            }
         }
      }
   }
}
