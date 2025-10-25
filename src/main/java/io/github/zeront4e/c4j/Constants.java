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

class Constants {
    /**
     * The default directory to download files into (located at the user home directory or the set directory).
     */
    public static final String DEFAULT_HOME_DOWNLOAD_DIRECTORY = ".chromium4j";

    /**
     * The user home directory.
     */
    public static final String USER_HOME_DIRECTORY = System.getProperty("user.home");

    /**
     * The chromium4j home directory property.
     */
    public static final String CHROMIUM4J_HOME_DIRECTORY_PATH_PROPERTY = "chromium4j.home-directory-path";

    /**
     * The chromium4j home directory (defaults to the user-home-directory property-value if no custom property is set).
     */
    public static final String CHROMIUM4J_HOME_DIRECTORY_PATH =
            System.getProperty(CHROMIUM4J_HOME_DIRECTORY_PATH_PROPERTY, USER_HOME_DIRECTORY);
}
