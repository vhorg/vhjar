package iskallia.vault.init;

import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.renderer.AggressiveCowBossRenderer;
import iskallia.vault.entity.renderer.BlueBlazeRenderer;
import iskallia.vault.entity.renderer.BoogiemanRenderer;
import iskallia.vault.entity.renderer.DollMiniMeRenderer;
import iskallia.vault.entity.renderer.EffectCloudRenderer;
import iskallia.vault.entity.renderer.ElixirOrbRenderer;
import iskallia.vault.entity.renderer.EtchingVendorRenderer;
import iskallia.vault.entity.renderer.EternalRenderer;
import iskallia.vault.entity.renderer.EternalSpiritRenderer;
import iskallia.vault.entity.renderer.FighterRenderer;
import iskallia.vault.entity.renderer.MonsterEyeRenderer;
import iskallia.vault.entity.renderer.RobotRenderer;
import iskallia.vault.entity.renderer.ShiverRenderer;
import iskallia.vault.entity.renderer.SpiritRenderer;
import iskallia.vault.entity.renderer.ThrownJavelinRenderer;
import iskallia.vault.entity.renderer.TreasureGoblinRenderer;
import iskallia.vault.entity.renderer.VaultDoodRenderer;
import iskallia.vault.entity.renderer.VaultGuardianRenderer;
import iskallia.vault.entity.renderer.VaultGummySoldierRenderer;
import iskallia.vault.entity.renderer.VaultHorseRenderer;
import iskallia.vault.entity.renderer.VaultSpiderBabyRenderer;
import iskallia.vault.entity.renderer.VaultSpiderRenderer;
import iskallia.vault.entity.renderer.WinterWolfRenderer;
import iskallia.vault.entity.renderer.deep_dark.DeepDarkHorrorRenderer;
import iskallia.vault.entity.renderer.deep_dark.DeepDarkPiglinRenderer;
import iskallia.vault.entity.renderer.deep_dark.DeepDarkSilverfishRenderer;
import iskallia.vault.entity.renderer.deep_dark.DeepDarkSkeletonRenderer;
import iskallia.vault.entity.renderer.deep_dark.DeepDarkZombieRenderer;
import iskallia.vault.entity.renderer.elite.EliteDrownedRenderer;
import iskallia.vault.entity.renderer.elite.EliteEndermanRenderer;
import iskallia.vault.entity.renderer.elite.EliteHuskRenderer;
import iskallia.vault.entity.renderer.elite.EliteSkeletonRenderer;
import iskallia.vault.entity.renderer.elite.EliteSpiderRenderer;
import iskallia.vault.entity.renderer.elite.EliteStrayRenderer;
import iskallia.vault.entity.renderer.elite.EliteWitchRenderer;
import iskallia.vault.entity.renderer.elite.EliteWitherSkeletonRenderer;
import iskallia.vault.entity.renderer.elite.EliteZombieRenderer;
import iskallia.vault.entity.renderer.eyesore.EyesoreRenderer;
import iskallia.vault.entity.renderer.eyesore.EyestalkRenderer;
import iskallia.vault.entity.renderer.miner_zombie.Tier0MinerZombieRenderer;
import iskallia.vault.entity.renderer.miner_zombie.Tier1MinerZombieRenderer;
import iskallia.vault.entity.renderer.miner_zombie.Tier2MinerZombieRenderer;
import iskallia.vault.entity.renderer.miner_zombie.Tier3MinerZombieRenderer;
import iskallia.vault.entity.renderer.miner_zombie.Tier4MinerZombieRenderer;
import iskallia.vault.entity.renderer.miner_zombie.Tier5MinerZombieRenderer;
import iskallia.vault.entity.renderer.mummy.Tier0MummyRenderer;
import iskallia.vault.entity.renderer.mummy.Tier1MummyRenderer;
import iskallia.vault.entity.renderer.mummy.Tier2MummyRenderer;
import iskallia.vault.entity.renderer.mushroom.DeathcapRenderer;
import iskallia.vault.entity.renderer.mushroom.SmolcapRenderer;
import iskallia.vault.entity.renderer.mushroom.Tier0MushroomRenderer;
import iskallia.vault.entity.renderer.mushroom.Tier1MushroomRenderer;
import iskallia.vault.entity.renderer.mushroom.Tier2MushroomRenderer;
import iskallia.vault.entity.renderer.mushroom.Tier3MushroomRenderer;
import iskallia.vault.entity.renderer.mushroom.Tier4MushroomRenderer;
import iskallia.vault.entity.renderer.mushroom.Tier5MushroomRenderer;
import iskallia.vault.entity.renderer.overgrown_zombie.Tier0OvergrownZombieRenderer;
import iskallia.vault.entity.renderer.overgrown_zombie.Tier1OvergrownZombieRenderer;
import iskallia.vault.entity.renderer.overgrown_zombie.Tier2OvergrownZombieRenderer;
import iskallia.vault.entity.renderer.overgrown_zombie.Tier3OvergrownZombieRenderer;
import iskallia.vault.entity.renderer.overgrown_zombie.Tier4OvergrownZombieRenderer;
import iskallia.vault.entity.renderer.overgrown_zombie.Tier5OvergrownZombieRenderer;
import iskallia.vault.entity.renderer.skeleton_pirate.Tier0SkeletonPirateRenderer;
import iskallia.vault.entity.renderer.skeleton_pirate.Tier1SkeletonPirateRenderer;
import iskallia.vault.entity.renderer.skeleton_pirate.Tier2SkeletonPirateRenderer;
import iskallia.vault.entity.renderer.skeleton_pirate.Tier3SkeletonPirateRenderer;
import iskallia.vault.entity.renderer.skeleton_pirate.Tier4SkeletonPirateRenderer;
import iskallia.vault.entity.renderer.skeleton_pirate.Tier5SkeletonPirateRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1CreeperRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1DrownedRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1EndermanRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1HuskRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1PiglinRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1SkeletonRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1StrayRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1WitherSkeletonRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1ZombieRenderer;
import iskallia.vault.entity.renderer.tier2.Tier2CreeperRenderer;
import iskallia.vault.entity.renderer.tier2.Tier2DrownedRenderer;
import iskallia.vault.entity.renderer.tier2.Tier2EndermanRenderer;
import iskallia.vault.entity.renderer.tier2.Tier2HuskRenderer;
import iskallia.vault.entity.renderer.tier2.Tier2PiglinRenderer;
import iskallia.vault.entity.renderer.tier2.Tier2SkeletonRenderer;
import iskallia.vault.entity.renderer.tier2.Tier2StrayRenderer;
import iskallia.vault.entity.renderer.tier2.Tier2WitherSkeletonRenderer;
import iskallia.vault.entity.renderer.tier2.Tier2ZombieRenderer;
import iskallia.vault.entity.renderer.tier3.Tier3CreeperRenderer;
import iskallia.vault.entity.renderer.tier3.Tier3DrownedRenderer;
import iskallia.vault.entity.renderer.tier3.Tier3EndermanRenderer;
import iskallia.vault.entity.renderer.tier3.Tier3HuskRenderer;
import iskallia.vault.entity.renderer.tier3.Tier3PiglinRenderer;
import iskallia.vault.entity.renderer.tier3.Tier3SkeletonRenderer;
import iskallia.vault.entity.renderer.tier3.Tier3StrayRenderer;
import iskallia.vault.entity.renderer.tier3.Tier3WitherSkeletonRenderer;
import iskallia.vault.entity.renderer.tier3.Tier3ZombieRenderer;
import iskallia.vault.entity.renderer.winterwalker.Tier0WinterwalkerRenderer;
import iskallia.vault.entity.renderer.winterwalker.Tier1WinterwalkerRenderer;
import iskallia.vault.entity.renderer.winterwalker.Tier2WinterwalkerRenderer;
import iskallia.vault.entity.renderer.winterwalker.Tier3WinterwalkerRenderer;
import iskallia.vault.entity.renderer.winterwalker.Tier4WinterwalkerRenderer;
import iskallia.vault.entity.renderer.winterwalker.Tier5WinterwalkerRenderer;
import iskallia.vault.skill.ability.effect.DashWarpAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractSmiteAbility;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class ModEntityRenderers {
   public static void register(FMLClientSetupEvent event) {
      AtomicInteger itor = new AtomicInteger();
      ModEntities.VAULT_FIGHTER_TYPES.forEach(type -> {
         switch (itor.get()) {
            case 1:
               EntityRenderers.register(type, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER));
               break;
            case 2:
               EntityRenderers.register(type, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER));
               break;
            case 3:
               EntityRenderers.register(type, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER_T3));
               break;
            case 4:
               EntityRenderers.register(type, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER_T4));
               break;
            case 5:
               EntityRenderers.register(type, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER_T3));
               break;
            case 6:
               EntityRenderers.register(type, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER_T3));
               break;
            case 7:
               EntityRenderers.register(type, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER_T3));
               break;
            case 8:
               EntityRenderers.register(type, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER_T3));
               break;
            case 9:
               EntityRenderers.register(type, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER_T3));
               break;
            default:
               EntityRenderers.register(type, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER));
         }

         itor.getAndIncrement();
      });
      EntityRenderers.register(ModEntities.FIGHTER, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER));
      EntityRenderers.register(ModEntities.ARENA_BOSS, ctx -> new FighterRenderer(ctx, ModModelLayers.FIGHTER));
      EntityRenderers.register(ModEntities.BRUISER_GUARDIAN, VaultGuardianRenderer::new);
      EntityRenderers.register(ModEntities.ARBALIST_GUARDIAN, VaultGuardianRenderer::new);
      EntityRenderers.register(ModEntities.ETERNAL, EternalRenderer::new);
      EntityRenderers.register(ModEntities.TREASURE_GOBLIN, TreasureGoblinRenderer::new);
      EntityRenderers.register(ModEntities.AGGRESSIVE_COW, CowRenderer::new);
      EntityRenderers.register(ModEntities.ETCHING_VENDOR, EtchingVendorRenderer::new);
      EntityRenderers.register(ModEntities.VAULT_SPIDER, VaultSpiderRenderer::new);
      EntityRenderers.register(ModEntities.VAULT_SPIDER_BABY, VaultSpiderBabyRenderer::new);
      EntityRenderers.register(ModEntities.VAULT_GREEN_GUMMY_SOLDIER, ctx -> new VaultGummySoldierRenderer(ctx, VaultGummySoldierRenderer.Color.GREEN));
      EntityRenderers.register(ModEntities.VAULT_BLUE_GUMMY_SOLDIER, ctx -> new VaultGummySoldierRenderer(ctx, VaultGummySoldierRenderer.Color.BLUE));
      EntityRenderers.register(ModEntities.VAULT_YELLOW_GUMMY_SOLDIER, ctx -> new VaultGummySoldierRenderer(ctx, VaultGummySoldierRenderer.Color.YELLOW));
      EntityRenderers.register(ModEntities.VAULT_RED_GUMMY_SOLDIER, ctx -> new VaultGummySoldierRenderer(ctx, VaultGummySoldierRenderer.Color.RED));
      EntityRenderers.register(ModEntities.WINTER_WOLF, WinterWolfRenderer::new);
      EntityRenderers.register(ModEntities.SHIVER, ShiverRenderer::new);
      EntityRenderers.register(ModEntities.VAULT_HORSE, VaultHorseRenderer::new);
      EntityRenderers.register(ModEntities.VAULT_DOOD, VaultDoodRenderer::new);
      EntityRenderers.register(ModEntities.MONSTER_EYE, MonsterEyeRenderer::new);
      EntityRenderers.register(ModEntities.ROBOT, RobotRenderer::new);
      EntityRenderers.register(ModEntities.BLUE_BLAZE, BlueBlazeRenderer::new);
      EntityRenderers.register(ModEntities.BOOGIEMAN, BoogiemanRenderer::new);
      EntityRenderers.register(ModEntities.AGGRESSIVE_COW_BOSS, AggressiveCowBossRenderer::new);
      EntityRenderers.register(ModEntities.EYESORE, EyesoreRenderer::new);
      EntityRenderers.register(ModEntities.EYESTALK, EyestalkRenderer::new);
      EntityRenderers.register(ModEntities.DOLL_MINI_ME, DollMiniMeRenderer::new);
      EntityRenderers.register(ModEntities.DRILL_ARROW, TippableArrowRenderer::new);
      EntityRenderers.register(ModEntities.EFFECT_CLOUD, EffectCloudRenderer::new);
      EntityRenderers.register(ModEntities.VAULT_SAND, ItemEntityRenderer::new);
      EntityRenderers.register(ModEntities.FLOATING_ITEM, ItemEntityRenderer::new);
      EntityRenderers.register(ModEntities.FLOATING_ALTAR_ITEM, ItemEntityRenderer::new);
      EntityRenderers.register(ModEntities.EYESORE_FIREBALL, rm -> new ThrownItemRenderer(rm, 2.0F, true));
      EntityRenderers.register(ModEntities.ELITE_ZOMBIE, EliteZombieRenderer::new);
      EntityRenderers.register(ModEntities.ELITE_HUSK, EliteHuskRenderer::new);
      EntityRenderers.register(ModEntities.ELITE_DROWNED, EliteDrownedRenderer::new);
      EntityRenderers.register(ModEntities.ELITE_SPIDER, EliteSpiderRenderer::new);
      EntityRenderers.register(ModEntities.ELITE_SKELETON, EliteSkeletonRenderer::new);
      EntityRenderers.register(ModEntities.ELITE_STRAY, EliteStrayRenderer::new);
      EntityRenderers.register(ModEntities.ELITE_WITHER_SKELETON, EliteWitherSkeletonRenderer::new);
      EntityRenderers.register(ModEntities.ELITE_ENDERMAN, EliteEndermanRenderer::new);
      EntityRenderers.register(ModEntities.ELITE_WITCH, EliteWitchRenderer::new);
      EntityRenderers.register(ModEntities.T0_SKELETON_PIRATE, Tier0SkeletonPirateRenderer::new);
      EntityRenderers.register(ModEntities.T1_SKELETON_PIRATE, Tier1SkeletonPirateRenderer::new);
      EntityRenderers.register(ModEntities.T2_SKELETON_PIRATE, Tier2SkeletonPirateRenderer::new);
      EntityRenderers.register(ModEntities.T3_SKELETON_PIRATE, Tier3SkeletonPirateRenderer::new);
      EntityRenderers.register(ModEntities.T4_SKELETON_PIRATE, Tier4SkeletonPirateRenderer::new);
      EntityRenderers.register(ModEntities.T5_SKELETON_PIRATE, Tier5SkeletonPirateRenderer::new);
      EntityRenderers.register(ModEntities.T0_WINTERWALKER, Tier0WinterwalkerRenderer::new);
      EntityRenderers.register(ModEntities.T1_WINTERWALKER, Tier1WinterwalkerRenderer::new);
      EntityRenderers.register(ModEntities.T2_WINTERWALKER, Tier2WinterwalkerRenderer::new);
      EntityRenderers.register(ModEntities.T3_WINTERWALKER, Tier3WinterwalkerRenderer::new);
      EntityRenderers.register(ModEntities.T4_WINTERWALKER, Tier4WinterwalkerRenderer::new);
      EntityRenderers.register(ModEntities.T5_WINTERWALKER, Tier5WinterwalkerRenderer::new);
      EntityRenderers.register(ModEntities.T0_OVERGROWN_ZOMBIE, Tier0OvergrownZombieRenderer::new);
      EntityRenderers.register(ModEntities.T1_OVERGROWN_ZOMBIE, Tier1OvergrownZombieRenderer::new);
      EntityRenderers.register(ModEntities.T2_OVERGROWN_ZOMBIE, Tier2OvergrownZombieRenderer::new);
      EntityRenderers.register(ModEntities.T3_OVERGROWN_ZOMBIE, Tier3OvergrownZombieRenderer::new);
      EntityRenderers.register(ModEntities.T4_OVERGROWN_ZOMBIE, Tier4OvergrownZombieRenderer::new);
      EntityRenderers.register(ModEntities.T5_OVERGROWN_ZOMBIE, Tier5OvergrownZombieRenderer::new);
      EntityRenderers.register(ModEntities.T0_MUMMY, Tier0MummyRenderer::new);
      EntityRenderers.register(ModEntities.T1_MUMMY, Tier1MummyRenderer::new);
      EntityRenderers.register(ModEntities.T2_MUMMY, Tier2MummyRenderer::new);
      EntityRenderers.register(ModEntities.T0_MUSHROOM, Tier0MushroomRenderer::new);
      EntityRenderers.register(ModEntities.T1_MUSHROOM, Tier1MushroomRenderer::new);
      EntityRenderers.register(ModEntities.T2_MUSHROOM, Tier2MushroomRenderer::new);
      EntityRenderers.register(ModEntities.T3_MUSHROOM, Tier3MushroomRenderer::new);
      EntityRenderers.register(ModEntities.T4_MUSHROOM, Tier4MushroomRenderer::new);
      EntityRenderers.register(ModEntities.T5_MUSHROOM, Tier5MushroomRenderer::new);
      EntityRenderers.register(ModEntities.DEATHCAP, DeathcapRenderer::new);
      EntityRenderers.register(ModEntities.SMOLCAP, SmolcapRenderer::new);
      EntityRenderers.register(ModEntities.T0_MINER_ZOMBIE, Tier0MinerZombieRenderer::new);
      EntityRenderers.register(ModEntities.T1_MINER_ZOMBIE, Tier1MinerZombieRenderer::new);
      EntityRenderers.register(ModEntities.T2_MINER_ZOMBIE, Tier2MinerZombieRenderer::new);
      EntityRenderers.register(ModEntities.T3_MINER_ZOMBIE, Tier3MinerZombieRenderer::new);
      EntityRenderers.register(ModEntities.T4_MINER_ZOMBIE, Tier4MinerZombieRenderer::new);
      EntityRenderers.register(ModEntities.T5_MINER_ZOMBIE, Tier5MinerZombieRenderer::new);
      EntityRenderers.register(ModEntities.DEEP_DARK_ZOMBIE, DeepDarkZombieRenderer::new);
      EntityRenderers.register(ModEntities.DEEP_DARK_SKELETON, DeepDarkSkeletonRenderer::new);
      EntityRenderers.register(ModEntities.DEEP_DARK_PIGLIN, DeepDarkPiglinRenderer::new);
      EntityRenderers.register(ModEntities.DEEP_DARK_SILVERFISH, DeepDarkSilverfishRenderer::new);
      EntityRenderers.register(ModEntities.DEEP_DARK_HORROR, DeepDarkHorrorRenderer::new);
      EntityRenderers.register(ModEntities.T1_CREEPER, Tier1CreeperRenderer::new);
      EntityRenderers.register(ModEntities.T1_DROWNED, Tier1DrownedRenderer::new);
      EntityRenderers.register(ModEntities.T1_ENDERMAN, Tier1EndermanRenderer::new);
      EntityRenderers.register(ModEntities.T1_HUSK, Tier1HuskRenderer::new);
      EntityRenderers.register(ModEntities.T1_PIGLIN, Tier1PiglinRenderer::new);
      EntityRenderers.register(ModEntities.T1_SKELETON, Tier1SkeletonRenderer::new);
      EntityRenderers.register(ModEntities.T1_STRAY, Tier1StrayRenderer::new);
      EntityRenderers.register(ModEntities.T1_WITHER_SKELETON, Tier1WitherSkeletonRenderer::new);
      EntityRenderers.register(ModEntities.T1_ZOMBIE, Tier1ZombieRenderer::new);
      EntityRenderers.register(ModEntities.T2_CREEPER, Tier2CreeperRenderer::new);
      EntityRenderers.register(ModEntities.T2_DROWNED, Tier2DrownedRenderer::new);
      EntityRenderers.register(ModEntities.T2_ENDERMAN, Tier2EndermanRenderer::new);
      EntityRenderers.register(ModEntities.T2_HUSK, Tier2HuskRenderer::new);
      EntityRenderers.register(ModEntities.T2_PIGLIN, Tier2PiglinRenderer::new);
      EntityRenderers.register(ModEntities.T2_SKELETON, Tier2SkeletonRenderer::new);
      EntityRenderers.register(ModEntities.T2_STRAY, Tier2StrayRenderer::new);
      EntityRenderers.register(ModEntities.T2_WITHER_SKELETON, Tier2WitherSkeletonRenderer::new);
      EntityRenderers.register(ModEntities.T2_ZOMBIE, Tier2ZombieRenderer::new);
      EntityRenderers.register(ModEntities.T3_CREEPER, Tier3CreeperRenderer::new);
      EntityRenderers.register(ModEntities.T3_DROWNED, Tier3DrownedRenderer::new);
      EntityRenderers.register(ModEntities.T3_ENDERMAN, Tier3EndermanRenderer::new);
      EntityRenderers.register(ModEntities.T3_HUSK, Tier3HuskRenderer::new);
      EntityRenderers.register(ModEntities.T3_SKELETON, Tier3SkeletonRenderer::new);
      EntityRenderers.register(ModEntities.T3_STRAY, Tier3StrayRenderer::new);
      EntityRenderers.register(ModEntities.T3_WITHER_SKELETON, Tier3WitherSkeletonRenderer::new);
      EntityRenderers.register(ModEntities.T3_PIGLIN, Tier3PiglinRenderer::new);
      EntityRenderers.register(ModEntities.T3_ZOMBIE, Tier3ZombieRenderer::new);
      EntityRenderers.register(ModEntities.SPIRIT, SpiritRenderer::new);
      EntityRenderers.register(ModEntities.ETERNAL_SPIRIT, EternalSpiritRenderer::new);
      EntityRenderers.register(ModEntities.BRICK, ThrownItemRenderer::new);
      EntityRenderers.register(ModEntities.THROWN_JAVELIN, ThrownJavelinRenderer::new);
      EntityRenderers.register(ModEntities.ELIXIR_ORB, ElixirOrbRenderer::new);
      EntityRenderers.register(ModEntities.SMITE_ABILITY_BOLT, AbstractSmiteAbility.SmiteBoltRenderer::new);
      EntityRenderers.register(ModEntities.WARP_ARROW, DashWarpAbility.WarpArrowRenderer::new);
   }
}
