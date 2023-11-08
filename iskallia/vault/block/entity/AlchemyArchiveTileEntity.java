package iskallia.vault.block.entity;

import iskallia.vault.config.AlchemyTableConfig;
import iskallia.vault.container.AlchemyArchiveContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.DiscoveredAlchemyEffectsData;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class AlchemyArchiveTileEntity extends BlockEntity implements MenuProvider {
   private static final int NUMBER_OF_EFFECTS_TO_CHOOSE_FROM = 3;
   private Set<UUID> usedPlayers = new HashSet<>();
   private Map<UUID, List<String>> playerEffects = new HashMap<>();
   private static final Random bookRand = new Random();
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float rot;
   public float oRot;
   public float tRot;

   public AlchemyArchiveTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.ALCHEMY_ARCHIVE_TILE_ENTITY, pos, state);
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
   public static void clientBookTick(Level level, BlockPos pos, BlockState state, AlchemyArchiveTileEntity tile) {
      tile.oRot = tile.rot;
      Player player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0, false);
      if (player != null) {
         double d0 = player.getX() - (pos.getX() + 0.6);
         double d1 = player.getZ() - (pos.getZ() + 0.4);
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
            pos.getX() + 0.6,
            pos.getY() + 2,
            pos.getZ() + 0.4,
            -1.0F + bookRand.nextFloat() + 0.5,
            -1.0,
            -1.0F + bookRand.nextFloat() + 0.5
         );
      }
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.usedPlayers = NBTHelper.readSet(tag, "players", StringTag.class, strTag -> UUID.fromString(strTag.getAsString()));
      if (tag.contains("playerEffects")) {
         this.playerEffects = NBTHelper.readMap(tag, "playerEffects", ListTag.class, AlchemyArchiveTileEntity::readEffects);
      }
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      NBTHelper.writeCollection(tag, "players", this.usedPlayers, StringTag.class, uuid -> StringTag.valueOf(uuid.toString()));
      NBTHelper.writeMap(tag, "playerEffects", this.playerEffects, ListTag.class, AlchemyArchiveTileEntity::writeEffects);
   }

   public static List<String> readEffects(ListTag effectsTag) {
      return effectsTag.stream().map(Tag::getAsString).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   public static ListTag writeEffects(List<String> effects) {
      return effects.stream().map(StringTag::valueOf).collect(ListTag::new, AbstractList::add, AbstractCollection::addAll);
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this && !this.usedPlayers.contains(player.getUUID());
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
      return this.getLevel() == null
         ? null
         : new AlchemyArchiveContainer(id, this.getLevel(), this.getBlockPos(), player, this.playerEffects.get(player.getUUID()));
   }

   public void initPlayerEffects(Player player) {
      if (!this.playerEffects.containsKey(player.getUUID())) {
         if (this.getLevel() instanceof ServerLevel serverLevel) {
            DiscoveredAlchemyEffectsData var8 = DiscoveredAlchemyEffectsData.get(serverLevel);
            List<String> undiscoveredEffects = ModConfigs.VAULT_ALCHEMY_TABLE
               .getCraftableEffects()
               .stream()
               .filter(
                  effectCfg -> effectCfg.getUnlockCategory() == AlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY
                     && !var8.hasDiscoveredEffect(player, effectCfg.getEffectId())
               )
               .map(AlchemyTableConfig.CraftableEffectConfig::getEffectId)
               .toList();
            if (undiscoveredEffects.size() <= 3) {
               this.playerEffects.put(player.getUUID(), undiscoveredEffects);
            } else {
               Set<Integer> selectedIndices = new HashSet<>();

               while (selectedIndices.size() < 3) {
                  int randomIndex = this.getLevel().getRandom().nextInt(undiscoveredEffects.size());
                  if (!selectedIndices.contains(randomIndex)) {
                     selectedIndices.add(randomIndex);
                     String randomElement = undiscoveredEffects.get(randomIndex);
                     this.playerEffects.computeIfAbsent(player.getUUID(), uuid -> new ArrayList<>()).add(randomElement);
                  }
               }
            }

            this.setChanged();
         }
      }
   }

   public void use(ServerPlayer player) {
      this.initPlayerEffects(player);
      List<String> effects = this.playerEffects.get(player.getUUID());
      if (effects.isEmpty()) {
         player.sendMessage(new TextComponent("No effects left to discover").withStyle(ChatFormatting.RED), Util.NIL_UUID);
      } else if (effects.size() == 1) {
         this.discoverEffect(player, effects.get(0));
      } else {
         NetworkHooks.openGui(player, this, buffer -> {
            buffer.writeBlockPos(this.getBlockPos());
            CompoundTag effectTag = new CompoundTag();
            effectTag.put("effects", writeEffects(effects));
            buffer.writeNbt(effectTag);
         });
      }
   }

   public void discoverEffect(ServerPlayer player, String effectId) {
      this.setUsedByPlayer(player);
      DiscoveredAlchemyEffectsData discoveredEffectsData = DiscoveredAlchemyEffectsData.get(player.getLevel());
      if (discoveredEffectsData.compoundDiscoverEffect(player, effectId)) {
         AlchemyTableConfig cfg = ModConfigs.VAULT_ALCHEMY_TABLE;
         AlchemyTableConfig.CraftableEffectConfig effectCfg = cfg.getConfig(effectId);
         if (effectCfg != null) {
            ItemStack stack = BottleItem.create(null, null);
            MutableComponent cmp = new TextComponent("")
               .append(player.getDisplayName())
               .append(" discovered the ")
               .append(stack.getHoverName())
               .append(" effect: ")
               .append(effectCfg.getEffectName());
            MiscUtils.broadcast(cmp);
         }
      }
   }
}
