package iskallia.vault.block.entity;

import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.container.ModifierDiscoveryContainer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.DiscoveredWorkbenchModifiersData;
import iskallia.vault.world.data.ServerVaults;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ModifierDiscoveryTileEntity extends BlockEntity implements MenuProvider {
   private static final int NUMBER_OF_MODIFIERS_TO_CHOOSE_FROM = 3;
   private Set<UUID> usedPlayers = new HashSet<>();
   private Map<UUID, List<Tuple<Item, ResourceLocation>>> playerGearModifiers = new HashMap<>();
   private static final Random bookRand = new Random();
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float rot;
   public float oRot;
   public float tRot;

   public ModifierDiscoveryTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.MODIFIER_DISCOVERY_ENTITY, pos, state);
   }

   public Set<UUID> getUsedPlayers() {
      return this.usedPlayers;
   }

   public boolean setUsedByPlayer(Player player) {
      if (this.usedPlayers.add(player.getUUID())) {
         this.setChanged();
         return true;
      } else {
         return false;
      }
   }

   public boolean canBeUsed(Player player) {
      return !this.getUsedPlayers().contains(player.getUUID());
   }

   @OnlyIn(Dist.CLIENT)
   public static void clientBookTick(Level level, BlockPos pos, BlockState state, ModifierDiscoveryTileEntity tile) {
      tile.oRot = tile.rot;
      Player player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0, false);
      if (player != null) {
         double d0 = player.getX() - (pos.getX() + 0.5);
         double d1 = player.getZ() - (pos.getZ() + 0.5);
         tile.tRot = (float)Mth.atan2(d1, d0);
         if (bookRand.nextInt(40) == 0) {
            float f1 = tile.flipT;

            do {
               tile.flipT = tile.flipT + (bookRand.nextInt(4) - bookRand.nextInt(4));
            } while (f1 == tile.flipT);
         }
      } else {
         tile.tRot += 0.02F;
      }

      while (tile.rot >= Math.PI) {
         tile.rot = (float)(tile.rot - (Math.PI * 2));
      }

      while (tile.rot < -Math.PI) {
         tile.rot = (float)(tile.rot + (Math.PI * 2));
      }

      while (tile.tRot >= Math.PI) {
         tile.tRot = (float)(tile.tRot - (Math.PI * 2));
      }

      while (tile.tRot < -Math.PI) {
         tile.tRot = (float)(tile.tRot + (Math.PI * 2));
      }

      float f2 = tile.tRot - tile.rot;

      while (f2 >= Math.PI) {
         f2 = (float)(f2 - (Math.PI * 2));
      }

      while (f2 < -Math.PI) {
         f2 = (float)(f2 + (Math.PI * 2));
      }

      tile.rot += f2 * 0.4F;
      tile.time++;
      tile.oFlip = tile.flip;
      float f = (tile.flipT - tile.flip) * 0.4F;
      f = Mth.clamp(f, -0.2F, 0.2F);
      tile.flipA = tile.flipA + (f - tile.flipA) * 0.9F;
      tile.flip = tile.flip + tile.flipA;
      if (bookRand.nextInt(5) == 0) {
         level.addParticle(
            ParticleTypes.ENCHANT,
            pos.getX() + 0.5,
            pos.getY() + 2,
            pos.getZ() + 0.5,
            -1.0F + bookRand.nextFloat() + 0.5,
            -1.0,
            -1.0F + bookRand.nextFloat() + 0.5
         );
      }
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.usedPlayers = NBTHelper.readSet(tag, "players", StringTag.class, strTag -> UUID.fromString(strTag.getAsString()));
      if (tag.contains("playerGearModifiers")) {
         this.playerGearModifiers = NBTHelper.readMap(tag, "playerGearModifiers", ListTag.class, ModifierDiscoveryTileEntity::readGearModifiers);
      }
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      NBTHelper.writeCollection(tag, "players", this.usedPlayers, StringTag.class, uuid -> StringTag.valueOf(uuid.toString()));
      NBTHelper.writeMap(tag, "playerGearModifiers", this.playerGearModifiers, ListTag.class, ModifierDiscoveryTileEntity::writeGearModifiers);
   }

   public static List<Tuple<Item, ResourceLocation>> readGearModifiers(ListTag gearModifiersTag) {
      return gearModifiersTag.stream().map(tag -> {
         if (tag instanceof CompoundTag compoundTag) {
            String itemRegistryName = compoundTag.getString("item");
            Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemRegistryName));
            if (item != null) {
               ResourceLocation modifier = new ResourceLocation(compoundTag.getString("modifier"));
               return Optional.of(new Tuple(item, modifier));
            }
         }

         return Optional.empty();
      }).filter(Optional::isPresent).map(Optional::get).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   public static ListTag writeGearModifiers(List<Tuple<Item, ResourceLocation>> gearModifiers) {
      return gearModifiers.stream().map(gearModifier -> {
         CompoundTag gmNbt = new CompoundTag();
         gmNbt.putString("item", ((Item)gearModifier.getA()).getRegistryName().toString());
         gmNbt.putString("modifier", ((ResourceLocation)gearModifier.getB()).toString());
         return gmNbt;
      }).collect(ListTag::new, AbstractList::add, AbstractCollection::addAll);
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this && !this.usedPlayers.contains(player.getUUID());
   }

   @Nullable
   public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
      return this.getLevel() == null
         ? null
         : new ModifierDiscoveryContainer(id, this.getLevel(), this.getBlockPos(), player, this.playerGearModifiers.get(player.getUUID()));
   }

   public void initPlayerGearModifiers(Player player) {
      if (!this.playerGearModifiers.containsKey(player.getUUID())) {
         if (this.getLevel() instanceof ServerLevel serverLevel) {
            Map var7 = getUndiscoveredGearModifiers(player, serverLevel);
            int combinations = 0;

            for (Entry<Item, HashMap<ResourceLocation, ArrayList<ResourceLocation>>> entry : var7.entrySet()) {
               combinations += entry.getValue().size();
            }

            if (combinations <= 3) {
               this.playerGearModifiers
                  .put(
                     player.getUUID(),
                     var7.entrySet()
                        .stream()
                        .flatMap(
                           entryx -> ((HashMap)entryx.getValue())
                              .values()
                              .stream()
                              .flatMap(innerEntry -> innerEntry.stream().map(modifier -> new Tuple((Item)entryx.getKey(), modifier)))
                        )
                        .toList()
                  );
            } else {
               this.selectRandomModifiers(player, combinations, var7);
               this.setChanged();
            }
         }
      }
   }

   private void selectRandomModifiers(Player player, int combinations, Map<Item, HashMap<ResourceLocation, ArrayList<ResourceLocation>>> undiscovered) {
      Set<Integer> selectedIndices = new HashSet<>();

      while (selectedIndices.size() < 3) {
         int randomIndex = this.getLevel().getRandom().nextInt(combinations);
         if (!selectedIndices.contains(randomIndex)) {
            selectedIndices.add(randomIndex);
            this.addGearModifierAtIndex(player, undiscovered, randomIndex);
         }
      }
   }

   private void addGearModifierAtIndex(Player player, Map<Item, HashMap<ResourceLocation, ArrayList<ResourceLocation>>> undiscovered, int randomIndex) {
      int currentIndex = 0;

      for (Entry<Item, HashMap<ResourceLocation, ArrayList<ResourceLocation>>> entry : undiscovered.entrySet()) {
         for (Entry<ResourceLocation, ArrayList<ResourceLocation>> innerEntry : entry.getValue().entrySet()) {
            for (ResourceLocation modifier : innerEntry.getValue()) {
               if (currentIndex == randomIndex) {
                  this.playerGearModifiers.computeIfAbsent(player.getUUID(), uuid -> new ArrayList<>()).add(new Tuple(entry.getKey(), modifier));
                  return;
               }

               currentIndex++;
            }
         }
      }
   }

   private static Map<Item, HashMap<ResourceLocation, ArrayList<ResourceLocation>>> getUndiscoveredGearModifiers(Player player, ServerLevel serverLevel) {
      Vault vault = ServerVaults.get(serverLevel).orElse(null);
      int vaultLevel = vault.has(Vault.LEVEL) ? vault.get(Vault.LEVEL).get() : 0;
      Map<Item, HashMap<ResourceLocation, ArrayList<ResourceLocation>>> itemCfg = new HashMap<>();
      Set<ResourceLocation> availableIdolCraftIds = new HashSet<>();
      DiscoveredWorkbenchModifiersData discoveredModifiers = DiscoveredWorkbenchModifiersData.get(serverLevel);

      for (VaultGearWorkbenchConfig config : ModConfigs.VAULT_GEAR_WORKBENCH_CONFIG.values()) {
         Item item = config.getGearItem();

         for (VaultGearWorkbenchConfig.CraftableModifierConfig cfg : config.getAllCraftableModifiers()) {
            if (cfg.getUnlockCategory() == VaultGearWorkbenchConfig.UnlockCategory.VAULT_DISCOVERY) {
               ResourceLocation key = cfg.getWorkbenchCraftIdentifier();
               if (!discoveredModifiers.hasDiscoveredCraft(player, item, key) && cfg.getMinLevel() <= vaultLevel) {
                  if (item instanceof IdolItem) {
                     availableIdolCraftIds.add(key);
                     if (itemCfg.containsKey(ModItems.IDOL_BENEVOLENT)) {
                        if (itemCfg.get(ModItems.IDOL_BENEVOLENT).containsKey(cfg.getModifierIdentifier())) {
                           itemCfg.get(ModItems.IDOL_BENEVOLENT).get(cfg.getModifierIdentifier()).add(cfg.getWorkbenchCraftIdentifier());
                        } else {
                           itemCfg.get(ModItems.IDOL_BENEVOLENT).put(cfg.getModifierIdentifier(), new ArrayList<>(List.of(cfg.getWorkbenchCraftIdentifier())));
                        }
                     } else {
                        HashMap<ResourceLocation, ArrayList<ResourceLocation>> hashmap = new HashMap<>();
                        hashmap.put(cfg.getModifierIdentifier(), new ArrayList<>(List.of(cfg.getWorkbenchCraftIdentifier())));
                        itemCfg.put(ModItems.IDOL_BENEVOLENT, hashmap);
                     }
                  } else if (itemCfg.containsKey(item)) {
                     if (itemCfg.get(item).containsKey(cfg.getModifierIdentifier())) {
                        itemCfg.get(item).get(cfg.getModifierIdentifier()).add(cfg.getWorkbenchCraftIdentifier());
                     } else {
                        itemCfg.get(item).put(cfg.getModifierIdentifier(), new ArrayList<>(List.of(cfg.getWorkbenchCraftIdentifier())));
                     }
                  } else {
                     HashMap<ResourceLocation, ArrayList<ResourceLocation>> hashmap = new HashMap<>();
                     hashmap.put(cfg.getModifierIdentifier(), new ArrayList<>(List.of(cfg.getWorkbenchCraftIdentifier())));
                     itemCfg.put(item, hashmap);
                  }
               }
            }
         }
      }

      return itemCfg;
   }

   public void use(ServerPlayer player) {
      this.initPlayerGearModifiers(player);
      List<Tuple<Item, ResourceLocation>> gearModifiers = this.playerGearModifiers.get(player.getUUID());
      if (gearModifiers.isEmpty()) {
         player.sendMessage(new TextComponent("No modifiers left to discover").withStyle(ChatFormatting.RED), Util.NIL_UUID);
      } else if (gearModifiers.size() == 1) {
         this.discoverGearModifier(player, gearModifiers.get(0));
      } else {
         NetworkHooks.openGui(player, this, buffer -> {
            buffer.writeBlockPos(this.getBlockPos());
            CompoundTag gearModifiersTag = new CompoundTag();
            gearModifiersTag.put("gearModifiers", writeGearModifiers(gearModifiers));
            buffer.writeNbt(gearModifiersTag);
         });
      }
   }

   public void discoverGearModifier(ServerPlayer player, Tuple<Item, ResourceLocation> gearModifier) {
      this.setUsedByPlayer(player);
      DiscoveredWorkbenchModifiersData discoveredModifiers = DiscoveredWorkbenchModifiersData.get(player.getLevel());
      if (discoveredModifiers.compoundDiscoverWorkbenchCraft(player, (Item)gearModifier.getA(), (ResourceLocation)gearModifier.getB())) {
         VaultGearWorkbenchConfig.getConfig((Item)gearModifier.getA())
            .ifPresent(
               cfg -> {
                  VaultGearWorkbenchConfig.CraftableModifierConfig modifierCfg = cfg.getConfig((ResourceLocation)gearModifier.getB());
                  if (modifierCfg != null) {
                     modifierCfg.createModifier()
                        .ifPresent(
                           modifier -> {
                              ItemStack stack = new ItemStack((ItemLike)gearModifier.getA());
                              if (stack.getItem() instanceof VaultGearItem) {
                                 VaultGearData vgData = VaultGearData.read(stack);
                                 vgData.setState(VaultGearState.IDENTIFIED);
                                 vgData.setRarity(VaultGearRarity.COMMON);
                                 vgData.write(stack);
                              }

                              modifier.getConfigDisplay(stack)
                                 .ifPresent(
                                    configDisplay -> {
                                       MutableComponent cmp = new TextComponent("")
                                          .append(player.getDisplayName())
                                          .append(" discovered the ")
                                          .append(stack.getHoverName())
                                          .append(" modifier: ")
                                          .append(configDisplay);
                                       MiscUtils.broadcast(cmp);
                                    }
                                 );
                           }
                        );
                  }
               }
            );
      }
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }
}
