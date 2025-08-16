package io.github.zeront4e.c4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

class PosixFilePermissionUtil {
    public enum PermissionSet {
        OWNER_READ_WRITE("r-x------");

        private final String permission;

        PermissionSet(String permission) {
            this.permission = permission;
        }

        public String getPermission() {
            return permission;
        }
    }

    /**
     * Set permissions on a file.
     * @param path The path to the file.
     * @param permissionSet The permissions to set.
     * @throws IOException An unexpected exception.
     */
    public static void setPermissionOrFail(Path path, PermissionSet permissionSet) throws IOException {
        Set<PosixFilePermission> filePermissions = PosixFilePermissions.fromString(permissionSet.getPermission());

        Files.setPosixFilePermissions(path, filePermissions);
    }
}
