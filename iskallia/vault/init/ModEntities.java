package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.entity.AggressiveCowBossEntity;
import iskallia.vault.entity.AggressiveCowEntity;
import iskallia.vault.entity.ArenaBossEntity;
import iskallia.vault.entity.BlueBlazeEntity;
import iskallia.vault.entity.BoogiemanEntity;
import iskallia.vault.entity.DrillArrowEntity;
import iskallia.vault.entity.EffectCloudEntity;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.entity.FloatingItemEntity;
import iskallia.vault.entity.MonsterEyeEntity;
import iskallia.vault.entity.RobotEntity;
import iskallia.vault.entity.TreasureGoblinEntity;
import iskallia.vault.entity.VaultFighterEntity;
import iskallia.vault.entity.VaultGuardianEntity;
import iskallia.vault.entity.renderer.AggressiveCowBossRenderer;
import iskallia.vault.entity.renderer.BlueBlazeRenderer;
import iskallia.vault.entity.renderer.BoogiemanRenderer;
import iskallia.vault.entity.renderer.EffectCloudRenderer;
import iskallia.vault.entity.renderer.EternalRenderer;
import iskallia.vault.entity.renderer.FighterRenderer;
import iskallia.vault.entity.renderer.MonsterEyeRenderer;
import iskallia.vault.entity.renderer.RobotRenderer;
import iskallia.vault.entity.renderer.TreasureGoblinRenderer;
import iskallia.vault.entity.renderer.VaultGuardianRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ModEntities {
   public static List<EntityType<VaultFighterEntity>> VAULT_FIGHTER_TYPES = new ArrayList<>();
   public static EntityType<FighterEntity> FIGHTER;
   public static EntityType<ArenaBossEntity> ARENA_BOSS;
   public static EntityType<VaultGuardianEntity> VAULT_GUARDIAN;
   public static EntityType<EternalEntity> ETERNAL;
   public static EntityType<TreasureGoblinEntity> TREASURE_GOBLIN;
   public static EntityType<AggressiveCowEntity> AGGRESSIVE_COW;
   public static EntityType<MonsterEyeEntity> MONSTER_EYE;
   public static EntityType<RobotEntity> ROBOT;
   public static EntityType<BlueBlazeEntity> BLUE_BLAZE;
   public static EntityType<BoogiemanEntity> BOOGIEMAN;
   public static EntityType<AggressiveCowBossEntity> AGGRESSIVE_COW_BOSS;
   public static EntityType<DrillArrowEntity> DRILL_ARROW;
   public static EntityType<EffectCloudEntity> EFFECT_CLOUD;
   public static EntityType<FloatingItemEntity> FLOATING_ITEM;

   public static void register(Register<EntityType<?>> event) {
      for (int i = 0; i < 10; i++) {
         VAULT_FIGHTER_TYPES.add(registerVaultFighter(i, event));
      }

      FIGHTER = registerLiving(
         "fighter", Builder.func_220322_a(FighterEntity::new, EntityClassification.MONSTER).func_220321_a(0.6F, 1.95F), ZombieEntity::func_234342_eQ_, event
      );
      ARENA_BOSS = registerLiving(
         "arena_boss",
         Builder.func_220322_a(ArenaBossEntity::new, EntityClassification.MONSTER).func_220321_a(0.6F, 1.95F),
         ArenaBossEntity::getAttributes,
         event
      );
      VAULT_GUARDIAN = registerLiving(
         "vault_guardian",
         Builder.func_220322_a(VaultGuardianEntity::new, EntityClassification.MONSTER).func_220321_a(1.3F, 2.95F),
         ZombieEntity::func_234342_eQ_,
         event
      );
      ETERNAL = registerLiving(
         "eternal", Builder.func_220322_a(EternalEntity::new, EntityClassification.CREATURE).func_220321_a(0.6F, 1.95F), ZombieEntity::func_234342_eQ_, event
      );
      TREASURE_GOBLIN = registerLiving(
         "treasure_goblin",
         Builder.func_220322_a(TreasureGoblinEntity::new, EntityClassification.CREATURE).func_220321_a(0.5F, 1.5F),
         ZombieEntity::func_234342_eQ_,
         event
      );
      AGGRESSIVE_COW = registerLiving(
         "aggressive_cow",
         Builder.func_220322_a(AggressiveCowEntity::new, EntityClassification.MONSTER).func_220321_a(0.9F, 1.4F).func_233606_a_(8),
         AggressiveCowEntity::getAttributes,
         event
      );
      MONSTER_EYE = registerLiving(
         "monster_eye",
         Builder.func_220322_a(MonsterEyeEntity::new, EntityClassification.MONSTER).func_220321_a(4.08F, 4.08F),
         ZombieEntity::func_234342_eQ_,
         event
      );
      ROBOT = registerLiving(
         "robot", Builder.func_220322_a(RobotEntity::new, EntityClassification.MONSTER).func_220321_a(2.8F, 5.4F), ZombieEntity::func_234342_eQ_, event
      );
      BLUE_BLAZE = registerLiving(
         "blue_blaze",
         Builder.func_220322_a(BlueBlazeEntity::new, EntityClassification.MONSTER).func_220321_a(1.2F, 3.6F),
         ZombieEntity::func_234342_eQ_,
         event
      );
      BOOGIEMAN = registerLiving(
         "boogieman", Builder.func_220322_a(BoogiemanEntity::new, EntityClassification.MONSTER).func_220321_a(1.2F, 3.9F), ZombieEntity::func_234342_eQ_, event
      );
      AGGRESSIVE_COW_BOSS = registerLiving(
         "aggressive_cow_boss",
         Builder.func_220322_a(AggressiveCowBossEntity::new, EntityClassification.MONSTER).func_220321_a(2.6999998F, 4.2F),
         AggressiveCowEntity::getAttributes,
         event
      );
      DRILL_ARROW = register("drill_arrow", Builder.func_220322_a(DrillArrowEntity::new, EntityClassification.MISC), event);
      EFFECT_CLOUD = register("effect_cloud", Builder.func_220322_a(EffectCloudEntity::new, EntityClassification.MISC), event);
      FLOATING_ITEM = register("floating_item", Builder.func_220322_a(FloatingItemEntity::new, EntityClassification.MISC), event);
   }

   private static EntityType<VaultFighterEntity> registerVaultFighter(int count, Register<EntityType<?>> event) {
      return registerLiving(
         count > 0 ? "vault_fighter_" + count : "vault_fighter",
         Builder.func_220322_a(VaultFighterEntity::new, EntityClassification.MONSTER).func_220321_a(0.6F, 1.95F),
         ZombieEntity::func_234342_eQ_,
         event
      );
   }

   private static <T extends Entity> EntityType<T> register(String name, Builder<T> builder, Register<EntityType<?>> event) {
      EntityType<T> entityType = builder.func_206830_a(Vault.sId(name));
      event.getRegistry().register(entityType.setRegistryName(Vault.id(name)));
      return entityType;
   }

   private static <T extends LivingEntity> EntityType<T> registerLiving(
      String name, Builder<T> builder, Supplier<MutableAttribute> attributes, Register<EntityType<?>> event
   ) {
      EntityType<T> entityType = register(name, builder, event);
      if (attributes != null) {
         GlobalEntityTypeAttributes.put(entityType, attributes.get().func_233813_a_());
      }

      return entityType;
   }

   public static class Renderers {
      public static void register(FMLClientSetupEvent event) {
         ModEntities.VAULT_FIGHTER_TYPES.forEach(type -> RenderingRegistry.registerEntityRenderingHandler(type, FighterRenderer::new));
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.FIGHTER, FighterRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.ARENA_BOSS, FighterRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.VAULT_GUARDIAN, VaultGuardianRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.ETERNAL, EternalRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.TREASURE_GOBLIN, TreasureGoblinRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.AGGRESSIVE_COW, CowRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.MONSTER_EYE, MonsterEyeRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.ROBOT, RobotRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.BLUE_BLAZE, BlueBlazeRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.BOOGIEMAN, BoogiemanRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.AGGRESSIVE_COW_BOSS, AggressiveCowBossRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.DRILL_ARROW, TippedArrowRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.EFFECT_CLOUD, EffectCloudRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.FLOATING_ITEM, rm -> new ItemRenderer(rm, Minecraft.func_71410_x().func_175599_af()));
      }
   }
}
