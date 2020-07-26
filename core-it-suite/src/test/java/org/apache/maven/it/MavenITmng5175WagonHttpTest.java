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

import org.apache.maven.it.util.ResourceExtractor;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This is a test set for <a href="https://issues.apache.org/jira/browse/MNG-5175">MNG-5175</a>.
 * test correct integration of wagon http: read time out configuration from settings.xml
 *
 *
 */
public class MavenITmng5175WagonHttpTest
    extends AbstractMavenIntegrationTestCase
{
    private Server server;

    private int port;
    private File testDir;

    public MavenITmng5175WagonHttpTest()
    {
        super( "[3.0.4,)" ); // 3.0.4+
    }

    @Override
    protected void setUp()
        throws Exception
    {
        final Path timeWriterLog = Paths.get( "target/time-writer.txt" );
        Handler handler = new AbstractHandler()
        {
            @Override
            public void handle( String target, Request baseRequest, HttpServletRequest request,
                                HttpServletResponse response )
                throws IOException, ServletException
            {
                try
                {
                    Thread.sleep( 15 );
                }
                catch ( InterruptedException e )
                {
                    throw new ServletException( e.getMessage() );
                }

                response.setContentType( "text/plain" );
                response.setStatus( HttpServletResponse.SC_OK );
                response.getWriter().println( "some content" );
                response.getWriter().println();

                ( (Request) request ).setHandled( true );
            }
        };

        server = new Server( 0 );
        server.setHandler( handler );
        server.start();
        if ( server.isFailed() )
        {
            fail( "Couldn't bind the server socket to a free port!" );
        }
        port = ( (NetworkConnector) server.getConnectors()[0] ).getLocalPort();
        System.out.println( "Bound server socket to the port " + port );

        testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-5175" );
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        if ( server != null )
        {
            server.stop();
            server.join();
        }
    }

    /**
     * Test that the read time out from settings is used.
     * basically use a 1ms time out and wait a bit in the handler
     */
    public void testmng5175_ReadTimeOutFromSettings1()
        throws Exception
    {
        executeTest(1);
    }

    public void testmng5175_ReadTimeOutFromSettings2()
            throws Exception
    {
        executeTest(2);
    }

    public void testmng5175_ReadTimeOutFromSettings3()
            throws Exception
    {
        executeTest(3);
    }

    public void testmng5175_ReadTimeOutFromSettings4()
            throws Exception
    {
        executeTest(4);
    }

    public void testmng5175_ReadTimeOutFromSettings5()
            throws Exception
    {
        executeTest(5);
    }

    public void testmng5175_ReadTimeOutFromSettings6()
            throws Exception
    {
        executeTest(6);
    }

    public void testmng5175_ReadTimeOutFromSettings7()
            throws Exception
    {
        executeTest(7);
    }

    public void testmng5175_ReadTimeOutFromSettings8()
            throws Exception
    {
        executeTest(8);
    }

    public void testmng5175_ReadTimeOutFromSettings9()
            throws Exception
    {
        executeTest(9);
    }

    public void testmng5175_ReadTimeOutFromSettings10()
            throws Exception
    {
        executeTest(10);
    }

    private void executeTest( int i ) throws VerificationException, IOException
    {
        Verifier verifier = newVerifier( testDir.getAbsolutePath() );

        Properties filterProps = new Properties();
        filterProps.setProperty( "@port@", Integer.toString( port ) );

        verifier.filterFile( "settings-template.xml", "settings.xml", "UTF-8", filterProps );

        verifier.deleteArtifacts( "org.apache.maven.its.mng5175", "fake-dependency", "1.0-SNAPSHOT" );

        verifier.addCliOption( "-U" );
        verifier.addCliOption( "--settings" );
        verifier.addCliOption( "settings.xml" );
        verifier.addCliOption( "--fail-never" );
        verifier.addCliOption( "--errors" );
        verifier.setLogFileName( "log" + i + ".txt" );
        verifier.setMavenDebug( true );
        verifier.executeGoal( "validate" );

        verifier.verifyTextInLog(
                "Could not transfer artifact org.apache.maven.its.mng5175:fake-dependency:pom:1.0-SNAPSHOT" );
        verifier.verifyTextInLog( "Read timed out" );
        verifier.resetStreams();
    }
}
