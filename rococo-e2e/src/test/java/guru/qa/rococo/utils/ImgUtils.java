package guru.qa.rococo.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;

public class ImgUtils {

    @NotNull
    public static String convertImageToBase64(String filePath) {
        byte[] fileContent = readImageAsByteArray(filePath);
        if (fileContent == null) {
            throw new IllegalArgumentException("Failed to read image: " + filePath);
        }
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(fileContent);
    }

    @Nullable
    private static byte[] readImageAsByteArray(String filePath) {
        try (InputStream inputStream = Objects.requireNonNull(ImgUtils.class.getClassLoader().getResourceAsStream(filePath))) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
