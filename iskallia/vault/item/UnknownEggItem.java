package iskallia.vault.item;

import iskallia.vault.Vault;
import iskallia.vault.attribute.StringAttribute;
import iskallia.vault.attribute.VAttribute;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class UnknownEggItem extends Item {
   public static VAttribute<String, StringAttribute> STORED_EGG = new VAttribute<>(Vault.id("stored_egg"), StringAttribute::new);

   public UnknownEggItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public ActionResultType func_195939_a(ItemUseContext context) {
      World world = context.func_195991_k();
      if (!world.field_72995_K) {
         VaultRaid vault = VaultRaidData.get((ServerWorld)world).getAt((ServerWorld)world, context.func_195995_a());
         if (vault != null) {
            Optional<Item> egg = this.getStoredEgg(vault, context.func_195996_i(), world.func_201674_k());
            egg.ifPresent(item -> item.func_195939_a(context));
         }
      }

      return super.func_195939_a(context);
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      if (!world.field_72995_K) {
         VaultRaid vault = VaultRaidData.get((ServerWorld)world).getActiveFor(player.func_110124_au());
         Optional<Item> egg = this.getStoredEgg(vault, player.func_184586_b(hand), world.func_201674_k());
         egg.ifPresent(item -> item.func_77659_a(world, player, hand));
      }

      return super.func_77659_a(world, player, hand);
   }

   public Optional<Item> getStoredEgg(VaultRaid vault, ItemStack stack, Random random) {
      String itemName;
      if (vault == null) {
         List<Item> spawnEggs = Registry.field_212630_s.func_201756_e().filter(item -> item instanceof SpawnEggItem).collect(Collectors.toList());
         itemName = STORED_EGG.getOrCreate(stack, spawnEggs.get(random.nextInt(spawnEggs.size())).getRegistryName().toString()).getValue(stack);
      } else {
         int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
         itemName = STORED_EGG.getOrCreate(stack, ModConfigs.UNKNOWN_EGG.getForLevel(level).EGG_POOL.getRandom(random)).getValue(stack);
      }

      return itemName == null ? Optional.empty() : Registry.field_212630_s.func_241873_b(new ResourceLocation(itemName));
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      super.func_77624_a(stack, world, tooltip, flag);
      tooltip.add(
         new StringTextComponent("Target: ")
            .func_230529_a_(new StringTextComponent(STORED_EGG.getOrDefault(stack, "NONE").getValue(stack)).func_240699_a_(TextFormatting.GREEN))
      );
   }
}
