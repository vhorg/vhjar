package iskallia.vault.world.data;

import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.network.message.transmog.DiscoveredEntriesMessage;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public class DiscoveredModelsData extends SavedData {
   protected static final String DATA_NAME = "the_vault_DiscoveredModels";
   protected Map<UUID, Set<ResourceLocation>> discoveredModels = new HashMap<>();

   private DiscoveredModelsData() {
   }

   private DiscoveredModelsData(CompoundTag tag) {
      this.load(tag);
   }

   public void discoverModelAndBroadcast(ItemStack vaultGear, Player player) {
      if (!vaultGear.isEmpty() && vaultGear.getItem() instanceof VaultGearItem) {
         VaultGearData data = VaultGearData.read(vaultGear);
         data.getFirstValue(ModGearAttributes.GEAR_MODEL).ifPresent(modelId -> this.discoverModelAndBroadcast(vaultGear.getItem(), modelId, player));
      }
   }

   public void discoverModelAndBroadcast(Item gearItem, ResourceLocation modelId, PlayerReference playerReference) {
      if (gearItem instanceof VaultGearItem vaultGearItem) {
         boolean collected = this.discoverModel(playerReference.getId(), modelId);
         if (collected) {
            this.broadcastModelDiscovery(modelId, playerReference.getName(), vaultGearItem);
         }
      }
   }

   public void discoverModelAndBroadcast(Item gearItem, ResourceLocation modelId, Player player) {
      if (gearItem instanceof VaultGearItem vaultGearItem) {
         boolean collected = this.discoverModel(player.getUUID(), modelId);
         if (collected) {
            this.broadcastModelDiscovery(modelId, player, vaultGearItem);
         }
      }
   }

   public void discoverAllArmorPieceAndBroadcast(Player player, ArmorModel armorModel) {
      armorModel.getPieces().values().forEach(armorModelPiece -> {
         boolean collected = this.discoverModel(player.getUUID(), armorModelPiece.getId());
         if (collected) {
            VaultArmorItem armorItem = VaultArmorItem.forSlot(armorModelPiece.getEquipmentSlot());
            this.broadcastModelDiscovery(armorModelPiece.getId(), player, armorItem);
         }
      });
   }

   public boolean discoverRandomArmorPieceAndBroadcast(Player player, ArmorModel armorModel, Random random) {
      List<ArmorPieceModel> pieces = armorModel.getPieces().values().stream().toList();
      ArmorPieceModel randomPiece = pieces.get(random.nextInt(pieces.size()));
      boolean collected = this.discoverModel(player.getUUID(), randomPiece.getId());
      if (collected) {
         VaultArmorItem armorItem = VaultArmorItem.forSlot(randomPiece.getEquipmentSlot());
         this.broadcastModelDiscovery(randomPiece.getId(), player, armorItem);
         return true;
      } else {
         return false;
      }
   }

   public void reset(UUID playerUUID) {
      if (this.discoveredModels.remove(playerUUID) != null) {
         this.setDirty();
      }
   }

   public boolean discoverModel(UUID playerUUID, ResourceLocation modelId) {
      if (this.discoveredModels.computeIfAbsent(playerUUID, id -> new HashSet<>()).add(modelId)) {
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   private void broadcastModelDiscovery(ResourceLocation modelId, String playerNickname, VaultGearItem vaultGearItem) {
      this.broadcastModelDiscovery(modelId, new TextComponent(playerNickname), vaultGearItem);
   }

   private void broadcastModelDiscovery(ResourceLocation modelId, Player player, VaultGearItem vaultGearItem) {
      this.broadcastModelDiscovery(modelId, player.getName().copy(), vaultGearItem);
   }

   private void broadcastModelDiscovery(ResourceLocation modelId, MutableComponent playerName, VaultGearItem vaultGearItem) {
      ModDynamicModels.REGISTRIES.getAssociatedRegistry(vaultGearItem.getItem()).flatMap(registry -> registry.get(modelId)).ifPresent(model -> {
         MutableComponent msgContainer = new TextComponent("").withStyle(ChatFormatting.WHITE);
         playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
         MutableComponent pieceName = new TextComponent(model.getDisplayName());
         VaultGearRarity rollRarity = ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRarityOf(vaultGearItem, modelId);
         pieceName.setStyle(Style.EMPTY.withColor(rollRarity.getColor()));
         MiscUtils.broadcast(msgContainer.append(playerName).append(" has discovered ").append(pieceName).append(" transmog."));
      });
   }

   public Set<ResourceLocation> getDiscoveredModels(UUID playerUUID) {
      return this.discoveredModels.getOrDefault(playerUUID, Collections.emptySet());
   }

   public void ensureResearchDiscoverables(ServerPlayer player) {
      DiscoveredModelsData discoveredModelsData = get(player.getLevel());
      PlayerResearchesData researchesData = PlayerResearchesData.get(player.getLevel());
      ResearchTree researches = researchesData.getResearches(player);

      for (String research : researches.getResearchesDone()) {
         Research byName = ModConfigs.RESEARCHES.getByName(research);
         if (byName == null) {
            VaultMod.LOGGER.error("Attempted to get research and resulting in NPE: {}", research);
         }
      }

      for (ResourceLocation modelId : researches.getResearchesDone()
         .stream()
         .map(ModConfigs.RESEARCHES::getByName)
         .filter(Objects::nonNull)
         .filter(researchx -> researchx.getDiscoversModels() != null)
         .flatMap(researchx -> researchx.getDiscoversModels().stream())
         .<ResourceLocation>map(ResourceLocation::tryParse)
         .filter(Objects::nonNull)
         .toList()) {
         ModDynamicModels.REGISTRIES.getModelAndAssociatedItem(modelId).ifPresent(pair -> {
            DynamicModel<?> gearModel = (DynamicModel<?>)pair.getFirst();
            Item associatedItem = (Item)pair.getSecond();
            discoveredModelsData.discoverModelAndBroadcast(associatedItem, gearModel.getId(), player);
         });
      }
   }

   public void setDirty(boolean dirty) {
      super.setDirty(dirty);
      if (dirty) {
         MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
         if (srv != null) {
            srv.getPlayerList().getPlayers().forEach(this::syncTo);
         }
      }
   }

   private DiscoveredEntriesMessage getUpdatePacket(UUID playerId) {
      return new DiscoveredEntriesMessage(DiscoveredEntriesMessage.Type.MODELS, this.getDiscoveredModels(playerId));
   }

   public void syncTo(ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(this.getUpdatePacket(player.getUUID()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   @Nonnull
   public CompoundTag save(@Nonnull CompoundTag compound) {
      this.discoveredModels.forEach((uuid, modelIds) -> {
         ListTag modelIdsTag = new ListTag();
         modelIds.forEach(modelId -> modelIdsTag.add(StringTag.valueOf(modelId.toString())));
         compound.put(uuid.toString(), modelIdsTag);
      });
      return compound;
   }

   public void load(CompoundTag compound) {
      this.discoveredModels.clear();

      for (String uuidKey : compound.getAllKeys()) {
         UUID uuid = UUID.fromString(uuidKey);
         Set<ResourceLocation> modelIds = new HashSet<>();
         compound.getList(uuidKey, 8).stream().map(Tag::getAsString).map(ResourceLocation::new).forEach(modelIds::add);
         this.discoveredModels.put(uuid, modelIds);
      }
   }

   public static DiscoveredModelsData get(ServerLevel level) {
      return get(level.getServer());
   }

   public static DiscoveredModelsData get(MinecraftServer server) {
      return (DiscoveredModelsData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(DiscoveredModelsData::new, DiscoveredModelsData::new, "the_vault_DiscoveredModels");
   }
}
