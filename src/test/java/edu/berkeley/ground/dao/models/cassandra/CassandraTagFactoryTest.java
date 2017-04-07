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

package edu.berkeley.ground.dao.models.cassandra;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.berkeley.ground.dao.CassandraTest;
import edu.berkeley.ground.model.models.Tag;
import edu.berkeley.ground.model.versions.GroundType;
import edu.berkeley.ground.exceptions.GroundException;

import static org.junit.Assert.*;

public class CassandraTagFactoryTest extends CassandraTest {

  public CassandraTagFactoryTest() throws GroundException {
    super();
  }

  @Test
  public void testGetItemIdsByTag() throws GroundException {
    Map<String, Tag> tagsMap = new HashMap<>();
    tagsMap.put("testtag", new Tag(1, "testtag", "tag", GroundType.STRING));

    long nodeId1 = CassandraTest.nodesResource.createNode("test1", null, tagsMap).getId();
    long nodeId2 = CassandraTest.nodesResource.createNode("test2", null, tagsMap).getId();

    List<Long> ids = CassandraTest.tagFactory.getItemIdsByTag("testtag");

    CassandraTest.cassandraClient.commit();

    assertTrue(ids.contains(nodeId1));
    assertTrue(ids.contains(nodeId2));
  }

  @Test
  public void testGetVersionIdsByTag() throws GroundException {
    Map<String, Tag> tagsMap = new HashMap<>();
    tagsMap.put("testtag", new Tag(1, "testtag", "tag", GroundType.STRING));

    long nodeId = CassandraTest.createNode("testNode").getId();

    long nodeVersionId1 = CassandraTest.nodesResource.createNodeVersion(nodeId, tagsMap,
        new HashMap<>(), -1, null, new ArrayList<>()).getId();
    long nodeVersionId2 = CassandraTest.nodesResource.createNodeVersion(nodeId, tagsMap,
        new HashMap<>(), -1, null, new ArrayList<>()).getId();

    List<Long> ids = CassandraTest.tagFactory.getVersionIdsByTag("testtag");

    CassandraTest.cassandraClient.commit();

    assertTrue(ids.contains(nodeVersionId1));
    assertTrue(ids.contains(nodeVersionId2));
  }
}