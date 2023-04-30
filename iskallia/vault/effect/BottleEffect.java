package iskallia.vault.effect;

import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.item.BottleItem;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;

public class BottleEffect extends MobEffect {
   public BottleEffect(MobEffectCategory category, int color, ResourceLocation id) {
      super(category, color);
      this.setRegistryName(id);
   }

   public void addAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap map, int pAmplifier) {
      if (entity instanceof ServerPlayer player) {
         ServerVaults.get(player.level)
            .flatMap(vault -> BottleItem.getActive(vault, player))
            .ifPresent(stack -> map.addTransientAttributeModifiers(VaultGearHelper.getModifiers(VaultGearData.read(stack))));
      }
   }

   public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap map, int pAmplifier) {
      if (entity instanceof ServerPlayer player) {
         ServerVaults.get(player.level)
            .flatMap(vault -> BottleItem.getActive(vault, player))
            .ifPresent(stack -> map.removeAttributeModifiers(VaultGearHelper.getModifiers(VaultGearData.read(stack))));
      }
   }
}
