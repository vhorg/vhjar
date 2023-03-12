package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.VaultMod;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class AugmentConfig extends Config {
   @Expose
   private float dropChance;
   @Expose
   private Map<ResourceLocation, Map<String, Integer>> drops;

   @Override
   public String getName() {
      return "augment";
   }

   public float getDropChance() {
      return this.dropChance;
   }

   public Optional<ItemStack> generate(ResourceLocation id, RandomSource random) {
      return Optional.ofNullable(this.drops.get(id)).flatMap(map -> {
         WeightedList<String> list = new WeightedList<>();
         map.forEach(list::add);
         return list.getRandom(random);
      }).map(s -> {
         ItemParser parser;
         try {
            parser = new ItemParser(new StringReader(s), false).parse();
         } catch (CommandSyntaxException var3) {
            var3.printStackTrace();
            return ItemStack.EMPTY;
         }

         ItemStack stack = new ItemStack(parser.getItem());
         if (parser.getNbt() != null) {
            stack.setTag(parser.getNbt());
         }

         return stack;
      });
   }

   @Override
   protected void reset() {
      this.dropChance = 1.0F;
      this.drops = new LinkedHashMap<>();
      Map<String, Integer> map = new LinkedHashMap<>();
      map.put("{theme:\"the_vault:classic_vault_ice\", model:0}", 5);
      map.put("{theme:\"the_vault:classic_vault_ice_cave\", model:0}", 3);
      map.put("{theme:\"the_vault:classic_vault_festive\", model:0}", 1);
      map.put("{theme:\"the_vault:classic_vault_gingerbread\", model:0}", 1);
      this.drops.put(VaultMod.id("classic_vault_ice"), map);
      map = new LinkedHashMap<>();
      map.put("{theme:\"the_vault:classic_vault_desert\", model:0}", 5);
      map.put("{theme:\"the_vault:classic_vault_sandy_cave\", model:0}", 2);
      map.put("{theme:\"the_vault:classic_vault_beach\", model:0}", 1);
      map.put("{theme:\"the_vault:classic_vault_mesa\", model:0}", 1);
      map.put("{theme:\"the_vault:classic_vault_deep_mesa\", model:0}", 1);
      this.drops.put(VaultMod.id("classic_vault_desert"), map);
   }
}
