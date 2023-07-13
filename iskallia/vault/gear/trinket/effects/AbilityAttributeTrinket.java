package iskallia.vault.gear.trinket.effects;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.gear.trinket.GearAttributeTrinket;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class AbilityAttributeTrinket extends TrinketEffect<AbilityAttributeTrinket.Config> implements GearAttributeTrinket {
   private final AbilityLevelAttribute defaultAttributeValue;

   public AbilityAttributeTrinket(ResourceLocation name, AbilityLevelAttribute attributeValue) {
      super(name);
      this.defaultAttributeValue = attributeValue;
   }

   @Override
   public Class<AbilityAttributeTrinket.Config> getConfigClass() {
      return MiscUtils.cast(AbilityAttributeTrinket.Config.class);
   }

   public AbilityAttributeTrinket.Config getDefaultConfig() {
      return new AbilityAttributeTrinket.Config(this.defaultAttributeValue.getAbility(), this.defaultAttributeValue.getLevelChange());
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      return Lists.newArrayList(new VaultGearAttributeInstance[]{this.getConfig().toAttributeInstance()});
   }

   public static class Config extends TrinketEffect.Config {
      @Expose
      private final String name;
      @Expose
      private final int value;

      public Config(String name, int value) {
         this.name = name;
         this.value = value;
      }

      public VaultGearAttributeInstance<?> toAttributeInstance() {
         return VaultGearAttributeInstance.cast(ModGearAttributes.ABILITY_LEVEL, new AbilityLevelAttribute(this.name, this.value));
      }
   }
}
