/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.berkeley.ground.dao.models.postgres;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.berkeley.ground.dao.PostgresTest;
import edu.berkeley.ground.model.models.Graph;
import edu.berkeley.ground.exceptions.GroundException;
import edu.berkeley.ground.model.versions.VersionHistoryDag;
import edu.berkeley.ground.model.versions.VersionSuccessor;

import static org.junit.Assert.*;

public class PostgresGraphFactoryTest extends PostgresTest {

  public PostgresGraphFactoryTest() throws GroundException {
    super();
  }


  @Test
  public void testGraphCreation() throws GroundException {
    String testName = "test";
    String sourceKey = "testKey";

    PostgresTest.graphsResource.createGraph(testName, sourceKey, new HashMap<>());
    Graph graph = PostgresTest.graphsResource.getGraph(testName);

    assertEquals(testName, graph.getName());
    assertEquals(sourceKey, graph.getSourceKey());
  }

  @Test(expected = GroundException.class)
  public void testRetrieveBadGraph() throws GroundException {
    String testName = "test";

    try {
      PostgresTest.graphsResource.getGraph(testName);
    } catch (GroundException e) {
      assertEquals("No Graph found with name " + testName + ".", e.getMessage());

      throw e;
    }
  }

  @Test
  public void testTruncate() throws GroundException {
    long edgeVersionId = PostgresTest.createTwoNodesAndEdge();

    List<Long> edgeVersionIds = new ArrayList<>();
    edgeVersionIds.add(edgeVersionId);

    String graphName = "testGraph";
    long graphId = PostgresTest.createGraph(graphName).getId();

    long graphVersionId = PostgresTest.createGraphVersion(graphId, edgeVersionIds).getId();

    List<Long> parents = new ArrayList<>();
    parents.add(graphVersionId);
    long newGraphVersionId = PostgresTest.createGraphVersion(graphId, edgeVersionIds, parents)
        .getId();

    PostgresTest.graphsResource.truncateGraph(graphName, 1);

    VersionHistoryDag<?> dag = PostgresTest.versionHistoryDAGFactory.retrieveFromDatabase(graphId);

    assertEquals(1, dag.getEdgeIds().size());

    VersionSuccessor<?> successor = PostgresTest.versionSuccessorFactory.retrieveFromDatabase(
        dag.getEdgeIds().get(0));

    PostgresTest.postgresClient.commit();

    assertEquals(0, successor.getFromId());
    assertEquals(newGraphVersionId, successor.getToId());
  }
}