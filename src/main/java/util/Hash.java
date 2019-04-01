package util;

import com.google.common.hash.Hashing;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;

public abstract class Hash {

    @NotNull
    public static String getHash(@NotNull String source) {
        return Hashing.sha256()
                .hashString(source, StandardCharsets.UTF_8)
                .toString();
    }

    public static boolean isEqual(@NotNull String source, @NotNull String hash) {
        return hash.equals(getHash(source));
    }
}
