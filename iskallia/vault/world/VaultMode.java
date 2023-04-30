package iskallia.vault.world;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.GameRules.Type;
import net.minecraft.world.level.GameRules.Value;
import net.minecraftforge.server.command.EnumArgument;
import org.jetbrains.annotations.Nullable;

public enum VaultMode implements StringRepresentable {
   NORMAL("normal"),
   CASUAL("casual"),
   HARDCORE("hardcore");

   private final String name;
   private static final Map<String, VaultMode> NAME_VALUES;

   private VaultMode(String name) {
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public static VaultMode fromName(String name) {
      return NAME_VALUES.getOrDefault(name, NORMAL);
   }

   static {
      Builder<String, VaultMode> builder = new Builder();

      for (VaultMode value : values()) {
         builder.put(value.getSerializedName(), value);
      }

      NAME_VALUES = builder.build();
   }

   public static class GameRuleValue extends Value<VaultMode.GameRuleValue> {
      private VaultMode mode = VaultMode.NORMAL;

      public GameRuleValue(Type<VaultMode.GameRuleValue> type) {
         super(type);
      }

      public GameRuleValue(Type<VaultMode.GameRuleValue> type, VaultMode mode) {
         super(type);
         this.mode = mode;
      }

      public static Type<VaultMode.GameRuleValue> create(VaultMode defaultValue) {
         return new Type(
            () -> EnumArgument.enumArgument(VaultMode.class), type -> new VaultMode.GameRuleValue(type, defaultValue), (s, v) -> {}, (v, k, t) -> {}
         );
      }

      protected void updateFromArgument(CommandContext<CommandSourceStack> context, String paramName) {
         this.mode = (VaultMode)context.getArgument(paramName, VaultMode.class);
      }

      protected void deserialize(String value) {
         this.mode = VaultMode.fromName(value);
      }

      public String serialize() {
         return this.mode.getSerializedName();
      }

      public int getCommandResult() {
         return this.mode.getSerializedName().hashCode();
      }

      protected VaultMode.GameRuleValue getSelf() {
         return this;
      }

      protected VaultMode.GameRuleValue copy() {
         return new VaultMode.GameRuleValue(this.type, this.mode);
      }

      public void setFrom(VaultMode.GameRuleValue value, @Nullable MinecraftServer pServer) {
         this.mode = value.mode;
         this.onChanged(pServer);
      }

      public VaultMode get() {
         return this.mode;
      }
   }
}
