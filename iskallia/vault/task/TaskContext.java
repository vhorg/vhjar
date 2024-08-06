package iskallia.vault.task;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.task.source.TaskSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import net.minecraft.server.MinecraftServer;

public class TaskContext {
   private TaskSource source;
   private MinecraftServer server;
   private Vault vault;
   private Stack<Task> parent = new Stack<>();
   private Stack<List<Task>> children;

   public TaskContext() {
      this.parent.push(null);
      this.children = new Stack<>();
      this.children.push(new ArrayList<>());
   }

   public static TaskContext of(TaskSource source, MinecraftServer server) {
      TaskContext context = new TaskContext();
      context.setSource(source);
      context.server = server;
      return context;
   }

   public TaskSource getSource() {
      return this.source;
   }

   public TaskContext setSource(TaskSource source) {
      this.source = source;
      return this;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public long getTickTime() {
      return this.server.getWorldData().overworldData().getGameTime();
   }

   public int getLevel() {
      return this.vault == null ? 0 : this.vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
   }

   public Vault getVault() {
      return this.vault;
   }

   public TaskContext setVault(Vault vault) {
      this.vault = vault;
      return this;
   }

   public Optional<Task> getParent() {
      return Optional.ofNullable(this.parent.peek());
   }

   public void pushParent(Task task) {
      this.parent.push(task);
   }

   public <T extends Task> T popParent() {
      return (T)this.parent.pop();
   }

   public List<? extends Task> getChildren() {
      return this.children.peek();
   }

   public TaskContext pushChildren(List<? extends Task> children) {
      this.children.push(children);
      return this;
   }

   public <T extends Task> TaskContext pushChildren(T[] children) {
      this.children.push(Arrays.asList(children));
      return this;
   }

   public <T extends Task> List<T> popChildren() {
      return (List<T>)this.children.pop();
   }
}
