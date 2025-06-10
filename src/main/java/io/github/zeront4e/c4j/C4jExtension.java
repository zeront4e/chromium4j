/*
Copyright 2025 zeront4e (https://github.com/zeront4e)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package io.github.zeront4e.c4j;

import java.util.Objects;

/**
 * Class to represent a Chromium extensions.
 */
public class C4jExtension {
    /**
     * The "uBlock Origin Lite" extension instance.
     */
    public static final C4jExtension U_BLOCK_ORIGIN_LITE_EXTENSION = new C4jExtension(
                    "c4j-ublock-origin-lite",
                    "uBlock Origin Lite",
                    "The lite version of uBlock Origin.",
                    "https://github.com/zeront4e/chromium4j/raw/refs/heads/main/static/" +
                            "chromium-extensions/ublock-origin-lite/extension.crx"
    );

    private final String id;
    private final String name;
    private final String description;
    private final String downloadUrl;
    private final String optionalSha256Checksum;

    public C4jExtension(String id, String name, String description, String downloadUrl, String optionalSha256Checksum) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.optionalSha256Checksum = optionalSha256Checksum;
    }

    public C4jExtension(String id, String name, String description, String downloadUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.optionalSha256Checksum = null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getOptionalSha256Checksum() {
        return optionalSha256Checksum;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        C4jExtension that = (C4jExtension) object;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) && Objects.equals(downloadUrl, that.downloadUrl) &&
                Objects.equals(optionalSha256Checksum, that.optionalSha256Checksum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, downloadUrl, optionalSha256Checksum);
    }

    @Override
    public String toString() {
        return "C4jExtension{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", optionalSha256Checksum='" + optionalSha256Checksum + '\'' +
                '}';
    }

    /**
     * Creates a new custom extension with the given ID, name, description, and download URL.
     * @param id The ID of the Chromium extension.
     * @param name The name of the Chromium extension (will be used as extension file name).
     * @param description The description of the Chromium extension.
     * @param downloadUrl The download URL of the extension.
     * @param sha256Checksum The SHA256 checksum of the extension to download.
     * @return A new custom extension.
     */
    public static C4jExtension createCustomExtension(String id, String name, String description, String downloadUrl,
                                                     String sha256Checksum) {
        return new C4jExtension(id, name, description, downloadUrl, sha256Checksum);
    }

    /**
     * Creates a new custom extension with the given ID, name, description, and download URL.
     * @param id The ID of the Chromium extension.
     * @param name The name of the Chromium extension (will be used as extension file name).
     * @param description The description of the Chromium extension.
     * @param downloadUrl The download URL of the extension.
     * @return A new custom extension.
     */
    public static C4jExtension createCustomExtension(String id, String name, String description, String downloadUrl) {
        return new C4jExtension(id, name, description, downloadUrl, null);
    }
}
