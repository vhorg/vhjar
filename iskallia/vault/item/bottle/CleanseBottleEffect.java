package iskallia.vault.item.bottle;

import iskallia.vault.init.ModEffects;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class CleanseBottleEffect extends BottleEffect {
   public static final String TYPE = "cleanse";
   private final int duration;

   public CleanseBottleEffect(String effectId, int duration) {
      super(effectId);
      this.duration = duration;
   }

   @Override
   protected String getType() {
      return "cleanse";
   }

   @Override
   protected void trigger(ServerPlayer player) {
      Set<MobEffect> effectsToRemove = new HashSet<>();
      player.getActiveEffects().forEach(effectInstance -> {
         if (effectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            effectsToRemove.add(effectInstance.getEffect());
         }
      });
      effectsToRemove.forEach(player::removeEffect);
      player.addEffect(new MobEffectInstance(ModEffects.PURIFYING_AURA, this.duration, 0, false, false, true));
   }

   @Override
   public CompoundTag serializeData(CompoundTag tag) {
      tag.putInt("duration", this.duration);
      return tag;
   }

   @Override
   protected String getTooltipText(String tooltipFormat) {
      return String.format(tooltipFormat, this.duration / 20);
   }

   @SubscribeEvent
   public static void onPotionApplicable(PotionApplicableEvent event) {
      if (event.getEntityLiving().hasEffect(ModEffects.PURIFYING_AURA) && event.getPotionEffect().getEffect().getCategory() == MobEffectCategory.HARMFUL) {
         event.setResult(Result.DENY);
      }
   }

   public static BottleEffect deserialize(String effectId, CompoundTag tag) {
      return new CleanseBottleEffect(effectId, tag.getInt("duration"));
   }

   public static class PurifyingAuraEffect extends MobEffect {
      public PurifyingAuraEffect(ResourceLocation key, int color) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(key);
      }
   }
}
