package iskallia.vault.world.vault.logic;

import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.influence.DamageInfluence;
import iskallia.vault.world.vault.influence.DamageTakenInfluence;
import iskallia.vault.world.vault.influence.EffectInfluence;
import iskallia.vault.world.vault.influence.MobAttributeInfluence;
import iskallia.vault.world.vault.influence.MobsInfluence;
import iskallia.vault.world.vault.influence.VaultAttributeInfluence;
import iskallia.vault.world.vault.influence.VaultInfluence;
import iskallia.vault.world.vault.influence.VaultInfluences;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.potion.Effects;
import net.minecraft.util.Tuple;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.server.ServerWorld;

public class VaultInfluenceHandler {
   private static final float influenceChance = 0.35F;
   private static final UUID BENEVOLENT_HP_REDUCTION = UUID.fromString("bb3be804-44c2-474a-af69-b300f5d01bc7");
   private static final UUID OMNISCIENT_SPEED_REDUCTION = UUID.fromString("3d0402b6-4edc-49fc-ada6-23700a9737ac");
   private static final UUID MALEVOLENCE_DAMAGE_REDUCTION = UUID.fromString("5d54dcbf-cb04-4716-85b7-e262080049c0");
   private static final UUID BENEVOLENT_HP_INCREASE = UUID.fromString("9093f3ee-64d8-4d64-b410-7052872f4b94");
   private static final UUID OMNISCIENT_ARMOR_INCREASE = UUID.fromString("15f0faaa-c014-4063-a3e5-ae801a95e721");
   private static final UUID MALEVOLENCE_DAMAGE_INCREASE = UUID.fromString("0011379d-97e7-44b1-860e-9d355746e886");
   private static final Random rand = new Random();
   private static final Map<PlayerFavourData.VaultGodType, VaultInfluenceHandler.InfluenceMessages> messages = new HashMap<>();

   public static void initializeInfluences(VaultRaid vault, ServerWorld world) {
      int vaultLvl = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      if (vaultLvl >= 50) {
         CrystalData data = vault.getProperties().getBase(VaultRaid.CRYSTAL_DATA).orElse(null);
         if (data != null && data.canTriggerInfluences() && data.getType().canTriggerInfluences() && vault.getPlayers().size() <= 1) {
            if (!vault.getAllObjectives().stream().anyMatch(VaultObjective::preventsInfluences)) {
               VaultInfluences influences = vault.getInfluences();
               PlayerFavourData favourData = PlayerFavourData.get(world);
               Map<PlayerFavourData.VaultGodType, Integer> positives = new HashMap<>();
               Map<PlayerFavourData.VaultGodType, Integer> negatives = new HashMap<>();

               for (PlayerFavourData.VaultGodType type : PlayerFavourData.VaultGodType.values()) {
                  for (VaultPlayer vPlayer : vault.getPlayers()) {
                     int favour = favourData.getFavour(vPlayer.getPlayerId(), type);
                     if (Math.abs(favour) >= 4 && !(rand.nextFloat() >= 0.35F)) {
                        if (favour < 0) {
                           negatives.put(type, favour);
                        } else {
                           positives.put(type, favour);
                        }
                        break;
                     }
                  }
               }

               positives.forEach(
                  (typex, favourx) -> {
                     Tuple<VaultInfluence, String> influenceResult = getPositiveInfluence(typex, Math.abs(favourx));
                     influences.addInfluence((VaultInfluence)influenceResult.func_76341_a(), vault, world);
                     String message = messages.get(typex).getPositiveMessage();
                     IFormattableTextComponent vgName = new StringTextComponent(typex.getName()).func_240699_a_(typex.getChatColor());
                     vgName.func_240700_a_(style -> style.func_240716_a_(new HoverEvent(Action.field_230550_a_, typex.getHoverChatComponent())));
                     IFormattableTextComponent txt = new StringTextComponent("");
                     txt.func_230529_a_(new StringTextComponent("[VG] ").func_240699_a_(TextFormatting.DARK_PURPLE))
                        .func_230529_a_(vgName)
                        .func_230529_a_(new StringTextComponent(": ").func_240699_a_(TextFormatting.WHITE))
                        .func_230529_a_(new StringTextComponent(message));
                     IFormattableTextComponent info = new StringTextComponent((String)influenceResult.func_76340_b()).func_240699_a_(TextFormatting.DARK_GRAY);
                     vault.getPlayers().forEach(vPlayerx -> vPlayerx.runIfPresent(world.func_73046_m(), sPlayer -> {
                        sPlayer.func_145747_a(txt, Util.field_240973_b_);
                        sPlayer.func_145747_a(info, Util.field_240973_b_);
                     }));
                  }
               );
               negatives.forEach(
                  (typex, favourx) -> {
                     Tuple<VaultInfluence, String> influenceResult = getNegativeInfluence(typex, Math.abs(favourx));
                     influences.addInfluence((VaultInfluence)influenceResult.func_76341_a(), vault, world);
                     String message = messages.get(typex).getNegativeMessage();
                     IFormattableTextComponent vgName = new StringTextComponent(typex.getName()).func_240699_a_(typex.getChatColor());
                     vgName.func_240700_a_(style -> style.func_240716_a_(new HoverEvent(Action.field_230550_a_, typex.getHoverChatComponent())));
                     IFormattableTextComponent txt = new StringTextComponent("");
                     txt.func_230529_a_(new StringTextComponent("[VG] ").func_240699_a_(TextFormatting.DARK_PURPLE))
                        .func_230529_a_(vgName)
                        .func_230529_a_(new StringTextComponent(": ").func_240699_a_(TextFormatting.WHITE))
                        .func_230529_a_(new StringTextComponent(message));
                     IFormattableTextComponent info = new StringTextComponent((String)influenceResult.func_76340_b()).func_240699_a_(TextFormatting.DARK_GRAY);
                     vault.getPlayers().forEach(vPlayerx -> vPlayerx.runIfPresent(world.func_73046_m(), sPlayer -> {
                        sPlayer.func_145747_a(txt, Util.field_240973_b_);
                        sPlayer.func_145747_a(info, Util.field_240973_b_);
                     }));
                  }
               );
            }
         }
      }
   }

