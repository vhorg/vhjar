package iskallia.vault.util;

import com.mojang.authlib.GameProfile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket.AddPlayerData;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class SkinProfile {
   public static final ExecutorService SERVICE = Executors.newFixedThreadPool(4);
   private String latestNickname;
   public AtomicReference<GameProfile> gameProfile = new AtomicReference<>();
   public AtomicReference<NetworkPlayerInfo> playerInfo = new AtomicReference<>();

   public String getLatestNickname() {
      return this.latestNickname;
   }

   public void updateSkin(String name) {
      if (!name.equals(this.latestNickname)) {
         this.latestNickname = name;
         if (FMLEnvironment.dist.isClient()) {
            SERVICE.submit(() -> {
               this.gameProfile.set(new GameProfile(null, name));
               this.gameProfile.set(SkullTileEntity.func_174884_b(this.gameProfile.get()));
               SPlayerListItemPacket var10002 = new SPlayerListItemPacket();
               var10002.getClass();
               AddPlayerData data = new AddPlayerData(var10002, this.gameProfile.get(), 0, null, null);
               this.playerInfo.set(new NetworkPlayerInfo(data));
            });
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getLocationSkin() {
      if (this.playerInfo != null && this.playerInfo.get() != null) {
         try {
            return this.playerInfo.get().func_178837_g();
         } catch (Exception var2) {
            var2.printStackTrace();
            return DefaultPlayerSkin.func_177335_a();
         }
      } else {
         return DefaultPlayerSkin.func_177335_a();
      }
   }
}
