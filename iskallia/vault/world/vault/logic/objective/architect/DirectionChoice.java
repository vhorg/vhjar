package iskallia.vault.world.vault.logic.objective.architect;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.logic.objective.architect.modifier.VoteModifier;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

public class DirectionChoice {
   private final Direction direction;
   private final TextFormatting chatColor;
   private final List<String> modifiers = new ArrayList<>();
   private int votes;

   DirectionChoice(Direction direction) {
      this.direction = direction;
      this.chatColor = getDirectionColor(this.direction);
      this.votes = 1;
   }

   DirectionChoice(CompoundNBT tag) {
      this.direction = Direction.func_176739_a(tag.func_74779_i("direction"));
      this.chatColor = getDirectionColor(this.direction);
      this.votes = tag.func_74762_e("votes");
      ListNBT modifierList = tag.func_150295_c("modifiers", 8);

      for (int i = 0; i < modifierList.size(); i++) {
         this.modifiers.add(modifierList.func_150307_f(i));
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

   public TextFormatting getChatColor() {
      return this.chatColor;
   }

   public ITextComponent getDirectionDisplay() {
      return this.getDirectionDisplay(null);
   }

   public ITextComponent getDirectionDisplay(@Nullable String prefix) {
      String directionName = (prefix == null ? "" : prefix) + StringUtils.capitalize(this.getDirection().func_176742_j());
      return new StringTextComponent(directionName).func_240699_a_(this.getChatColor());
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

   CompoundNBT serialize() {
      CompoundNBT tag = new CompoundNBT();
      tag.func_74778_a("direction", this.direction.func_176742_j());
      tag.func_74768_a("votes", this.votes);
      ListNBT modifierList = new ListNBT();
      this.modifiers.forEach(modifier -> modifierList.add(StringNBT.func_229705_a_(modifier)));
      tag.func_218657_a("modifiers", modifierList);
      return tag;
   }

   public static int getVOffset(Direction dir) {
      return 33 + (dir.ordinal() - 2) * 9;
   }

   private static TextFormatting getDirectionColor(Direction dir) {
      if (dir != null) {
         switch (dir) {
            case NORTH:
               return TextFormatting.RED;
            case SOUTH:
               return TextFormatting.AQUA;
            case WEST:
               return TextFormatting.GOLD;
            case EAST:
               return TextFormatting.GREEN;
         }
      }

      return TextFormatting.WHITE;
   }
}
