package iskallia.vault.util;

import com.google.common.base.Functions;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.util.StringRepresentable;

public enum CooldownGuiOption implements StringRepresentable {
   OFF(0),
   LEFT(1),
   CENTER(2);

   private final int id;
   private static final Map<String, CooldownGuiOption> NAME_TO_TYPE = Arrays.stream(values())
      .collect(Collectors.toMap(CooldownGuiOption::getSerializedName, Functions.identity()));
   private static final CooldownGuiOption[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(CooldownGuiOption::getId))
      .toArray(CooldownGuiOption[]::new);

   private CooldownGuiOption(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static CooldownGuiOption byId(int pId) {
      return BY_ID[pId % BY_ID.length];
   }

   public static CooldownGuiOption fromString(String name) {
      return NAME_TO_TYPE.get(name.toLowerCase(Locale.ROOT));
   }

   public CooldownGuiOption cycle() {
      return this.getId() + 1 >= BY_ID.length ? byId(0) : byId(this.getId() + 1);
   }

   @Nonnull
   public String getSerializedName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   @Nonnull
   public String getSerializedNameUpper() {
      return this.name().toUpperCase(Locale.ROOT);
   }
}
