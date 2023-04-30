package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.IPlayerSkinHolder;
import iskallia.vault.entity.entity.AggressiveCowBossEntity;
import iskallia.vault.entity.entity.AggressiveCowEntity;
import iskallia.vault.entity.entity.ArenaBossEntity;
import iskallia.vault.entity.entity.BlueBlazeEntity;
import iskallia.vault.entity.entity.BoogiemanEntity;
import iskallia.vault.entity.entity.DollMiniMeEntity;
import iskallia.vault.entity.entity.DrillArrowEntity;
import iskallia.vault.entity.entity.EffectCloudEntity;
import iskallia.vault.entity.entity.ElixirOrbEntity;
import iskallia.vault.entity.entity.EtchingVendorEntity;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.entity.EternalSpiritEntity;
import iskallia.vault.entity.entity.FighterEntity;
import iskallia.vault.entity.entity.FloatingGodAltarItemEntity;
import iskallia.vault.entity.entity.FloatingItemEntity;
import iskallia.vault.entity.entity.MonsterEyeEntity;
import iskallia.vault.entity.entity.RobotEntity;
import iskallia.vault.entity.entity.ShiverEntity;
import iskallia.vault.entity.entity.SpiritEntity;
import iskallia.vault.entity.entity.TreasureGoblinEntity;
import iskallia.vault.entity.entity.VaultDoodEntity;
import iskallia.vault.entity.entity.VaultFighterEntity;
import iskallia.vault.entity.entity.VaultGuardianEntity;
import iskallia.vault.entity.entity.VaultGummySoldier;
import iskallia.vault.entity.entity.VaultHorseEntity;
import iskallia.vault.entity.entity.VaultSandEntity;
import iskallia.vault.entity.entity.VaultSpiderBabyEntity;
import iskallia.vault.entity.entity.VaultSpiderEntity;
import iskallia.vault.entity.entity.VaultThrownJavelin;
import iskallia.vault.entity.entity.WinterWolfEntity;
import iskallia.vault.entity.entity.deepdark.DeepDarkHorrorEntity;
import iskallia.vault.entity.entity.deepdark.DeepDarkPiglinEntity;
import iskallia.vault.entity.entity.deepdark.DeepDarkSilverfishEntity;
import iskallia.vault.entity.entity.deepdark.DeepDarkSkeletonEntity;
import iskallia.vault.entity.entity.deepdark.DeepDarkZombieEntity;
import iskallia.vault.entity.entity.elite.EliteDrownedEntity;
import iskallia.vault.entity.entity.elite.EliteHuskEntity;
import iskallia.vault.entity.entity.elite.EliteSpiderEntity;
import iskallia.vault.entity.entity.elite.EliteStrayEntity;
import iskallia.vault.entity.entity.elite.EliteWitherSkeleton;
import iskallia.vault.entity.entity.elite.EliteZombieEntity;
import iskallia.vault.entity.entity.eyesore.EyesoreEntity;
import iskallia.vault.entity.entity.eyesore.EyesoreFireballEntity;
import iskallia.vault.entity.entity.eyesore.EyestalkEntity;
import iskallia.vault.entity.entity.guardian.FixedArrowEntity;
import iskallia.vault.entity.entity.guardian.GuardianType;
import iskallia.vault.entity.entity.miner_zombie.Tier0MinerZombieEntity;
import iskallia.vault.entity.entity.miner_zombie.Tier1MinerZombieEntity;
import iskallia.vault.entity.entity.miner_zombie.Tier2MinerZombieEntity;
import iskallia.vault.entity.entity.miner_zombie.Tier3MinerZombieEntity;
import iskallia.vault.entity.entity.miner_zombie.Tier4MinerZombieEntity;
import iskallia.vault.entity.entity.miner_zombie.Tier5MinerZombieEntity;
import iskallia.vault.entity.entity.mummy.Tier0MummyEntity;
import iskallia.vault.entity.entity.mummy.Tier1MummyEntity;
import iskallia.vault.entity.entity.mummy.Tier2MummyEntity;
import iskallia.vault.entity.entity.mushroom.DeathcapEntity;
import iskallia.vault.entity.entity.mushroom.MushroomEntity;
import iskallia.vault.entity.entity.mushroom.SmolcapEntity;
import iskallia.vault.entity.entity.mushroom.Tier0MushroomEntity;
import iskallia.vault.entity.entity.mushroom.Tier1MushroomEntity;
import iskallia.vault.entity.entity.mushroom.Tier2MushroomEntity;
import iskallia.vault.entity.entity.mushroom.Tier3MushroomEntity;
import iskallia.vault.entity.entity.mushroom.Tier4MushroomEntity;
import iskallia.vault.entity.entity.mushroom.Tier5MushroomEntity;
import iskallia.vault.entity.entity.overgrown_zombie.Tier0OvergrownZombieEntity;
import iskallia.vault.entity.entity.overgrown_zombie.Tier1OvergrownZombieEntity;
import iskallia.vault.entity.entity.overgrown_zombie.Tier2OvergrownZombieEntity;
import iskallia.vault.entity.entity.overgrown_zombie.Tier3OvergrownZombieEntity;
import iskallia.vault.entity.entity.overgrown_zombie.Tier4OvergrownZombieEntity;
import iskallia.vault.entity.entity.overgrown_zombie.Tier5OvergrownZombieEntity;
import iskallia.vault.entity.entity.skeleton_pirate.Tier0SkeletonPirateEntity;
import iskallia.vault.entity.entity.skeleton_pirate.Tier1SkeletonPirateEntity;
import iskallia.vault.entity.entity.skeleton_pirate.Tier2SkeletonPirateEntity;
import iskallia.vault.entity.entity.skeleton_pirate.Tier3SkeletonPirateEntity;
import iskallia.vault.entity.entity.skeleton_pirate.Tier4SkeletonPirateEntity;
import iskallia.vault.entity.entity.skeleton_pirate.Tier5SkeletonPirateEntity;
import iskallia.vault.entity.entity.tier1.Tier1CreeperEntity;
import iskallia.vault.entity.entity.tier1.Tier1DrownedEntity;
import iskallia.vault.entity.entity.tier1.Tier1EndermanEntity;
import iskallia.vault.entity.entity.tier1.Tier1HuskEntity;
import iskallia.vault.entity.entity.tier1.Tier1PiglinEntity;
import iskallia.vault.entity.entity.tier1.Tier1SkeletonEntity;
import iskallia.vault.entity.entity.tier1.Tier1StrayEntity;
import iskallia.vault.entity.entity.tier1.Tier1WitherSkeletonEntity;
import iskallia.vault.entity.entity.tier1.Tier1ZombieEntity;
import iskallia.vault.entity.entity.tier2.Tier2CreeperEntity;
import iskallia.vault.entity.entity.tier2.Tier2DrownedEntity;
import iskallia.vault.entity.entity.tier2.Tier2EndermanEntity;
import iskallia.vault.entity.entity.tier2.Tier2HuskEntity;
import iskallia.vault.entity.entity.tier2.Tier2PiglinEntity;
import iskallia.vault.entity.entity.tier2.Tier2SkeletonEntity;
import iskallia.vault.entity.entity.tier2.Tier2StrayEntity;
import iskallia.vault.entity.entity.tier2.Tier2WitherSkeletonEntity;
import iskallia.vault.entity.entity.tier2.Tier2ZombieEntity;
import iskallia.vault.entity.entity.tier3.Tier3CreeperEntity;
import iskallia.vault.entity.entity.tier3.Tier3DrownedEntity;
import iskallia.vault.entity.entity.tier3.Tier3EndermanEntity;
import iskallia.vault.entity.entity.tier3.Tier3HuskEntity;
import iskallia.vault.entity.entity.tier3.Tier3PiglinEntity;
import iskallia.vault.entity.entity.tier3.Tier3SkeletonEntity;
import iskallia.vault.entity.entity.tier3.Tier3StrayEntity;
import iskallia.vault.entity.entity.tier3.Tier3WitherSkeletonEntity;
import iskallia.vault.entity.entity.tier3.Tier3ZombieEntity;
import iskallia.vault.entity.entity.winterwalker.Tier0WinterwalkerEntity;
import iskallia.vault.entity.entity.winterwalker.Tier1WinterwalkerEntity;
import iskallia.vault.entity.entity.winterwalker.Tier2WinterwalkerEntity;
import iskallia.vault.entity.entity.winterwalker.Tier3WinterwalkerEntity;
import iskallia.vault.entity.entity.winterwalker.Tier4WinterwalkerEntity;
import iskallia.vault.entity.entity.winterwalker.Tier5WinterwalkerEntity;
import iskallia.vault.entity.model.FighterModel;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.ShiverModel;
import iskallia.vault.entity.model.TreasureGoblinModel;
import iskallia.vault.entity.model.VaultDoodModel;
import iskallia.vault.entity.model.VaultSpiderBabyModel;
import iskallia.vault.entity.model.WinterWolfModel;
import iskallia.vault.entity.model.deep_dark.DeepDarkHorrorModel;
import iskallia.vault.entity.model.deep_dark.DeepDarkPiglinModel;
import iskallia.vault.entity.model.deep_dark.DeepDarkSilverfishModel;
import iskallia.vault.entity.model.deep_dark.DeepDarkSkeletonModel;
import iskallia.vault.entity.model.deep_dark.DeepDarkZombieModel;
import iskallia.vault.entity.model.elite.EliteDrownedModel;
import iskallia.vault.entity.model.elite.EliteHuskModel;
import iskallia.vault.entity.model.elite.EliteSkeletonModel;
import iskallia.vault.entity.model.elite.EliteSpiderModel;
import iskallia.vault.entity.model.elite.EliteStrayModel;
import iskallia.vault.entity.model.elite.EliteWitherSkeletonModel;
import iskallia.vault.entity.model.elite.EliteZombieModel;
import iskallia.vault.entity.model.eyesore.EyesoreModel;
import iskallia.vault.entity.model.eyesore.EyestalkModel;
import iskallia.vault.entity.model.miner_zombie.Tier0MinerZombieModel;
import iskallia.vault.entity.model.miner_zombie.Tier1MinerZombieModel;
import iskallia.vault.entity.model.miner_zombie.Tier2MinerZombieModel;
import iskallia.vault.entity.model.miner_zombie.Tier3MinerZombieModel;
import iskallia.vault.entity.model.miner_zombie.Tier4MinerZombieModel;
import iskallia.vault.entity.model.miner_zombie.Tier5MinerZombieModel;
import iskallia.vault.entity.model.mummy.Tier0MummyModel;
import iskallia.vault.entity.model.mummy.Tier1MummyModel;
import iskallia.vault.entity.model.mummy.Tier2MummyModel;
import iskallia.vault.entity.model.mushroom.DeathcapModel;
import iskallia.vault.entity.model.mushroom.SmolcapModel;
import iskallia.vault.entity.model.mushroom.Tier0MushroomModel;
import iskallia.vault.entity.model.mushroom.Tier1MushroomModel;
import iskallia.vault.entity.model.mushroom.Tier2MushroomModel;
import iskallia.vault.entity.model.mushroom.Tier3MushroomModel;
import iskallia.vault.entity.model.mushroom.Tier4MushroomModel;
import iskallia.vault.entity.model.mushroom.Tier5MushroomModel;
import iskallia.vault.entity.model.overgrown_zombie.Tier0OvergrownZombieModel;
import iskallia.vault.entity.model.overgrown_zombie.Tier1OvergrownZombieModel;
import iskallia.vault.entity.model.overgrown_zombie.Tier2OvergrownZombieModel;
import iskallia.vault.entity.model.overgrown_zombie.Tier3OvergrownZombieModel;
import iskallia.vault.entity.model.overgrown_zombie.Tier4OvergrownZombieModel;
import iskallia.vault.entity.model.overgrown_zombie.Tier5OvergrownZombieModel;
import iskallia.vault.entity.model.skeleton_pirate.Tier0SkeletonPirateModel;
import iskallia.vault.entity.model.skeleton_pirate.Tier1SkeletonPirateModel;
import iskallia.vault.entity.model.skeleton_pirate.Tier2SkeletonPirateModel;
import iskallia.vault.entity.model.skeleton_pirate.Tier3SkeletonPirateModel;
import iskallia.vault.entity.model.skeleton_pirate.Tier4SkeletonPirateModel;
import iskallia.vault.entity.model.skeleton_pirate.Tier5SkeletonPirateModel;
import iskallia.vault.entity.model.tier2.Tier2CreeperModel;
import iskallia.vault.entity.model.tier2.Tier2EndermanModel;
import iskallia.vault.entity.model.tier2.Tier2HuskModel;
import iskallia.vault.entity.model.tier3.Tier3CreeperModel;
import iskallia.vault.entity.model.tier3.Tier3DrownedModel;
import iskallia.vault.entity.model.tier3.Tier3EndermanModel;
import iskallia.vault.entity.model.tier3.Tier3HuskModel;
import iskallia.vault.entity.model.tier3.Tier3PiglinModel;
import iskallia.vault.entity.model.tier3.Tier3SkeletonModel;
import iskallia.vault.entity.model.tier3.Tier3StrayModel;
import iskallia.vault.entity.model.tier3.Tier3WitherSkeletonModel;
import iskallia.vault.entity.model.tier3.Tier3ZombieModel;
import iskallia.vault.entity.model.winterwalker.Tier0WinterwalkerModel;
import iskallia.vault.entity.model.winterwalker.Tier1WinterwalkerModel;
import iskallia.vault.entity.model.winterwalker.Tier2WinterwalkerModel;
import iskallia.vault.entity.model.winterwalker.Tier3WinterwalkerModel;
import iskallia.vault.entity.model.winterwalker.Tier4WinterwalkerModel;
import iskallia.vault.entity.model.winterwalker.Tier5WinterwalkerModel;
import iskallia.vault.entity.renderer.elite.EliteEnderOrnamentLayer;
import iskallia.vault.skill.ability.effect.DashWarpAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractSmiteAbility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import org.jetbrains.annotations.NotNull;

