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

public enum VaultLoot implements StringRepresentable {
   LEGACY("legacy", 0.5F),
   NORMAL("normal", 1.0F),
   PLENTY("plenty", 2.0F),
   EXTREME("extreme", 3.0F);

   private final String name;
   private final float multiplier;
   private static final Map<String, VaultLoot> NAME_VALUES;

   private VaultLoot(String name, float multiplier) {
      this.name = name;
      this.multiplier = multiplier;
   }

   public String getSerializedName() {
      return this.name;
   }

   public float getMultiplier() {
      return this.multiplier;
   }

   public static VaultLoot fromName(String name) {
      return NAME_VALUES.getOrDefault(name, NORMAL);
   }

   static {
      Builder<String, VaultLoot> builder = new Builder();

      for (VaultLoot value : values()) {
         builder.put(value.getSerializedName(), value);
      }

      NAME_VALUES = builder.build();
   }

   public static class GameRuleValue extends Value<VaultLoot.GameRuleValue> {
      private VaultLoot mode = VaultLoot.NORMAL;

      public GameRuleValue(Type<VaultLoot.GameRuleValue> type) {
         super(type);
      }

      public GameRuleValue(Type<VaultLoot.GameRuleValue> type, VaultLoot mode) {
         super(type);
         this.mode = mode;
      }

      public static Type<VaultLoot.GameRuleValue> create(VaultLoot defaultValue) {
         return new Type(
            () -> EnumArgument.enumArgument(VaultLoot.class), type -> new VaultLoot.GameRuleValue(type, defaultValue), (s, v) -> {}, (v, k, t) -> {}
         );
      }

      protected void updateFromArgument(CommandContext<CommandSourceStack> context, String paramName) {
         this.mode = (VaultLoot)context.getArgument(paramName, VaultLoot.class);
      }

      protected void deserialize(String value) {
         this.mode = VaultLoot.fromName(value);
      }

      public String serialize() {
         return this.mode.getSerializedName();
      }

      public int getCommandResult() {
         return this.mode.getSerializedName().hashCode();
      }

      protected VaultLoot.GameRuleValue getSelf() {
         return this;
      }

      protected VaultLoot.GameRuleValue copy() {
         return new VaultLoot.GameRuleValue(this.type, this.mode);
      }

      public void setFrom(VaultLoot.GameRuleValue value, @Nullable MinecraftServer pServer) {
         this.mode = value.mode;
         this.onChanged(pServer);
      }

      public VaultLoot get() {
         return this.mode;
      }
   }
}
