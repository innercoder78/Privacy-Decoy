package com.privacydecoy.research;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/** Standard instrumentation status protocol without adding a Maven runner dependency. */
public final class PrototypeTestRunner extends Instrumentation {
    @Override public void onCreate(Bundle arguments) { super.onCreate(arguments); start(); }
    @Override public void onStart() {
        Method[] tests = Arrays.stream(PrototypeTests.class.getDeclaredMethods())
            .filter(m -> m.getName().startsWith("test") && m.getParameterCount() == 0)
            .sorted(Comparator.comparing(Method::getName)).toArray(Method[]::new);
        int failures = 0;
        for (int i = 0; i < tests.length; i++) {
            Bundle status = new Bundle(); status.putString("id", "PrivacyDecoyPrototype");
            status.putString("class", PrototypeTests.class.getName()); status.putString("test", tests[i].getName());
            status.putInt("numtests", tests.length); status.putInt("current", i + 1); sendStatus(1, status);
            try {
                tests[i].invoke(new PrototypeTests(this)); status.putString("stream", "."); sendStatus(0, status);
                android.util.Log.i("PD_PR4", "PASS " + tests[i].getName());
            }
            catch (Exception e) {
                failures++;
                Throwable cause = e.getCause();
                // Our assertions contain fixed messages only. Never report arbitrary platform exception payloads.
                String detail = cause instanceof AssertionError ? cause.getMessage()
                    : "platform-or-harness-exception:" + (cause == null ? "unknown" : cause.getClass().getSimpleName());
                status.putString("stack", tests[i].getName() + ": " + detail);
                status.putString("stream", "FAIL " + tests[i].getName() + ": " + detail + "\n"); sendStatus(-2, status);
                android.util.Log.i("PD_PR4", "FAIL " + tests[i].getName() + ": " + detail);
            }
        }
        Bundle result = new Bundle();
        result.putString("stream", "Tests run: " + tests.length + ", Failures: " + failures + "\n");
        if (tests.length == 0) result.putString("shortMsg", "No prototype tests discovered");
        finish(tests.length > 0 ? Activity.RESULT_OK : Activity.RESULT_CANCELED, result);
    }
}