public class ModEntities {
   public static final DeferredRegister<DataSerializerEntry> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(Keys.DATA_SERIALIZERS, "the_vault");
   public static List<EntityType<VaultFighterEntity>> VAULT_FIGHTER_TYPES = new ArrayList<>();
   public static EntityType<FighterEntity> FIGHTER;
   public static EntityType<ArenaBossEntity> ARENA_BOSS;
   public static EntityType<VaultGuardianEntity> BRUISER_GUARDIAN;
   public static EntityType<VaultGuardianEntity> ARBALIST_GUARDIAN;
   public static EntityType<EternalEntity> ETERNAL;
   public static EntityType<TreasureGoblinEntity> TREASURE_GOBLIN;
   public static EntityType<AggressiveCowEntity> AGGRESSIVE_COW;
   public static EntityType<EtchingVendorEntity> ETCHING_VENDOR;
   public static EntityType<VaultSpiderEntity> VAULT_SPIDER;
   public static EntityType<VaultSpiderBabyEntity> VAULT_SPIDER_BABY;
   public static EntityType<VaultGummySoldier> VAULT_GREEN_GUMMY_SOLDIER;
   public static EntityType<VaultGummySoldier> VAULT_BLUE_GUMMY_SOLDIER;
   public static EntityType<VaultGummySoldier> VAULT_YELLOW_GUMMY_SOLDIER;
   public static EntityType<VaultGummySoldier> VAULT_RED_GUMMY_SOLDIER;
   public static EntityType<WinterWolfEntity> WINTER_WOLF;
   public static EntityType<VaultHorseEntity> VAULT_HORSE;
   public static EntityType<ShiverEntity> SHIVER;
   public static EntityType<VaultDoodEntity> VAULT_DOOD;
   public static EntityType<MonsterEyeEntity> MONSTER_EYE;
   public static EntityType<RobotEntity> ROBOT;
   public static EntityType<BlueBlazeEntity> BLUE_BLAZE;
   public static EntityType<BoogiemanEntity> BOOGIEMAN;
   public static EntityType<AggressiveCowBossEntity> AGGRESSIVE_COW_BOSS;
   public static EntityType<EyesoreEntity> EYESORE;
   public static EntityType<EyestalkEntity> EYESTALK;
   public static EntityType<DollMiniMeEntity> DOLL_MINI_ME;
   public static EntityType<EliteZombieEntity> ELITE_ZOMBIE;
   public static EntityType<EliteHuskEntity> ELITE_HUSK;
   public static EntityType<EliteDrownedEntity> ELITE_DROWNED;
   public static EntityType<EliteSpiderEntity> ELITE_SPIDER;
   public static EntityType<Skeleton> ELITE_SKELETON;
   public static EntityType<EliteStrayEntity> ELITE_STRAY;
   public static EntityType<EliteWitherSkeleton> ELITE_WITHER_SKELETON;
   public static EntityType<EnderMan> ELITE_ENDERMAN;
   public static EntityType<Witch> ELITE_WITCH;
   public static EntityType<Tier0SkeletonPirateEntity> T0_SKELETON_PIRATE;
   public static EntityType<Tier1SkeletonPirateEntity> T1_SKELETON_PIRATE;
   public static EntityType<Tier2SkeletonPirateEntity> T2_SKELETON_PIRATE;
   public static EntityType<Tier3SkeletonPirateEntity> T3_SKELETON_PIRATE;
   public static EntityType<Tier4SkeletonPirateEntity> T4_SKELETON_PIRATE;
   public static EntityType<Tier5SkeletonPirateEntity> T5_SKELETON_PIRATE;
   public static EntityType<Tier0WinterwalkerEntity> T0_WINTERWALKER;
   public static EntityType<Tier1WinterwalkerEntity> T1_WINTERWALKER;
   public static EntityType<Tier2WinterwalkerEntity> T2_WINTERWALKER;
   public static EntityType<Tier3WinterwalkerEntity> T3_WINTERWALKER;
   public static EntityType<Tier4WinterwalkerEntity> T4_WINTERWALKER;
   public static EntityType<Tier5WinterwalkerEntity> T5_WINTERWALKER;
   public static EntityType<Tier0OvergrownZombieEntity> T0_OVERGROWN_ZOMBIE;
   public static EntityType<Tier1OvergrownZombieEntity> T1_OVERGROWN_ZOMBIE;
   public static EntityType<Tier2OvergrownZombieEntity> T2_OVERGROWN_ZOMBIE;
   public static EntityType<Tier3OvergrownZombieEntity> T3_OVERGROWN_ZOMBIE;
   public static EntityType<Tier4OvergrownZombieEntity> T4_OVERGROWN_ZOMBIE;
   public static EntityType<Tier5OvergrownZombieEntity> T5_OVERGROWN_ZOMBIE;
   public static EntityType<Tier0MummyEntity> T0_MUMMY;
   public static EntityType<Tier1MummyEntity> T1_MUMMY;
   public static EntityType<Tier2MummyEntity> T2_MUMMY;
   public static EntityType<DeepDarkZombieEntity> DEEP_DARK_ZOMBIE;
   public static EntityType<DeepDarkSkeletonEntity> DEEP_DARK_SKELETON;
   public static EntityType<DeepDarkPiglinEntity> DEEP_DARK_PIGLIN;
   public static EntityType<DeepDarkSilverfishEntity> DEEP_DARK_SILVERFISH;
   public static EntityType<DeepDarkHorrorEntity> DEEP_DARK_HORROR;
   public static EntityType<Tier0MushroomEntity> T0_MUSHROOM;
   public static EntityType<Tier1MushroomEntity> T1_MUSHROOM;
   public static EntityType<Tier2MushroomEntity> T2_MUSHROOM;
   public static EntityType<Tier3MushroomEntity> T3_MUSHROOM;
   public static EntityType<Tier4MushroomEntity> T4_MUSHROOM;
   public static EntityType<Tier5MushroomEntity> T5_MUSHROOM;
   public static EntityType<DeathcapEntity> DEATHCAP;
   public static EntityType<SmolcapEntity> SMOLCAP;
   public static EntityType<Tier0MinerZombieEntity> T0_MINER_ZOMBIE;
   public static EntityType<Tier1MinerZombieEntity> T1_MINER_ZOMBIE;
   public static EntityType<Tier2MinerZombieEntity> T2_MINER_ZOMBIE;
   public static EntityType<Tier3MinerZombieEntity> T3_MINER_ZOMBIE;
   public static EntityType<Tier4MinerZombieEntity> T4_MINER_ZOMBIE;
   public static EntityType<Tier5MinerZombieEntity> T5_MINER_ZOMBIE;
   public static EntityType<Tier1CreeperEntity> T1_CREEPER;
   public static EntityType<Tier1DrownedEntity> T1_DROWNED;
   public static EntityType<Tier1EndermanEntity> T1_ENDERMAN;
   public static EntityType<Tier1HuskEntity> T1_HUSK;
   public static EntityType<Tier1PiglinEntity> T1_PIGLIN;
   public static EntityType<Tier1SkeletonEntity> T1_SKELETON;
   public static EntityType<Tier1StrayEntity> T1_STRAY;
   public static EntityType<Tier1WitherSkeletonEntity> T1_WITHER_SKELETON;
   public static EntityType<Tier1ZombieEntity> T1_ZOMBIE;
   public static EntityType<Tier2CreeperEntity> T2_CREEPER;
   public static EntityType<Tier2DrownedEntity> T2_DROWNED;
   public static EntityType<Tier2EndermanEntity> T2_ENDERMAN;
   public static EntityType<Tier2HuskEntity> T2_HUSK;
   public static EntityType<Tier2PiglinEntity> T2_PIGLIN;
   public static EntityType<Tier2SkeletonEntity> T2_SKELETON;
   public static EntityType<Tier2StrayEntity> T2_STRAY;
   public static EntityType<Tier2WitherSkeletonEntity> T2_WITHER_SKELETON;
   public static EntityType<Tier2ZombieEntity> T2_ZOMBIE;
   public static EntityType<Tier3CreeperEntity> T3_CREEPER;
   public static EntityType<Tier3DrownedEntity> T3_DROWNED;
   public static EntityType<Tier3EndermanEntity> T3_ENDERMAN;
   public static EntityType<Tier3HuskEntity> T3_HUSK;
   public static EntityType<Tier3PiglinEntity> T3_PIGLIN;
   public static EntityType<Tier3SkeletonEntity> T3_SKELETON;
   public static EntityType<Tier3StrayEntity> T3_STRAY;
   public static EntityType<Tier3WitherSkeletonEntity> T3_WITHER_SKELETON;
   public static EntityType<Tier3ZombieEntity> T3_ZOMBIE;
   public static EntityType<DrillArrowEntity> DRILL_ARROW;
   public static EntityType<EffectCloudEntity> EFFECT_CLOUD;
   public static EntityType<VaultSandEntity> VAULT_SAND;
   public static EntityType<FloatingItemEntity> FLOATING_ITEM;
   public static EntityType<FloatingGodAltarItemEntity> FLOATING_ALTAR_ITEM;
   public static EntityType<EyesoreFireballEntity> EYESORE_FIREBALL;
   public static EntityType<FixedArrowEntity> FIXED_ARROW;
   public static EntityType<ElixirOrbEntity> ELIXIR_ORB;
   public static EntityType<AbstractSmiteAbility.SmiteBolt> SMITE_ABILITY_BOLT;
   public static EntityType<SpiritEntity> SPIRIT;
   public static EntityType<EternalSpiritEntity> ETERNAL_SPIRIT;
   public static EntityType<FighterEntity.ThrowableBrick> BRICK;
   public static EntityType<VaultThrownJavelin> THROWN_JAVELIN;
   public static EntityType<DashWarpAbility.WarpArrow> WARP_ARROW;
   private static final Map<EntityType<? extends LivingEntity>, Supplier<net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder>> ATTRIBUTE_BUILDERS = new HashMap<>();

