package iskallia.vault.gear.trinket.effects;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.trinket.GearAttributeTrinket;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.init.ModGearAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class VanillaAttributeTrinket extends TrinketEffect<VanillaAttributeTrinket.Config> implements GearAttributeTrinket {
   private final Attribute defaultAttribute;
   private final double value;
   private final Operation operation;

   public VanillaAttributeTrinket(ResourceLocation name, Attribute defaultAttribute, double value, Operation operation) {
      super(name);
      this.defaultAttribute = defaultAttribute;
      this.value = value;
      this.operation = operation;
   }

   @Override
   public Class<VanillaAttributeTrinket.Config> getConfigClass() {
      return VanillaAttributeTrinket.Config.class;
   }

   public VanillaAttributeTrinket.Config getDefaultConfig() {
      return new VanillaAttributeTrinket.Config(
         this.defaultAttribute.getRegistryName().toString(),
         new VanillaAttributeTrinket.Modifier(this.defaultAttribute.getDescriptionId(), this.value, this.operation)
      );
   }

   @Override
   public void onWornTick(LivingEntity entity, ItemStack stack) {
      super.onWornTick(entity, stack);
      if (entity instanceof Player player) {
         this.runIfPresent(player, attributeData -> {
            if (this.isUsable(stack, player)) {
               if (!attributeData.hasModifier(this.getConfig().getModifier().toMCModifier())) {
                  this.addModifier(player);
               }
            } else {
               this.removeModifier(player);
            }
         });
      }
   }

   @Override
   public void onEquip(LivingEntity entity, ItemStack stack) {
      super.onEquip(entity, stack);
      if (entity instanceof Player player && this.isUsable(stack, player)) {
         this.runIfPresent(player, attributeData -> {
            if (!attributeData.hasModifier(this.getConfig().getModifier().toMCModifier())) {
               this.addModifier(player);
            }
         });
      }
   }

   @Override
   public void onUnEquip(LivingEntity entity, ItemStack stack) {
      super.onUnEquip(entity, stack);
      if (entity instanceof Player player) {
         this.removeModifier(player);
      }
   }

   private void removeModifier(Player player) {
      this.runIfPresent(player, attributeData -> attributeData.removeModifier(UUID.fromString(this.getConfig().getModifier().id)));
   }

   private void addModifier(Player player) {
      this.runIfPresent(player, attributeData -> attributeData.addTransientModifier(this.getConfig().getModifier().toMCModifier()));
   }

   public boolean runIfPresent(LivingEntity entity, Consumer<AttributeInstance> action) {
      AttributeInstance instance = entity.getAttribute(this.getConfig().getAttribute());
      if (instance == null) {
         return false;
      } else {
         action.accept(instance);
         return true;
      }
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      VanillaAttributeTrinket.Config cfg = this.getConfig();
      VaultGearAttribute<?> gearAttribute = ModGearAttributes.getGearAttribute(cfg.getAttribute(), cfg.getModifier().getOperation());
      return (List<VaultGearAttributeInstance<?>>)(gearAttribute == null
         ? Collections.emptyList()
         : Lists.newArrayList(new VaultGearAttributeInstance[]{VaultGearAttributeInstance.cast(gearAttribute, cfg.getModifier().amount)}));
   }

   public static class Config extends TrinketEffect.Config {
      @Expose
      private final String attribute;
      @Expose
      private final VanillaAttributeTrinket.Modifier modifier;

      public Config(String attribute, VanillaAttributeTrinket.Modifier modifier) {
         this.attribute = attribute;
         this.modifier = modifier;
      }

      public Attribute getAttribute() {
         return (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.attribute));
      }

      public VanillaAttributeTrinket.Modifier getModifier() {
         return this.modifier;
      }
   }

   public static class Modifier {
      @Expose
      public final String id;
      @Expose
      public final String name;
      @Expose
      public final double amount;
      @Expose
      public final int operation;

      public Modifier(String name, double amount, Operation operation) {
         this.id = Mth.createInsecureUUID(new Random(name.hashCode())).toString();
         this.name = name;
         this.amount = amount;
         this.operation = operation.toValue();
      }

      public Operation getOperation() {
         return Operation.fromValue(this.operation);
      }

      public AttributeModifier toMCModifier() {
         return new AttributeModifier(UUID.fromString(this.id), this.name, this.amount, this.getOperation());
      }
   }
}
