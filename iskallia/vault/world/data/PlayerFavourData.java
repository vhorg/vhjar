package iskallia.vault.world.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class PlayerFavourData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerFavour";
   protected Map<UUID, Map<PlayerFavourData.VaultGodType, Integer>> favourStats = new HashMap<>();

   public PlayerFavourData() {
      this("the_vault_PlayerFavour");
   }

   public PlayerFavourData(String name) {
      super(name);
   }

   public boolean addFavour(PlayerEntity player, PlayerFavourData.VaultGodType type, int count) {
      UUID playerUUID = player.func_110124_au();
      int favour = this.favourStats.computeIfAbsent(playerUUID, key -> new HashMap<>()).getOrDefault(type, 0);
      if (Math.abs(favour + count) > 16) {
         return false;
      } else {
         favour += count;
         this.favourStats.computeIfAbsent(playerUUID, key -> new HashMap<>()).put(type, favour);
         this.func_76185_a();
         return true;
      }
   }

   public int getFavour(UUID playerUUID, PlayerFavourData.VaultGodType type) {
      return this.favourStats.getOrDefault(playerUUID, Collections.emptyMap()).getOrDefault(type, 0);
   }

   public void func_76184_a(CompoundNBT nbt) {
      this.favourStats.clear();

      for (String key : nbt.func_150296_c()) {
         UUID playerUUID;
         try {
            playerUUID = UUID.fromString(key);
         } catch (IllegalArgumentException var11) {
            continue;
         }

         Map<PlayerFavourData.VaultGodType, Integer> playerFavour = new HashMap<>();
         CompoundNBT favourTag = nbt.func_74775_l(key);

         for (String godKey : favourTag.func_150296_c()) {
            try {
               playerFavour.put(PlayerFavourData.VaultGodType.valueOf(godKey), favourTag.func_74762_e(godKey));
            } catch (IllegalArgumentException var10) {
            }
         }

         this.favourStats.put(playerUUID, playerFavour);
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT compound) {
      this.favourStats.forEach((uuid, playerFavour) -> {
         CompoundNBT favourTag = new CompoundNBT();
         playerFavour.forEach((type, count) -> favourTag.func_74768_a(type.name(), count));
         compound.func_218657_a(uuid.toString(), favourTag);
      });
      return compound;
   }

   public static PlayerFavourData get(ServerWorld world) {
      return (PlayerFavourData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(PlayerFavourData::new, "the_vault_PlayerFavour");
   }

   public static enum VaultGodType {
      BENEVOLENT("Velara", "The Benevolent", TextFormatting.GREEN),
      OMNISCIENT("Tenos", "The Omniscient", TextFormatting.AQUA),
      TIMEKEEPER("Wendarr", "The Timekeeper", TextFormatting.GOLD),
      MALEVOLENCE("Idona", "The Malevolence", TextFormatting.RED);

      private final String name;
      private final String title;
      private final TextFormatting color;

      private VaultGodType(String name, String title, TextFormatting color) {
         this.name = name;
         this.title = title;
         this.color = color;
      }

      public String getName() {
         return this.name;
      }

      public String getTitle() {
         return this.title;
      }

      public TextFormatting getChatColor() {
         return this.color;
      }

      public ITextComponent getHoverChatComponent() {
         return new StringTextComponent("[Vault God] ")
            .func_240699_a_(TextFormatting.WHITE)
            .func_230529_a_(new StringTextComponent(this.name + ", " + this.title).func_240699_a_(this.color));
      }

      public ITextComponent getIdolDescription() {
         String s = this.getName().endsWith("s") ? "" : "s";
         return new StringTextComponent(String.format("%s'%s Idol", this.getName(), s)).func_240699_a_(this.getChatColor());
      }

      public IFormattableTextComponent getChosenPrefix() {
         String prefix = "[" + this.getName().charAt(0) + "C] ";
         IFormattableTextComponent cmp = new StringTextComponent(prefix).func_240699_a_(this.color);
         String s = this.getName().endsWith("s") ? "" : "s";
         IFormattableTextComponent hover = new StringTextComponent(String.format("%s'%s Chosen", this.getName(), s)).func_240699_a_(this.getChatColor());
         cmp.func_240700_a_(style -> style.func_240716_a_(new HoverEvent(Action.field_230550_a_, hover)));
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
