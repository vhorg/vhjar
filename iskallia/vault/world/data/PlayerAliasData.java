package iskallia.vault.world.data;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

public class PlayerAliasData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerAlias";
   private final Map<String, String> aliases = new HashMap<>();

   public void putAlias(String from, String to) {
      this.aliases.put(from.toLowerCase(), to);
      this.setDirty();
   }

   @Nullable
   public String getAlias(String from) {
      return this.aliases.get(from.toLowerCase());
   }

   private static PlayerAliasData create(CompoundTag tag) {
      PlayerAliasData data = new PlayerAliasData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag tag) {
      this.aliases.clear();
      ListTag aliasList = tag.getList("aliases", 10);

      for (int i = 0; i < aliasList.size(); i++) {
         CompoundTag aliasEntry = aliasList.getCompound(i);
         String from = aliasEntry.getString("from");
         String to = aliasEntry.getString("to");
         this.aliases.put(from.toLowerCase(), to);
      }
   }

   public CompoundTag save(CompoundTag tag) {
      ListTag aliasList = new ListTag();
      this.aliases.forEach((from, to) -> {
         CompoundTag aliasEntry = new CompoundTag();
         aliasEntry.putString("from", from);
         aliasEntry.putString("to", to);
         aliasList.add(aliasEntry);
      });
      tag.put("aliases", aliasList);
      return tag;
   }

   public static PlayerAliasData get(ServerLevel world) {
      return (PlayerAliasData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerAliasData::create, PlayerAliasData::new, "the_vault_PlayerAlias");
   }

   @Nonnull
   public static String applyAlias(ServerPlayer player, String gifter) {
      return applyAlias(player, gifter, false);
   }

   @Nonnull
   public static String applyAlias(ServerPlayer player, String gifter, boolean silent) {
      String alias = get(player.getLevel()).getAlias(gifter);
      if (alias != null) {
         if (!silent && !alias.equalsIgnoreCase(gifter)) {
            player.sendMessage(new TextComponent("Alias: Renamed " + gifter + " to " + alias).withStyle(ChatFormatting.GRAY), Util.NIL_UUID);
         }

         return alias;
      } else {
         return gifter;
      }
   }
}
