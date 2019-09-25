package at.willhaben.willtest.test;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ExtensionMock {

    public static ExtensionContext mockWithTestClassAndMethod(Class testclass, String methodName) {
        ExtensionContext context = mock(ExtensionContext.class);
        doReturn(testclass).when(context).getRequiredTestClass();
        Method testMethod = mock(Method.class);
        doReturn(methodName).when(testMethod).getName();
        doReturn(testMethod).when(context).getRequiredTestMethod();
        return context;
    }
}
