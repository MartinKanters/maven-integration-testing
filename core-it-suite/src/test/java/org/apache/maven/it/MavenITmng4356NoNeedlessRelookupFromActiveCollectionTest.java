package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.util.Properties;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-4356">MNG-4356</a>.
 * 
 * @author Benjamin Bentmann
 */
public class MavenITmng4356NoNeedlessRelookupFromActiveCollectionTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng4356NoNeedlessRelookupFromActiveCollectionTest()
    {
        super( ALL_MAVEN_VERSIONS );
    }

    /**
     * Test that read operations on active collections of components do not cause a re-lookup of the components from
     * the container which would prevent usage of stateful components that are usually instantiated per lookup.
     */
    public void testit()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-4356" );

        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        Properties props = verifier.loadProperties( "target/component.properties" );
        assertEquals( "2", props.getProperty( "count" ) );
        // the proper behavior of the collections is actually checked by the IT plugin, we merely check it was run
    }

}