package iskallia.vault.world.vault.logic.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class CompoundVaultTask extends VaultTask {
   private List<String> postfix = new ArrayList<>();

   protected CompoundVaultTask() {
   }

   protected CompoundVaultTask(IVaultTask task, List<String> postfix, Consumer<List<String>> action) {
      super(null, task);
      this.postfix.addAll(postfix);
      action.accept(this.postfix);
   }

   public CompoundVaultTask(VaultTask a, VaultTask b, String operator, IVaultTask result) {
      super(null, result);
      if (a.getId() == null) {
         throw new IllegalStateException("Parent id can't be null!");
      } else {
         this.postfix.add(a.getId().toString());
         if (b instanceof CompoundVaultTask) {
            this.postfix.addAll(((CompoundVaultTask)b).postfix);
         } else if (b != null) {
            this.postfix.add(b.getId().toString());
         }

         this.postfix.add(operator);
      }
   }

   @Override
   public VaultTask then(VaultTask other) {
      return new CompoundVaultTask(this.task.then(other), this.postfix, postfix -> {
         if (other instanceof CompoundVaultTask) {
            postfix.addAll(((CompoundVaultTask)other).postfix);
         } else {
            postfix.add(other.getId().toString());
         }

         postfix.add(">");
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
            case ">":
               IVaultTask a = stack.peek() instanceof ResourceLocation ? REGISTRY.get(stack.pop()) : (IVaultTask)stack.pop();
               IVaultTask b = stack.peek() instanceof ResourceLocation ? REGISTRY.get(stack.pop()) : (IVaultTask)stack.pop();
               stack.push(a.then(b));
               break;
            default:
               stack.push(new ResourceLocation(s));
         }
      }

      if (stack.size() != 1) {
         throw new IllegalStateException("Invalid end stack " + stack);
      } else {
         this.task = (IVaultTask)stack.pop();
      }
   }

   public static CompoundVaultTask fromNBT(CompoundTag nbt) {
      CompoundVaultTask condition = new CompoundVaultTask();
      condition.deserializeNBT(nbt);
      return condition;
   }
}
