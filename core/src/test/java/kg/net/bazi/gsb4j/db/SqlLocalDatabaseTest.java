/*
 * Copyright 2018 Azilet B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kg.net.bazi.gsb4j.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import kg.net.bazi.gsb4j.data.PlatformType;
import kg.net.bazi.gsb4j.data.ThreatEntryType;
import kg.net.bazi.gsb4j.data.ThreatListDescriptor;
import kg.net.bazi.gsb4j.data.ThreatType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sqlite.JDBC;

/**
 *
 * @author azilet
 */
public class SqlLocalDatabaseTest {

    static DataSource dataSource;

    private SqlLocalDatabase db = new SqlLocalDatabase();
    private int itemsCount = 123456;
    private ThreatListDescriptor descriptor;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Path path = Files.createTempFile("sql-local-db", ".db");

        HikariConfig config = new HikariConfig();
        config.setPoolName("GsbTestDbPool");
        config.setAutoCommit(false);
        config.setJdbcUrl(JDBC.PREFIX + path.toString());
        config.setMinimumIdle(2);
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource ds = (HikariDataSource) dataSource;
            Files.delete(Paths.get(ds.getJdbcUrl().substring(JDBC.PREFIX.length())));
        }
    }

    @Before
    public void setUp() throws IOException {
        descriptor = new ThreatListDescriptor();
        descriptor.setThreatType(ThreatType.MALWARE);
        descriptor.setPlatformType(PlatformType.LINUX);
        descriptor.setThreatEntryType(ThreatEntryType.URL);

        List<String> ls = new ArrayList<>();
        for (int i = 1; i <= itemsCount; i++) {
            ls.add(String.valueOf(i));
        }

        db.dataSource = dataSource;
        db.persist(descriptor, ls);
    }

    @After
    public void tearDown() throws IOException {
        db.clear(descriptor);
    }

    @Test
    public void testLoad() throws Exception {
        List<String> ls = db.load(descriptor);
        Assert.assertEquals(itemsCount, ls.size());
        Assert.assertTrue(ls.contains("1"));
        Assert.assertTrue(ls.contains("2"));
    }

    @Test
    public void testPersist() throws Exception {
        List<String> newValues = new ArrayList<>();
        newValues.add("abc");
        newValues.add("def");
        db.persist(descriptor, newValues);

        List<String> ls = db.load(descriptor);
        Assert.assertEquals(itemsCount + 2, ls.size());
        Assert.assertTrue(ls.contains("abc"));
        Assert.assertTrue(ls.contains("def"));
        Assert.assertTrue(ls.contains("1"));
        Assert.assertTrue(ls.contains("2"));
    }

    @Test
    public void testContains() throws Exception {
        Assert.assertTrue(db.contains("1", descriptor));
        Assert.assertTrue(db.contains("2", descriptor));
        Assert.assertTrue(db.contains("3", descriptor));
        Assert.assertTrue(db.contains("4", descriptor));

        Assert.assertFalse(db.contains(String.valueOf(itemsCount + 1), descriptor));
        Assert.assertFalse(db.contains(String.valueOf(itemsCount + 2), descriptor));
    }

    @Test
    public void testClear() throws Exception {
        db.clear(descriptor);

        List<String> ls = db.load(descriptor);
        Assert.assertTrue(ls.isEmpty());
    }

}