   public static void register(Register<EntityType<?>> event) {
      for (int i = 0; i < 10; i++) {
         VAULT_FIGHTER_TYPES.add(registerVaultFighter(i, event));
      }

      FIGHTER = registerLiving("fighter", Builder.of(FighterEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F), Zombie::createAttributes, event);
      ARENA_BOSS = registerLiving(
         "arena_boss", Builder.of(ArenaBossEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F), ArenaBossEntity::createAttributes, event
      );
      BRUISER_GUARDIAN = registerLiving(
         "bruiser_guardian",
         Builder.of((type, world) -> new VaultGuardianEntity(type, GuardianType.BRUISER, world), MobCategory.MONSTER).sized(0.88F, 2.36F),
         Zombie::createAttributes,
         event
      );
      ARBALIST_GUARDIAN = registerLiving(
         "arbalist_guardian",
         Builder.of((type, world) -> new VaultGuardianEntity(type, GuardianType.ARBALIST, world), MobCategory.MONSTER).sized(0.88F, 2.36F),
         Zombie::createAttributes,
         event
      );
      ETERNAL = registerLiving("eternal", Builder.of(EternalEntity::new, MobCategory.CREATURE).sized(0.6F, 1.95F), Zombie::createAttributes, event);
      TREASURE_GOBLIN = registerLiving(
         "treasure_goblin", Builder.of(TreasureGoblinEntity::new, MobCategory.CREATURE).sized(0.5F, 1.5F), Zombie::createAttributes, event
      );
      AGGRESSIVE_COW = registerLiving(
         "aggressive_cow",
         Builder.of(AggressiveCowEntity::new, MobCategory.MONSTER).sized(0.9F, 1.4F).clientTrackingRange(8),
         AggressiveCowEntity::createAttributes,
         event
      );
      ETCHING_VENDOR = registerLiving("etching_vendor", Builder.of(EtchingVendorEntity::new, MobCategory.MISC), Zombie::createAttributes, event);
      VAULT_SPIDER = registerLiving("vault_spider", Builder.of(VaultSpiderEntity::new, MobCategory.MONSTER).sized(0.7F, 0.5F), Spider::createAttributes, event);
      VAULT_SPIDER_BABY = registerLiving(
         "vault_spider_baby", Builder.of(VaultSpiderBabyEntity::new, MobCategory.MONSTER).sized(0.8F, 0.5F), Spider::createAttributes, event
      );
      VAULT_GREEN_GUMMY_SOLDIER = registerLiving(
         "vault_green_gummy_soldier",
         Builder.of(VaultGummySoldier::new, MobCategory.MONSTER).sized(0.72F, 1.95F).clientTrackingRange(8),
         VaultGummySoldier::createAttributes,
         event
      );
      VAULT_BLUE_GUMMY_SOLDIER = registerLiving(
         "vault_blue_gummy_soldier",
         Builder.of(VaultGummySoldier::new, MobCategory.MONSTER).sized(0.72F, 1.95F).clientTrackingRange(8),
         VaultGummySoldier::createAttributes,
         event
      );
      VAULT_YELLOW_GUMMY_SOLDIER = registerLiving(
         "vault_yellow_gummy_soldier",
         Builder.of(VaultGummySoldier::new, MobCategory.MONSTER).sized(0.72F, 1.95F).clientTrackingRange(8),
         VaultGummySoldier::createAttributes,
         event
      );
      VAULT_RED_GUMMY_SOLDIER = registerLiving(
         "vault_red_gummy_soldier",
         Builder.of(VaultGummySoldier::new, MobCategory.MONSTER).sized(0.72F, 1.95F).clientTrackingRange(8),
         VaultGummySoldier::createAttributes,
         event
      );
      WINTER_WOLF = registerLiving(
         "winter_wolf", Builder.of(WinterWolfEntity::new, MobCategory.MONSTER).sized(0.72F, 1.15F).clientTrackingRange(8), Wolf::createAttributes, event
      );
      VAULT_HORSE = registerLiving(
         "vault_horse",
         Builder.of(VaultHorseEntity::new, MobCategory.CREATURE).sized(1.0F, 1.6F).clientTrackingRange(10),
         VaultHorseEntity::createAttributes,
         event
      );
      SHIVER = registerLiving(
         "shiver",
         Builder.of(ShiverEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth() * 1.3F, EntityType.ZOMBIE.getHeight() * 1.3F)
            .clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      VAULT_DOOD = registerLiving(
         "vault_dood",
         Builder.of(VaultDoodEntity::new, MobCategory.MONSTER)
            .sized(EntityType.IRON_GOLEM.getWidth() * 0.7F, EntityType.IRON_GOLEM.getHeight() * 0.7F)
            .clientTrackingRange(8),
         IronGolem::createAttributes,
         event
      );
      MONSTER_EYE = registerLiving("monster_eye", Builder.of(MonsterEyeEntity::new, MobCategory.MONSTER).sized(4.08F, 4.08F), Zombie::createAttributes, event);
      ROBOT = registerLiving("robot", Builder.of(RobotEntity::new, MobCategory.MONSTER).sized(2.8F, 5.4F), Zombie::createAttributes, event);
      BLUE_BLAZE = registerLiving("blue_blaze", Builder.of(BlueBlazeEntity::new, MobCategory.MONSTER).sized(1.2F, 3.6F), Zombie::createAttributes, event);
      BOOGIEMAN = registerLiving("boogieman", Builder.of(BoogiemanEntity::new, MobCategory.MONSTER).sized(1.2F, 3.9F), Zombie::createAttributes, event);
      AGGRESSIVE_COW_BOSS = registerLiving(
         "aggressive_cow_boss",
         Builder.of(AggressiveCowBossEntity::new, MobCategory.MONSTER).sized(2.6999998F, 4.2F),
         AggressiveCowEntity::createAttributes,
         event
      );
      EYESORE = registerLiving("eyesore", Builder.of(EyesoreEntity::new, MobCategory.MONSTER).sized(5.5F, 5.5F), EyesoreEntity::createAttributes, event);
      EYESTALK = registerLiving("eyestalk", Builder.of(EyestalkEntity::new, MobCategory.MONSTER).sized(0.2F, 0.4F), EyestalkEntity::createAttributes, event);
      DOLL_MINI_ME = registerLiving(
         "doll_mini_me", Builder.of(DollMiniMeEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F), DollMiniMeEntity::createAttributes, event
      );
      ELITE_ZOMBIE = registerLiving(
         "elite_zombie", Builder.of(EliteZombieEntity::new, MobCategory.MONSTER).sized(0.72F, 2.85F).clientTrackingRange(8), Zombie::createAttributes, event
      );
      ELITE_HUSK = registerLiving(
         "elite_husk", Builder.of(EliteHuskEntity::new, MobCategory.MONSTER).sized(0.72F, 2.85F).clientTrackingRange(8), Zombie::createAttributes, event
      );
      ELITE_DROWNED = registerLiving(
         "elite_drowned", Builder.of(EliteDrownedEntity::new, MobCategory.MONSTER).sized(0.72F, 2.85F).clientTrackingRange(8), Zombie::createAttributes, event
      );
      ELITE_SPIDER = registerLiving(
         "elite_spider", Builder.of(EliteSpiderEntity::new, MobCategory.MONSTER).sized(1.68F, 2.0F).clientTrackingRange(8), Spider::createAttributes, event
      );
      ELITE_SKELETON = registerLiving(
         "elite_skeleton",
         Builder.of(Skeleton::new, MobCategory.MONSTER).fireImmune().sized(0.75F, 2.85F).clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      ELITE_STRAY = registerLiving(
         "elite_stray",
         Builder.of(EliteStrayEntity::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(0.72F, 2.39F)
            .immuneTo(new Block[]{Blocks.POWDER_SNOW})
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      ELITE_WITHER_SKELETON = registerLiving(
         "elite_wither_skeleton",
         Builder.of(EliteWitherSkeleton::new, MobCategory.MONSTER)
            .fireImmune()
            .immuneTo(new Block[]{Blocks.WITHER_ROSE})
            .sized(0.75F, 2.85F)
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      ELITE_ENDERMAN = registerLiving(
         "elite_enderman", Builder.of(EnderMan::new, MobCategory.MONSTER).sized(0.72F, 3.5F).clientTrackingRange(8), EnderMan::createAttributes, event
      );
      ELITE_WITCH = registerLiving(
         "elite_witch", Builder.of(Witch::new, MobCategory.MONSTER).sized(0.72F, 2.34F).clientTrackingRange(8), Witch::createAttributes, event
      );
      T0_SKELETON_PIRATE = registerLiving(
         "skeleton_pirate_t0",
         Builder.of(Tier0SkeletonPirateEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T1_SKELETON_PIRATE = registerLiving(
         "skeleton_pirate_t1",
         Builder.of(Tier1SkeletonPirateEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T2_SKELETON_PIRATE = registerLiving(
         "skeleton_pirate_t2",
         Builder.of(Tier2SkeletonPirateEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T3_SKELETON_PIRATE = registerLiving(
         "skeleton_pirate_t3",
         Builder.of(Tier3SkeletonPirateEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T4_SKELETON_PIRATE = registerLiving(
         "skeleton_pirate_t4",
         Builder.of(Tier4SkeletonPirateEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T5_SKELETON_PIRATE = registerLiving(
         "skeleton_pirate_t5",
         Builder.of(Tier5SkeletonPirateEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T0_WINTERWALKER = registerLiving(
         "winterwalker_t0",
         Builder.of(Tier0WinterwalkerEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T1_WINTERWALKER = registerLiving(
         "winterwalker_t1",
         Builder.of(Tier1WinterwalkerEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T2_WINTERWALKER = registerLiving(
         "winterwalker_t2",
         Builder.of(Tier2WinterwalkerEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T3_WINTERWALKER = registerLiving(
         "winterwalker_t3",
         Builder.of(Tier3WinterwalkerEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T4_WINTERWALKER = registerLiving(
         "winterwalker_t4",
         Builder.of(Tier4WinterwalkerEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T5_WINTERWALKER = registerLiving(
         "winterwalker_t5",
         Builder.of(Tier5WinterwalkerEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T0_OVERGROWN_ZOMBIE = registerLiving(
         "overgrown_zombie_t0",
         Builder.of(Tier0OvergrownZombieEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T1_OVERGROWN_ZOMBIE = registerLiving(
         "overgrown_zombie_t1",
         Builder.of(Tier1OvergrownZombieEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T2_OVERGROWN_ZOMBIE = registerLiving(
         "overgrown_zombie_t2",
         Builder.of(Tier2OvergrownZombieEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T3_OVERGROWN_ZOMBIE = registerLiving(
         "overgrown_zombie_t3",
         Builder.of(Tier3OvergrownZombieEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T4_OVERGROWN_ZOMBIE = registerLiving(
         "overgrown_zombie_t4",
         Builder.of(Tier4OvergrownZombieEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T5_OVERGROWN_ZOMBIE = registerLiving(
         "overgrown_zombie_t5",
         Builder.of(Tier5OvergrownZombieEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
            .clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T0_MUMMY = registerLiving(
         "mummy_t0",
         Builder.of(Tier0MummyEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T1_MUMMY = registerLiving(
         "mummy_t1",
         Builder.of(Tier1MummyEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T2_MUMMY = registerLiving(
         "mummy_t2",
         Builder.of(Tier2MummyEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      DEEP_DARK_ZOMBIE = registerLiving(
         "deep_dark_zombie",
         Builder.of(DeepDarkZombieEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      DEEP_DARK_SKELETON = registerLiving(
         "deep_dark_skeleton",
         Builder.of(DeepDarkSkeletonEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      DEEP_DARK_PIGLIN = registerLiving(
         "deep_dark_piglin",
         Builder.of(DeepDarkPiglinEntity::new, MobCategory.MONSTER).sized(EntityType.PIGLIN.getWidth(), EntityType.PIGLIN.getHeight()).clientTrackingRange(8),
         Piglin::createAttributes,
         event
      );
      DEEP_DARK_SILVERFISH = registerLiving(
         "deep_dark_silverfish",
         Builder.of(DeepDarkSilverfishEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SILVERFISH.getWidth(), EntityType.SILVERFISH.getHeight())
            .clientTrackingRange(8),
         Silverfish::createAttributes,
         event
      );
      DEEP_DARK_HORROR = registerLiving(
         "deep_dark_horror",
         Builder.of(DeepDarkHorrorEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth() * 1.6F, EntityType.ZOMBIE.getHeight() * 1.6F)
            .clientTrackingRange(8),
         DeepDarkHorrorEntity::createAttributes,
         event
      );
      T0_MUSHROOM = registerLiving(
         "mushroom_t0",
         Builder.of(Tier0MushroomEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         MushroomEntity::createAttributes,
         event
      );
      T1_MUSHROOM = registerLiving(
         "mushroom_t1",
         Builder.of(Tier1MushroomEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         MushroomEntity::createAttributes,
         event
      );
      T2_MUSHROOM = registerLiving(
         "mushroom_t2",
         Builder.of(Tier2MushroomEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         MushroomEntity::createAttributes,
         event
      );
      T3_MUSHROOM = registerLiving(
         "mushroom_t3",
         Builder.of(Tier3MushroomEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight() + 0.8F)
            .clientTrackingRange(8),
         MushroomEntity::createAttributes,
         event
      );
      T4_MUSHROOM = registerLiving(
         "mushroom_t4",
         Builder.of(Tier4MushroomEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight() + 0.8F)
            .clientTrackingRange(8),
         MushroomEntity::createAttributes,
         event
      );
      T5_MUSHROOM = registerLiving(
         "mushroom_t5",
         Builder.of(Tier5MushroomEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight() + 0.8F)
            .clientTrackingRange(8),
         MushroomEntity::createAttributes,
         event
      );
      DEATHCAP = registerLiving(
         "deathcap",
         Builder.of(DeathcapEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         MushroomEntity::createAttributes,
         event
      );
      SMOLCAP = registerLiving(
         "smolcap", Builder.of(SmolcapEntity::new, MobCategory.MONSTER).sized(0.875F, 0.4375F).clientTrackingRange(8), MushroomEntity::createAttributes, event
      );
      T0_MINER_ZOMBIE = registerLiving(
         "miner_zombie_t0",
         Builder.of(Tier0MinerZombieEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T1_MINER_ZOMBIE = registerLiving(
         "miner_zombie_t1",
         Builder.of(Tier1MinerZombieEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T2_MINER_ZOMBIE = registerLiving(
         "miner_zombie_t2",
         Builder.of(Tier2MinerZombieEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T3_MINER_ZOMBIE = registerLiving(
         "miner_zombie_t3",
         Builder.of(Tier3MinerZombieEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T4_MINER_ZOMBIE = registerLiving(
         "miner_zombie_t4",
         Builder.of(Tier4MinerZombieEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T5_MINER_ZOMBIE = registerLiving(
         "miner_zombie_t5",
         Builder.of(Tier5MinerZombieEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T1_CREEPER = registerLiving(
         "t1_creeper",
         Builder.of(Tier1CreeperEntity::new, MobCategory.MONSTER).sized(EntityType.CREEPER.getWidth(), EntityType.CREEPER.getHeight()).clientTrackingRange(8),
         Creeper::createAttributes,
         event
      );
      T1_DROWNED = registerLiving(
         "t1_drowned",
         Builder.of(Tier1DrownedEntity::new, MobCategory.MONSTER).sized(EntityType.DROWNED.getWidth(), EntityType.DROWNED.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T1_ENDERMAN = registerLiving(
         "t1_enderman",
         Builder.of(Tier1EndermanEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ENDERMAN.getWidth(), EntityType.ENDERMAN.getHeight())
            .clientTrackingRange(8),
         EnderMan::createAttributes,
         event
      );
      T1_HUSK = registerLiving(
         "t1_husk",
         Builder.of(Tier1HuskEntity::new, MobCategory.MONSTER).sized(EntityType.HUSK.getWidth(), EntityType.HUSK.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T1_PIGLIN = registerLiving(
         "t1_piglin",
         Builder.of(Tier1PiglinEntity::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(EntityType.PIGLIN.getWidth(), EntityType.PIGLIN.getHeight())
            .clientTrackingRange(8),
         Piglin::createAttributes,
         event
      );
      T1_SKELETON = registerLiving(
         "t1_skeleton",
         Builder.of(Tier1SkeletonEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T1_STRAY = registerLiving(
         "t1_stray",
         Builder.of(Tier1StrayEntity::new, MobCategory.MONSTER).sized(EntityType.STRAY.getWidth(), EntityType.STRAY.getHeight()).clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T1_WITHER_SKELETON = registerLiving(
         "t1_wither_skeleton",
         Builder.of(Tier1WitherSkeletonEntity::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(EntityType.WITHER_SKELETON.getWidth(), EntityType.WITHER_SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T1_ZOMBIE = registerLiving(
         "t1_zombie",
         Builder.of(Tier1ZombieEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T2_CREEPER = registerLiving(
         "t2_creeper",
         Builder.of(Tier2CreeperEntity::new, MobCategory.MONSTER).sized(EntityType.CREEPER.getWidth(), EntityType.CREEPER.getHeight()).clientTrackingRange(8),
         Creeper::createAttributes,
         event
      );
      T2_DROWNED = registerLiving(
         "t2_drowned",
         Builder.of(Tier2DrownedEntity::new, MobCategory.MONSTER).sized(EntityType.DROWNED.getWidth(), EntityType.DROWNED.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T2_ENDERMAN = registerLiving(
         "t2_enderman",
         Builder.of(Tier2EndermanEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ENDERMAN.getWidth(), EntityType.ENDERMAN.getHeight())
            .clientTrackingRange(8),
         EnderMan::createAttributes,
         event
      );
      T2_HUSK = registerLiving(
         "t2_husk",
         Builder.of(Tier2HuskEntity::new, MobCategory.MONSTER).sized(EntityType.HUSK.getWidth(), EntityType.HUSK.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T2_PIGLIN = registerLiving(
         "t2_piglin",
         Builder.of(Tier2PiglinEntity::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(EntityType.PIGLIN.getWidth(), EntityType.PIGLIN.getHeight())
            .clientTrackingRange(8),
         Piglin::createAttributes,
         event
      );
      T2_SKELETON = registerLiving(
         "t2_skeleton",
         Builder.of(Tier2SkeletonEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T2_STRAY = registerLiving(
         "t2_stray",
         Builder.of(Tier2StrayEntity::new, MobCategory.MONSTER).sized(EntityType.STRAY.getWidth(), EntityType.STRAY.getHeight()).clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T2_WITHER_SKELETON = registerLiving(
         "t2_wither_skeleton",
         Builder.of(Tier2WitherSkeletonEntity::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(EntityType.WITHER_SKELETON.getWidth(), EntityType.WITHER_SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T2_ZOMBIE = registerLiving(
         "t2_zombie",
         Builder.of(Tier2ZombieEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T3_CREEPER = registerLiving(
         "t3_creeper",
         Builder.of(Tier3CreeperEntity::new, MobCategory.MONSTER).sized(EntityType.CREEPER.getWidth(), EntityType.CREEPER.getHeight()).clientTrackingRange(8),
         Creeper::createAttributes,
         event
      );
      T3_DROWNED = registerLiving(
         "t3_drowned",
         Builder.of(Tier3DrownedEntity::new, MobCategory.MONSTER).sized(EntityType.DROWNED.getWidth(), EntityType.DROWNED.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T3_ENDERMAN = registerLiving(
         "t3_enderman",
         Builder.of(Tier3EndermanEntity::new, MobCategory.MONSTER)
            .sized(EntityType.ENDERMAN.getWidth(), EntityType.ENDERMAN.getHeight())
            .clientTrackingRange(8),
         EnderMan::createAttributes,
         event
      );
      T3_HUSK = registerLiving(
         "t3_husk",
         Builder.of(Tier3HuskEntity::new, MobCategory.MONSTER).sized(EntityType.HUSK.getWidth(), EntityType.HUSK.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      T3_PIGLIN = registerLiving(
         "t3_piglin",
         Builder.of(Tier3PiglinEntity::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(EntityType.PIGLIN.getWidth(), EntityType.PIGLIN.getHeight())
            .clientTrackingRange(8),
         Piglin::createAttributes,
         event
      );
      T3_SKELETON = registerLiving(
         "t3_skeleton",
         Builder.of(Tier3SkeletonEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T3_STRAY = registerLiving(
         "t3_stray",
         Builder.of(Tier3StrayEntity::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(EntityType.STRAY.getWidth(), EntityType.STRAY.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T3_WITHER_SKELETON = registerLiving(
         "t3_wither_skeleton",
         Builder.of(Tier3WitherSkeletonEntity::new, MobCategory.MONSTER)
            .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
            .clientTrackingRange(8),
         AbstractSkeleton::createAttributes,
         event
      );
      T3_ZOMBIE = registerLiving(
         "t3_zombie",
         Builder.of(Tier3ZombieEntity::new, MobCategory.MONSTER).sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).clientTrackingRange(8),
         Zombie::createAttributes,
         event
      );
      DRILL_ARROW = register("drill_arrow", Builder.of(DrillArrowEntity::new, MobCategory.MISC), event);
      EFFECT_CLOUD = register("effect_cloud", Builder.of(EffectCloudEntity::new, MobCategory.MISC), event);
      VAULT_SAND = register("vault_sand", Builder.of(VaultSandEntity::new, MobCategory.MISC), event);
      FLOATING_ITEM = register("floating_item", Builder.of(FloatingItemEntity::new, MobCategory.MISC), event);
      FLOATING_ALTAR_ITEM = register("floating_altar_item", Builder.of(FloatingGodAltarItemEntity::new, MobCategory.MISC), event);
      EYESORE_FIREBALL = register("eyesore_fireball", Builder.of(EyesoreFireballEntity::new, MobCategory.MISC), event);
      FIXED_ARROW = register(
         "fixed_arrow", Builder.of(FixedArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20), event
      );
      ELIXIR_ORB = register("elixir_orb", Builder.of(ElixirOrbEntity::new, MobCategory.MISC).sized(0.5F, 0.5F), event);
      SMITE_ABILITY_BOLT = register(
         "smite_ability_bolt",
         Builder.of((entityType, level) -> new AbstractSmiteAbility.SmiteBolt(entityType, level, false, -1864448), MobCategory.MISC),
         event
      );
      SPIRIT = registerLiving(
         "spirit", Builder.of(SpiritEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8F).clientTrackingRange(8), Witch::createAttributes, event
      );
      ETERNAL_SPIRIT = registerLiving(
         "eternal_spirit", Builder.of(EternalSpiritEntity::new, MobCategory.MISC).sized(0.6F, 1.8F).clientTrackingRange(8), Witch::createAttributes, event
      );
      BRICK = register(
         "brick", Builder.of(FighterEntity.ThrowableBrick::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10), event
      );
      THROWN_JAVELIN = register(
         "thrown_javelin", Builder.of(VaultThrownJavelin::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20), event
      );
      WARP_ARROW = register(
         "warp_arrow", Builder.of(DashWarpAbility.WarpArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20), event
      );
   }

   private static EntityType<VaultFighterEntity> registerVaultFighter(int count, Register<EntityType<?>> event) {
      return registerLiving(
         count > 0 ? "vault_fighter_" + count : "vault_fighter",
         Builder.of(VaultFighterEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F),
         Zombie::createAttributes,
         event
      );
   }

   private static <T extends Entity> EntityType<T> register(String name, Builder<T> builder, Register<EntityType<?>> event) {
      EntityType<T> entityType = builder.build(VaultMod.sId(name));
      event.getRegistry().register((EntityType)entityType.setRegistryName(VaultMod.id(name)));
      return entityType;
   }

   private static <T extends LivingEntity> EntityType<T> registerLiving(
      String name, Builder<T> builder, Supplier<net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder> attributes, Register<EntityType<?>> event
   ) {
      EntityType<T> entityType = register(name, builder, event);
      if (attributes != null) {
         ATTRIBUTE_BUILDERS.put(entityType, attributes);
      }

      return entityType;
   }

   public static void registerAttributes(EntityAttributeCreationEvent event) {
      ATTRIBUTE_BUILDERS.forEach((e, b) -> event.put(e, b.get().build()));
      ATTRIBUTE_BUILDERS.clear();
   }

   static {
      ENTITY_DATA_SERIALIZERS.register("optional_game_profile", () -> new DataSerializerEntry(IPlayerSkinHolder.OPTIONAL_GAME_PROFILE_SERIALIZER));
   }

   public static class Models {
      public static void registerLayers(RegisterLayerDefinitions event) {
         Supplier<LayerDefinition> outerArmorLayerSupplier = () -> LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(1.0F), 0.0F), 64, 32);
         Supplier<LayerDefinition> innerArmorLayerSupplier = () -> LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.5F), 0.0F), 64, 32);
         event.registerLayerDefinition(ModModelLayers.TREASURE_GOBLIN, TreasureGoblinModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.EYESORE, EyesoreModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.EYESTALK, EyestalkModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.ETERNAL, ModEntities.Models::getPlayerModelDefinition);
         event.registerLayerDefinition(ModModelLayers.STATUE, ModEntities.Models::getPlayerModelDefinition);
         event.registerLayerDefinition(ModModelLayers.FIGHTER, FighterModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.FIGHTER_T3, FighterModel::createBodyLayerT3);
         event.registerLayerDefinition(ModModelLayers.FIGHTER_T4, FighterModel::createBodyLayerT4);
         event.registerLayerDefinition(ModModelLayers.ELITE_ZOMBIE, EliteZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.ELITE_HUSK, EliteHuskModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.ELITE_DROWNED, EliteDrownedModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.ELITE_DROWNED_INNER_ARMOR, innerArmorLayerSupplier);
         event.registerLayerDefinition(ModModelLayers.ELITE_DROWNED_OUTER_ARMOR, outerArmorLayerSupplier);
         event.registerLayerDefinition(ModModelLayers.ELITE_SPIDER, EliteSpiderModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.ELITE_SKELETON, EliteSkeletonModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.ELITE_STRAY, EliteStrayModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.ELITE_WITHER_SKELETON, EliteWitherSkeletonModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.ELITE_ENDERMAN_ORNAMENT, EliteEnderOrnamentLayer.EnderOrnamentModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T2_HUSK, Tier2HuskModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T2_CREEPER, Tier2CreeperModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T2_ENDERMAN, Tier2EndermanModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_CREEPER, Tier3CreeperModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_DROWNED, Tier3DrownedModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_ENDERMAN, Tier3EndermanModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_HUSK, Tier3HuskModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_SKELETON, Tier3SkeletonModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_STRAY, Tier3StrayModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_WITHER_SKELETON, Tier3WitherSkeletonModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_PIGLIN, Tier3PiglinModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_ZOMBIE, Tier3ZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.VAULT_SPIDER_BABY, VaultSpiderBabyModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.WINTER_WOLF, WinterWolfModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.SHIVER, ShiverModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.VAULT_DOOD, VaultDoodModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T0_SKELETON_PIRATE, Tier0SkeletonPirateModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T1_SKELETON_PIRATE, Tier1SkeletonPirateModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T2_SKELETON_PIRATE, Tier2SkeletonPirateModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_SKELETON_PIRATE, Tier3SkeletonPirateModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T4_SKELETON_PIRATE, Tier4SkeletonPirateModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T5_SKELETON_PIRATE, Tier5SkeletonPirateModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T0_WINTERWALKER, Tier0WinterwalkerModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T1_WINTERWALKER, Tier1WinterwalkerModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T2_WINTERWALKER, Tier2WinterwalkerModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_WINTERWALKER, Tier3WinterwalkerModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T4_WINTERWALKER, Tier4WinterwalkerModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T5_WINTERWALKER, Tier5WinterwalkerModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T0_OVERGROWN_ZOMBIE, Tier0OvergrownZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T1_OVERGROWN_ZOMBIE, Tier1OvergrownZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T2_OVERGROWN_ZOMBIE, Tier2OvergrownZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_OVERGROWN_ZOMBIE, Tier3OvergrownZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T4_OVERGROWN_ZOMBIE, Tier4OvergrownZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T5_OVERGROWN_ZOMBIE, Tier5OvergrownZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T0_MUMMY, Tier0MummyModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T1_MUMMY, Tier1MummyModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T2_MUMMY, Tier2MummyModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T0_MUSHROOM, Tier0MushroomModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T1_MUSHROOM, Tier1MushroomModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T2_MUSHROOM, Tier2MushroomModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_MUSHROOM, Tier3MushroomModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T4_MUSHROOM, Tier4MushroomModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T5_MUSHROOM, Tier5MushroomModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.DEATHCAP, DeathcapModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.SMOLCAP, SmolcapModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T0_MINER_ZOMBIE, Tier0MinerZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T1_MINER_ZOMBIE, Tier1MinerZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T2_MINER_ZOMBIE, Tier2MinerZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T3_MINER_ZOMBIE, Tier3MinerZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T4_MINER_ZOMBIE, Tier4MinerZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.T5_MINER_ZOMBIE, Tier5MinerZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.DEEP_DARK_ZOMBIE, DeepDarkZombieModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.DEEP_DARK_SKELETON, DeepDarkSkeletonModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.DEEP_DARK_PIGLIN, DeepDarkPiglinModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.DEEP_DARK_SILVERFISH, DeepDarkSilverfishModel::createBodyLayer);
         event.registerLayerDefinition(ModModelLayers.DEEP_DARK_HORROR, DeepDarkHorrorModel::createBodyLayer);
      }

      @NotNull
      private static LayerDefinition getPlayerModelDefinition() {
         return LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64);
      }
   }
}
