package iskallia.vault.world.vault.logic;

import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.influence.DamageInfluence;
import iskallia.vault.world.vault.influence.EffectInfluence;
import iskallia.vault.world.vault.influence.MobAttributeInfluence;
import iskallia.vault.world.vault.influence.MobsInfluence;
import iskallia.vault.world.vault.influence.TimeInfluence;
import iskallia.vault.world.vault.influence.VaultAttributeInfluence;
import iskallia.vault.world.vault.influence.VaultInfluence;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class VaultInfluenceHandler {
   private static final float influenceChance = 0.66F;
   private static final UUID BENEVOLENT_HP_REDUCTION = UUID.fromString("bb3be804-44c2-474a-af69-b300f5d01bc7");
   private static final UUID OMNISCIENT_SPEED_REDUCTION = UUID.fromString("3d0402b6-4edc-49fc-ada6-23700a9737ac");
   private static final UUID MALEVOLENCE_DAMAGE_REDUCTION = UUID.fromString("5d54dcbf-cb04-4716-85b7-e262080049c0");
   private static final UUID BENEVOLENT_HP_INCREASE = UUID.fromString("9093f3ee-64d8-4d64-b410-7052872f4b94");
   private static final UUID OMNISCIENT_ARMOR_INCREASE = UUID.fromString("15f0faaa-c014-4063-a3e5-ae801a95e721");
   private static final UUID MALEVOLENCE_DAMAGE_INCREASE = UUID.fromString("0011379d-97e7-44b1-860e-9d355746e886");
   private static final Random rand = new Random();
   private static final Map<VaultGod, VaultInfluenceHandler.InfluenceMessages> messages = new HashMap<>();

   public static void initializeInfluences(VaultRaid vault, ServerLevel world) {
   }

   private static Tuple<VaultInfluence, String> getPositiveInfluence(VaultGod type, int favour) {
      new DecimalFormat("0.##");
      VaultInfluence influence = null;
      String text = null;
      switch (type) {
         case VELARA:
            switch (rand.nextInt(2)) {
               case 0:
                  int heal = 50 + Mth.ceil((favour - 4) * 4.166666F);
                  float healPerc = heal / 100.0F;
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.HEALING_EFFECTIVENESS, 1.0F + healPerc, true);
                  text = "Effectiveness of Healing is increased by " + heal + "%";
                  break;
               case 1:
                  int ampl = Mth.clamp((favour - 4) / 8 + 1, 1, 2);
                  influence = new EffectInfluence(MobEffects.REGENERATION, ampl);
                  text = "Grants +" + ampl + " Regeneration";
            }

            return new Tuple(influence, text);
         case TENOS:
            switch (rand.nextInt(2)) {
               case 0:
                  int increased = 25 + Math.min(Math.round((favour - 4) * 6.25F), 75);
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.CHEST_RARITY, increased / 100.0F, false);
                  text = "Grants " + increased + "% Chest Rarity";
                  break;
               case 1:
                  int ampl = Mth.clamp(favour / 5, 1, 3);
                  influence = new EffectInfluence(MobEffects.LUCK, ampl);
                  text = "Grants +" + ampl + " Luck";
            }

            return new Tuple(influence, text);
         case WENDARR:
            switch (rand.nextInt(2)) {
               case 0:
                  int cdReduction = 10 + Math.round((favour - 4) * 2.5F);
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.COOLDOWN_REDUCTION, 1.0F + cdReduction / 100.0F, true);
                  text = "Grants +" + cdReduction + "% Cooldown Reduction";
                  break;
               case 1:
                  int time = favour / 4;
                  influence = new TimeInfluence(time * 60 * 20);
                  text = "Grants " + time + " additional minutes";
            }

            return new Tuple(influence, text);
         case IDONA:
            switch (rand.nextInt(2)) {
               case 0:
                  int incDrops = 100 + (favour - 4) * 25;
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.SOUL_SHARD_DROPS, 1.0F + incDrops / 100.0F, true);
                  text = "Monsters drop " + incDrops + "% more Soul Shards.";
                  break;
               case 1:
                  int more = 25 + Math.round((favour - 4) * 14.58F);
                  float perc = 1.0F + more / 100.0F;
                  influence = new DamageInfluence(perc);
                  text = "You deal " + more + "% more damage";
            }

            return new Tuple(influence, text);
         default:
            throw new IllegalArgumentException("Unknown type: " + type.name());
      }
   }

   private static Tuple<VaultInfluence, String> getNegativeInfluence(VaultGod type, int favour) {
      VaultInfluence influence = null;
      String text = null;
      switch (type) {
         case VELARA:
            switch (rand.nextInt(2)) {
               case 0:
                  int reduced = 10 + Math.round((favour - 4) * 3.3333F);
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.HEALING_EFFECTIVENESS, 1.0F - reduced / 100.0F, true);
                  text = "Effectiveness of Healing is reduced by " + reduced + "%";
                  break;
               case 1:
                  int more = 4 + Math.round((favour - 4) * 0.5F);
                  influence = new MobsInfluence(more);
                  text = more + " additional monsters spawn around you";
            }

            return new Tuple(influence, text);
         case TENOS:
            switch (rand.nextInt(2)) {
               case 0:
                  int ampl = Mth.clamp(favour / 5, 1, 3);
                  influence = new EffectInfluence(MobEffects.UNLUCK, ampl);
                  text = "Applies -" + ampl + " Luck";
                  break;
               case 1:
                  int decreased = 25 + Math.min(Math.round((favour - 4) * 6.25F), 75);
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.CHEST_RARITY, -decreased / 100.0F, false);
                  text = "Reduces Chest Rarity by " + decreased + "%";
            }

            return new Tuple(influence, text);
         case WENDARR:
            switch (rand.nextInt(2)) {
               case 0:
                  int more = 10 + Math.round((favour - 4) * 3.333F);
                  float perc = more / 100.0F;
                  influence = new MobAttributeInfluence(
                     Attributes.MOVEMENT_SPEED, new AttributeModifier(OMNISCIENT_SPEED_REDUCTION, "Favours", perc, Operation.MULTIPLY_TOTAL)
                  );
                  text = "Monsters move " + more + "% faster";
                  break;
               case 1:
                  int time = 1 + (favour - 4) / 6;
                  influence = new TimeInfluence(-time * 60 * 20);
                  text = "Removes " + time + " minutes";
            }

            return new Tuple(influence, text);
         case IDONA:
            switch (rand.nextInt(2)) {
               case 0:
                  int less = 10 + Math.round((favour - 4) * 5.416666F);
                  influence = new DamageInfluence(1.0F - less / 100.0F);
                  text = "You deal " + less + "% less damage";
                  break;
               case 1:
                  int more = 20 + (favour - 4) * 15;
                  influence = new MobAttributeInfluence(
                     Attributes.MAX_HEALTH, new AttributeModifier(BENEVOLENT_HP_REDUCTION, "Favours", more / 100.0F, Operation.MULTIPLY_TOTAL)
                  );
                  text = "Monsters have " + more + "% more Health";
            }

            return new Tuple(influence, text);
         default:
            throw new IllegalArgumentException("Unknown type: " + type.name());
      }
   }

   static {
      VaultInfluenceHandler.InfluenceMessages benevolent = new VaultInfluenceHandler.InfluenceMessages();
      benevolent.positiveMessages.add("Our domain's ground will carve a path.");
      benevolent.positiveMessages.add("Tread upon our domain with care and it will respond in kind.");
      benevolent.positiveMessages.add("May your desire blossom into a wildfire.");
      benevolent.positiveMessages.add("Creation bends to our will.");
      benevolent.negativeMessages.add("Nature rises against you.");
      benevolent.negativeMessages.add("Prosperity withers at your touch.");
      benevolent.negativeMessages.add("Defile, rot, decay and fester.");
      benevolent.negativeMessages.add("The flower of your aspirations will waste away.");
      messages.put(VaultGod.VELARA, benevolent);
      VaultInfluenceHandler.InfluenceMessages omniscient = new VaultInfluenceHandler.InfluenceMessages();
      omniscient.positiveMessages.add("May foresight guide your step.");
      omniscient.positiveMessages.add("Careful planning and strategy may lead you.");
      omniscient.positiveMessages.add("A set choice; followed through and flawlessly executed.");
      omniscient.positiveMessages.add("Chance's hand may favour your goals.");
      omniscient.negativeMessages.add("A choice; leading one to disfavour.");
      omniscient.negativeMessages.add("Riches, Wealth, Prosperity. An illusion.");
      omniscient.negativeMessages.add("Cascading eventuality. Solidified in ruin.");
      omniscient.negativeMessages.add("Diminishing reality.");
      messages.put(VaultGod.TENOS, omniscient);
      VaultInfluenceHandler.InfluenceMessages timekeeper = new VaultInfluenceHandler.InfluenceMessages();
      timekeeper.positiveMessages.add("Seize the opportunity.");
      timekeeper.positiveMessages.add("A single instant, stretched to infinity.");
      timekeeper.positiveMessages.add("Your future glows golden with possibility.");
      timekeeper.positiveMessages.add("Hasten and value every passing moment.");
      timekeeper.negativeMessages.add("Eternity in the moment of standstill.");
      timekeeper.negativeMessages.add("Drown in the flow of time.");
      timekeeper.negativeMessages.add("Transience manifested.");
      timekeeper.negativeMessages.add("Immutable emptiness.");
      messages.put(VaultGod.WENDARR, timekeeper);
      VaultInfluenceHandler.InfluenceMessages malevolence = new VaultInfluenceHandler.InfluenceMessages();
      malevolence.positiveMessages.add("Enforce your path through obstacles.");
      malevolence.positiveMessages.add("Our vigor may aid your conquest.");
      malevolence.positiveMessages.add("Cherish this mote of my might.");
      malevolence.positiveMessages.add("A tempest incarnate.");
      malevolence.negativeMessages.add("Feel our domain's wrath.");
      malevolence.negativeMessages.add("Malice and spite given form.");
      malevolence.negativeMessages.add("Flee before the growing horde.");
      malevolence.negativeMessages.add("Perish from your own ambition.");
      messages.put(VaultGod.IDONA, malevolence);
   }

   private static class InfluenceMessages {
      private final List<String> positiveMessages = new ArrayList<>();
      private final List<String> negativeMessages = new ArrayList<>();

      private String getNegativeMessage() {
         return this.negativeMessages.get(VaultInfluenceHandler.rand.nextInt(this.negativeMessages.size()));
      }

      private String getPositiveMessage() {
         return this.positiveMessages.get(VaultInfluenceHandler.rand.nextInt(this.positiveMessages.size()));
      }
   }
}
