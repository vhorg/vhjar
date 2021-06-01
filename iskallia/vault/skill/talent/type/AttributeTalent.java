package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class AttributeTalent extends PlayerTalent {
   @Expose
   private final String attribute;
   @Expose
   private final AttributeTalent.Modifier modifier;

   public AttributeTalent(int cost, Attribute attribute, AttributeTalent.Modifier modifier) {
      this(cost, Registry.field_239692_aP_.func_177774_c(attribute).toString(), modifier);
   }

   public AttributeTalent(int cost, String attribute, AttributeTalent.Modifier modifier) {
      super(cost);
      this.attribute = attribute;
      this.modifier = modifier;
   }

   public Attribute getAttribute() {
      return (Attribute)Registry.field_239692_aP_.func_82594_a(new ResourceLocation(this.attribute));
   }

   public AttributeTalent.Modifier getModifier() {
      return this.modifier;
   }

   @Override
   public void onAdded(PlayerEntity player) {
      this.onRemoved(player);
      this.runIfPresent(player, attributeData -> attributeData.func_233767_b_(this.getModifier().toMCModifier()));
   }

   @Override
   public void tick(PlayerEntity player) {
      this.runIfPresent(player, attributeData -> {
         if (!attributeData.func_180374_a(this.getModifier().toMCModifier())) {
            this.onAdded(player);
         }
      });
   }

   @Override
   public void onRemoved(PlayerEntity player) {
      this.runIfPresent(player, attributeData -> attributeData.func_188479_b(UUID.fromString(this.getModifier().id)));
   }

   public boolean runIfPresent(PlayerEntity player, Consumer<ModifiableAttributeInstance> action) {
      ModifiableAttributeInstance attributeData = player.func_110148_a(this.getAttribute());
      if (attributeData == null) {
         return false;
      } else {
         action.accept(attributeData);
         return true;
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
         this.id = MathHelper.func_180182_a(new Random(name.hashCode())).toString();
         this.name = name;
         this.amount = amount;
         this.operation = operation.func_220371_a();
      }

      public AttributeModifier toMCModifier() {
         return new AttributeModifier(UUID.fromString(this.id), this.name, this.amount, Operation.func_220372_a(this.operation));
      }
   }
}
