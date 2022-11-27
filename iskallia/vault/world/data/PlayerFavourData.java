package iskallia.vault.world.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

public class PlayerFavourData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerFavour";
   protected Map<UUID, Map<PlayerFavourData.VaultGodType, Integer>> favourStats = new HashMap<>();

   public boolean addFavour(Player player, PlayerFavourData.VaultGodType type, int count) {
      UUID playerUUID = player.getUUID();
      int favour = this.favourStats.computeIfAbsent(playerUUID, key -> new HashMap<>()).getOrDefault(type, 0);
      if (Math.abs(favour + count) > 16) {
         return false;
      } else {
         favour += count;
         this.favourStats.computeIfAbsent(playerUUID, key -> new HashMap<>()).put(type, favour);
         this.setDirty();
         return true;
      }
   }

   public int getFavour(UUID playerUUID, PlayerFavourData.VaultGodType type) {
      return this.favourStats.getOrDefault(playerUUID, Collections.emptyMap()).getOrDefault(type, 0);
   }

   private static PlayerFavourData create(CompoundTag tag) {
      PlayerFavourData data = new PlayerFavourData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      this.favourStats.clear();

      for (String key : nbt.getAllKeys()) {
         UUID playerUUID;
         try {
            playerUUID = UUID.fromString(key);
         } catch (IllegalArgumentException var11) {
            continue;
         }

         Map<PlayerFavourData.VaultGodType, Integer> playerFavour = new HashMap<>();
         CompoundTag favourTag = nbt.getCompound(key);

         for (String godKey : favourTag.getAllKeys()) {
            try {
               playerFavour.put(PlayerFavourData.VaultGodType.valueOf(godKey), favourTag.getInt(godKey));
            } catch (IllegalArgumentException var10) {
            }
         }

         this.favourStats.put(playerUUID, playerFavour);
      }
   }

   public CompoundTag save(CompoundTag compound) {
      this.favourStats.forEach((uuid, playerFavour) -> {
         CompoundTag favourTag = new CompoundTag();
         playerFavour.forEach((type, count) -> favourTag.putInt(type.name(), count));
         compound.put(uuid.toString(), favourTag);
      });
      return compound;
   }

   public static PlayerFavourData get(ServerLevel world) {
      return (PlayerFavourData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerFavourData::create, PlayerFavourData::new, "the_vault_PlayerFavour");
   }

   public static enum VaultGodType implements StringRepresentable {
      BENEVOLENT("Velara", "The Benevolent", ChatFormatting.GREEN),
      OMNISCIENT("Tenos", "The Omniscient", ChatFormatting.AQUA),
      TIMEKEEPER("Wendarr", "The Timekeeper", ChatFormatting.GOLD),
      MALEVOLENT("Idona", "The Malevolent", ChatFormatting.RED);

      private final String name;
      private final String title;
      private final ChatFormatting color;

      private VaultGodType(String name, String title, ChatFormatting color) {
         this.name = name;
         this.title = title;
         this.color = color;
      }

      public static PlayerFavourData.VaultGodType fromName(String name) {
         for (PlayerFavourData.VaultGodType type : values()) {
            if (name.equalsIgnoreCase(type.getName())) {
               return type;
            }
         }

         return null;
      }

      public String getName() {
         return this.name;
      }

      public String getTitle() {
         return this.title;
      }

      public ChatFormatting getChatColor() {
         return this.color;
      }

      @Nonnull
      public String getSerializedName() {
         return this.getName().toLowerCase();
      }

      public Component getHoverChatComponent() {
         return new TextComponent("[Vault God] ")
            .withStyle(ChatFormatting.WHITE)
            .append(new TextComponent(this.name + ", " + this.title).withStyle(this.color));
      }

      public Component getIdolDescription() {
         String s = this.getName().endsWith("s") ? "" : "s";
         return new TextComponent(String.format("%s'%s Idol", this.getName(), s)).withStyle(this.getChatColor());
      }

      public MutableComponent getChosenPrefix() {
         String prefix = "[" + this.getName().charAt(0) + "C] ";
         MutableComponent cmp = new TextComponent(prefix).withStyle(this.color);
         String s = this.getName().endsWith("s") ? "" : "s";
         MutableComponent hover = new TextComponent(String.format("%s'%s Chosen", this.getName(), s)).withStyle(this.getChatColor());
         cmp.withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, hover)));
         return cmp;
      }

      public PlayerFavourData.VaultGodType getOther(Random rand) {
         int i;
         do {
            i = rand.nextInt(values().length);
         } while (i == this.ordinal());

         return values()[i];
      }
   }
}
