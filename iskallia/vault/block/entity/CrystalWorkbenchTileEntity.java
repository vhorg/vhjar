package iskallia.vault.block.entity;

import iskallia.vault.container.CrystalWorkbenchContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.InfusedCatalystItem;
import iskallia.vault.item.InscriptionItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.properties.CapacityCrystalProperties;
import iskallia.vault.item.crystal.recipe.AnvilExecutor;
import iskallia.vault.item.data.InscriptionData;
import iskallia.vault.item.gear.CharmItem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrystalWorkbenchTileEntity extends BlockEntity implements MenuProvider {
   private final OverSizedInventory input = new OverSizedInventory(1, this) {
      @Override
      public void setChanged() {
         super.setChanged();
         CrystalWorkbenchTileEntity.this.updateExecutors();
      }
   };
   private final OverSizedInventory ingredients = new OverSizedInventory(60, this) {
      @Override
      public void setChanged() {
         super.setChanged();
         CrystalWorkbenchTileEntity.this.updateExecutors();
      }
   };
   private final OverSizedInventory uniqueIngredients = new OverSizedInventory(3, this) {
      @Override
      public void setChanged() {
         super.setChanged();
         CrystalWorkbenchTileEntity.this.updateExecutors();
      }
   };
   private Set<Player> players;
   private Map<UUID, AnvilExecutor.Result> executors;
   private int firstCursedIngredient = 1000;

   public CrystalWorkbenchTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.CRYSTAL_WORKBENCH_ENTITY, pos, state);
      this.players = new HashSet<>();
      this.executors = new HashMap<>();
   }

   public OverSizedInventory getInput() {
      return this.input;
   }

   public OverSizedInventory getIngredients() {
      return this.ingredients;
   }

   public OverSizedInventory getUniqueIngredients() {
      return this.uniqueIngredients;
   }

   public AnvilExecutor.Result getExecutor(UUID uuid) {
      return this.executors.get(uuid);
   }

   @OnlyIn(Dist.CLIENT)
   public AnvilExecutor.Result getClientExecutor() {
      return this.executors.get(Minecraft.getInstance().getConnection().getLocalGameProfile().getId());
   }

   public int getFirstCursedIngredient() {
      return this.firstCursedIngredient;
   }

   private void updateExecutor(Player player) {
      this.executors
         .put(
            player.getGameProfile().getId(),
            AnvilExecutor.test(player, this.input.getItem(0), this.ingredients.getContents(), this.uniqueIngredients.getContents())
         );
      this.calculateFirstCursedIngredient();
   }

   private void calculateFirstCursedIngredient() {
      this.firstCursedIngredient = 1000;
      ItemStack crystal = this.input.getItem(0);
      if (crystal.getItem() instanceof VaultCrystalItem) {
         CrystalData data = CrystalData.read(crystal);
         if (data.getProperties() instanceof CapacityCrystalProperties capacityCrystalProperties) {
            capacityCrystalProperties.getCapacity().ifPresent(capacity -> {
               for (int i = 0; i < this.ingredients.getContainerSize(); i++) {
                  ItemStack ingredient = this.ingredients.getItem(i);
                  if (ingredient.getItem() instanceof InfusedCatalystItem) {
                     capacity = capacity - InfusedCatalystItem.getSize(ingredient).orElse(0);
                  } else if (ingredient.getItem() instanceof InscriptionItem) {
                     InscriptionData inscriptionData = InscriptionData.from(ingredient);
                     capacity = capacity - inscriptionData.getSize();
                  } else if (ingredient.getItem() instanceof CharmItem) {
                     capacity = capacity - CharmItem.getCrystalIngredientSize();
                  }

                  if (capacity < 0) {
                     this.firstCursedIngredient = i;
                     break;
                  }
               }
            });
         }
      }
   }

   private void updateExecutors() {
      for (Player player : this.players) {
         this.executors
            .put(
               player.getGameProfile().getId(),
               AnvilExecutor.test(player, this.input.getItem(0), this.ingredients.getContents(), this.uniqueIngredients.getContents())
            );
      }

      this.calculateFirstCursedIngredient();
   }

   public void onCraft(Player player) {
      this.updateExecutor(player);
      AnvilExecutor.Result executor = this.executors.get(player.getGameProfile().getId());
      if (executor != null) {
         for (int i = 0; i < executor.getIngredients().length; i++) {
            this.ingredients.setItem(i, executor.getIngredients()[i]);
         }

         for (int i = 0; i < executor.getUniqueIngredients().length; i++) {
            this.uniqueIngredients.setItem(i, executor.getUniqueIngredients()[i]);
         }

         this.input.setItem(0, executor.getOutput());

         for (ItemStack extra : executor.getExtra()) {
            int left = extra.getCount();

            for (int i = 0; i < this.ingredients.getContainerSize(); i++) {
               ItemStack stack = this.ingredients.getItem(i);
               if (left <= 0) {
                  break;
               }

               if (stack.isEmpty() || ItemStack.isSameItemSameTags(stack, extra)) {
                  int difference = Math.min(extra.getMaxStackSize() - stack.getCount(), left);
                  ItemStack copy = extra.copy();
                  copy.setCount(stack.getCount() + difference);
                  this.ingredients.setItem(i, copy);
                  left -= difference;
               }
            }
         }

         if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), ModSounds.ARTISAN_SMITHING, SoundSource.BLOCKS, 0.2F, this.level.random.nextFloat() * 0.1F + 0.9F);
         }
      }
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this
         ? this.input.stillValid(player) && this.ingredients.stillValid(player)
         : false;
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
      return this.getLevel() == null ? null : new CrystalWorkbenchContainer(containerId, this.getLevel(), this.getBlockPos(), player);
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      this.input.save("input", nbt);
      this.ingredients.save("ingredients", nbt);
      this.uniqueIngredients.save("uniqueIngredients", nbt);
      CompoundTag executors = new CompoundTag();
      this.executors.forEach((uuid, result) -> result.writeNbt().ifPresent(executor -> executors.put(uuid.toString(), executor)));
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.input.load("input", nbt);
      this.ingredients.load("ingredients", nbt);
      this.uniqueIngredients.load("uniqueIngredients", nbt);
      this.executors.clear();
      CompoundTag executors = nbt.getCompound("executors");

      for (String key : executors.getAllKeys()) {
         AnvilExecutor.Result executor = new AnvilExecutor.Result();
         executor.readNbt(executors.getCompound(key));
         this.executors.put(UUID.fromString(key), executor);
      }
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onOpen(Player player) {
      this.players.add(player);
      this.updateExecutor(player);
   }

   public void onClose(Player player) {
      this.players.add(player);
      this.executors.remove(player.getGameProfile().getId());
   }
}
