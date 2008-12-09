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
import java.util.ArrayList;
import java.util.List;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-282">MNG-282</a>.
 * 
 * @author John Casey
 * @version $Id$
 */
public class MavenITmng0282NonReactorExecWhenProjectIndependentTest
    extends AbstractMavenIntegrationTestCase
{

    /**
     * Test non-reactor behavior when plugin declares "@requiresProject false"
     */
    public void testitMNG282()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-0282" );
        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.deleteArtifact( "org.apache.maven.its.plugins", "maven-it-plugin-no-project", "1.0", "maven-plugin" );
        List cliOptions = new ArrayList();
        cliOptions.add( "--no-plugin-registry" );
        verifier.setCliOptions( cliOptions );
        verifier.executeGoal( "org.apache.maven.its.plugins:maven-it-plugin-no-project:light-touch" );
        verifier.assertFilePresent( "target/touch.txt" );
        verifier.assertFileNotPresent( "subproject/target/touch.txt" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

    }
}