   private static Tuple<VaultInfluence, String> getPositiveInfluence(PlayerFavourData.VaultGodType type, int favour) {
      DecimalFormat percentFormat = new DecimalFormat("0.##");
      switch (type) {
         case BENEVOLENT:
            VaultInfluence influence;
            String text;
            switch (rand.nextInt(3)) {
               case 0:
               default:
                  int less = Math.min(favour * 4, 60);
                  float perc = 1.0F - less / 100.0F;
                  influence = new MobAttributeInfluence(
                     Attributes.field_233818_a_, new AttributeModifier(BENEVOLENT_HP_REDUCTION, "Favours", perc - 1.0F, Operation.MULTIPLY_TOTAL)
                  );
                  text = "Monsters have " + less + "% less Health";
                  break;
               case 1: {
                  int ampl = MathHelper.func_76125_a(favour / 2, 1, 10);
                  influence = new EffectInfluence(Effects.field_76443_y, ampl);
                  text = "Grants +" + ampl + " Saturation";
                  break;
               }
               case 2: {
                  int ampl = MathHelper.func_76125_a(favour / 5, 1, 3);
                  influence = new EffectInfluence(Effects.field_76428_l, ampl);
                  text = "Grants +" + ampl + " Regeneration";
               }
            }

            return new Tuple(influence, text);
         case OMNISCIENT:
            VaultInfluence influence;
            String text;
            switch (rand.nextInt(3)) {
               case 0:
               default:
                  int less = Math.min(favour * 5, 50);
                  float perc = 1.0F - less / 100.0F;
                  influence = new MobAttributeInfluence(
                     Attributes.field_233821_d_, new AttributeModifier(OMNISCIENT_SPEED_REDUCTION, "Favours", perc - 1.0F, Operation.MULTIPLY_TOTAL)
                  );
                  text = "Monsters move " + less + "% slower";
                  break;
               case 1:
                  int increased = 10 + Math.min((favour - 4) * 3, 30);
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.PARRY, increased / 100.0F, false);
                  text = "Grants " + increased + "% Parry";
                  break;
               case 2:
                  int ampl = MathHelper.func_76125_a(favour / 5, 1, 3);
                  influence = new EffectInfluence(Effects.field_188425_z, ampl);
                  text = "Grants +" + ampl + " Luck";
            }

            return new Tuple(influence, text);
         case TIMEKEEPER:
            VaultInfluence influence;
            String text;
            switch (rand.nextInt(3)) {
               case 0:
               default:
                  int dmgIncrease = 10 + (favour - 4) * 5;
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.DURABILITY_DAMAGE, dmgIncrease / 100.0F, true);
                  text = "Your gear takes " + dmgIncrease + "% less durability damage.";
                  break;
               case 1:
                  int less = (int)(favour * 3.125F);
                  float perc = 1.0F - less / 100.0F;
                  influence = new MobAttributeInfluence(
                     Attributes.field_233823_f_, new AttributeModifier(MALEVOLENCE_DAMAGE_REDUCTION, "Favours", perc - 1.0F, Operation.MULTIPLY_TOTAL)
                  );
                  text = "Monsters deal " + less + "% less damage";
                  break;
               case 2:
                  int ampl = Math.min(favour / 4, 4);
                  influence = new EffectInfluence(Effects.field_76422_e, ampl);
                  text = "Grants +" + ampl + " Haste";
            }

