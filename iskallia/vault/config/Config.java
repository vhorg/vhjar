package iskallia.vault.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public abstract class Config {
   protected static final Random rand = new Random();
   private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
   protected String root = "config/the_vault/";
   protected String extension = ".json";

   public void generateConfig() {
      this.reset();

      try {
         this.writeConfig();
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   private File getConfigFile() {
      return new File(this.root + this.getName() + this.extension);
   }

   public abstract String getName();

   public <T extends Config> T readConfig() {
      try {
         return (T)GSON.fromJson(new FileReader(this.getConfigFile()), this.getClass());
      } catch (FileNotFoundException var2) {
         this.generateConfig();
         return (T)this;
      }
   }

   protected abstract void reset();

   public void writeConfig() throws IOException {
      File dir = new File(this.root);
      if (dir.exists() || dir.mkdirs()) {
         if (this.getConfigFile().exists() || this.getConfigFile().createNewFile()) {
            FileWriter writer = new FileWriter(this.getConfigFile());
            GSON.toJson(this, writer);
            writer.flush();
            writer.close();
         }
      }
   }
}
