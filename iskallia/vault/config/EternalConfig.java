package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.raid.VaultRaid;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class EternalConfig extends Config {
   @Expose
   private int EXTRA_HP_PER_ETERNAL;
   @Expose
   private int EXTRA_DAMAGE_PER_ETERNAL;
   @Expose
   private List<EternalConfig.Level> LEVEL_OVERRIDES = new ArrayList<>();

   @Override
   public String getName() {
      return "eternal";
   }

   public EternalConfig.Level getForLevel(int level) {
      for (int i = 0; i < this.LEVEL_OVERRIDES.size(); i++) {
         if (level < this.LEVEL_OVERRIDES.get(i).MIN_LEVEL) {
            if (i != 0) {
               return this.LEVEL_OVERRIDES.get(i - 1);
            }
            break;
         }

         if (i == this.LEVEL_OVERRIDES.size() - 1) {
            return this.LEVEL_OVERRIDES.get(i);
         }
      }

      return EternalConfig.Level.EMPTY;
   }

   @Override
   protected void reset() {
      this.EXTRA_HP_PER_ETERNAL = 30;
      this.EXTRA_DAMAGE_PER_ETERNAL = 10;
      this.LEVEL_OVERRIDES
         .add(
            new EternalConfig.Level(5)
               .attribute(ModAttributes.CRIT_CHANCE, 0.5)
               .attribute(ModAttributes.CRIT_MULTIPLIER, 5.0)
               .attribute(Attributes.field_233818_a_, 60.0)
         );
   }

   @SubscribeEvent
   public static void onEternalScaled(LivingUpdateEvent event) {
      if (!event.getEntity().field_70170_p.field_72995_K) {
         if (event.getEntity() instanceof EternalEntity) {
            if (!event.getEntity().func_184216_O().contains("VaultScaled")) {
               EternalEntity eternal = (EternalEntity)event.getEntity();
               ServerWorld world = (ServerWorld)eternal.field_70170_p;
               VaultRaid raid = VaultRaidData.get(world).getAt(eternal.func_233580_cy_());
               if (raid != null) {
                  EternalConfig.Level level = ModConfigs.ETERNAL.getForLevel(raid.level);

                  for (VaultMobsConfig.Mob.AttributeOverride override : level.ATTRIBUTES) {
                     if (!(world.field_73012_v.nextDouble() >= override.ROLL_CHANCE)) {
                        Attribute attribute = (Attribute)Registry.field_239692_aP_.func_241873_b(new ResourceLocation(override.NAME)).orElse(null);
                        if (attribute != null) {
                           ModifiableAttributeInstance instance = eternal.func_110148_a(attribute);
                           if (instance != null) {
                              instance.func_111128_a(override.getValue(instance.func_111125_b(), world.func_201674_k()));
                           }
                        }
                     }
                  }

                  EternalsData.EternalGroup eternals = EternalsData.get(world).getEternals(eternal.getOwner());
                  int extraHealth = eternals.getEternals().size() * ModConfigs.ETERNAL.EXTRA_HP_PER_ETERNAL;
                  eternal.func_110148_a(Attributes.field_233818_a_)
                     .func_233769_c_(new AttributeModifier("Multiple eternals health bonus", extraHealth, Operation.ADDITION));
                  int extraDamage = eternals.getEternals().size() * ModConfigs.ETERNAL.EXTRA_DAMAGE_PER_ETERNAL;
                  eternal.func_110148_a(Attributes.field_233823_f_)
                     .func_233769_c_(new AttributeModifier("Multiple eternals damage bonus", extraDamage, Operation.ADDITION));
                  eternal.func_70691_i(1000000.0F);
                  eternal.func_184216_O().add("VaultScaled");
               }
            }
         }
      }
   }

   public static class Level {
      public static final EternalConfig.Level EMPTY = new EternalConfig.Level(0);
      @Expose
      public int MIN_LEVEL;
      @Expose
      public List<VaultMobsConfig.Mob.AttributeOverride> ATTRIBUTES;

      public Level(int minLevel) {
         this.MIN_LEVEL = minLevel;
         this.ATTRIBUTES = new ArrayList<>();
      }

      public EternalConfig.Level attribute(Attribute attribute, double defaultValue) {
         this.ATTRIBUTES.add(new VaultMobsConfig.Mob.AttributeOverride(attribute, defaultValue));
         return this;
      }
   }
}
