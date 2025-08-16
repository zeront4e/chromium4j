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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

class DownloaderGitHubAssetUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloaderGitHubAssetUtil.class);

    /**
     * Downloads the asset file from the constructed GitHub URL.
     * @param organizationName The GitHub organization name.
      * @param repositoryName The GitHub repository name.
      * @param assetName The asset name.
      * @return The asset file (as a byte-array).
      * @throws Exception An unexpected exception.
      */
    public static byte[] downloadAssetFileOrFail(String organizationName, String repositoryName,
                                                 String assetName) throws Exception {
        String assetUrl = extractAssetUrlOrFail(organizationName, repositoryName, assetName);

        return FilesDownloadUtil.downloadFileOrFail(assetUrl, assetUrl,
                status -> LOGGER.info("Asset status: {}", status));
    }

    private static String extractAssetUrlOrFail(String organizationName, String repositoryName,
                                                String assetName) throws Exception {
        String apiUrl = "https://api.github.com/repos/" + organizationName + "/" + repositoryName + "/releases/latest";

        byte[] response = FilesDownloadUtil.downloadFileOrFail(apiUrl, apiUrl,
                status -> LOGGER.info("Release status: {}", status));

        String jsonResponse = new String(response, StandardCharsets.UTF_8);

        JsonObject releaseJsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        JsonArray assetsArray = releaseJsonObject.getAsJsonArray("assets");

        String downloadUrl = null;

        for (JsonElement assetElement : assetsArray) {
            JsonObject asset = assetElement.getAsJsonObject();

            String name = asset.get("name").getAsString();

            if (assetName.equals(name)) {
                downloadUrl = asset.get("browser_download_url").getAsString();
                break;
            }
        }

        return downloadUrl;
    }
}