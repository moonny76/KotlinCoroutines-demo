# KotlinCoroutines-demo

**Run/Debug Configuration Template**

1. **Edit Configurations...**
2. Click on **Edit Configurations templates...**
3. Select **Kotlin** and set **VM options**:

```shell
-Dkotlinx.coroutines.debug
```

***

### Android

> #### Myth Buster: Suspending Functions

- [coroutines/android/MythMainActivity.kt](app/src/main/java/com/scarlet/coroutines/android/MythMainActivity.kt)

> #### Launching Coroutines in Views

- [coroutines/android/CoActivity.kt](app/src/main/java/com/scarlet/coroutines/android/CoActivity.kt)

> #### `supervisorScope` with `CoroutineExceptionHandler`

- [coroutines/android/ScopedActivity.kt](app/src/main/java/com/scarlet/coroutines/android/ScopedActivity.kt)

> #### LiveData

- [coroutines/android/livedata/ApiService.kt](app/src/main/java/com/scarlet/coroutines/android/livedata/ApiService.kt)
- [coroutines/android/livedata/ArticleActivity.kt](app/src/main/java/com/scarlet/coroutines/android/livedata/ArticleActivity.kt)
- [coroutines/android/livedata/ArticleViewModel.kt](app/src/main/java/com/scarlet/coroutines/android/livedata/ArticleViewModel.kt)
- [coroutines/android/livedata/FakeApiService.kt](app/src/main/java/com/scarlet/coroutines/android/livedata/FakeApiService.kt)

***

### Coroutine Basics

- [coroutines/basics/B00_WhyCoroutine.kt](app/src/main/java/com/scarlet/coroutines/basics/B00_WhyCoroutine.kt)
- [coroutines/basics/B01_RunBlocking.kt](app/src/main/java/com/scarlet/coroutines/basics/B01_RunBlocking.kt)
- [coroutines/basics/B02_Launch.kt](app/src/main/java/com/scarlet/coroutines/basics/B02_Launch.kt)
- [coroutines/basics/B03_Async.kt](app/src/main/java/com/scarlet/coroutines/basics/B03_Async.kt)
- [coroutines/basics/B04_StructuredConcurrency.kt](app/src/main/java/com/scarlet/coroutines/basics/B04_StructuredConcurrency.kt)
- [coroutines/basics/CoroutinesDemo.kt](app/src/main/java/com/scarlet/coroutines/basics/CoroutinesDemo.kt)

***

### Advanced Coroutine

- [coroutines/advanced/C01_ThreadVsCoroutine.kt](app/src/main/java/com/scarlet/coroutines/advanced/C01_ThreadVsCoroutine.kt)
- [coroutines/advanced/C02_CPS.kt](app/src/main/java/com/scarlet/coroutines/advanced/C02_CPS.kt)
- [coroutines/advanced/C03_Context.kt](app/src/main/java/com/scarlet/coroutines/advanced/C03_Context.kt)
- [coroutines/advanced/C04_CoroutineScope.kt](app/src/main/java/com/scarlet/coroutines/advanced/C04_CoroutineScope.kt)
- [coroutines/advanced/C05_JobsRelation.kt](app/src/main/java/com/scarlet/coroutines/advanced/C05_JobsRelation.kt)
- [coroutines/advanced/C06_SupervisorJob.kt](app/src/main/java/com/scarlet/coroutines/advanced/C06_SupervisorJob.kt)
- [coroutines/advanced/C07_Dispatchers.kt](app/src/main/java/com/scarlet/coroutines/advanced/C07_Dispatchers.kt)
- [coroutines/advanced/C08_CoroutineScopeFunctions.kt](app/src/main/java/com/scarlet/coroutines/advanced/C08_CoroutineScopeFunctions.kt)
- [coroutines/advanced/C09_ParallelDecomposition.kt](app/src/main/java/com/scarlet/coroutines/advanced/C09_ParallelDecomposition.kt)
- [coroutines/advanced/C10_SuspendOrigin.kt](app/src/main/java/com/scarlet/coroutines/advanced/C10_SuspendOrigin.kt)

***

### Coroutine Cancellation

- [coroutines/cancellation/C01_Cancellation.kt](app/src/main/java/com/scarlet/coroutines/cancellation/C01_Cancellation.kt)
- [coroutines/cancellation/C02_NonCancellable.kt](app/src/main/java/com/scarlet/coroutines/cancellation/C02_NonCancellable.kt)
- [coroutines/cancellation/C03_CooperationForCancellation.kt](app/src/main/java/com/scarlet/coroutines/cancellation/C03_CooperationForCancellation.kt)

***

### Migration from Callback to Coroutines

- [coroutines/migration/M01_CvtCallbackToSuspendFun1.kt](app/src/main/java/com/scarlet/coroutines/migration/M01_CvtCallbackToSuspendFun1.kt)
- [coroutines/migration/M02_CvtCallbackToSuspendFun2.kt](app/src/main/java/com/scarlet/coroutines/migration/M02_CvtCallbackToSuspendFun2.kt)

***

## Coroutine Testing

### JUnit 4 Annotations and  Myth Buster

