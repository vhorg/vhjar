package iskallia.vault.world.vault.logic.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class CompoundVaultCondition extends VaultCondition {
   private final List<String> postfix = new ArrayList<>();

   protected CompoundVaultCondition() {
   }

   protected CompoundVaultCondition(IVaultCondition condition, List<String> postfix, Consumer<List<String>> action) {
      super(null, condition);
      this.postfix.addAll(postfix);
      action.accept(this.postfix);
   }

   protected CompoundVaultCondition(VaultCondition a, VaultCondition b, String operator, IVaultCondition result) {
      super(null, result);
      if (a.getId() == null) {
         throw new IllegalStateException("Parent id can't be null!");
      } else {
         this.postfix.add(a.getId().toString());
         if (b instanceof CompoundVaultCondition) {
            this.postfix.addAll(((CompoundVaultCondition)b).postfix);
         } else if (b != null) {
            this.postfix.add(b.getId().toString());
         }

         this.postfix.add(operator);
      }
   }

   @Override
   public VaultCondition negate() {
      return new CompoundVaultCondition(this.condition.negate(), this.postfix, postfix -> postfix.add("~"));
   }

   @Override
   public VaultCondition and(VaultCondition other) {
      return new CompoundVaultCondition(this.condition.and(other), this.postfix, postfix -> {
         if (other instanceof CompoundVaultCondition) {
            postfix.addAll(((CompoundVaultCondition)other).postfix);
         } else {
            postfix.add(other.getId().toString());
         }

         postfix.add("&");
      });
   }

   @Override
   public VaultCondition or(VaultCondition other) {
      return new CompoundVaultCondition(this.condition.or(other), this.postfix, postfix -> {
         if (other instanceof CompoundVaultCondition) {
            postfix.addAll(((CompoundVaultCondition)other).postfix);
         } else {
            postfix.add(other.getId().toString());
         }

         postfix.add("|");
      });
   }

   @Override
   public VaultCondition xor(VaultCondition other) {
      return new CompoundVaultCondition(this.condition.xor(other), this.postfix, postfix -> {
         if (other instanceof CompoundVaultCondition) {
            postfix.addAll(((CompoundVaultCondition)other).postfix);
         } else {
            postfix.add(other.getId().toString());
         }

         postfix.add("^");
      });
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Postfix", String.join(" ", this.postfix));
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      Stack<Object> stack = new Stack<>();
      String[] data = nbt.getString("Postfix").split(Pattern.quote(" "));

      for (String s : data) {
         this.postfix.add(s);
         switch (s) {
            case "~": {
               IVaultCondition a = stack.peek() instanceof ResourceLocation ? REGISTRY.get(stack.pop()) : (IVaultCondition)stack.pop();
               stack.push(a.negate());
               break;
            }
            case "&": {
               IVaultCondition a = stack.peek() instanceof ResourceLocation ? REGISTRY.get(stack.pop()) : (IVaultCondition)stack.pop();
               IVaultCondition b = stack.peek() instanceof ResourceLocation ? REGISTRY.get(stack.pop()) : (IVaultCondition)stack.pop();
               stack.push(a.and(b));
               break;
            }
            case "|": {
               IVaultCondition a = stack.peek() instanceof ResourceLocation ? REGISTRY.get(stack.pop()) : (IVaultCondition)stack.pop();
               IVaultCondition b = stack.peek() instanceof ResourceLocation ? REGISTRY.get(stack.pop()) : (IVaultCondition)stack.pop();
               stack.push(a.or(b));
               break;
            }
            case "^": {
               IVaultCondition a = stack.peek() instanceof ResourceLocation ? REGISTRY.get(stack.pop()) : (IVaultCondition)stack.pop();
               IVaultCondition b = stack.peek() instanceof ResourceLocation ? REGISTRY.get(stack.pop()) : (IVaultCondition)stack.pop();
               stack.push(a.xor(b));
               break;
            }
            default:
               stack.push(new ResourceLocation(s));
         }
      }

      if (stack.size() != 1) {
         throw new IllegalStateException("Invalid end stack " + stack);
      } else {
         this.condition = (IVaultCondition)stack.pop();
      }
   }

   public static CompoundVaultCondition fromNBT(CompoundTag nbt) {
      CompoundVaultCondition condition = new CompoundVaultCondition();
      condition.deserializeNBT(nbt);
      return condition;
   }
}
