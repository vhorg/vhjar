package iskallia.vault.world.vault.logic.objective.architect;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.logic.objective.architect.modifier.VoteModifier;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;

public class DirectionChoice {
   private final Direction direction;
   private final ChatFormatting chatColor;
   private final List<String> modifiers = new ArrayList<>();
   private int votes;

   DirectionChoice(Direction direction) {
      this.direction = direction;
      this.chatColor = getDirectionColor(this.direction);
      this.votes = 10;
   }

   DirectionChoice(CompoundTag tag) {
      this.direction = Direction.byName(tag.getString("direction"));
      this.chatColor = getDirectionColor(this.direction);
      this.votes = tag.getInt("votes");
      ListTag modifierList = tag.getList("modifiers", 8);

      for (int i = 0; i < modifierList.size(); i++) {
         this.modifiers.add(modifierList.getString(i));
      }
   }

   public void addVote() {
      this.votes++;
   }

   public int getVotes() {
      return this.votes;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public ChatFormatting getChatColor() {
      return this.chatColor;
   }

   public Component getDirectionDisplay() {
      return this.getDirectionDisplay(null);
   }

   public Component getDirectionDisplay(@Nullable String prefix) {
      String directionName = (prefix == null ? "" : prefix) + StringUtils.capitalize(this.getDirection().getName());
      return new TextComponent(directionName).withStyle(this.getChatColor());
   }

   public void addModifier(VoteModifier modifier) {
      this.modifiers.add(modifier.getName());
   }

   public List<VoteModifier> getModifiers() {
      List<VoteModifier> modifierList = new ArrayList<>();
      this.modifiers.forEach(modifierStr -> {
         VoteModifier modifier = ModConfigs.ARCHITECT_EVENT.getModifier(modifierStr);
         if (modifier != null) {
            modifierList.add(modifier);
         }
      });
      return modifierList;
   }

   CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.putString("direction", this.direction.getName());
      tag.putInt("votes", this.votes);
      ListTag modifierList = new ListTag();
      this.modifiers.forEach(modifier -> modifierList.add(StringTag.valueOf(modifier)));
      tag.put("modifiers", modifierList);
      return tag;
   }

   public static int getVOffset(Direction dir) {
      return 33 + (dir.ordinal() - 2) * 9;
   }

   private static ChatFormatting getDirectionColor(Direction dir) {
      if (dir != null) {
         switch (dir) {
            case NORTH:
               return ChatFormatting.RED;
            case SOUTH:
               return ChatFormatting.AQUA;
            case WEST:
               return ChatFormatting.GOLD;
            case EAST:
               return ChatFormatting.GREEN;
         }
      }

      return ChatFormatting.WHITE;
   }
}