- [junit/mythbuster/Junit4Test.kt](app/src/test/java/com/scarlet/junit/mythbuster/Junit4Test.kt)
- [junit/mythbuster/MythBuster.kt](app/src/test/java/com/scarlet/junit/mythbuster/MythBuster.kt)

***

### JUnit 4 Rules

- [junit/rules/MyTestRule.kt](app/src/test/java/com/scarlet/junit/rules/MyTestRule.kt)
- [junit/rules/YourTestRule.kt](app/src/test/java/com/scarlet/junit/rules/YourTestRule.kt)
- [junit/rules/TestRulesTest.kt](app/src/test/java/com/scarlet/junit/rules/TestRulesTest.kt)

***

### Mockk vs. Mockito

- [mockk/M01_ActionHandlerTest.kt](app/src/test/java/com/scarlet/mockk/M01_ActionHandlerTest.kt)
- [mockk/M02_Mockk_CoroutinesTest.kt](app/src/test/java/com/scarlet/mockk/M02_Mockk_CoroutinesTest.kt)
- [mockk/M03_ObjectMockTest.kt](app/src/test/java/com/scarlet/mockk/M03_ObjectMockTest.kt)
- [mockk/M04_ExtensionFunctionTest.kt](app/src/test/java/com/scarlet/mockk/M04_ExtensionFunctionTest.kt)
- [mockk/MockKTest.kt](app/src/test/java/com/scarlet/mockk/MockKTest.kt)
- [mockk/MockitoTest.kt](app/src/test/java/com/scarlet/mockk/MockitoTest.kt)

***

### Testing Utilities

- [util/LiveDataTestUtil.kt (getValueForTest, captureValues, etc.)](app/src/test/java/com/scarlet/util/LiveDataTestUtil.kt)
- [util/TestUtils.kt (testDispatcher)](app/src/test/java/com/scarlet/util/TestUtils.kt)

***

### Coroutines Exception Handling Tests

- [coroutines/exceptions/CE01_LaunchEHTest.kt](app/src/test/java/com/scarlet/coroutines/exceptions/CE01_LaunchEHTest.kt)
- [coroutines/exceptions/CE02_LaunchSupervisorJobTest.kt](app/src/test/java/com/scarlet/coroutines/exceptions/CE02_LaunchSupervisorJobTest.kt)
- [coroutines/exceptions/CE03_coroutineScope_ScopeBuilderTest.kt](app/src/test/java/com/scarlet/coroutines/exceptions/CE03_coroutineScope_ScopeBuilderTest.kt)
- [coroutines/exceptions/CE04_supervisorScope_ScopeBuilderTest.kt](app/src/test/java/com/scarlet/coroutines/exceptions/CE03_coroutineScope_ScopeBuilderTest.kt)
- [coroutines/exceptions/CE05_ExceptionHandlerTest.kt](app/src/test/java/com/scarlet/coroutines/exceptions/CE05_ExceptionHandlerTest.kt)
- [coroutines/exceptions/CE06_AsyncEHTest.kt](app/src/test/java/com/scarlet/coroutines/exceptions/CE06_AsyncEHTest.kt)
- [coroutines/exceptions/CE07_CancellationTest.kt](app/src/test/java/com/scarlet/coroutines/exceptions/CE07_CancellationTest.kt)
- [coroutines/exceptions/CE08_CoroutineScopeFunctionsTest.kt](app/src/test/java/com/scarlet/coroutines/exceptions/CE08_CoroutineScopeFunctionsTest.kt)
- [coroutines/exceptions/StructuredConcurrencyTest.kt](app/src/test/java/com/scarlet/coroutines/exceptions/StructuredConcurrencyTest.kt)

***

### Migration from Callback to Coroutines Tests

- [coroutines/migration/CvtToSuspendingFunctionTest.kt](app/src/test/java/com/scarlet/coroutines/migration/CvtToSuspendingFunctionTest.kt)

***

### Test Coroutine Builder, Virtual Time Control, Test Dispatchers, and TestScope

- [coroutines/testing/intro/T01_RunBlockingVsRunTest.kt](app/src/test/java/com/scarlet/coroutines/testing/intro/T01_RunBlockingVsRunTest.kt)
- [coroutines/testing/intro/T02_VirtualTimeControlTest.kt](app/src/test/java/com/scarlet/coroutines/testing/intro/T02_VirtualTimeControlTest.kt)
- [coroutines/testing/intro/T03_Timeout01Test.kt](app/src/test/java/com/scarlet/coroutines/testing/intro/T03_Timeout01Test.kt)
- [coroutines/testing/intro/T04_Timeout02Test.kt](app/src/test/java/com/scarlet/coroutines/testing/intro/T04_Timeout02Test.kt)
- [coroutines/testing/intro/T05_CoroutineLeakTest.kt](app/src/test/java/com/scarlet/coroutines/testing/intro/T05_CoroutineLeakTest.kt)

***

### Testing Livedata

- [coroutines/testing/livedata/ArticleViewModelTest.kt](app/src/test/java/com/scarlet/coroutines/testing/livedata/ArticleViewModelTest.kt)

***

### Coroutine Test Rules

- [coroutines/testing/CoroutineTestRule.kt](app/src/test/java/com/scarlet/coroutines/testing/CoroutineTestRule.kt)
