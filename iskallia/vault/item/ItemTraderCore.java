package iskallia.vault.item;

import iskallia.vault.config.entry.vending.TradeEntry;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.util.NameProviderPublic;
import iskallia.vault.util.RenameType;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import iskallia.vault.vending.TraderCore;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemTraderCore extends Item {
   public ItemTraderCore(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(1));
      this.setRegistryName(id);
   }

   public static ItemStack generate(String nickname, int vaultLevel) {
      TradeEntry tradeEntry = ModConfigs.TRADER_CORE_COMMON.getRandomTrade(vaultLevel);
      return tradeEntry == null ? ItemStack.field_190927_a : getStackFromCore(new TraderCore(nickname, tradeEntry.toTrade()));
   }

   public static ItemStack getStackFromCore(TraderCore core) {
      ItemStack stack = new ItemStack(ModItems.TRADER_CORE, 1);
      CompoundNBT nbt = new CompoundNBT();

      try {
         nbt.func_218657_a("core", NBTSerializer.serialize(core));
         stack.func_77982_d(nbt);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return stack;
   }

   public static TraderCore getCoreFromStack(ItemStack itemStack) {
      CompoundNBT nbt = itemStack.func_77978_p();
      if (nbt == null) {
         return null;
      } else {
         try {
            return NBTSerializer.deserialize(TraderCore.class, nbt.func_74775_l("core"));
         } catch (Exception var3) {
            var3.printStackTrace();
            return null;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      CompoundNBT nbt = stack.func_196082_o();
      if (nbt.func_74764_b("core")) {
         TraderCore core;
         try {
            core = NBTSerializer.deserialize(TraderCore.class, (CompoundNBT)nbt.func_74781_a("core"));
         } catch (Exception var14) {
            var14.printStackTrace();
            return;
         }

         Trade trade = core.getTrade();
         if (!trade.isValid()) {
            return;
         }

         Product buy = trade.getBuy();
         Product extra = trade.getExtra();
         Product sell = trade.getSell();
         tooltip.add(new StringTextComponent(""));
         tooltip.add(new StringTextComponent("Trader: "));
         StringTextComponent traderName = new StringTextComponent(" " + core.getName());
         traderName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16755200)));
         tooltip.add(traderName);
         tooltip.add(new StringTextComponent(""));
         tooltip.add(new StringTextComponent("Trades: "));
         if (buy != null && buy.isValid()) {
            StringTextComponent comp = new StringTextComponent(" - Buy: ");
            TranslationTextComponent name = new TranslationTextComponent(buy.getItem().func_77658_a());
            name.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16755200)));
            comp.func_230529_a_(name).func_230529_a_(new StringTextComponent(" x" + buy.getAmount()));
            tooltip.add(comp);
         }

         if (extra != null && extra.isValid()) {
            StringTextComponent comp = new StringTextComponent(" - Extra: ");
            TranslationTextComponent name = new TranslationTextComponent(extra.getItem().func_77658_a());
            name.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16755200)));
            comp.func_230529_a_(name).func_230529_a_(new StringTextComponent(" x" + extra.getAmount()));
            tooltip.add(comp);
         }

         if (sell != null && sell.isValid()) {
            StringTextComponent comp = new StringTextComponent(" - Sell: ");
            TranslationTextComponent name = new TranslationTextComponent(sell.getItem().func_77658_a());
            name.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16755200)));
            comp.func_230529_a_(name).func_230529_a_(new StringTextComponent(" x" + sell.getAmount()));
            tooltip.add(comp);
         }

         tooltip.add(new StringTextComponent(""));
         if (trade.getTradesLeft() == 0) {
            StringTextComponent comp = new StringTextComponent("[0] Sold out, sorry!");
            comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)));
            tooltip.add(comp);
         } else if (trade.getTradesLeft() == -1) {
            StringTextComponent comp = new StringTextComponent("[∞] Has unlimited trades.");
            comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(43775)));
            tooltip.add(comp);
         } else {
            StringTextComponent comp = new StringTextComponent("[" + trade.getTradesLeft() + "] Has a limited stock.");
            comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16755200)));
            tooltip.add(comp);
         }
      }
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.func_77663_a(stack, world, entity, itemSlot, isSelected);
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)entity;
         CompoundNBT nbt = stack.func_196082_o();
         if (!nbt.func_150297_b("core", 10)) {
            PlayerVaultStats stats = PlayerVaultStatsData.get(sPlayer.func_71121_q()).getVaultStats(sPlayer);
            ItemStack generated = generate(NameProviderPublic.getRandomName(), stats.getVaultLevel());
            if (!generated.func_190926_b()) {
               stack.func_77982_d(generated.func_77978_p());
            }
         }

         if (nbt.func_150297_b("rename_trader", 8)) {
            String rename = nbt.func_74779_i("rename_trader");
            stack.func_196082_o().func_74775_l("core").func_74778_a("NAME", rename);
            stack.func_196082_o().func_82580_o("rename_trader");
         }
      }
   }

   public Rarity func_77613_e(ItemStack stack) {
      return Rarity.EPIC;
   }

   public ITextComponent func_200295_i(ItemStack stack) {
      ITextComponent text = super.func_200295_i(stack);
      CompoundNBT nbt = stack.func_196082_o();
      if (nbt.func_150297_b("core", 10)) {
         try {
            TraderCore core = NBTSerializer.deserialize(TraderCore.class, nbt.func_74775_l("core"));
            text = new StringTextComponent(core.getName());
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

      return text;
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, PlayerEntity player, Hand handIn) {
      if (worldIn.field_72995_K) {
         return super.func_77659_a(worldIn, player, handIn);
      } else if (handIn == Hand.OFF_HAND) {
         return super.func_77659_a(worldIn, player, handIn);
      } else {
         ItemStack stack = player.func_184614_ca();
         if (player.func_225608_bj_()) {
            final CompoundNBT nbt = new CompoundNBT();
            nbt.func_74768_a("RenameType", RenameType.TRADER_CORE.ordinal());
            nbt.func_218657_a("Data", stack.serializeNBT());
            NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
               public ITextComponent func_145748_c_() {
                  return new StringTextComponent("Trader Core");
               }

               @Nullable
               public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                  return new RenamingContainer(windowId, nbt);
               }
            }, buffer -> buffer.func_150786_a(nbt));
         }

         return super.func_77659_a(worldIn, player, handIn);
      }
   }
}