            return new Tuple(influence, text);
         case MALEVOLENCE:
            VaultInfluence influence;
            String text;
            switch (rand.nextInt(3)) {
               case 0:
               default:
                  int less = MathHelper.func_76125_a(favour / 4, 1, 5);
                  influence = new MobsInfluence(-less);
                  text = less + " fewer monsters spawn around you";
                  break;
               case 1:
                  float increased = 10.0F + (favour - 4) * 2.5F;
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.FATAL_STRIKE_CHANCE, increased / 100.0F, false);
                  text = "Grants +" + percentFormat.format(increased) + "% Fatal Strike Chance";
                  break;
               case 2:
                  int more = Math.min(favour * 6, 75);
                  float perc = 1.0F + more / 100.0F;
                  influence = new DamageInfluence(perc);
                  text = "You deal " + more + "% more damage";
            }

            return new Tuple(influence, text);
         default:
            throw new IllegalArgumentException("Unknown type: " + type.name());
      }
   }

   private static Tuple<VaultInfluence, String> getNegativeInfluence(PlayerFavourData.VaultGodType type, int favour) {
      switch (type) {
         case BENEVOLENT:
            VaultInfluence influence;
            String text;
            switch (rand.nextInt(3)) {
               case 0:
               default:
                  int reduced = 50 + (favour - 4) * 3;
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.HEALING_EFFECTIVENESS, 1.0F - reduced / 100.0F, true);
                  text = "Effectiveness of Healing is reduced by " + reduced + "%";
                  break;
               case 1:
                  int more = 50 + favour * 20;
                  float perc = more / 100.0F;
                  influence = new MobAttributeInfluence(
                     Attributes.field_233818_a_, new AttributeModifier(BENEVOLENT_HP_INCREASE, "Favours", perc, Operation.MULTIPLY_TOTAL)
                  );
                  text = "Monsters have " + more + "% more Health";
                  break;
               case 2:
                  int ampl = Math.min(favour - 1, 10);
                  influence = new EffectInfluence(Effects.field_76438_s, ampl);
                  text = "Applies +" + ampl + " Hunger";
            }

            return new Tuple(influence, text);
         case OMNISCIENT:
            VaultInfluence influence;
            String text;
            switch (rand.nextInt(3)) {
               case 0:
               default:
                  int mre = 20 + (favour - 4) * 10;
                  float prc = 1.0F + mre / 100.0F;
                  influence = new DamageTakenInfluence(prc);
                  text = "You take " + mre + "% more damage";
                  break;
               case 1:
                  int less = 50 + MathHelper.func_76123_f((favour - 4) / 12.0F * 50.0F);
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.PARRY, 1.0F - less / 100.0F, true);
                  text = "You have " + less + "% less Parry";
                  break;
               case 2:
                  int more = Math.max((favour - 3) * 10, 10);
                  float perc = more / 100.0F;
                  influence = new MobAttributeInfluence(
                     Attributes.field_233821_d_, new AttributeModifier(OMNISCIENT_SPEED_REDUCTION, "Favours", perc, Operation.MULTIPLY_TOTAL)
                  );
                  text = "Monsters move " + more + "% faster";
            }

            return new Tuple(influence, text);
         case TIMEKEEPER:
            VaultInfluence influence;
            String text;
            switch (rand.nextInt(3)) {
               case 0:
               default:
                  int cdrReduction = Math.min(50 + (favour - 4) * 5, 100);
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.COOLDOWN_REDUCTION, 1.0F - cdrReduction / 100.0F, true);
                  text = "You have " + cdrReduction + "% less cooldown reduction.";
                  break;
               case 1:
                  int dmgIncrease = 10 + (favour - 4) * 5;
                  influence = new VaultAttributeInfluence(VaultAttributeInfluence.Type.DURABILITY_DAMAGE, 1.0F + dmgIncrease / 100.0F, true);
                  text = "Your gear takes " + dmgIncrease + "% more durability damage.";
                  break;
               case 2:
                  int ampl = Math.min(favour / 4, 4);
                  influence = new EffectInfluence(Effects.field_76419_f, ampl);
                  text = "Grants +" + ampl + " Mining Fatigue";
            }

            return new Tuple(influence, text);
         case MALEVOLENCE:
            VaultInfluence influence;
            String text;
            switch (rand.nextInt(3)) {
               case 0:
               default:
                  int less = 10 + Math.min((favour - 4) * 7, 85);
                  influence = new DamageInfluence(1.0F - less / 100.0F);
                  text = "You deal " + less + "% less damage";
                  break;
               case 1:
                  int moreDmg = 20 + Math.min(favour * 12, 140);
                  float perc = moreDmg / 100.0F;
                  influence = new MobAttributeInfluence(
                     Attributes.field_233823_f_, new AttributeModifier(MALEVOLENCE_DAMAGE_INCREASE, "Favours", perc, Operation.MULTIPLY_TOTAL)
                  );
                  text = "Monsters deal " + moreDmg + "% more damage";
                  break;
               case 2:
                  int more = MathHelper.func_76141_d(Math.min(favour - 4 + 1, 20));
                  influence = new MobsInfluence(more);
                  text = more + " additional monsters spawn around you";
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
      messages.put(PlayerFavourData.VaultGodType.BENEVOLENT, benevolent);
      VaultInfluenceHandler.InfluenceMessages omniscient = new VaultInfluenceHandler.InfluenceMessages();
      omniscient.positiveMessages.add("May foresight guide your step.");
      omniscient.positiveMessages.add("Careful planning and strategy may lead you.");
      omniscient.positiveMessages.add("A set choice; followed through and flawlessly executed.");
      omniscient.positiveMessages.add("Chance's hand may favour your goals.");
      omniscient.negativeMessages.add("A choice; leading one to disfavour.");
      omniscient.negativeMessages.add("Riches, Wealth, Prosperity. An illusion.");
      omniscient.negativeMessages.add("Cascading eventuality. Solidified in ruin.");
      omniscient.negativeMessages.add("Diminishing reality.");
      messages.put(PlayerFavourData.VaultGodType.OMNISCIENT, omniscient);
      VaultInfluenceHandler.InfluenceMessages timekeeper = new VaultInfluenceHandler.InfluenceMessages();
      timekeeper.positiveMessages.add("Seize the opportunity.");
      timekeeper.positiveMessages.add("A single instant, stretched to infinity.");
      timekeeper.positiveMessages.add("Your future glows golden with possibility.");
      timekeeper.positiveMessages.add("Hasten and value every passing moment.");
      timekeeper.negativeMessages.add("Eternity in the moment of standstill.");
      timekeeper.negativeMessages.add("Drown in the flow of time.");
      timekeeper.negativeMessages.add("Transience manifested.");
      timekeeper.negativeMessages.add("Immutable emptiness.");
      messages.put(PlayerFavourData.VaultGodType.TIMEKEEPER, timekeeper);
      VaultInfluenceHandler.InfluenceMessages malevolence = new VaultInfluenceHandler.InfluenceMessages();
      malevolence.positiveMessages.add("Enforce your path through obstacles.");
      malevolence.positiveMessages.add("Our vigor may aid your conquest.");
      malevolence.positiveMessages.add("Cherish this mote of my might.");
      malevolence.positiveMessages.add("A tempest incarnate.");
      malevolence.negativeMessages.add("Feel our domain's wrath.");
      malevolence.negativeMessages.add("Malice and spite given form.");
      malevolence.negativeMessages.add("Flee before the growing horde.");
      malevolence.negativeMessages.add("Perish from your own ambition.");
      messages.put(PlayerFavourData.VaultGodType.MALEVOLENCE, malevolence);
   }

   private static class InfluenceMessages {
      private final List<String> positiveMessages = new ArrayList<>();
      private final List<String> negativeMessages = new ArrayList<>();

      private InfluenceMessages() {
      }

      private String getNegativeMessage() {
         return this.negativeMessages.get(VaultInfluenceHandler.rand.nextInt(this.negativeMessages.size()));
      }

      private String getPositiveMessage() {
         return this.positiveMessages.get(VaultInfluenceHandler.rand.nextInt(this.positiveMessages.size()));
      }
   }
}
