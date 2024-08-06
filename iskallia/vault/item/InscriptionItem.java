package iskallia.vault.item;

import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.generator.layout.ArchitectRoomEntry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.data.InscriptionData;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.RecyclableItem;
import iskallia.vault.item.gear.VaultLevelItem;
import iskallia.vault.item.tool.ColorBlender;
import iskallia.vault.item.tool.IManualModelLoading;
import iskallia.vault.item.tool.InscriptionItemRenderer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

public class InscriptionItem extends Item implements VaultLevelItem, IManualModelLoading, DataInitializationItem, RecyclableItem {
   public static final UUID ID = UUID.fromString("dd941c87-ea2f-42a4-951d-89fcbc433170");

   public InscriptionItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(1));
      this.setRegistryName(id);
   }

   public Component getName(ItemStack stack) {
      return new TextComponent("").append(super.getName(stack)).setStyle(Style.EMPTY.withColor(getColor(stack)));
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
      if (this.allowdedIn(group)) {
         for (ArchitectRoomEntry.Type type : ArchitectRoomEntry.Type.values()) {
            ItemStack stack = new ItemStack(this);
            InscriptionData data = InscriptionData.from(stack);

            data.add(type, 1, switch (type) {
               case COMMON -> 16777215;
               case CHALLENGE -> 16733695;
               case OMEGA -> 5635925;
            });
            data.setCompletion(0.05F);
            data.setTime(400);
            data.setInstability(0.01F);
            data.setSize(10);
            data.setModel(0);
            data.write(stack);
            items.add(stack);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      InscriptionData data = InscriptionData.from(stack);
      data.appendHoverText(stack, world, tooltip, flag);
   }

   @Override
   public void initializeVaultLoot(int vaultLevel, ItemStack stack, @Nullable BlockPos pos, @Nullable Vault vault) {
      stack.getOrCreateTag().putInt("level", vaultLevel);
   }

   @Override
   public void initialize(ItemStack stack, RandomSource rand) {
      CompoundTag nbt = stack.getTag();
      if (nbt != null) {
         if (nbt.contains("pool", 8)) {
            ResourceLocation pool = new ResourceLocation(nbt.getString("pool"));
            int level = nbt.getInt("level");
            ModConfigs.INSCRIPTION.generate(pool, level, rand).ifPresent(data -> data.write(stack));
            nbt.remove("pool");
            nbt.remove("level");
         }
      }
   }

   @Override
   public Optional<UUID> getUuid(ItemStack stack) {
      return Optional.of(ID);
   }

   @Override
   public boolean isValidInput(ItemStack input) {
      return !input.isEmpty();
   }

   @Override
   public VaultRecyclerConfig.RecyclerOutput getOutput(ItemStack input) {
      return ModConfigs.VAULT_RECYCLER.getInscriptionRecyclingOutput();
   }

   @Override
   public float getResultPercentage(ItemStack input) {
      return input.isEmpty() ? 0.0F : 1.0F;
   }

   @Override
   public void loadModels(Consumer<ModelResourceLocation> consumer) {
      consumer.accept(new ModelResourceLocation("the_vault:inscription/core#inventory"));

      for (int i = 0; i < 16; i++) {
         consumer.accept(new ModelResourceLocation("the_vault:inscription/%d#inventory".formatted(i)));
      }
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(new IItemRenderProperties() {
         public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return InscriptionItemRenderer.INSTANCE;
         }
      });
   }

   public static int getColor(ItemStack stack) {
      InscriptionData data = InscriptionData.from(stack);
      if (data.getColor() != null) {
         return data.getColor();
      } else {
         ColorBlender blender = new ColorBlender(1.0F);

         for (InscriptionData.Entry entry : data.getEntries()) {
            blender.add(entry.color, 60.0F);
         }

         float time = (float)ClientScheduler.INSTANCE.getTickCount();
         return blender.getColor(time);
      }
   }
}
