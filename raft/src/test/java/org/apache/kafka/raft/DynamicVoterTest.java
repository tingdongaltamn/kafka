/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.raft;

import org.apache.kafka.common.Uuid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DynamicVoterTest {
    @Test
    public void testParseDynamicVoter() {
        assertEquals(new DynamicVoter(Uuid.fromString("K90IZ-0DRNazJ49kCZ1EMQ"),
                2,
                "localhost",
                (short) 8020),
            DynamicVoter.parse("2@localhost:8020:K90IZ-0DRNazJ49kCZ1EMQ"));
    }

    @Test
    public void testParseDynamicVoter2() {
        assertEquals(new DynamicVoter(Uuid.fromString("__0IZ-0DRNazJ49kCZ1EMQ"),
                100,
                "192.128.0.100",
                (short) 800),
            DynamicVoter.parse("100@192.128.0.100:800:__0IZ-0DRNazJ49kCZ1EMQ"));
    }

    @Test
    public void testParseDynamicVoterWithBrackets() {
        assertEquals(new DynamicVoter(Uuid.fromString("__0IZ-0DRNazJ49kCZ1EMQ"),
                5,
                "2001:4860:4860::8888",
                (short) 8020),
            DynamicVoter.parse("5@[2001:4860:4860::8888]:8020:__0IZ-0DRNazJ49kCZ1EMQ"));
    }

    @Test
    public void testParseDynamicVoterWithoutId() {
        assertEquals("No @ found in dynamic voter string.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("localhost:8020:K90IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testParseDynamicVoterWithoutId2() {
        assertEquals("Invalid @ at beginning of dynamic voter string.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("@localhost:8020:K90IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testParseDynamicVoterWithInvalidNegativeId() {
        assertEquals("Invalid negative node id -1 in dynamic voter string.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("-1@localhost:8020:K90IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testFailedToParseNodeId() {
        assertEquals("Failed to parse node id in dynamic voter string.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("blah@localhost:8020:K90IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testParseDynamicVoterWithoutHostname() {
        assertEquals("No hostname found after node id.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("2@")).
                    getMessage());
    }

    @Test
    public void testParseDynamicVoterWithUnbalancedBrackets() {
        assertEquals("Hostname began with left bracket, but no right bracket was found.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("5@[2001:4860:4860::8888:8020:__0IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testNoColonFollowingHostname() {
        assertEquals("No colon following hostname could be found.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("2@localhost8020K90IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testPortSectionMustStartWithAColon() {
        assertEquals("Port section must start with a colon.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("5@[2001:4860:4860::8888]8020:__0IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testParseDynamicVoterWithNoColonFollowingPort() {
        assertEquals("No colon following port could be found.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("5@[2001:4860:4860::8888]:8020__0IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testFailedToParsePort() {
        assertEquals("Failed to parse port in dynamic voter string.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("5@[2001:4860:4860::8888]:8020m:__0IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testInvalidNegativePort() {
        assertEquals("Invalid port -8020 in dynamic voter string.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("5@[2001:4860:4860::8888]:-8020:__0IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testInvalidPositivePort() {
        assertEquals("Invalid port 666666 in dynamic voter string.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("5@[2001:4860:4860::8888]:666666:__0IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testFailedToParseDirectoryId() {
        assertEquals("Failed to parse directory ID in dynamic voter string.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("5@[2001:4860:4860::8888]:8020:%_0IZ-0DRNazJ49kCZ1EMQ")).
                    getMessage());
    }

    @Test
    public void testFailedToParseDirectoryId2() {
        assertEquals("Failed to parse directory ID in dynamic voter string.",
            assertThrows(IllegalArgumentException.class,
                () -> DynamicVoter.parse("5@[2001:4860:4860::8888]:8020:")).
                    getMessage());
    }
}
