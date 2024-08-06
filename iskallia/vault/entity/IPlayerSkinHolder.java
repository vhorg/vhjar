package iskallia.vault.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.entity.entity.DollMiniMeEntity;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;

public interface IPlayerSkinHolder {
   EntityDataSerializer<Optional<GameProfile>> OPTIONAL_GAME_PROFILE_SERIALIZER = new EntityDataSerializer<Optional<GameProfile>>() {
      public void write(FriendlyByteBuf byteBuf, Optional<GameProfile> gameProfile) {
         byteBuf.writeBoolean(gameProfile.isPresent());
         gameProfile.ifPresent(gp -> {
            byteBuf.writeOptional(Optional.ofNullable(gp.getId()), FriendlyByteBuf::writeUUID);
            byteBuf.writeUtf(gp.getName());
         });
      }

      public Optional<GameProfile> read(FriendlyByteBuf byteBuf) {
         return !byteBuf.readBoolean()
            ? Optional.empty()
            : Optional.of(new GameProfile((UUID)byteBuf.readOptional(FriendlyByteBuf::readUUID).orElse(null), byteBuf.readUtf()));
      }

      public Optional<GameProfile> copy(Optional<GameProfile> gameProfile) {
         return gameProfile;
      }
   };
   EntityDataAccessor<Optional<GameProfile>> OPTIONAL_GAME_PROFILE = SynchedEntityData.defineId(DollMiniMeEntity.class, OPTIONAL_GAME_PROFILE_SERIALIZER);

   Optional<GameProfile> getGameProfile();

   void setGameProfile(GameProfile var1);

   Optional<ResourceLocation> getSkinLocation();

   boolean isUpdatingSkin();

   void setSkinLocation(ResourceLocation var1);

   void startUpdatingSkin();

   void stopUpdatingSkin();

   boolean hasSlimSkin();

   void setSlimSkin(boolean var1);
}
