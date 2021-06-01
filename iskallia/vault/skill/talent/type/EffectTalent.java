package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.skill.set.EffectSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class EffectTalent extends PlayerTalent {
   @Expose
   private final String effect;
   @Expose
   private final int amplifier;
   @Expose
   private final String type;
   @Expose
   private final String operator;

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

   @Override
   public void tick(PlayerEntity player) {
      Tuple<Integer, EffectTalent> data = getData(player, (ServerWorld)player.field_70170_p, this.getEffect());
      EffectInstance activeEffect = player.func_70660_b(this.getEffect());
      if ((Integer)data.func_76341_a() >= 0) {
         EffectInstance newEffect = new EffectInstance(
            this.getEffect(),
            100,
            (Integer)data.func_76341_a(),
            false,
            ((EffectTalent)data.func_76340_b()).getType().showParticles,
            ((EffectTalent)data.func_76340_b()).getType().showIcon
         );
         if (activeEffect == null || activeEffect.func_76458_c() < newEffect.func_76458_c()) {
            player.func_195064_c(newEffect);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (!event.player.field_70170_p.field_72995_K) {
         ForgeRegistries.POTIONS
            .forEach(
               effect -> {
                  Tuple<Integer, EffectTalent> data = getData(event.player, (ServerWorld)event.player.field_70170_p, effect);
                  EffectInstance activeEffect = event.player.func_70660_b(effect);
                  if ((Integer)data.func_76341_a() >= 0) {
                     EffectInstance newEffect = new EffectInstance(
                        effect,
                        339,
                        (Integer)data.func_76341_a(),
                        false,
                        ((EffectTalent)data.func_76340_b()).getType().showParticles,
                        ((EffectTalent)data.func_76340_b()).getType().showIcon
                     );
                     if (activeEffect == null || activeEffect.func_76458_c() < newEffect.func_76458_c()) {
                        event.player.func_195064_c(newEffect);
                     } else if (activeEffect.func_76459_b() <= 259) {
                        event.player.func_195064_c(newEffect);
                     }
                  }
               }
            );
      }
   }

   public static Tuple<Integer, EffectTalent> getData(PlayerEntity player, ServerWorld world, Effect effect) {
      TalentTree abilities = PlayerTalentsData.get(world).getTalents(player);
      SetTree sets = PlayerSetsData.get(world).getSets(player);
      List<EffectTalent> overrides = new ArrayList<>();
      List<EffectTalent> addends = new ArrayList<>();

      for (TalentNode<?> node : abilities.getNodes()) {
         if (node.getTalent() instanceof EffectTalent) {
            EffectTalent talent = (EffectTalent)node.getTalent();
            if (talent.getEffect() == effect) {
               if (talent.getOperator() == EffectTalent.Operator.SET) {
                  overrides.add(talent);
               } else if (talent.getOperator() == EffectTalent.Operator.ADD) {
                  addends.add(talent);
               }
            }
         }
      }

      for (SetNode<?> nodex : sets.getNodes()) {
         if (nodex.getSet() instanceof EffectSet) {
            EffectSet set = (EffectSet)nodex.getSet();
            if (set.getChild().getEffect() == effect) {
               if (set.getChild().getOperator() == EffectTalent.Operator.SET) {
                  overrides.add(set.getChild());
               } else if (set.getChild().getOperator() == EffectTalent.Operator.ADD) {
                  addends.add(set.getChild());
               }
            }
         }
      }

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = player.func_184582_a(slot);

         for (EffectTalent effect1 : ModAttributes.EXTRA_EFFECTS.getOrDefault(stack, new ArrayList<>()).getValue(stack)) {
            if (effect1.getEffect() == effect) {
               if (effect1.getOperator() == EffectTalent.Operator.SET) {
                  overrides.add(effect1);
               } else if (effect1.getOperator() == EffectTalent.Operator.ADD) {
                  addends.add(effect1);
               }
            }
         }
      }

      if (overrides.isEmpty() && addends.isEmpty()) {
         return new Tuple(-1, null);
      } else {
         int newAmplifier = overrides.isEmpty() ? -1 : overrides.stream().mapToInt(EffectTalent::getAmplifier).max().getAsInt();
         newAmplifier += addends.stream().mapToInt(EffectTalent::getAmplifier).sum();
         EffectTalent priority = overrides.isEmpty()
            ? addends.stream().max(Comparator.comparingInt(EffectTalent::getAmplifier)).get()
            : overrides.stream().max(Comparator.comparingInt(EffectTalent::getAmplifier)).get();
         return new Tuple(newAmplifier, priority);
      }
   }

   @Override
   public void onRemoved(PlayerEntity player) {
      player.func_195063_d(this.getEffect());
   }

   public static enum Operator {
      SET("set"),
      ADD("add");

      private static Map<String, EffectTalent.Operator> STRING_TO_TYPE = Arrays.stream(values())
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

      private static Map<String, EffectTalent.Type> STRING_TO_TYPE = Arrays.stream(values())
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
