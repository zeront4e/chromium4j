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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class C4jTest {
    @TempDir
    private Path tempDir;

    private File mockChromiumFile;
    private C4jOsChromiumDistribution mockDistribution;
    private C4jChromeOptions mockOptions;
    private C4j.StatusCallback mockCallback;

    @BeforeEach
    void setUp() throws IOException {
        //Enable the test instance mode.

        C4j.setTestInstance(true);

        //Setup mocks.

        mockChromiumFile = new File(tempDir.toFile(), "chrome.exe");
        mockChromiumFile.createNewFile();

        mockDistribution = mock(C4jOsChromiumDistribution.class);

        mockOptions = mock(C4jChromeOptions.class);

        mockCallback = mock(C4j.StatusCallback.class);

        //Setup mock distribution.

        Map<C4jOsArchitecture, String> execMap = new HashMap<>();
        execMap.put(C4jOsArchitecture.WINDOWS_X64, "chrome.exe");

        when(mockDistribution.getArchitectureExecutableNameMap()).thenReturn(execMap);

        //Setup mock options.

        when(mockOptions.getChromeOptions()).thenReturn(new ChromeOptions());
    }

    @Test
    void testCreateInstanceWithDistribution() throws Exception {
        try (MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class);
             MockedStatic<C4jOsDetectionUtil> mockedDetection = mockStatic(C4jOsDetectionUtil.class);
             MockedStatic<FileSearchUtil> mockedFileSearch = mockStatic(FileSearchUtil.class)) {

            //Setup mocks.

            File mockDir = new File(tempDir.toFile(), "chrome-dir");
            mockDir.mkdirs();

            mockedDownloader.when(() -> C4jChromiumDownloader.getDefaultDistributionInstallationDirectory(any()))
                    .thenReturn(mockDir);

            mockedDetection.when(C4jOsDetectionUtil::detectOsArchitecture)
                    .thenReturn(C4jOsArchitecture.WINDOWS_X64);

            mockedFileSearch.when(() -> FileSearchUtil.findFileOrNull(any(), any()))
                    .thenReturn(mockChromiumFile);

            //Test the method.

            C4jRemoteChromium result = C4j.createInstance(mockDistribution);

            //Verify.

            assertNotNull(result);
        }
    }

    @Test
    void testCreateInstanceWithDistributionAndOptions() throws Exception {
        try (MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class);
             MockedStatic<C4jOsDetectionUtil> mockedDetection = mockStatic(C4jOsDetectionUtil.class);
             MockedStatic<FileSearchUtil> mockedFileSearch = mockStatic(FileSearchUtil.class)) {

            //Setup mocks.

            File mockDir = new File(tempDir.toFile(), "chrome-dir");
            mockDir.mkdirs();

            mockedDownloader.when(() -> C4jChromiumDownloader.getDefaultDistributionInstallationDirectory(any()))
                    .thenReturn(mockDir);

            mockedDetection.when(C4jOsDetectionUtil::detectOsArchitecture)
                    .thenReturn(C4jOsArchitecture.WINDOWS_X64);

            mockedFileSearch.when(() -> FileSearchUtil.findFileOrNull(any(), any()))
                    .thenReturn(mockChromiumFile);

            //Test the method.

            C4jRemoteChromium result = C4j.createInstance(mockDistribution, mockOptions);

            //Verify.

            assertNotNull(result);
        }
    }

    @Test
    void testCreateInstanceWithOverwrite() throws Exception {
        try (MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class);
             MockedStatic<C4jOsDetectionUtil> mockedDetection = mockStatic(C4jOsDetectionUtil.class);
             MockedStatic<FileSearchUtil> mockedFileSearch = mockStatic(FileSearchUtil.class);
             MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            //Setup mocks.

            File mockDir = new File(tempDir.toFile(), "chrome-dir");
            mockDir.mkdirs();

            mockedDownloader.when(() -> C4jChromiumDownloader.getDefaultDistributionInstallationDirectory(any()))
                    .thenReturn(mockDir);

            mockedDetection.when(C4jOsDetectionUtil::detectOsArchitecture)
                    .thenReturn(C4jOsArchitecture.WINDOWS_X64);

            mockedFileSearch.when(() -> FileSearchUtil.findFileOrNull(any(), any()))
                    .thenReturn(mockChromiumFile);

            mockedFiles.when(() -> Files.deleteIfExists(any())).thenReturn(true);

            //Test the method.

            C4jRemoteChromium result = C4j.createInstance(mockDistribution, mockOptions, true);

            //Verify.

            assertNotNull(result);
        }
    }

    @Test
    void testCreateInstanceWithCallback() throws Exception {
        try (MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class);
             MockedStatic<C4jOsDetectionUtil> mockedDetection = mockStatic(C4jOsDetectionUtil.class);
             MockedStatic<FileSearchUtil> mockedFileSearch = mockStatic(FileSearchUtil.class);
             MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            //Setup mocks.

            File mockDir = new File(tempDir.toFile(), "chrome-dir");
            mockDir.mkdirs();

            mockedDownloader.when(() -> C4jChromiumDownloader.getDefaultDistributionInstallationDirectory(any()))
                    .thenReturn(mockDir);

            mockedDetection.when(C4jOsDetectionUtil::detectOsArchitecture)
                    .thenReturn(C4jOsArchitecture.WINDOWS_X64);

            mockedFileSearch.when(() -> FileSearchUtil.findFileOrNull(any(), any()))
                    .thenReturn(mockChromiumFile);

            mockedFiles.when(() -> Files.deleteIfExists(any())).thenReturn(true);

            //Test the method.

            C4jRemoteChromium result = C4j.createInstance(mockDistribution, mockOptions, true, mockCallback);

            //Verify.

            assertNotNull(result);
            verify(mockCallback, atLeastOnce()).onStatusUpdate(anyString());
        }
    }

    @Test
    void testCreateInstanceWithFile() throws Exception {
        //Test the method-

        C4jRemoteChromium result = C4j.createInstance(mockChromiumFile, mockOptions);

        //Verify.

        assertNotNull(result);
    }

    @Test
    void testIsDefaultInstallationPresent() {
        try (MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class);
             MockedStatic<C4jOsDetectionUtil> mockedDetection = mockStatic(C4jOsDetectionUtil.class);
             MockedStatic<FileSearchUtil> mockedFileSearch = mockStatic(FileSearchUtil.class)) {

            //Setup mocks.

            File mockDir = new File(tempDir.toFile(), "chrome-dir");
            mockDir.mkdirs();

            mockedDownloader.when(() -> C4jChromiumDownloader.getDefaultDistributionInstallationDirectory(any()))
                    .thenReturn(mockDir);
            mockedDetection.when(C4jOsDetectionUtil::detectOsArchitecture)
                    .thenReturn(C4jOsArchitecture.WINDOWS_X64);

            //Test when file exists.

            mockedFileSearch.when(() -> FileSearchUtil.findFileOrNull(any(), any()))
                    .thenReturn(mockChromiumFile);

            assertTrue(C4j.isDefaultInstallationPresent(mockDistribution));

            //Test when file doesn't exist.

            mockedFileSearch.when(() -> FileSearchUtil.findFileOrNull(any(), any()))
                    .thenReturn(null);
            assertFalse(C4j.isDefaultInstallationPresent(mockDistribution));
        }
    }

    @Test
    void testIsDefaultInstallationPresentWithArchitecture() {
        try (MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class);
             MockedStatic<FileSearchUtil> mockedFileSearch = mockStatic(FileSearchUtil.class)) {

            //Setup mocks.

            File mockDir = new File(tempDir.toFile(), "chrome-dir");
            mockDir.mkdirs();

            mockedDownloader.when(() -> C4jChromiumDownloader.getDefaultDistributionInstallationDirectory(any()))
                    .thenReturn(mockDir);

            //Test when file exists.

            mockedFileSearch.when(() -> FileSearchUtil.findFileOrNull(any(), any()))
                    .thenReturn(mockChromiumFile);

            assertTrue(C4j.isDefaultInstallationPresent(mockDistribution, C4jOsArchitecture.WINDOWS_X64));

            //Test when file doesn't exist.

            mockedFileSearch.when(() -> FileSearchUtil.findFileOrNull(any(), any()))
                    .thenReturn(null);

            assertFalse(C4j.isDefaultInstallationPresent(mockDistribution, C4jOsArchitecture.WINDOWS_X64));
        }
    }

    @Test
    void testObtainDefaultChromiumOrFailWithExistingInstallation() throws Exception {
        try (MockedStatic<C4j> mockedC4j = mockStatic(C4j.class, invocation -> {
            if (invocation.getMethod().getName().equals("isDefaultInstallationPresent") &&
                    invocation.getArguments().length == 1) {
                return true;
            }
            else if (invocation.getMethod().getName().equals("getDefaultInstallationChromiumFile") &&
                    invocation.getArguments().length == 1) {
                return mockChromiumFile;
            }

            return invocation.callRealMethod();
        });
             MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class)) {

            //Call the real method we want to test.

            mockedC4j.when(() -> C4j.obtainDefaultChromiumOrFail(any(), any(), anyBoolean()))
                    .thenCallRealMethod();

            //Test the method.

            File result = C4j.obtainDefaultChromiumOrFail(mockDistribution, mockCallback, false);

            //Verify.

            assertEquals(mockChromiumFile, result);
            verify(mockCallback, atLeastOnce()).onStatusUpdate(anyString());

            mockedDownloader.verify(() -> C4jChromiumDownloader.downloadChromiumOrFail(any()), never());
        }
    }

    @Test
    void testObtainDefaultChromiumOrFailWithDownload() throws Exception {
        try (MockedStatic<C4j> mockedC4j = mockStatic(C4j.class, invocation -> {
            if (invocation.getMethod().getName().equals("isDefaultInstallationPresent") &&
                    invocation.getArguments().length == 1) {
                return false;
            }
            else if (invocation.getMethod().getName().equals("getDefaultInstallationChromiumFile") &&
                    invocation.getArguments().length == 1) {
                return mockChromiumFile;
            }
            return invocation.callRealMethod();
        });
             MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class)) {

            //Call the real method we want to test.

            mockedC4j.when(() -> C4j.obtainDefaultChromiumOrFail(any(), any(), anyBoolean()))
                    .thenCallRealMethod();

            //Test the method.

            File result = C4j.obtainDefaultChromiumOrFail(mockDistribution, mockCallback, false);

            //Verify.

            assertEquals(mockChromiumFile, result);

            verify(mockCallback, atLeastOnce()).onStatusUpdate(anyString());

            mockedDownloader.verify(() -> C4jChromiumDownloader.downloadChromiumOrFail(any()),
                    times(1));
        }
    }

    @Test
    void testObtainDefaultChromiumOrFailWithOverwrite() throws Exception {
        try (MockedStatic<C4j> mockedC4j = mockStatic(C4j.class, invocation -> {
            if (invocation.getMethod().getName().equals("getDefaultInstallationChromiumFile") &&
                    invocation.getArguments().length == 1) {
                return mockChromiumFile;
            }
            return invocation.callRealMethod();
        });
             MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class);
             MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            //Setup mocks.

            File mockDir = new File(tempDir.toFile(), "chrome-dir");

            mockedDownloader.when(() -> C4jChromiumDownloader.getDefaultDistributionInstallationDirectory(any()))
                    .thenReturn(mockDir);

            mockedFiles.when(() -> Files.deleteIfExists(any())).thenReturn(true);

            //Call the real method we want to test.

            mockedC4j.when(() -> C4j.obtainDefaultChromiumOrFail(any(), any(), anyBoolean()))
                    .thenCallRealMethod();

            //Test the method.

            File result = C4j.obtainDefaultChromiumOrFail(mockDistribution, mockCallback, true);

            //Verify.

            assertEquals(mockChromiumFile, result);
            verify(mockCallback, atLeastOnce()).onStatusUpdate(anyString());

            mockedDownloader.verify(() -> C4jChromiumDownloader.downloadChromiumOrFail(any()),
                    times(1));

            mockedFiles.verify(() -> Files.deleteIfExists(any()), times(1));
        }
    }

    @Test
    void testFindChromiumExecutableOrNullWithUnsupportedArchitecture() {
        try (MockedStatic<C4j> mockedC4j = mockStatic(C4j.class)) {
            //Call the real method we want to test.

            mockedC4j.when(() -> C4j.findChromiumExecutableOrNull(any(), any(), any()))
                    .thenCallRealMethod();

            //Test with unsupported architecture.

            File result = C4j.findChromiumExecutableOrNull(mockDistribution, C4jOsArchitecture.UNSUPPORTED,
                    tempDir.toFile());

            //Verify.

            assertNull(result);
        }
    }

    @Test
    void testFindChromiumExecutableOrNullWithNullExecutableName() {
        try (MockedStatic<C4j> mockedC4j = mockStatic(C4j.class)) {
            //Setup mock distribution with null executable name.

            C4jOsChromiumDistribution nullExecDistribution = mock(C4jOsChromiumDistribution.class);

            Map<C4jOsArchitecture, String> emptyMap = new HashMap<>();

            when(nullExecDistribution.getArchitectureExecutableNameMap()).thenReturn(emptyMap);

            //Call the real method we want to test.

            mockedC4j.when(() -> C4j.findChromiumExecutableOrNull(any(), any(), any()))
                    .thenCallRealMethod();

            //Test with null executable name.

            File result = C4j.findChromiumExecutableOrNull(nullExecDistribution, C4jOsArchitecture.WINDOWS_X64,
                    tempDir.toFile());

            //Verify.

            assertNull(result);
        }
    }

    @Test
    void testFindChromiumExecutableOrNullWithValidExecutable() {
        try (MockedStatic<C4j> mockedC4j = mockStatic(C4j.class);
             MockedStatic<FileSearchUtil> mockedFileSearch = mockStatic(FileSearchUtil.class)) {

            //Setup mocks.

            mockedFileSearch.when(() -> FileSearchUtil.findFileOrNull(any(), eq("chrome.exe")))
                    .thenReturn(mockChromiumFile);

            //Call the real method we want to test.

            mockedC4j.when(() -> C4j.findChromiumExecutableOrNull(any(), any(), any()))
                    .thenCallRealMethod();

            //Test with valid executable.

            File result = C4j.findChromiumExecutableOrNull(mockDistribution, C4jOsArchitecture.WINDOWS_X64,
                    tempDir.toFile());

            //Verify.

            assertEquals(mockChromiumFile, result);
        }
    }

    @Test
    void testGetDefaultInstallationChromiumFileWithNonExistentDirectory() {
        try (MockedStatic<C4j> mockedC4j = mockStatic(C4j.class);
             MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class);
             MockedStatic<C4jOsDetectionUtil> mockedDetection = mockStatic(C4jOsDetectionUtil.class)) {

            //Setup mocks.

            File nonExistentDir = new File(tempDir.toFile(), "non-existent");

            mockedDownloader.when(() -> C4jChromiumDownloader.getDefaultDistributionInstallationDirectory(any()))
                    .thenReturn(nonExistentDir);

            mockedDetection.when(C4jOsDetectionUtil::detectOsArchitecture)
                    .thenReturn(C4jOsArchitecture.WINDOWS_X64);

            //Call the real methods we want to test.

            mockedC4j.when(() -> C4j.getDefaultInstallationChromiumFile(any()))
                    .thenCallRealMethod();

            mockedC4j.when(() -> C4j.getDefaultInstallationChromiumFile(any(), any()))
                    .thenCallRealMethod();

            //Test the method.

            File result = C4j.getDefaultInstallationChromiumFile(mockDistribution);

            //Verify.

            assertNull(result);
        }
    }

    @Test
    void testGetDefaultInstallationChromiumFileWithExistingDirectory() {
        try (MockedStatic<C4j> mockedC4j = mockStatic(C4j.class);
             MockedStatic<C4jChromiumDownloader> mockedDownloader = mockStatic(C4jChromiumDownloader.class);
             MockedStatic<C4jOsDetectionUtil> mockedDetection = mockStatic(C4jOsDetectionUtil.class);
             MockedStatic<FileSearchUtil> mockedFileSearch = mockStatic(FileSearchUtil.class)) {

            //Setup mocks.

            File existingDir = new File(tempDir.toFile(), "existing-dir");
            existingDir.mkdirs();

            mockedDownloader.when(() -> C4jChromiumDownloader.getDefaultDistributionInstallationDirectory(any()))
                    .thenReturn(existingDir);

            mockedDetection.when(C4jOsDetectionUtil::detectOsArchitecture)
                    .thenReturn(C4jOsArchitecture.WINDOWS_X64);

            mockedFileSearch.when(() -> FileSearchUtil.findFileOrNull(any(), any()))
                    .thenReturn(mockChromiumFile);

            //Call the real methods we want to test.

            mockedC4j.when(() -> C4j.getDefaultInstallationChromiumFile(any()))
                    .thenCallRealMethod();

            mockedC4j.when(() -> C4j.getDefaultInstallationChromiumFile(any(), any()))
                    .thenCallRealMethod();

            mockedC4j.when(() -> C4j.findChromiumExecutableOrNull(any(), any(), any()))
                    .thenCallRealMethod();

            //Test the method.

            File result = C4j.getDefaultInstallationChromiumFile(mockDistribution);

            //Verify.

            assertEquals(mockChromiumFile, result);
        }
    }
}