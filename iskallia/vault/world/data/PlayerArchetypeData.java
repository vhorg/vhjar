package iskallia.vault.world.data;

import iskallia.vault.skill.archetype.ArchetypeContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerArchetypeData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerArchetype";
   private final Map<UUID, ArchetypeContainer> playerMap = new HashMap<>();
   private static final String TAG_PLAYER_LIST = "playerList";
   private static final String TAG_ARCHETYPE_LIST = "archetypeList";

   public static PlayerArchetypeData get(ServerLevel level) {
      return get(level.getServer());
   }

   public static PlayerArchetypeData get(MinecraftServer server) {
      return (PlayerArchetypeData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerArchetypeData::create, PlayerArchetypeData::new, "the_vault_PlayerArchetype");
   }

   private static PlayerArchetypeData create(CompoundTag compoundTag) {
      return new PlayerArchetypeData(compoundTag);
   }

   private PlayerArchetypeData() {
   }

   private PlayerArchetypeData(CompoundTag compoundTag) {
      this();
      this.load(compoundTag);
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.side.isServer() && event.phase == Phase.START && event.player instanceof ServerPlayer player) {
         get(player.server).getArchetypeContainer(player).tick(player.server, player);
      }
   }

   public ArchetypeContainer getArchetypeContainer(Player player) {
      return this.getArchetypeContainer(player.getUUID());
   }

   public ArchetypeContainer getArchetypeContainer(UUID playerUuid) {
      return this.playerMap.computeIfAbsent(playerUuid, ArchetypeContainer::new);
   }

   public PlayerArchetypeData set(ServerPlayer player, ResourceLocation id) {
      this.getArchetypeContainer(player).setCurrentArchetype(player.getServer(), id);
      this.setDirty();
      return this;
   }

   private void load(CompoundTag compoundTag) {
      ListTag playerList = compoundTag.getList("playerList", 8);
      ListTag archetypeList = compoundTag.getList("archetypeList", 10);
      if (playerList.size() != archetypeList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getArchetypeContainer(playerUUID).deserializeNBT(archetypeList.getCompound(i));
         }
      }
   }

   @Nonnull
   public CompoundTag save(CompoundTag compoundTag) {
      ListTag playerList = new ListTag();
      ListTag archetypeList = new ListTag();
      this.playerMap.forEach((key, value) -> {
         playerList.add(StringTag.valueOf(key.toString()));
         archetypeList.add(value.serializeNBT());
      });
      compoundTag.put("playerList", playerList);
      compoundTag.put("archetypeList", archetypeList);
      return compoundTag;
   }
}
