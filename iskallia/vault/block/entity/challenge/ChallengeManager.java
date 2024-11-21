package iskallia.vault.block.entity.challenge;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.entity.challenge.elite.EliteChallengeManager;
import iskallia.vault.block.entity.challenge.raid.RaidChallengeManager;
import iskallia.vault.block.entity.challenge.xmark.XMarkChallengeManager;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.world.data.ChallengeData;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;

public class ChallengeManager implements ISerializable<CompoundTag, JsonObject> {
   public UUID uuid;
   public ResourceKey<Level> dimension;
   public BlockPos pos;
   public Set<UUID> players;
   public boolean deleted;

   protected ChallengeManager() {
      this.players = new HashSet<>();
   }

   public ChallengeManager(UUID uuid, ResourceKey<Level> dimension, BlockPos pos) {
      this.uuid = uuid;
      this.dimension = dimension;
      this.pos = pos;
      this.players = new HashSet<>();
   }

   public boolean addPlayer(Player player) {
      return this.players.add(player.getUUID());
   }

   public boolean removePlayer(Player player) {
      if (this.players.remove(player.getUUID())) {
         if (player instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
            ChallengeData.get(serverPlayer.getServer()).sendUpdatesToClient(serverPlayer);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));
         Adapters.DIMENSION.writeNbt(this.dimension).ifPresent(tag -> nbt.put("dimension", tag));
         Adapters.BLOCK_POS.writeNbt(this.pos).ifPresent(tag -> nbt.put("pos", tag));
         ListTag list = new ListTag();

         for (UUID player : this.players) {
            Adapters.UUID.writeNbt(player).ifPresent(list::add);
         }

         nbt.put("players", list);
         Adapters.BOOLEAN.writeNbt(this.deleted).ifPresent(tag -> nbt.put("deleted", tag));
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElseThrow();
      this.dimension = Adapters.DIMENSION.readNbt(nbt.get("dimension")).orElseThrow();
      this.pos = Adapters.BLOCK_POS.readNbt(nbt.get("pos")).orElseThrow();
      this.players.clear();
      Tag var3 = nbt.get("players");
      if (var3 instanceof ListTag) {
         for (Tag tag : (ListTag)var3) {
            Adapters.UUID.readNbt(tag).ifPresent(this.players::add);
         }
      }

      this.deleted = Adapters.BOOLEAN.readNbt(nbt.get("deleted")).orElse(false);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public boolean isDeleted() {
      return this.deleted;
   }

   public void setDeleted(boolean deleted) {
      this.deleted = deleted;
   }

   public BlockEntity getBlockEntity(MinecraftServer server) {
      ServerLevel world = server.getLevel(this.dimension);
      return world == null ? null : world.getBlockEntity(this.pos);
   }

   public void onAttach(ServerLevel world) {
      CommonEvents.BLOCK_USE.at(BlockUseEvent.Phase.HEAD).register(this, data -> {
         if (data.getHand() != InteractionHand.MAIN_HAND) {
            data.setResult(InteractionResult.SUCCESS);
         } else if (data.getWorld() == world) {
            if (data.getPos().equals(this.pos)) {
               this.onClick(world, data.getPlayer());
            }
         }
      });
      CommonEvents.SERVER_TICK.at(Phase.END).register(this, event -> this.onTick(world));
   }

   public void onTick(ServerLevel world) {
   }

   public void onRemove(MinecraftServer server) {
   }

   public void onClick(ServerLevel world, Player player) {
      if (world.getBlockEntity(this.pos) instanceof ChallengeControllerBlockEntity<?> controller
         && controller.getState() == ChallengeControllerBlockEntity.State.IDLE) {
         world.playSound(null, this.pos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.MASTER, 1.0F, 0.7F);
         controller.setState(ChallengeControllerBlockEntity.State.GENERATING);
      }
   }

   public void onDetach() {
      CommonEvents.release(this);
   }

   @OnlyIn(Dist.CLIENT)
   public void onRender(PoseStack matrixStack, float partialTicks, Window window) {
   }

   public boolean shouldRenderObjectives() {
      return false;
   }

   public static class Adapter extends TypeSupplierAdapter<ChallengeManager> {
      public Adapter() {
         super("type", true);
         this.register("raid", RaidChallengeManager.class, RaidChallengeManager::new);
         this.register("x-mark", XMarkChallengeManager.class, XMarkChallengeManager::new);
         this.register("elite", EliteChallengeManager.class, EliteChallengeManager::new);
      }
   }
}
