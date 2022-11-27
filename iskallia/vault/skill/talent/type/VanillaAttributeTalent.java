package iskallia.vault.skill.talent.type;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.talent.GearAttributeTalent;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public class VanillaAttributeTalent extends PlayerTalent implements GearAttributeTalent {
   @Expose
   private final String attribute;
   @Expose
   private final VanillaAttributeTalent.Modifier modifier;

   public VanillaAttributeTalent(int cost, Attribute attribute, VanillaAttributeTalent.Modifier modifier) {
      this(cost, Registry.ATTRIBUTE.getKey(attribute).toString(), modifier);
   }

   public VanillaAttributeTalent(int cost, String attribute, VanillaAttributeTalent.Modifier modifier) {
      super(cost);
      this.attribute = attribute;
      this.modifier = modifier;
   }

   public Attribute getAttribute() {
      return (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.attribute));
   }

   public VanillaAttributeTalent.Modifier getModifier() {
      return this.modifier;
   }

   @Override
   public void onAdded(Player player) {
      this.onRemoved(player);
      this.runIfPresent(player, attributeData -> attributeData.addTransientModifier(this.getModifier().toMCModifier()));
   }

   @Override
   public void tick(ServerPlayer player) {
      this.runIfPresent(player, attributeData -> {
         if (!attributeData.hasModifier(this.getModifier().toMCModifier())) {
            this.onAdded(player);
         }
      });
   }

   @Override
   public void onRemoved(Player player) {
      this.runIfPresent(player, attributeData -> attributeData.removeModifier(UUID.fromString(this.getModifier().id)));
   }

   public boolean runIfPresent(Player player, Consumer<AttributeInstance> action) {
      AttributeInstance instance = player.getAttribute(this.getAttribute());
      if (instance == null) {
         return false;
      } else {
         action.accept(instance);
         return true;
      }
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      Attribute attr = this.getAttribute();
      VaultGearAttribute<?> gearAttribute = ModGearAttributes.getGearAttribute(attr, this.getModifier().getOperation());
      return (List<VaultGearAttributeInstance<?>>)(gearAttribute == null
         ? Collections.emptyList()
         : Lists.newArrayList(new VaultGearAttributeInstance[]{VaultGearAttributeInstance.cast(gearAttribute, this.getModifier().amount)}));
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
