package iskallia.vault.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.PlayerUpdate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class SkinProfile {
   public static final ExecutorService SERVICE = Executors.newFixedThreadPool(4);
   private final AtomicReference<Boolean> slim = new AtomicReference<>(false);
   private String latestNickname;
   public AtomicReference<GameProfile> gameProfile = new AtomicReference<>();
   public AtomicReference<PlayerInfo> playerInfo = new AtomicReference<>();

   public String getLatestNickname() {
      return this.latestNickname;
   }

   public boolean isEmpty() {
      return this.getLatestNickname() == null;
   }

   public void updateSkin(@Nullable String name) {
      if (name == null || name.isEmpty()) {
         this.latestNickname = null;
         this.gameProfile.set(null);
         this.playerInfo.set(null);
         this.slim.set(false);
      } else if (!name.equals(this.latestNickname)) {
         this.latestNickname = name;
         if (FMLEnvironment.dist.isClient()) {
            SERVICE.submit(() -> {
               this.gameProfile.set(new GameProfile(null, name));
               SkullBlockEntity.updateGameprofile(this.gameProfile.get(), newProfile -> {
                  this.gameProfile.set(newProfile);
                  PlayerUpdate data = new PlayerUpdate(this.gameProfile.get(), 0, null, null);
                  this.playerInfo.set(new PlayerInfo(data));
                  this.slim.set(isSlim(newProfile));
               });
            });
         }
      }
   }

   public boolean isSlim() {
      return this.slim.get();
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getLocationSkin() {
      if (this.playerInfo != null && this.playerInfo.get() != null) {
         try {
            return this.playerInfo.get().getSkinLocation();
         } catch (Exception var2) {
            var2.printStackTrace();
            return DefaultPlayerSkin.getDefaultSkin();
         }
      } else {
         return DefaultPlayerSkin.getDefaultSkin();
      }
   }

   private static boolean isSlim(GameProfile gameProfile) {
      if (!gameProfile.isComplete()) {
         return false;
      } else {
         SkinManager skinManager = Minecraft.getInstance().getSkinManager();
         Map<Type, MinecraftProfileTexture> skinCache = skinManager.getInsecureSkinInformation(gameProfile);
         if (!skinCache.containsKey(Type.SKIN)) {
            return false;
         } else {
            MinecraftProfileTexture texture = skinCache.get(Type.SKIN);
            String s = texture.getMetadata("model");
            return s != null && !s.equals("default");
         }
      }
   }
}
