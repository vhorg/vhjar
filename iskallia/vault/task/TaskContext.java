package iskallia.vault.task;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.task.source.TaskSource;
import net.minecraft.server.MinecraftServer;

public class TaskContext {
   private TaskSource source;
   private MinecraftServer server;
   private Vault vault;

   public TaskContext() {
   }

   protected TaskContext(TaskSource source, MinecraftServer server, Vault vault) {
      this.source = source;
      this.server = server;
      this.vault = vault;
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

   public TaskContext setServer(MinecraftServer server) {
      this.server = server;
      return this;
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

   public TaskContext copy() {
      return new TaskContext(this.source.copy(), this.server, this.vault);
   }
}
