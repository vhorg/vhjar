package iskallia.vault.init;

import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.renderer.AggressiveCowBossRenderer;
import iskallia.vault.entity.renderer.ArenaTrackerRenderer;
import iskallia.vault.entity.renderer.BlueBlazeRenderer;
import iskallia.vault.entity.renderer.BoogiemanRenderer;
import iskallia.vault.entity.renderer.DollMiniMeRenderer;
import iskallia.vault.entity.renderer.EffectCloudRenderer;
import iskallia.vault.entity.renderer.EtchingVendorRenderer;
import iskallia.vault.entity.renderer.EternalRenderer;
import iskallia.vault.entity.renderer.FighterRenderer;
import iskallia.vault.entity.renderer.MonsterEyeRenderer;
import iskallia.vault.entity.renderer.RobotRenderer;
import iskallia.vault.entity.renderer.SpiritRenderer;
import iskallia.vault.entity.renderer.TreasureGoblinRenderer;
import iskallia.vault.entity.renderer.VaultGuardianRenderer;
import iskallia.vault.entity.renderer.VaultGummySoldierRenderer;
import iskallia.vault.entity.renderer.VaultSpiderBabyRenderer;
import iskallia.vault.entity.renderer.VaultSpiderRenderer;
import iskallia.vault.entity.renderer.WinterWolfRenderer;
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
import iskallia.vault.entity.renderer.tier1.Tier1CreeperRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1DrownedRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1EndermanRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1HuskRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1PiglinRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1SkeletonRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1StrayRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1WitherSkeletonRenderer;
import iskallia.vault.entity.renderer.tier1.Tier1ZombieRenderer;
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
      EntityRenderers.register(ModEntities.ARENA_TRACKER, ArenaTrackerRenderer::new);
      EntityRenderers.register(ModEntities.VAULT_GUARDIAN, VaultGuardianRenderer::new);
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
      EntityRenderers.register(ModEntities.T1_CREEPER, Tier1CreeperRenderer::new);
      EntityRenderers.register(ModEntities.T1_DROWNED, Tier1DrownedRenderer::new);
      EntityRenderers.register(ModEntities.T1_ENDERMAN, Tier1EndermanRenderer::new);
      EntityRenderers.register(ModEntities.T1_HUSK, Tier1HuskRenderer::new);
      EntityRenderers.register(ModEntities.T1_PIGLIN, Tier1PiglinRenderer::new);
      EntityRenderers.register(ModEntities.T1_SKELETON, Tier1SkeletonRenderer::new);
      EntityRenderers.register(ModEntities.T1_STRAY, Tier1StrayRenderer::new);
      EntityRenderers.register(ModEntities.T1_WITHER_SKELETON, Tier1WitherSkeletonRenderer::new);
      EntityRenderers.register(ModEntities.T1_ZOMBIE, Tier1ZombieRenderer::new);
      EntityRenderers.register(ModEntities.SPIRIT, SpiritRenderer::new);
      EntityRenderers.register(ModEntities.BRICK, ThrownItemRenderer::new);
   }
}
