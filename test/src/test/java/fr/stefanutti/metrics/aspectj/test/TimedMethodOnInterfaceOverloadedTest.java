package fr.stefanutti.metrics.aspectj.test;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TimedMethodOnInterfaceOverloadedTest {

    private TimedMethodOnInterfaceOverloaded instance;

    @Before
    public void createAtMetricsInstance() {
        instance = new TimedMethodOnInterfaceOverloadedImpl();
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void overloadedTimedMethodNotCalledYet() {
        assertThat(SharedMetricRegistries.names(), hasItem("overloadedInterfaceTimerRegistry"));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("overloadedInterfaceTimerRegistry");
        assertThat(registry.getTimers().keySet(), containsInAnyOrder("overloadedTimedMethodWithNoArguments"));

        // Make sure that all the timers haven't been called yet
        assertThat(registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(0L))));
    }

    @Test
    public void callOverloadedTimedMethodOnce() {
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("overloadedInterfaceTimerRegistry");

        // Call the timed methods and assert they've all been timed once
        instance.overloadedTimedMethod();
        instance.overloadedTimedMethod("string");
        instance.overloadedTimedMethod(Arrays.asList("string"), "string", new Object());
        assertThat(registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(1L))));
    }
}