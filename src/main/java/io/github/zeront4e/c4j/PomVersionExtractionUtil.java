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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

class PomVersionExtractionUtil {

    public static String getPomVersion() throws Exception {
        InputStream pomInputStream = PomVersionExtractionUtil.class.getClassLoader()
                .getResourceAsStream("META-INF/pom.xml");

        if (pomInputStream == null) {
            //We try to find the local POM file, if it's not included in the resources.

            File localPomFile = new File("pom.xml");

            if(localPomFile.isFile()) {
                pomInputStream = new FileInputStream(localPomFile);
            }
            else {
                throw new Exception("The file \"pom.xml\" was not found.");
            }
        }

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = documentBuilder.parse(pomInputStream);
        document.getDocumentElement().normalize();

        NodeList versionNodes = document.getElementsByTagName("version");

        if (versionNodes.getLength() > 0) {
            return versionNodes.item(0).getTextContent().trim();
        }
        else {
            throw new Exception("Unable to find version tag in \"pom.xml\"");
        }
    }
}

