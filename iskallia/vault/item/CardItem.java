package iskallia.vault.item;

import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.config.card.CardModifiersConfig;
import iskallia.vault.core.card.Card;
import iskallia.vault.core.card.CardEntry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.tool.IManualModelLoading;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.Nullable;

public class CardItem extends Item implements IManualModelLoading {
   public CardItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public static Card getCard(ItemStack stack) {
      Card card = new Card();
      if (stack.getTag() == null) {
         return card;
      } else {
         card.readNbt(stack.getTag().getCompound("data"));
         return card;
      }
   }

   public static Card setCard(ItemStack stack, Card card) {
      card.writeNbt().ifPresent(tag -> stack.getOrCreateTag().put("data", tag));
      return card;
   }

   public static Card modifyCard(ItemStack stack, UnaryOperator<Card> action) {
      return setCard(stack, action.apply(getCard(stack)));
   }

   public static ItemStack create(Card card) {
      ItemStack stack = new ItemStack(ModItems.CARD);
      setCard(stack, card);
      return stack;
   }

   public Component getName(ItemStack stack) {
      return Optional.ofNullable(getCard(stack).getFirstName()).orElseGet(() -> super.getName(stack));
   }

   public boolean isFoil(ItemStack stack) {
      return super.isFoil(stack) || getCard(stack).hasGroup("Foil");
   }

   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      Card card = getCard(stack);
      card.addText(tooltip, tooltip.size(), flag, (float)ClientScheduler.INSTANCE.getTickCount());
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
      super.inventoryTick(stack, world, entity, slot, selected);
      modifyCard(stack, card -> {
         card.onInventoryTick(world, entity, slot, selected);
         return card;
      });
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(new IItemRenderProperties() {
         public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return CardItemRenderer.INSTANCE;
         }
      });
   }

   @Override
   public void loadModels(Consumer<ModelResourceLocation> consumer) {
      for (CardEntry.Color color : CardEntry.Color.values()) {
         for (int tier = 1; tier <= 5; tier++) {
            consumer.accept(new ModelResourceLocation("the_vault:card/%s_%d#inventory".formatted(color.name().toLowerCase(), tier)));
            consumer.accept(new ModelResourceLocation("the_vault:card/%s_temporal_%d#inventory".formatted(color.name().toLowerCase(), tier)));
            consumer.accept(new ModelResourceLocation("the_vault:card/%s_evolution_%d#inventory".formatted(color.name().toLowerCase(), tier)));
         }

         consumer.accept(new ModelResourceLocation("the_vault:card/%s_arcane#inventory".formatted(color.name().toLowerCase())));
         consumer.accept(new ModelResourceLocation("the_vault:card/%s_resource#inventory".formatted(color.name().toLowerCase())));
      }

      CardEntry.Color[] colors = CardEntry.Color.values();

      for (int i = 0; i < 16; i++) {
         StringBuilder prefix = new StringBuilder();
         List<CardEntry.Color> matching = new ArrayList<>();
         if ((i & 1) == 1) {
            matching.add(colors[0]);
         }

         if ((i & 2) == 2) {
            matching.add(colors[1]);
         }

         if ((i & 4) == 4) {
            matching.add(colors[2]);
         }

         if ((i & 8) == 8) {
            matching.add(colors[3]);
         }

         for (CardEntry.Color color : matching) {
            prefix.append(color.name().toLowerCase()).append("_");
         }

         consumer.accept(new ModelResourceLocation("the_vault:card/%swild#inventory".formatted(prefix)));
      }

      CardModifiersConfig config = new CardModifiersConfig().readConfig();
      ModConfigs.CONFIGS.remove(config);

      for (CardEntry.Config entry : config.getEntries()) {
         if (entry.model != null) {
            consumer.accept(new ModelResourceLocation(entry.model));
         }
      }
   }
}
