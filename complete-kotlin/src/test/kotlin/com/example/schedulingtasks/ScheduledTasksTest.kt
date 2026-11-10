/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// tag::scheduled-tasks-test[]
package com.example.schedulingtasks

import org.awaitility.Awaitility.await
import org.awaitility.Durations
import org.junit.jupiter.api.Test
import org.mockito.Mockito.atLeast
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean

@SpringBootTest
class ScheduledTasksTest {

    @MockitoSpyBean
    private lateinit var tasks: ScheduledTasks

    @Test
    fun reportCurrentTime() {
        await().atMost(Durations.TEN_SECONDS).untilAsserted {
            verify(tasks, atLeast(2)).reportCurrentTime()
        }
    }
}
// end::scheduled-tasks-test[]
