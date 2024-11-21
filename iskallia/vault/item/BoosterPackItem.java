package iskallia.vault.item;

import iskallia.vault.VaultMod;
import iskallia.vault.config.card.BoosterPackConfig;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.tool.IManualModelLoading;
import iskallia.vault.network.message.OpenClientScreenMessage;
import iskallia.vault.util.ServerScheduler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.network.NetworkDirection;

public class BoosterPackItem extends Item implements IManualModelLoading {
   public BoosterPackItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public static String getId(ItemStack stack) {
      return stack.getTag() != null && stack.getTag().contains("id") ? stack.getTag().getString("id") : null;
   }

   public static void setId(ItemStack stack, String pool) {
      stack.getOrCreateTag().putString("id", pool);
   }

   public static List<ItemStack> getOutcomes(ItemStack stack) {
      if (stack.getTag() != null && stack.getTag().contains("outcomes")) {
         ListTag list = stack.getTag().getList("outcomes", 10);
         List<ItemStack> outcomes = new ArrayList<>();

         for (int i = 0; i < list.size(); i++) {
            Adapters.ITEM_STACK.readNbt(list.getCompound(i)).ifPresent(outcomes::add);
         }

         return outcomes;
      } else {
         return null;
      }
   }

   public static void setOutcomes(ItemStack stack, List<ItemStack> outcomes) {
      ListTag list = new ListTag();

      for (ItemStack outcome : outcomes) {
         Adapters.ITEM_STACK.writeNbt(outcome).ifPresent(list::add);
      }

      stack.getOrCreateTag().put("outcomes", list);
   }

   public Component getName(ItemStack stack) {
      return Optional.ofNullable(getId(stack)).flatMap(s -> ModConfigs.BOOSTER_PACK.getName(s)).orElseGet(() -> super.getName(stack));
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
      super.inventoryTick(stack, world, entity, slot, selected);
      if (getId(stack) == null) {
         setId(stack, VaultMod.sId("default"));
      }
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (hand == InteractionHand.MAIN_HAND && !world.isClientSide()) {
         if (getId(stack) == null) {
            setId(stack, VaultMod.sId("default"));
         }

         if (getOutcomes(stack) == null) {
            RandomSource random = JavaRandom.ofNanoTime();
            String id = getId(stack);
            setOutcomes(
               stack,
               ModConfigs.BOOSTER_PACK
                  .getOutcomes(id, random)
                  .stream()
                  .map(CardItem::create)
                  .peek(item -> item.inventoryTick(player.level, player, 0, false))
                  .toList()
            );
            player.level
               .playSound(
                  null,
                  player.blockPosition(),
                  ModSounds.BOOSTER_PACK_OPEN,
                  player.getSoundSource(),
                  0.8F,
                  0.8F + player.getLevel().getRandom().nextFloat(0.4F)
               );
         }

         ServerScheduler.INSTANCE
            .schedule(
               1,
               () -> ModNetwork.CHANNEL
                  .sendTo(
                     new OpenClientScreenMessage(OpenClientScreenMessage.Type.BOOSTER_PACK),
                     ((ServerPlayer)player).connection.connection,
                     NetworkDirection.PLAY_TO_CLIENT
                  )
            );
      }

      return super.use(world, player, hand);
   }

   public void fillItemCategory(@Nonnull CreativeModeTab category, @Nonnull NonNullList<ItemStack> items) {
      if (category == ModItems.GEAR_GROUP) {
         List<ItemStack> boosterPacks = new ArrayList<>();
         ModConfigs.BOOSTER_PACK.getValues().keySet().forEach(id -> {
            ItemStack stack = new ItemStack(this);
            setId(stack, id);
            boosterPacks.add(stack);
         });
         items.addAll(boosterPacks);
      }
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(new IItemRenderProperties() {
         public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return BoosterPackItemRenderer.INSTANCE;
         }
      });
   }

   @Override
   public void loadModels(Consumer<ModelResourceLocation> consumer) {
      BoosterPackConfig config = new BoosterPackConfig().readConfig();
      ModConfigs.CONFIGS.remove(config);

      for (BoosterPackConfig.BoosterPackModel model : config.getModels()) {
         consumer.accept(new ModelResourceLocation(model.getUnopened()));
         consumer.accept(new ModelResourceLocation(model.getOpened()));
      }
   }
}
