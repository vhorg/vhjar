package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.RelicPartItem;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class VaultRelicsConfig extends Config {
   @Expose
   private int extraTickPerSet;
   @Expose
   private List<VaultRelicsConfig.Relic> relicDrops;

   @Override
   public String getName() {
      return "vault_relics";
   }

   public int getExtraTickPerSet() {
      return this.extraTickPerSet;
   }

   public RelicPartItem getRandomPart() {
      this.relicDrops.sort(Comparator.comparingInt(a -> a.WEIGHT));
      int totalWeight = this.relicDrops.stream().mapToInt(relic -> relic.WEIGHT).sum();
      int random = new Random().nextInt(totalWeight);

      for (VaultRelicsConfig.Relic relicDrop : this.relicDrops) {
         if (random < relicDrop.WEIGHT) {
            return (RelicPartItem)Registry.field_212630_s.func_82594_a(new ResourceLocation(relicDrop.NAME));
         }

         random -= relicDrop.WEIGHT;
      }

      return (RelicPartItem)Registry.field_212630_s.func_82594_a(new ResourceLocation(this.relicDrops.get(this.relicDrops.size() - 1).NAME));
   }

   @Override
   protected void reset() {
      this.extraTickPerSet = 1200;
      this.relicDrops = new LinkedList<>();
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.DRAGON_HEAD_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.DRAGON_TAIL_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.DRAGON_FOOT_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.DRAGON_CHEST_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.DRAGON_BREATH_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.MINERS_DELIGHT_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.MINERS_LIGHT_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.PICKAXE_HANDLE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.PICKAXE_HEAD_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.PICKAXE_TOOL_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.SWORD_BLADE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.SWORD_HANDLE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.SWORD_STICK_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.WARRIORS_ARMOUR_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.WARRIORS_CHARM_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.DIAMOND_ESSENCE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.GOLD_ESSENCE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.MYSTIC_GEM_ESSENCE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.NETHERITE_ESSENCE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.PLATINUM_ESSENCE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.TWITCH_EMOTE1_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.TWITCH_EMOTE2_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.TWITCH_EMOTE3_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.TWITCH_EMOTE4_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.TWITCH_EMOTE5_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.CUPCAKE_BLUE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.CUPCAKE_LIME_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.CUPCAKE_PINK_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.CUPCAKE_PURPLE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.CUPCAKE_RED_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.AIR_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.SPIRIT_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.FIRE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.EARTH_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.WATER_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.TWOLF999_HEAD_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.TWOLF999_COMBAT_VEST_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.TWOLF999_COMBAT_LEGGINGS_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.TWOLF999_COMBAT_GLOVES_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.TWOLF999_COMBAT_BOOTS_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.ARMOR_OF_FORBEARANCE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.HEART_OF_THE_VOID_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.NEMESIS_THWARTER_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.REVERENCE_EDGE_RELIC.getRegistryName().toString(), 1));
      this.relicDrops.add(new VaultRelicsConfig.Relic(ModItems.WINGS_OF_EQUITY_RELIC.getRegistryName().toString(), 1));
   }

   public static class Relic {
      @Expose
      public String NAME;
      @Expose
      public int WEIGHT;

      public Relic(String name, int weight) {
         this.NAME = name;
         this.WEIGHT = weight;
      }
   }
}
