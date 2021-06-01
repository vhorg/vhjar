package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.entity.BlueBlazeEntity;
import iskallia.vault.entity.BoogiemanEntity;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.entity.MonsterEyeEntity;
import iskallia.vault.entity.RobotEntity;
import iskallia.vault.entity.TreasureGoblinEntity;
import iskallia.vault.entity.VaultFighterEntity;
import iskallia.vault.entity.VaultGuardianEntity;
import iskallia.vault.entity.renderer.BlueBlazeRenderer;
import iskallia.vault.entity.renderer.BoogiemanRenderer;
import iskallia.vault.entity.renderer.EternalRenderer;
import iskallia.vault.entity.renderer.FighterRenderer;
import iskallia.vault.entity.renderer.MonsterEyeRenderer;
import iskallia.vault.entity.renderer.RobotRenderer;
import iskallia.vault.entity.renderer.TreasureGoblinRenderer;
import iskallia.vault.entity.renderer.VaultGuardianRenderer;
import java.util.function.Supplier;
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
   public static EntityType<FighterEntity> FIGHTER;
   public static EntityType<MonsterEyeEntity> MONSTER_EYE;
   public static EntityType<RobotEntity> ROBOT;
   public static EntityType<BlueBlazeEntity> BLUE_BLAZE;
   public static EntityType<BoogiemanEntity> BOOGIEMAN;
   public static EntityType<VaultGuardianEntity> VAULT_GUARDIAN;
   public static EntityType<VaultFighterEntity> VAULT_FIGHTER;
   public static EntityType<EternalEntity> ETERNAL;
   public static EntityType<TreasureGoblinEntity> TREASURE_GOBLIN;

   public static void register(Register<EntityType<?>> event) {
      FIGHTER = register(
         "fighter", Builder.func_220322_a(FighterEntity::new, EntityClassification.MONSTER).func_220321_a(0.6F, 1.95F), ZombieEntity::func_234342_eQ_, event
      );
      MONSTER_EYE = register(
         "monster_eye",
         Builder.func_220322_a(MonsterEyeEntity::new, EntityClassification.MONSTER).func_220321_a(4.08F, 4.08F),
         ZombieEntity::func_234342_eQ_,
         event
      );
      ROBOT = register(
         "robot", Builder.func_220322_a(RobotEntity::new, EntityClassification.MONSTER).func_220321_a(2.8F, 5.4F), ZombieEntity::func_234342_eQ_, event
      );
      BLUE_BLAZE = register(
         "blue_blaze",
         Builder.func_220322_a(BlueBlazeEntity::new, EntityClassification.MONSTER).func_220321_a(1.2F, 3.6F),
         ZombieEntity::func_234342_eQ_,
         event
      );
      BOOGIEMAN = register(
         "boogieman", Builder.func_220322_a(BoogiemanEntity::new, EntityClassification.MONSTER).func_220321_a(1.2F, 3.9F), ZombieEntity::func_234342_eQ_, event
      );
      VAULT_GUARDIAN = register(
         "vault_guardian",
         Builder.func_220322_a(VaultGuardianEntity::new, EntityClassification.MONSTER).func_220321_a(1.3F, 2.95F),
         ZombieEntity::func_234342_eQ_,
         event
      );
      VAULT_FIGHTER = register(
         "vault_fighter",
         Builder.func_220322_a(VaultFighterEntity::new, EntityClassification.MONSTER).func_220321_a(0.6F, 1.95F),
         ZombieEntity::func_234342_eQ_,
         event
      );
      ETERNAL = register(
         "eternal", Builder.func_220322_a(EternalEntity::new, EntityClassification.CREATURE).func_220321_a(0.6F, 1.95F), ZombieEntity::func_234342_eQ_, event
      );
      TREASURE_GOBLIN = register(
         "treasure_goblin",
         Builder.func_220322_a(TreasureGoblinEntity::new, EntityClassification.CREATURE).func_220321_a(1.0F, 1.0F),
         ZombieEntity::func_234342_eQ_,
         event
      );
   }

   public static <T extends LivingEntity> EntityType<T> register(
      String name, Builder<T> builder, Supplier<MutableAttribute> attributes, Register<EntityType<?>> event
   ) {
      EntityType<T> entityType = builder.func_206830_a(Vault.sId(name));
      event.getRegistry().register(entityType.setRegistryName(Vault.id(name)));
      if (attributes != null) {
         GlobalEntityTypeAttributes.put(entityType, attributes.get().func_233813_a_());
      }

      return entityType;
   }

   public static class Renderers {
      public static void register(FMLClientSetupEvent event) {
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.FIGHTER, FighterRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.MONSTER_EYE, MonsterEyeRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.ROBOT, RobotRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.BLUE_BLAZE, BlueBlazeRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.BOOGIEMAN, BoogiemanRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.VAULT_GUARDIAN, VaultGuardianRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.VAULT_FIGHTER, FighterRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.ETERNAL, EternalRenderer::new);
         RenderingRegistry.registerEntityRenderingHandler(ModEntities.TREASURE_GOBLIN, TreasureGoblinRenderer::new);
      }
   }
}
