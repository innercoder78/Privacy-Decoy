package com.privacydecoy.research;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/** Standard instrumentation status protocol without adding a Maven runner dependency. */
public final class PrototypeTestRunner extends Instrumentation {

    private String suite="containment", selected="";
    private Bundle researchArguments = new Bundle();
    public Bundle researchArguments() { return new Bundle(researchArguments); }
    @Override public void onCreate(Bundle arguments) {
        if (arguments != null) researchArguments = new Bundle(arguments);
        if(arguments!=null){suite=arguments.getString("suite","containment");selected=arguments.getString("case","");}
        super.onCreate(arguments);start();
    }
    @Override public void onStart() {

        Class<?> testClass="ag1runtime".equals(suite)?com.privacydecoy.research.ag1.Ag1RuntimeTests.class
            :"network".equals(suite)?NetworkTests.class:PrototypeTests.class;
        if(!"network".equals(suite)&&!"containment".equals(suite)&&!"ag1runtime".equals(suite)) {
            Bundle error=new Bundle();error.putString("shortMsg","Unknown test suite");finish(Activity.RESULT_CANCELED,error);return;
        }
        String tag="ag1runtime".equals(suite)?"PD_AG1B":"network".equals(suite)?"PD_PR5":"PD_PR4";
        Method[] tests = Arrays.stream(testClass.getDeclaredMethods())
            .filter(m -> m.getName().startsWith("test") && m.getParameterCount() == 0)
            .filter(m -> selected.isEmpty() || m.getName().equals(selected))
            .sorted(Comparator.comparing(Method::getName)).toArray(Method[]::new);
        int failures = 0;
        for (int i = 0; i < tests.length; i++) {
            Bundle status = new Bundle(); status.putString("id", "PrivacyDecoyPrototype");
            status.putString("class", testClass.getName()); status.putString("test", tests[i].getName());
            status.putInt("numtests", tests.length); status.putInt("current", i + 1); sendStatus(1, status);
            try {
                tests[i].invoke(testClass.getConstructor(Instrumentation.class).newInstance(this)); status.putString("stream", "."); sendStatus(0, status);
                android.util.Log.i(tag, "PASS " + tests[i].getName());
            }
            catch (Exception e) {
                failures++;
                Throwable cause = e.getCause();
                // Our assertions contain fixed messages only. Never report arbitrary platform exception payloads.
                String detail = cause instanceof AssertionError ? cause.getMessage()
                    : "platform-or-harness-exception:" + (cause == null ? "unknown" : cause.getClass().getSimpleName());
                status.putString("stack", tests[i].getName() + ": " + detail);
                status.putString("stream", "FAIL " + tests[i].getName() + ": " + detail + "\n"); sendStatus(-2, status);
                android.util.Log.i(tag, "FAIL " + tests[i].getName() + ": " + detail);
            }
        }
        Bundle result = new Bundle();
        result.putString("stream", "Tests run: " + tests.length + ", Failures: " + failures + "\n");
        if (tests.length == 0) result.putString("shortMsg", "No prototype tests discovered");
        finish(tests.length > 0 && failures == 0 ? Activity.RESULT_OK : Activity.RESULT_CANCELED, result);
    }
}
