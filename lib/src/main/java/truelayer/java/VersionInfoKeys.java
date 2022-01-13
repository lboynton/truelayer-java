package truelayer.java;

import lombok.Builder;
import lombok.Data;

/**
 * Holds version info keys expected in the version properties
 */
@Builder
@Data
public class VersionInfoKeys {
    public static String NAME = "name";
    public static String VERSION = "version";
}