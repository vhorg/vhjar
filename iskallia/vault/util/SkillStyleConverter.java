package iskallia.vault.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.imageio.ImageIO;

public class SkillStyleConverter {
   public static void main(String[] args) throws IOException {
      extractSkillStyles(
         Paths.get(".local/old_configs/abilities_gui_styles.json"),
         Paths.get("src/main/resources/assets/the_vault/textures/gui/abilities.png"),
         Paths.get("src/main/resources/assets/the_vault/textures/gui/abilities")
      );
      extractSkillStyles(
         Paths.get(".local/old_configs/researches_gui_styles.json"),
         Paths.get("src/main/resources/assets/the_vault/textures/gui/researches.png"),
         Paths.get("src/main/resources/assets/the_vault/textures/gui/researches")
      );
      extractSkillStyles(
         Paths.get(".local/old_configs/talents_gui_styles.json"),
         Paths.get("src/main/resources/assets/the_vault/textures/gui/talents.png"),
         Paths.get("src/main/resources/assets/the_vault/textures/gui/talents")
      );
   }

   private static void extractSkillStyles(Path jsonPath, Path atlasPath, Path outputPath) throws IOException {
      Gson gson = new GsonBuilder().create();
      SkillStyleConverter.Styles styles = (SkillStyleConverter.Styles)gson.fromJson(new FileReader(jsonPath.toFile()), SkillStyleConverter.Styles.class);
      BufferedImage bufferedImage = loadImage(atlasPath);

      for (Entry<String, SkillStyleConverter.SkillStyle> entry : styles.styles.entrySet()) {
         String name = entry.getKey();
         SkillStyleConverter.SkillStyle style = entry.getValue();
         BufferedImage imageSlice = getImageSlice(bufferedImage, 16, 16, style.u / 16, style.v / 16);
         Path targetImagePath = outputPath.resolve(name.toLowerCase().replace(" ", "_") + ".png");
         writeImage(imageSlice, targetImagePath);
         System.out.println(targetImagePath);
      }
   }

   public static BufferedImage loadImage(Path imagePath) throws IOException {
      InputStream inputStream = null;

      BufferedImage e;
      try {
         inputStream = Files.newInputStream(imagePath);
         e = ImageIO.read(inputStream);
      } catch (IOException var11) {
         throw var11;
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException var10) {
            }
         }
      }

      return e;
   }

   public static void writeImage(BufferedImage sourceImage, Path targetImagePath) throws IOException {
      Path parent = targetImagePath.getParent();
      if (parent != null) {
         Files.createDirectories(parent);
      }

      ImageIO.write(sourceImage, "png", targetImagePath.toFile());
   }

   public static BufferedImage getImageSlice(BufferedImage sourceImage, int sliceWidth, int sliceHeight, int x, int y) {
      BufferedImage imageSlice = new BufferedImage(sliceWidth, sliceHeight, sourceImage.getType());
      Graphics2D gr = imageSlice.createGraphics();
      gr.drawImage(
         sourceImage, 0, 0, sliceWidth, sliceHeight, sliceWidth * x, sliceHeight * y, sliceWidth * x + sliceWidth, sliceHeight * y + sliceHeight, null
      );
      gr.dispose();
      return imageSlice;
   }

   public static class SkillStyle {
      @Expose
      public int u;
      @Expose
      public int v;

      @Override
      public String toString() {
         return "SkillStyle{u=" + this.u + ", v=" + this.v + "}";
      }
   }

   public static class Styles {
      @Expose
      public HashMap<String, SkillStyleConverter.SkillStyle> styles;
   }
}
