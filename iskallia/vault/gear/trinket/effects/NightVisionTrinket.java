package iskallia.vault.gear.trinket.effects;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.custom.effect.EffectGearAttribute;
import iskallia.vault.gear.trinket.GearAttributeTrinket;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.network.message.ClientboundNightVisionGogglesParticlesMessage;
import iskallia.vault.skill.ability.effect.spi.HunterAbility;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.world.data.ServerVaults;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

public class NightVisionTrinket extends TrinketEffect<NightVisionTrinket.Config> implements GearAttributeTrinket {
   private final MobEffect effect;
   private final int addedAmplifier;
   private final float radius;

   public NightVisionTrinket(ResourceLocation name, MobEffect effect, int addedAmplifier, float radius) {
      super(name);
      this.effect = effect;
      this.addedAmplifier = addedAmplifier;
      this.radius = radius;
   }

   @Override
   public Class<NightVisionTrinket.Config> getConfigClass() {
      return NightVisionTrinket.Config.class;
   }

   public NightVisionTrinket.Config getDefaultConfig() {
      return new NightVisionTrinket.Config(this.effect.getRegistryName(), this.addedAmplifier, this.radius);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      NightVisionTrinket.Config cfg = this.getConfig();
      return (List<VaultGearAttributeInstance<?>>)(cfg.getEffect() == null
         ? List.of()
         : Lists.newArrayList(
            new VaultGearAttributeInstance[]{
               new VaultGearAttributeInstance<>(ModGearAttributes.EFFECT, new EffectGearAttribute(cfg.getEffect(), cfg.getAddedAmplifier()))
            }
         ));
   }

   @Override
   public void onWornTick(LivingEntity entity, ItemStack stack) {
      super.onWornTick(entity, stack);
      if (entity instanceof ServerPlayer player) {
         if (ServerVaults.get(player.level).isEmpty()) {
            return;
         }

         Optional<TrinketEffect<?>> trinketEffect = TrinketItem.getTrinket(stack);
         if (trinketEffect.isPresent()) {
            if (!trinketEffect.get().isUsable(stack, player)) {
               return;
            }

            if (player.tickCount % 10 != 0) {
               return;
            }

            NightVisionTrinket.Config cfg = this.getConfig();
            float radius = AreaOfEffectHelper.adjustAreaOfEffect(player, null, cfg.radius);
            HunterAbility.selectPositions((ServerLevel)player.level, player, (double)radius)
               .forEach(
                  highlightPosition -> {
                     if (!highlightPosition.type().equals("blocks")) {
                        ModNetwork.CHANNEL
                           .sendTo(
                              new ClientboundNightVisionGogglesParticlesMessage(
                                 highlightPosition.blockPos().getX(),
                                 highlightPosition.blockPos().getY(),
                                 highlightPosition.blockPos().getZ(),
                                 30.0,
                                 highlightPosition.type()
                              ),
                              player.connection.getConnection(),
                              NetworkDirection.PLAY_TO_CLIENT
                           );
                     }
                  }
               );
         }
      }
   }

   public static class Config extends TrinketEffect.Config {
      @Expose
      private ResourceLocation effect;
      @Expose
      private int addedAmplifier;
      @Expose
      private float radius;

      public Config(ResourceLocation effect, int addedAmplifier, float radius) {
         this.effect = effect;
         this.addedAmplifier = addedAmplifier;
         this.radius = radius;
      }

      public MobEffect getEffect() {
         return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(this.effect);
      }

      public int getAddedAmplifier() {
         return this.addedAmplifier;
      }

      public float getRadius() {
         return this.radius;
      }
   }
}
