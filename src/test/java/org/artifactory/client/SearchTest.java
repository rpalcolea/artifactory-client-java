package org.artifactory.client;

import groovyx.net.http.HttpResponseException;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author jbaruch
 * @since 03/08/12
 */
public class SearchTest extends ArtifactoryTestBase {

    @Test
    public void testLimitlessQuickSearch() throws HttpResponseException {
        List<String> results = artifactory.searches().artifactsByName("junit").doSearch();
        assertJUnits(results);
    }

    @Test(expectedExceptions = HttpResponseException.class, expectedExceptionsMessageRegExp = "Not Found")
    public void testQuickSearchWithWrongSingleLimit() throws HttpResponseException {
        //should fail
        artifactory.searches().artifactsByName("junit").repositories(LIBS_RELEASES_LOCAL).doSearch();
    }

    @Test
    public void testQuickSearchWithRightSingleLimit() throws HttpResponseException {
        List<String> results = artifactory.searches().artifactsByName("junit").repositories(REPO1_CACHE).doSearch();
        assertJUnits(results);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testQuickSearchWithoutSearchTerm() throws HttpResponseException {
        artifactory.searches().doSearch();
    }

    @Test
    public void testQuickSearchWithMultipleLimits() throws HttpResponseException {
        List<String> results =
                artifactory.searches().artifactsByName("junit").repositories(LIBS_RELEASES_LOCAL, REPO1_CACHE)
                        .doSearch();
        assertJUnits(results);
    }

    @Test(dependsOnGroups = "uploadBasics")
    public void testSearchByProperty() throws HttpResponseException {
        List<String> results = artifactory.searches().itemsByProperty().property("released", false).doSearch();
        assertEquals(results.size(), 1);
        assertTrue(results.get(0).contains("a/b/c.txt"));
    }

    @Test(dependsOnGroups = "uploadBasics")
    public void testSearchByPropertyWithMapNotation() throws HttpResponseException {
        Map<String, Boolean> properties = new HashMap<>();
        properties.put("released", false);
        List<String> results = artifactory.searches().itemsByProperty().properties(properties).doSearch();
        assertEquals(results.size(), 1);
        assertTrue(results.get(0).contains("a/b/c.txt"));
    }

    @Test(dependsOnGroups = "uploadBasics")
    public void testSearchByPropertyAndRepoFilter() throws HttpResponseException {
        List<String> results =
                artifactory.searches().itemsByProperty().property("released", false).repositories(NEW_LOCAL).doSearch();
        assertEquals(results.size(), 1);
        assertTrue(results.get(0).contains("a/b/c.txt"));
    }

    @Test(dependsOnGroups = "uploadBasics", expectedExceptions = HttpResponseException.class,
            expectedExceptionsMessageRegExp = "Not Found")
    public void testSearchByPropertyAndWrongRepoFilter() throws HttpResponseException {
        artifactory.searches().repositories(REPO1).itemsByProperty().property("released", false).doSearch();
    }

    @Test(dependsOnGroups = "uploadBasics", expectedExceptions = HttpResponseException.class,
            expectedExceptionsMessageRegExp = "Not Found")
    public void testSearchByPropertyWithMulipleValue() throws HttpResponseException {
        artifactory.searches().itemsByProperty().property("colors", "red", "green", "blue").doSearch();
    }

    @Test(dependsOnGroups = "uploadBasics", expectedExceptions = HttpResponseException.class,
            expectedExceptionsMessageRegExp = "Not Found")
    public void testSearchByPropertyMapNotationWithMulipleValue() throws HttpResponseException {
        Map<String, List<String>> property = new HashMap<>();
        property.put("colors", asList("red", "green", "blue"));
        artifactory.searches().itemsByProperty().properties(property).doSearch();
    }

    @Test(dependsOnGroups = "uploadBasics")
    public void testSearchByPropertyWithoutValue() throws HttpResponseException {
        List<String> results =
                artifactory.searches().itemsByProperty().property("released").property("build", 28).doSearch();
        assertEquals(results.size(), 1);
        assertTrue(results.get(0).contains("a/b/c.txt"));
    }

    private void assertJUnits(List<String> results) {
        assertEquals(results.size(), 5);
        for (String result : results) {
            assertTrue(result.contains("junit"));
        }
    }

}
