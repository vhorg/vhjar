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

public enum VaultCrystalMode implements StringRepresentable {
   INFINITY("infinity", 0.0F, 0),
   NORMAL("normal", 1.0F, 1),
   GRINDY("grindy", 3.0F, 1),
   EXTREME("extreme", 10.0F, 1);

   private final String name;
   private final float multiplier;
   private final int minCost;
   private static final Map<String, VaultCrystalMode> NAME_VALUES;

   private VaultCrystalMode(String name, float multiplier, int minCost) {
      this.name = name;
      this.multiplier = multiplier;
      this.minCost = minCost;
   }

   public float getMultiplier() {
      return this.multiplier;
   }

   public int getMinCost() {
      return this.minCost;
   }

   public String getSerializedName() {
      return this.name;
   }

   public static VaultCrystalMode fromName(String name) {
      return NAME_VALUES.getOrDefault(name, NORMAL);
   }

   static {
      Builder<String, VaultCrystalMode> builder = new Builder();

      for (VaultCrystalMode value : values()) {
         builder.put(value.getSerializedName(), value);
      }

      NAME_VALUES = builder.build();
   }

   public static class GameRuleValue extends Value<VaultCrystalMode.GameRuleValue> {
      private VaultCrystalMode mode = VaultCrystalMode.NORMAL;

      public GameRuleValue(Type<VaultCrystalMode.GameRuleValue> type) {
         super(type);
      }

      public GameRuleValue(Type<VaultCrystalMode.GameRuleValue> type, VaultCrystalMode mode) {
         super(type);
         this.mode = mode;
      }

      public static Type<VaultCrystalMode.GameRuleValue> create(VaultCrystalMode defaultValue) {
         return new Type(
            () -> EnumArgument.enumArgument(VaultCrystalMode.class),
            type -> new VaultCrystalMode.GameRuleValue(type, defaultValue),
            (s, v) -> {},
            (v, k, t) -> {}
         );
      }

      protected void updateFromArgument(CommandContext<CommandSourceStack> context, String paramName) {
         this.mode = (VaultCrystalMode)context.getArgument(paramName, VaultCrystalMode.class);
      }

      protected void deserialize(String value) {
         this.mode = VaultCrystalMode.fromName(value);
      }

      public String serialize() {
         return this.mode.getSerializedName();
      }

      public int getCommandResult() {
         return this.mode.getSerializedName().hashCode();
      }

      protected VaultCrystalMode.GameRuleValue getSelf() {
         return this;
      }

      protected VaultCrystalMode.GameRuleValue copy() {
         return new VaultCrystalMode.GameRuleValue(this.type, this.mode);
      }

      public void setFrom(VaultCrystalMode.GameRuleValue value, @Nullable MinecraftServer pServer) {
         this.mode = value.mode;
         this.onChanged(pServer);
      }

      public VaultCrystalMode get() {
         return this.mode;
      }
   }
}
