/*
 * Copyright (C) 2006 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.test.test_druid.firewall.timelimiter;

import static org.test.test_druid.firewall.ratelimiter.Preconditions.checkArgument;
import static org.test.test_druid.firewall.ratelimiter.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.test.test_druid.firewall.ratelimiter.UncheckedTimeoutException;


/**
 * A TimeLimiter that runs method calls in the background using an
 * {@link ExecutorService}.  If the time limit expires for a given method call,
 * the thread running the call will be interrupted.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class SimpleTimeLimiter implements TimeLimiter {

  private final ExecutorService executor;

  /**
   * Constructs a TimeLimiter instance using the given executor service to
   * execute proxied method calls.
   * <p>
   * <b>Warning:</b> using a bounded executor
   * may be counterproductive!  If the thread pool fills up, any time callers
   * spend waiting for a thread may count toward their time limit, and in
   * this case the call may even time out before the target method is ever
   * invoked.
   *
   * @param executor the ExecutorService that will execute the method calls on
   *     the target objects; for example, a {@link
   *     Executors#newCachedThreadPool()}.
   */
  public SimpleTimeLimiter(ExecutorService executor) {
    this.executor = checkNotNull(executor);
  }

  /**
   * Constructs a TimeLimiter instance using a {@link
   * Executors#newCachedThreadPool()} to execute proxied method calls.
   *
   * <p><b>Warning:</b> using a bounded executor may be counterproductive! If
   * the thread pool fills up, any time callers spend waiting for a thread may
   * count toward their time limit, and in this case the call may even time out
   * before the target method is ever invoked.
   */
  public SimpleTimeLimiter() {
    this(Executors.newCachedThreadPool());
  }

  @Override
  public <T> T newProxy(final T target, Class<T> interfaceType,
      final long timeoutDuration, final TimeUnit timeoutUnit) {
    checkNotNull(target);
    checkNotNull(interfaceType);
    checkNotNull(timeoutUnit);
    checkArgument(timeoutDuration > 0, "bad timeout: %s", timeoutDuration);
    checkArgument(interfaceType.isInterface(),
        "interfaceType must be an interface type");

    final Set<Method> interruptibleMethods
        = findInterruptibleMethods(interfaceType);

    InvocationHandler handler = new InvocationHandler() {
      @Override
      public Object invoke(Object obj, final Method method, final Object[] args)
          throws Throwable {
        Callable<Object> callable = new Callable<Object>() {
          @Override
          public Object call() throws Exception {
            try {
              return method.invoke(target, args);
            } catch (InvocationTargetException e) {
              throwCause(e, false);
              throw new AssertionError("can't get here");
            }
          }
        };
        return callWithTimeout(callable, timeoutDuration, timeoutUnit,
            interruptibleMethods.contains(method));
      }
    };
    return newProxy(interfaceType, handler);
  }

  // TODO: should this actually throw only ExecutionException?
  @Override
  public <T> T callWithTimeout(Callable<T> callable, long timeoutDuration,
      TimeUnit timeoutUnit, boolean amInterruptible) throws Exception {
    checkNotNull(callable);
    checkNotNull(timeoutUnit);
    checkArgument(timeoutDuration > 0, "timeout must be positive: %s",
        timeoutDuration);
    Future<T> future = executor.submit(callable);
    try {
      if (amInterruptible) {
        try {
          return future.get(timeoutDuration, timeoutUnit);
        } catch (InterruptedException e) {
          future.cancel(true);
          throw e;
        }
      } else {
        return getUninterruptibly(future, 
            timeoutDuration, timeoutUnit);
      }
    } catch (ExecutionException e) {
      throw throwCause(e, true);
    } catch (TimeoutException e) {
      future.cancel(true);
      throw new UncheckedTimeoutException(e);
    }
  }

  private static Exception throwCause(Exception e, boolean combineStackTraces)
      throws Exception {
    Throwable cause = e.getCause();
    if (cause == null) {
      throw e;
    }
    if (combineStackTraces) {
      StackTraceElement[] combined = concat(cause.getStackTrace(),
          e.getStackTrace(), StackTraceElement.class);
      cause.setStackTrace(combined);
    }
    if (cause instanceof Exception) {
      throw (Exception) cause;
    }
    if (cause instanceof Error) {
      throw (Error) cause;
    }
    // The cause is a weird kind of Throwable, so throw the outer exception.
    throw e;
  }

  private static Set<Method> findInterruptibleMethods(Class<?> interfaceType) {
    Set<Method> set = new HashSet<Method>();
    for (Method m : interfaceType.getMethods()) {
      if (declaresInterruptedEx(m)) {
        set.add(m);
      }
    }
    return set;
  }

  private static boolean declaresInterruptedEx(Method method) {
    for (Class<?> exType : method.getExceptionTypes()) {
      // debate: == or isAssignableFrom?
      if (exType == InterruptedException.class) {
        return true;
      }
    }
    return false;
  }

  // TODO: replace with version in common.reflect if and when it's open-sourced
  private static <T> T newProxy(
      Class<T> interfaceType, InvocationHandler handler) {
    Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(),
        new Class<?>[] { interfaceType }, handler);
    return interfaceType.cast(object);
  }
  
  /**
   * Invokes
   * {@code future.}{@link Future#get(long, TimeUnit) get(timeout, unit)}
   * uninterruptibly.
   *
   * <p>If instead, you wish to treat {@link InterruptedException} uniformly
   * with other exceptions, see {@link Futures#getChecked(Future, Class)
   * Futures.getChecked}.
   *
   * @throws ExecutionException if the computation threw an exception
   * @throws CancellationException if the computation was cancelled
   * @throws TimeoutException if the wait timed out
   */
  public static <V> V getUninterruptibly(
      Future<V> future, long timeout, TimeUnit unit)
          throws ExecutionException, TimeoutException {
    boolean interrupted = false;
    try {
      long remainingNanos = unit.toNanos(timeout);
      long end = System.nanoTime() + remainingNanos;

      while (true) {
        try {
          // Future treats negative timeouts just like zero.
          return future.get(remainingNanos, NANOSECONDS);
        } catch (InterruptedException e) {
          interrupted = true;
          remainingNanos = end - System.nanoTime();
        }
      }
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }
  
  
  /**
   * Returns a new array that contains the concatenated contents of two arrays.
   *
   * @param first the first array of elements to concatenate
   * @param second the second array of elements to concatenate
   * @param type the component type of the returned array
   */
  @SuppressWarnings("unchecked")
	public static <T> T[] concat(T[] first, T[] second, Class<T> type) {
		T[] result = (T[]) Array
				.newInstance(type, first.length + second.length);
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
  
}
