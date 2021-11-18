package iskallia.vault.skill.talent.type;

import com.google.common.collect.Sets;
import com.google.gson.annotations.Expose;
import iskallia.vault.attribute.EffectAttribute;
import iskallia.vault.aura.AuraManager;
import iskallia.vault.aura.type.EffectAuraConfig;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.paxel.enhancement.EffectEnhancement;
import iskallia.vault.item.paxel.enhancement.PaxelEnhancement;
import iskallia.vault.item.paxel.enhancement.PaxelEnhancements;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.config.sub.GhostWalkRegenerationConfig;
import iskallia.vault.skill.ability.effect.GhostWalkAbility;
import iskallia.vault.skill.set.EffectSet;
import iskallia.vault.skill.set.PlayerSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.archetype.WardTalent;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.influence.EffectInfluence;
import iskallia.vault.world.vault.modifier.EffectModifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class EffectTalent extends PlayerTalent {
   @Expose
   private final String effect;
   @Expose
   private final int amplifier;
   @Expose
   private final String type;
   @Expose
   private final String operator;

   @Override
   public String toString() {
      return "EffectTalent{effect='"
         + this.effect
         + '\''
         + ", amplifier="
         + this.amplifier
         + ", type='"
         + this.type
         + '\''
         + ", operator='"
         + this.operator
         + '\''
         + '}';
   }

   public EffectTalent(int cost, Effect effect, int amplifier, EffectTalent.Type type, EffectTalent.Operator operator) {
      this(cost, Registry.field_212631_t.func_177774_c(effect).toString(), amplifier, type.toString(), operator.toString());
   }

   public EffectTalent(int cost, String effect, int amplifier, String type, String operator) {
      super(cost);
      this.effect = effect;
      this.amplifier = amplifier;
      this.type = type;
      this.operator = operator;
   }

   public Effect getEffect() {
      return (Effect)Registry.field_212631_t.func_82594_a(new ResourceLocation(this.effect));
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public EffectTalent.Type getType() {
      return EffectTalent.Type.fromString(this.type);
   }

   public EffectTalent.Operator getOperator() {
      return EffectTalent.Operator.fromString(this.operator);
   }

   public EffectInstance makeEffect(int duration) {
      return new EffectInstance(this.getEffect(), duration, this.getAmplifier(), true, this.getType().showParticles, this.getType().showIcon);
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      PlayerEntity player = event.player;
      if (!player.field_70170_p.field_72995_K && event.phase != Phase.START) {
         Collection<Effect> immunities = getImmunities(player);
         Map<Effect, EffectTalent.CombinedEffects> effectMap = getEffectData(
            player, (ServerWorld)player.func_130014_f_(), (Predicate<Effect>)(effect -> !immunities.contains(effect))
         );
         applyEffects(player, effectMap);
      }
   }

   public static void applyEffects(LivingEntity entity, Map<Effect, EffectTalent.CombinedEffects> effects) {
      effects.forEach(
         (effect, combinedEffects) -> {
            int amplifier = combinedEffects.getAmplifier();
            if (amplifier >= 0) {
               EffectTalent displayTalent = combinedEffects.getDisplayEffect();
               EffectInstance activeEffect = entity.func_70660_b(effect);
               EffectInstance newEffect = new EffectInstance(
                  effect, 339, amplifier, false, displayTalent.getType().showParticles, displayTalent.getType().showIcon
               );
               if (activeEffect == null || activeEffect.func_76458_c() < newEffect.func_76458_c()) {
                  entity.func_195064_c(newEffect);
               } else if (activeEffect.func_76459_b() <= 259) {
                  entity.func_195064_c(newEffect);
               }
            }
         }
      );
   }

   public static Map<Effect, EffectTalent.CombinedEffects> getEffectData(PlayerEntity player, ServerWorld world) {
      return getEffectData(player, world, (Predicate<Effect>)(effect -> true));
   }

   public static EffectTalent.CombinedEffects getEffectData(PlayerEntity player, ServerWorld world, Effect effect) {
      Map<Effect, EffectTalent.CombinedEffects> effectData = getEffectData(player, world, (Predicate<Effect>)(otherEffect -> otherEffect == effect));
      return effectData.getOrDefault(effect, new EffectTalent.CombinedEffects());
   }

   public static Map<Effect, EffectTalent.CombinedEffects> getEffectData(PlayerEntity player, ServerWorld world, Effect... effects) {
      Set<Effect> filter = Sets.newHashSet(effects);
      return getEffectData(player, world, filter::contains);
   }

   public static Map<Effect, EffectTalent.CombinedEffects> getEffectData(PlayerEntity player, ServerWorld world, Predicate<Effect> effectFilter) {
      Map<Effect, EffectTalent.CombinedEffects> effectMap = new HashMap<>();
      TalentTree talents = PlayerTalentsData.get(world).getTalents(player);
      SetTree sets = PlayerSetsData.get(world).getSets(player);
      talents.getLearnedNodes(EffectTalent.class).stream().map(TalentNode::getTalent).forEach(effectTalent -> {
         if (effectFilter.test(effectTalent.getEffect())) {
            effectMap.computeIfAbsent(effectTalent.getEffect(), effect -> new EffectTalent.CombinedEffects()).addTalent(effectTalent);
         }
      });

      for (SetNode<?> node : sets.getNodes()) {
         if (node.getSet() instanceof EffectSet) {
            EffectSet set = (EffectSet)node.getSet();
            if (effectFilter.test(set.getChild().getEffect())) {
               EffectTalent.CombinedEffects combinedEffects = effectMap.computeIfAbsent(
                  set.getChild().getEffect(), effect -> new EffectTalent.CombinedEffects()
               );
               combinedEffects.addTalent(set.getChild());
            }
         }
      }

      if (effectFilter.test(Effects.field_76428_l) && player.func_70644_a(ModEffects.GHOST_WALK)) {
         AbilityTree abilities = PlayerAbilitiesData.get(world).getAbilities(player);

         for (AbilityNode<?, ?> nodex : abilities.getNodes()) {
            if (nodex.isLearned() && nodex.getAbility() instanceof GhostWalkAbility) {
               AbilityConfig cfg = nodex.getAbilityConfig();
               if (cfg instanceof GhostWalkRegenerationConfig) {
                  GhostWalkRegenerationConfig config = (GhostWalkRegenerationConfig)cfg;
                  effectMap.computeIfAbsent(Effects.field_76428_l, effect -> new EffectTalent.CombinedEffects()).addTalent(config.makeRegenerationTalent());
               }
            }
         }
      }

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = player.func_184582_a(slot);
         if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
            for (EffectTalent gearEffect : ModAttributes.EXTRA_EFFECTS.getOrDefault(stack, new ArrayList<>()).getValue(stack)) {
               if (effectFilter.test(gearEffect.getEffect())) {
                  effectMap.computeIfAbsent(gearEffect.getEffect(), effect -> new EffectTalent.CombinedEffects()).addTalent(gearEffect);
               }
            }
         }
      }

      ItemStack heldItem = player.func_184586_b(Hand.MAIN_HAND);
      if (heldItem.func_77973_b() == ModItems.VAULT_PAXEL) {
         PaxelEnhancement enhancement = PaxelEnhancements.getEnhancement(heldItem);
         if (enhancement instanceof EffectEnhancement) {
            EffectEnhancement effectEnhancement = (EffectEnhancement)enhancement;
            if (effectFilter.test(effectEnhancement.getEffect())) {
               effectMap.computeIfAbsent(effectEnhancement.getEffect(), ct -> new EffectTalent.CombinedEffects()).addTalent(effectEnhancement.makeTalent());
            }
         }
      }

      if (WardTalent.isGrantedFullAbsorptionEffect(world, player)) {
         talents.getLearnedNodes(WardTalent.class).forEach(talent -> {
            EffectTalent effect = talent.getTalent().getFullAbsorptionEffect();
            if (effect != null && effectFilter.test(effect.getEffect())) {
               effectMap.computeIfAbsent(effect.getEffect(), ct -> new EffectTalent.CombinedEffects()).addTalent(effect);
            }
         });
      }

      VaultRaid vault = VaultRaidData.get(world).getActiveFor(player.func_110124_au());
      if (vault != null) {
         vault.getActiveModifiersFor(PlayerFilter.of(player), EffectModifier.class).forEach(modifier -> {
            if (effectFilter.test(modifier.getEffect())) {
               effectMap.computeIfAbsent(modifier.getEffect(), effect -> new EffectTalent.CombinedEffects()).addTalent(modifier.makeTalent());
            }
         });
         vault.getInfluences().getInfluences(EffectInfluence.class).forEach(influence -> {
            if (effectFilter.test(influence.getEffect())) {
               effectMap.computeIfAbsent(influence.getEffect(), effect -> new EffectTalent.CombinedEffects()).addTalent(influence.makeTalent());
            }
         });
      }

      AuraManager.getInstance()
         .getAurasAffecting(player)
         .stream()
         .filter(aura -> aura.getAura() instanceof EffectAuraConfig)
         .map(aura -> (EffectAuraConfig)aura.getAura())
         .forEach(effectAura -> {
            EffectTalent auraTalent = effectAura.getEffect();
            if (effectFilter.test(auraTalent.getEffect())) {
               effectMap.computeIfAbsent(auraTalent.getEffect(), effect -> new EffectTalent.CombinedEffects()).addTalent(auraTalent);
            }
         });
      effectMap.values().forEach(rec$ -> rec$.finish());
      return effectMap;
   }

   public static Map<Effect, EffectTalent.CombinedEffects> getGearEffectData(LivingEntity entity) {
      return getGearEffectData(entity, effect -> true);
   }

   public static Map<Effect, EffectTalent.CombinedEffects> getGearEffectData(LivingEntity entity, Predicate<Effect> effectFilter) {
      Map<Effect, EffectTalent.CombinedEffects> effectMap = new HashMap<>();

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = entity.func_184582_a(slot);
         if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
            for (EffectTalent gearEffect : ModAttributes.EXTRA_EFFECTS.getOrDefault(stack, new ArrayList<>()).getValue(stack)) {
               if (effectFilter.test(gearEffect.getEffect())) {
                  effectMap.computeIfAbsent(gearEffect.getEffect(), effect -> new EffectTalent.CombinedEffects()).addTalent(gearEffect);
               }
            }
         }
      }

      effectMap.values().forEach(rec$ -> rec$.finish());
      return effectMap;
   }

   public static Collection<Effect> getImmunities(LivingEntity entity) {
      Set<Effect> immunities = new HashSet<>();

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = entity.func_184582_a(slot);
         ModAttributes.EFFECT_IMMUNITY
            .get(stack)
            .map(attribute -> attribute.getValue(stack))
            .ifPresent(effectList -> effectList.stream().map(EffectAttribute.Instance::toEffect).forEach(immunities::add));
      }

      if (entity instanceof PlayerEntity && PlayerSet.isActive(VaultGear.Set.DIVINITY, (PlayerEntity)entity)) {
         ForgeRegistries.POTIONS.getValues().stream().filter(e -> !e.func_188408_i()).forEach(immunities::add);
      }

      return immunities;
   }

   @Override
   public void onRemoved(PlayerEntity player) {
      player.func_195063_d(this.getEffect());
   }

   public static class CombinedEffects {
      private EffectTalent maxOverride = null;
      private final List<EffectTalent> addends = new ArrayList<>();
      private int amplifier = -1;
      private EffectTalent displayEffect = null;

      private void addTalent(EffectTalent talent) {
         if (talent.getOperator() == EffectTalent.Operator.SET) {
            this.setOverride(talent);
         } else if (talent.getOperator() == EffectTalent.Operator.ADD) {
            this.addAddend(talent);
         }
      }

      private void setOverride(EffectTalent talent) {
         if (this.maxOverride == null) {
            this.maxOverride = talent;
         } else if (talent.amplifier > this.maxOverride.amplifier) {
            this.maxOverride = talent;
         }
      }

      private void addAddend(EffectTalent talent) {
         this.addends.add(talent);
      }

      private void finish() {
         this.amplifier = this.maxOverride == null ? -1 : this.maxOverride.getAmplifier();
         this.amplifier = this.amplifier + this.addends.stream().mapToInt(EffectTalent::getAmplifier).sum();
         if (this.maxOverride == null && !this.addends.isEmpty()) {
            this.displayEffect = this.addends.stream().max(Comparator.comparingInt(EffectTalent::getAmplifier)).get();
         } else {
            this.displayEffect = this.maxOverride;
         }
      }

      public int getAmplifier() {
         return this.amplifier;
      }

      @Nullable
      public EffectTalent getDisplayEffect() {
         return this.displayEffect;
      }
   }

   public static enum Operator {
      SET("set"),
      ADD("add");

      private static final Map<String, EffectTalent.Operator> STRING_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(EffectTalent.Operator::toString, o -> (EffectTalent.Operator)o));
      public final String name;

      private Operator(String name) {
         this.name = name;
      }

      public static EffectTalent.Operator fromString(String type) {
         return STRING_TO_TYPE.get(type);
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   public static enum Type {
      HIDDEN("hidden", false, false),
      PARTICLES_ONLY("particles_only", true, false),
      ICON_ONLY("icon_only", false, true),
      ALL("all", true, true);

      private static final Map<String, EffectTalent.Type> STRING_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(EffectTalent.Type::toString, o -> (EffectTalent.Type)o));
      public final String name;
      public final boolean showParticles;
      public final boolean showIcon;

      private Type(String name, boolean showParticles, boolean showIcon) {
         this.name = name;
         this.showParticles = showParticles;
         this.showIcon = showIcon;
      }

      public static EffectTalent.Type fromString(String type) {
         return STRING_TO_TYPE.get(type);
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
