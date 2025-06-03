package io.github.zeront4e.c4j;

/**
 * Enum to represent different common Chromium extensions.
 */
public enum C4jCommonExtension {
    /**
     * The lite version of uBlock Origin.
     */
    U_BLOCK_ORIGIN_LITE(
            "uBlockOriginLite",
            "The lite version of uBlock Origin.",
            "chromium4j.extensions.uBlockOriginLite.downloadUrl",
            "https://raw.githubusercontent.com/zeront4e/chromium-extensions/refs/heads/main/" +
                    "extensions/uBlockOriginLite/uBlockOriginLite.crx"
    );

    private final String id;
    private final String description;
    private final String downloadOverwriteProperty;
    private final String defaultDownloadUrl;

    C4jCommonExtension(String id, String description, String downloadOverwriteProperty, String defaultDownloadUrl) {
        this.id = id;
        this.description = description;
        this.downloadOverwriteProperty = downloadOverwriteProperty;
        this.defaultDownloadUrl = defaultDownloadUrl;
    }

    /**
     * Returns the ID of the Chromium extension.
     * @return The ID of the Chromium extension.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the description of the Chromium extension.
     * @return The description of the Chromium extension.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the property-name to use for overwriting the default download URL for the extension.
     * @return The property-name to overwrite the default URL.
     */
    public String getDownloadOverwriteProperty() {
        return downloadOverwriteProperty;
    }

    /**
     * Returns the default download URL for the extension.
     * @return The default download URL for the extension.
     */
    public String getDefaultDownloadUrl() {
        return defaultDownloadUrl;
    }
}
